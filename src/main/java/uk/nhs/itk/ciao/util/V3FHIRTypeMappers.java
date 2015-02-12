package uk.nhs.itk.ciao.util;

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
	public static String getFHIRGender(Sex v3gender) {
		if (Sex._Male.sameAs(v3gender)) {
			return "M";
		} else if (Sex._Female.sameAs(v3gender)) {
			return "F";
		}
		return null;
	}
	
	/**
	 * Map from a FHIR gender value to a HL7v3 gender code
	 * @param fhirGender
	 * @return HL7v3 gender code
	 */
	public static Sex getV3Gender(String fhirGender) {
		if (fhirGender == null) {
			return null;
		} else if (fhirGender.equals("M")) {
			return Sex._Male;
		} else if (fhirGender.equals("F")) {
			return Sex._Female;
		} else {
			return null;
		}
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
