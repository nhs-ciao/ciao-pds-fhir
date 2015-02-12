package uk.nhs.itk.ciao.fhir;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import uk.nhs.itk.ciao.fhir.resources.PatientResource;
import uk.nhs.itk.ciao.model.Patient;
import uk.nhs.itk.ciao.spine.HL7ResponseParser;

public class PatientResponseProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		String spineResponse = exchange.getIn().getBody(String.class);
		Patient patient = HL7ResponseParser.parseSpineResponse(spineResponse);
		String patientResource = PatientResource.buildPatientResource(patient);
		
		Message out = exchange.getOut();
		out.setBody(patientResource);
	}
	
}
