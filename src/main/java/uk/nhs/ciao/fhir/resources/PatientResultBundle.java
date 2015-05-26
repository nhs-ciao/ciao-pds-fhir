package uk.nhs.ciao.fhir.resources;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Resource;

import uk.nhs.interoperability.payloads.util.CDAUUID;

public class PatientResultBundle {
	public static AtomFeed buildBundle(List<Patient> patientList, String fhirBase) {
		DateAndTime now = new DateAndTime(new Date());
		AtomFeed feed = new AtomFeed();
		feed.setTitle("Patient matches");
		feed.setId("urn:uuid:"+CDAUUID.generateUUIDString());
		feed.setTotalResults(patientList.size());
		feed.setUpdated(now);
		feed.setAuthorName("PDS");
		
		List<AtomEntry<? extends Resource>> list = feed.getEntryList();
		for (Patient patient : patientList) {
			String nhsNo = patient.getIdentifier().get(0).getValueSimple();
			AtomEntry entry = new AtomEntry();
			entry.setResource(patient);
			entry.setTitle("PDS Patient match: " + nhsNo);
			entry.setId(fhirBase + "Patient/" + nhsNo);
			// FHIR requires an updated date for the resource - we don't get this back from PDS simple trace...
			// TODO: Establish what we should use for the last updated date
			entry.setUpdated(now);
			// We also don't get an author back from a simple trace...
			// TODO: Establish what we should use for the author
			entry.setPublished(now);
			entry.setAuthorName("PDS");
			list.add(entry);
		}
		
		return feed;
	}	
}
