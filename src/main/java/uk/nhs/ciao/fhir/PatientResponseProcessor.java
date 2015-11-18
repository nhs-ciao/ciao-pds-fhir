package uk.nhs.ciao.fhir;

import static org.hl7.fhir.instance.client.ResourceFormat.RESOURCE_JSON;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.hl7.fhir.instance.client.ResourceFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ciao.configuration.CIAOConfig;
import uk.nhs.ciao.fhir.resources.MimeTypes;
import uk.nhs.ciao.fhir.resources.PatientResource;
import uk.nhs.ciao.model.Patient;
import uk.nhs.ciao.spine.pds.hl7.HL7ResponseParser;

public class PatientResponseProcessor implements Processor {
	private static Logger logger = LoggerFactory.getLogger(PatientResponseProcessor.class);
	
	//@PropertyInject("FHIR_BASE")
	String fhirBase = "http://ciaotest1-x26.hscic.nhs.uk/fhir/";

	public void process(Exchange exchange) throws Exception {
		
		/*CIAOConfig ciaoConfig = exchange.getContext().getRegistry().lookupByNameAndType("cipConfig", CIAOConfig.class);
		if (ciaoConfig == null) {
			logger.error("CIAO Config is NULL!");
		} else {
			logger.info("Config: ", ciaoConfig);
		}*/
		
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
		String patientResource = PatientResource.buildPatientResource(patient, responseFormat, fhirBase);
		
		Message out = exchange.getOut();
		out.setBody(patientResource);
	}
	
}
