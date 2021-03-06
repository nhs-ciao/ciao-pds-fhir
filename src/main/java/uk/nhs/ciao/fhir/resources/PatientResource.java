package uk.nhs.ciao.fhir.resources;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.client.ResourceFormat;
import org.hl7.fhir.instance.model.Address.AddressUse;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.BooleanType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.Contact;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Identifier;
import org.hl7.fhir.instance.model.Patient;

import uk.nhs.ciao.util.V3FHIRTypeMappers;
import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.commontypes.Telecom;
import uk.nhs.interoperability.payloads.helpers.HumanReadableFormatter;

public class PatientResource {
	
	public static String buildPatientResource(uk.nhs.ciao.model.Patient spinePatient, ResourceFormat format, String fhirBase) {
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
			String genderCode = V3FHIRTypeMappers.getFHIRGender(spinePatient.getGender());
			if (genderCode != null) {
				CodeableConcept gender = new CodeableConcept();
				gender.setTextSimple(spinePatient.getGender().displayName);
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
		if (spinePatient.getDateOfBirth() != null) {
			patientResource.setBirthDateSimple(new DateAndTime(spinePatient.getDateOfBirth().getDate()));
		}
		
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
				Contact contact = patientResource.addTelecom();
				V3FHIRTypeMappers.getFHIRTelecom(contact, spineTelecom);
			}
		}
		// PracticeCode
		
		// Now put our patient resource into a bundle
		//List<Patient> patientList = new ArrayList<Patient>();
		//patientList.add(patientResource);
		//AtomFeed feed = PatientResultBundle.buildBundle(patientList, fhirBase);
		
		
		//return ResourceSerialiser.serialise(feed, format);
		return ResourceSerialiser.serialise(patientResource, format);
		
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
