package uk.nhs.itk.ciao.fhir.util;

import org.hl7.fhir.instance.model.Contact;
import org.hl7.fhir.instance.model.Contact.ContactSystem;
import org.hl7.fhir.instance.model.Contact.ContactUse;

import uk.nhs.interoperability.payloads.commontypes.Telecom;
import uk.nhs.interoperability.payloads.helpers.HelperUtils;
import uk.nhs.interoperability.payloads.vocabularies.generated.Sex;
import uk.nhs.interoperability.payloads.vocabularies.internal.TelecomUseType;

public class V3FHIRTypeMappers {
	
	/**
	 * Map from a gender value held in a HL7v3 message to a FHIR gender code
	 * @param v3gender
	 * @return FHIR gender code
	 */
	public static String getFHIRGender(String v3gender) {
		String genderCode = null;
		// Match gender based on display name
		for (Sex val : Sex.values()) {
			if (val.displayName.equals(v3gender)) {
				genderCode = val.code;
			}
		}
		return genderCode;
	}
	
	/**
	 * Converts from a HL7v3 Telecom type to a FHIR Contact type.
	 * @param fhirContact
	 * @param v3telecom
	 */
	public static void getFHIRTelecom(Contact fhirContact, Telecom v3telecom) {
		
		String number = v3telecom.getTelecom();
		
		// Number
		fhirContact.setValueSimple(HelperUtils.stripPrefixFromTelecom(number));
		
		// System
		if (HelperUtils.isTel(number)) {
			fhirContact.setSystemSimple(ContactSystem.phone);
		} else if (HelperUtils.isEmail(number)) {
			fhirContact.setSystemSimple(ContactSystem.email);
		} else if (HelperUtils.isFax(number)) {
			fhirContact.setSystemSimple(ContactSystem.fax);
		} 
		
		// Use Code
		String v3telecomUse = v3telecom.getTelecomType();
		if (v3telecomUse.equals(TelecomUseType.HomeAddress.code)
			|| v3telecomUse.equals(TelecomUseType.PrimaryHome.code)) {
			fhirContact.setUseSimple(ContactUse.home);
		}
		else if (v3telecomUse.equals(TelecomUseType.MobileContact.code)) {
			fhirContact.setUseSimple(ContactUse.mobile);
		}
	}
}
