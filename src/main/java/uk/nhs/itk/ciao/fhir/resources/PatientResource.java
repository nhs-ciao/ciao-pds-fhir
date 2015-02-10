package uk.nhs.itk.ciao.fhir.resources;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Identifier;
import org.hl7.fhir.instance.model.Patient;

public class PatientResource {
	
	public static void main(String[] args) throws Exception {
		Patient patient = new Patient();
		
		HumanName name = patient.addName();
		name.addGivenSimple("Adam");
		name.addFamilySimple("Hatherly");
		name.addPrefixSimple("Mr");
		name.setTextSimple("Mr Adam Hatherly");
		
		Identifier id = patient.addIdentifier();
		id.setLabelSimple("NHS Number");
		id.setSystemSimple("http://nhs.uk/fhir/nhsnumber");
		id.setValueSimple("1234567890");
		
		CodeableConcept gender = new CodeableConcept();
		gender.setTextSimple("Male");
		Coding coding = gender.addCoding();
		coding.setSystemSimple("http://hl7.org/fhir/vs/administrative-gender");
		coding.setCodeSimple("M");
		patient.setGender(gender);
		
		XmlComposer composer = new XmlComposer();
		OutputStream out = new ByteArrayOutputStream();
		composer.compose(out, patient, true);
		
		System.out.println("Patient Resource: \n\n" + out.toString());
	
	
		
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
