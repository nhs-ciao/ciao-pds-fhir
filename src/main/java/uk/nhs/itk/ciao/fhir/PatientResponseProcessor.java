package uk.nhs.itk.ciao.fhir;

import static org.hl7.fhir.instance.client.ResourceFormat.RESOURCE_JSON;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.client.ResourceFormat;

import uk.nhs.itk.ciao.fhir.resources.MimeTypes;
import uk.nhs.itk.ciao.fhir.resources.PatientResource;
import uk.nhs.itk.ciao.model.Patient;
import uk.nhs.itk.ciao.spine.HL7ResponseParser;

public class PatientResponseProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		
		Message in = exchange.getIn();
		
		// Check requested response format
		ResourceFormat responseFormat = RESOURCE_JSON;
		if (in.getHeader("_format") != null) {
			ResourceFormat requestedFormat = MimeTypes.getFormatFromMimeType(in.getHeader("_format").toString());
			if (requestedFormat != null) {
				responseFormat = requestedFormat;
			}
		}
		
		String spineResponse = in.getBody(String.class);
		Patient patient = HL7ResponseParser.parseSpineResponse(spineResponse);
		String patientResource = PatientResource.buildPatientResource(patient, responseFormat);
		
		Message out = exchange.getOut();
		out.setBody(patientResource);
	}
	
}
