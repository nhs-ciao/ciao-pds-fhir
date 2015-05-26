package uk.nhs.ciao.spine;

import uk.nhs.ciao.model.Patient;
import uk.nhs.interoperability.payloads.Payload;
import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.commontypes.Telecom;
import uk.nhs.interoperability.payloads.helpers.HelperUtils;
import uk.nhs.interoperability.payloads.spine.PDSPatientID;
import uk.nhs.interoperability.payloads.spine.SpineResponseBody;
import uk.nhs.interoperability.payloads.spine.SpineSOAPResponse;
import uk.nhs.interoperability.payloads.spine.SpineSOAPSimpleTraceResponseBody;
import uk.nhs.interoperability.payloads.vocabularies.generated.Sex;
import uk.nhs.interoperability.payloads.vocabularies.internal.AddressType;
import uk.nhs.interoperability.payloads.vocabularies.internal.PersonNameType;
import uk.nhs.interoperability.payloads.vocabularies.internal.TelecomUseType;

public class HL7ResponseParser {
	public static Patient parseSpineResponse(String response) {
		Patient patient = new Patient();
		// Parse the response using itk-payloads
		SpineSOAPResponse template = new SpineSOAPResponse();
		template.parse(response);
		
		SpineResponseBody payload = template.getPayload();
		if (payload != null) {
			String responseType = ((Payload)payload).getClassName();
			if (responseType.equals("SpineSOAPSimpleTraceResponseBody")) {
				// We have a simple trace response, extract the patient fields from it
				SpineSOAPSimpleTraceResponseBody responseBody = (SpineSOAPSimpleTraceResponseBody)payload;
				
				// Note: The rules for selecting names, addresses, etc are in the PDS Mini Services Specification
				// entitled "ITK Spine Mini Service" on TRUD.
				
				// Extract the name with an "L" use code, which is the "usual name" for the patient, filtering any
				// that are not active
				// REQ-PDSMS-4.5.2
				// REQ-PDSMS-4.6.5
				for (PersonName name : responseBody.getPatientName()) {
					if (name.getNameType().equals(PersonNameType.Legal.code)) {
						// TODO: Check validity dates for names
						patient.setName(name);
					}
				}
				
				// Extract the addresses (Home, Temporary, Correspondence), filtering any that are not active.
				// NOTE: According to MiM 6.2.02, the Simple Trace will only return the current (and future) home
				// addresses. It will not return temporary or correspondence addresses - these would need to come
				// from a retrieval.
				// REQ_PDSMS-4.6.4
				if (responseBody.getAddress() != null) {
					for (Address address : responseBody.getAddress()) {
						String use = address.getAddressUse();
						if (use != null) {
							// TODO: When retrieval is implemented, extend this to include temporary and correspondence addresses
							if (use.equals(AddressType.Home.code)) {
								if (address.getUseablePeriod() == null) {
									// No useable period, so assume current
									patient.addAddress(address);
								} else {
									if (HelperUtils.isCurrent(address.getUseablePeriod(), null)) {
										patient.addAddress(address);
									}
								}
							}
						}
					}
				}
				
				// Date of birth
				patient.setDateOfBirth(responseBody.getDateOfBirth());
				
				// NHS Number
				PDSPatientID id = responseBody.getNHSNumber();
				patient.setNhsNumber(id.getPatientID());
				
				// Date of Death
				patient.setDateOfDeath(responseBody.getDeceasedTime());
				
				// Gender
				patient.setGender(responseBody.getAdministrativeGenderEnum());
				
				// Telecoms - only Home, Primary Home and Mobile numbers, and only Telephone and Email values.
				// REQ-PDSMS-4.5.7
				// TODO: Implement effective dates for phone numbers
				if (responseBody.getTelecom() != null) {
					for (Telecom telecom : responseBody.getTelecom()) {
						String use = telecom.getTelecomType();
						String value = telecom.getTelecom();
						if (use != null) {
							if (use.equals(TelecomUseType.HomeAddress.code) ||
								use.equals(TelecomUseType.PrimaryHome.code) ||
								use.equals(TelecomUseType.MobileContact.code)) {
								// TODO: Check for values without a prefix
								if (HelperUtils.isTel(value) || HelperUtils.isEmail(value)) {
									patient.addTelecom(telecom);
								}
							}
						}
					}
				}
				
				// Practice Code
				patient.setPracticeCode(responseBody.getRegisteredGPID());
				
				// Practice Name
				// Practice Contact Telephone Number
				// Local Identifier
			}
		}
		
		return patient;
	}
}
