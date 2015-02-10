package uk.nhs.itk.ciao.model;

import java.util.ArrayList;

import uk.nhs.interoperability.payloads.HL7Date;
import uk.nhs.interoperability.payloads.commontypes.Address;
import uk.nhs.interoperability.payloads.commontypes.PersonName;
import uk.nhs.interoperability.payloads.commontypes.Telecom;

/**
 * This is a general patient object, agnostic of any particular system or standard - this can
 * then be used to link together routes which require details about a patient
 * @author Adam Hatherly
 */
public class Patient {
	
	private String nhsNumber;
	private PersonName name;
	private Address address;
	private HL7Date dateOfBirth;
	private HL7Date dateOfDeath;
	private String gender;
	private ArrayList<Telecom> telecom;
	private String practiceCode;
	
	public String getNhsNumber() {
		return nhsNumber;
	}
	public void setNhsNumber(String nhsNumber) {
		this.nhsNumber = nhsNumber;
	}
	public PersonName getName() {
		return name;
	}
	public void setName(PersonName name) {
		this.name = name;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public HL7Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(HL7Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public HL7Date getDateOfDeath() {
		return dateOfDeath;
	}
	public void setDateOfDeath(HL7Date dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public ArrayList<Telecom> getTelecom() {
		return telecom;
	}
	public void setTelecom(ArrayList<Telecom> telecom) {
		this.telecom = telecom;
	}
	public void addTelecom(Telecom telecom) {
		if (this.telecom == null) {
			this.telecom = new ArrayList<Telecom>();
		}
		this.telecom.add(telecom);
	}
	public String getPracticeCode() {
		return practiceCode;
	}
	public void setPracticeCode(String practiceCode) {
		this.practiceCode = practiceCode;
	}
	
	
	/*name.addGivenSimple("Adam");
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
	patient.setGender(gender);*/
}
