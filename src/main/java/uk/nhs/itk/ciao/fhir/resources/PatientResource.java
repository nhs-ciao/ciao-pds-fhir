package uk.nhs.itk.ciao.fhir.resources;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.Address.AddressUse;
import org.hl7.fhir.instance.model.BooleanType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Identifier;
import org.hl7.fhir.instance.model.Patient;

import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.commontypes.Telecom;
import uk.nhs.interoperability.payloads.helpers.HumanReadableFormatter;
import uk.nhs.interoperability.payloads.vocabularies.generated.Sex;

public class PatientResource {
	
	public static String buildPatientResource(uk.nhs.itk.ciao.model.Patient spinePatient) throws Exception {
		Patient patientResource = new Patient();
		
		// Name
		PersonName personName = spinePatient.getName();
		if (personName != null) {
			HumanName name = patientResource.addName();
			for (String givenName : personName.getGivenName()) {
				name.addGivenSimple(givenName);
			}
			name.addFamilySimple(personName.getFamilyName());
			name.addPrefixSimple(personName.getTitle());
			name.setTextSimple(HumanReadableFormatter.makeHumanReadablePersonName(personName));
		}
		
		// NHS Number
		if (spinePatient.getNhsNumber() != null) {
			Identifier id = patientResource.addIdentifier();
			id.setLabelSimple("NHS Number");
			id.setSystemSimple("http://nhs.uk/fhir/nhsnumber");
			id.setValueSimple(spinePatient.getNhsNumber());
		}
		
		// Gender
		if (spinePatient.getGender() != null) {
			String genderCode = null;
			// Match gender based on display name
			for (Sex val : Sex.values()) {
				if (val.displayName.equals(spinePatient.getGender())) {
					genderCode = val.code;
				}
			}
			if (genderCode != null) {
				CodeableConcept gender = new CodeableConcept();
				gender.setTextSimple(spinePatient.getGender());
				Coding coding = gender.addCoding();
				coding.setSystemSimple("http://hl7.org/fhir/vs/administrative-gender");
				coding.setCodeSimple(genderCode);
				patientResource.setGender(gender);
			}
		}
		
		// Address
		if (spinePatient.getAddress() != null) {
			for (Address spineAddress : spinePatient.getAddress()) {
				org.hl7.fhir.instance.model.Address address = patientResource.addAddress();
				for (String addressLine : spineAddress.getAddressLine()) {
					address.addLineSimple(addressLine);
				}
				address.setZipSimple(spineAddress.getPostcode());
				address.setUseSimple(AddressUse.home);
			}
		}
		
		// DateOfBirth
		patientResource.setBirthDateSimple(new DateAndTime(spinePatient.getDateOfBirth().getDate()));
		
		// DateOfDeath
		BooleanType bool = new BooleanType();
		if (spinePatient.getDateOfDeath() != null) {
			bool.setValue(true);
			patientResource.setDeceased(bool);
		} else {
			bool.setValue(false);
			patientResource.setDeceased(bool);
		}
		
		// Telecom
		if (spinePatient.getTelecom() != null) {
			for (Telecom spineTelecom : spinePatient.getTelecom()) {
				
			}
		}
		// PracticeCode
		
		XmlComposer composer = new XmlComposer();
		OutputStream out = new ByteArrayOutputStream();
		composer.compose(out, patientResource, true);
		
		return out.toString();
		
		/*
		If we were using DSTU2 the code would be different - e.g.:
		
		Patient patient = new Patient();
		HumanName name = patient.addName();
		
		name.addGiven("Adam");
		name.addFamily("Hatherly");
		name.addPrefix("Mr");
		name.setText("Mr Adam Hatherly");
		
		Identifier id = patient.addIdentifier();
		id.setLabel("NHS Number");
		id.setSystem("http://nhs.uk/fhir/nhsnumber");
		id.setValue("1234567890");
		
		AdministrativeGender gender = AdministrativeGender.MALE;
		patient.setGender(gender);
		
		XmlComposer composer = new XmlComposer();
		OutputStream out = new ByteArrayOutputStream();
		composer.compose(out, patient, true);
		
		System.out.println("Patient Resource: \n\n" + out.toString());
		*/
	}
}
