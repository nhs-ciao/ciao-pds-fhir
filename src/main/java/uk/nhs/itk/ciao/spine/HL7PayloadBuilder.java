package uk.nhs.itk.ciao.spine;

import uk.nhs.interoperability.payloads.DateValue;
import uk.nhs.interoperability.payloads.commontypes.SMSPPersonName;
import uk.nhs.interoperability.payloads.spine.SpineSOAP;
import uk.nhs.interoperability.payloads.spine.SpineSOAPSimpleTraceBody;
import uk.nhs.interoperability.payloads.util.CDAUUID;
import uk.nhs.interoperability.payloads.vocabularies.generated.HL7StandardVersionCode;
import uk.nhs.interoperability.payloads.vocabularies.generated.ProcessingID;
import uk.nhs.interoperability.payloads.vocabularies.generated.ProcessingMode;
import uk.nhs.interoperability.payloads.vocabularies.internal.PersonNameType;
import uk.nhs.itk.ciao.util.PropertyReader;

public class HL7PayloadBuilder {
	
	/**
	 * This method will validate the request parameters provided
	 * Get by NHS Number:
	 * REQ-PDSMS-4.5.1
	 * REQ-PDSMS-4.5.2
	 * REQ-PDSMS-4.5.3
	 * Get by search:
	 * REQ-PDSMS-4.6.1
	 * REQ-PDSMS-4.6.2
	 * @return true if request is valid
	 */
	public static boolean validateRequest() {
		// TODO: Implement validation
		return true;
	}
	
	public static String buildSimpleTrace(String surname, String gender, String dateOfBirth) {
		
		String messageUUID = CDAUUID.generateUUIDString();
		String ciaoASID = PropertyReader.getProperty("ASID");
		System.out.println(ciaoASID);
		
		SpineSOAP template = new SpineSOAP();

		template.setMessageID("uuid:"+messageUUID);
		template.setAction("urn:nhs:names:services:pdsquery/QUPA_IN000005UK01");
		template.setTo("https://pds-sync.nis1.national.ncrs.nhs.uk/syncservice-pds/pds");
		template.setFrom("FROMADDRESS");
		template.setReceiverASID("PDSASID");
		template.setSenderASID(ciaoASID);
		template.setReplyAddress("FROMADDRESS");
		
		SpineSOAPSimpleTraceBody body = new SpineSOAPSimpleTraceBody();
		// Transmission Wrapper Fields (Send Message Payload)
		body.setTransmissionID(messageUUID);
		//body.setTransmissionID(CDAUUID.generateUUIDString());
		body.setTransmissionCreationTime(new DateValue("20150118100231"));
		body.setTransmissionHL7VersionCode(HL7StandardVersionCode._V3NPfIT30);
		body.setTransmissionInteractionID("QUPA_IN000005UK01");
		body.setTransmissionProcessingCode(ProcessingID._Production);
		body.setTransmissionProcessingModeCode(ProcessingMode._Currentprocessing);
		body.setTransmissionReceiverASID("PDSASID");
		body.setTransmissionSenderASID(ciaoASID);
		
		// Control Act Wrapper Fields
		body.setControlActSenderASID(ciaoASID);
		
		// Actual Query Payload
		//body.setGender(Sex._Female.code);
		if (gender != null) {
			body.setGender(gender);
		}
		
		//body.setDateOfBirth(new DateValue("19661111"));
		if (dateOfBirth != null) {
			body.setDateOfBirth(new DateValue(dateOfBirth));
		}
		
		// Add provided parameters for query
		if (surname != null) {
			body.setName(new SMSPPersonName()
								.setFamilyName(surname));
			body.setNameType(PersonNameType.Legal.code);
		}
		//body.setPostcode("LS11AB");
		//body.setAddressUse(AddressType.Home.code);
		
		template.setPayload(body);
		
		return template.serialise();
	}
}
