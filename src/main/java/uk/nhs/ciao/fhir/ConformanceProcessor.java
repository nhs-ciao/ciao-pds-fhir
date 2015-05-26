package uk.nhs.ciao.fhir;
import java.io.File;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.client.ResourceFormat;

import static org.hl7.fhir.instance.client.ResourceFormat.RESOURCE_XML;
import static org.hl7.fhir.instance.client.ResourceFormat.RESOURCE_JSON;
import uk.nhs.ciao.fhir.resources.ConformanceResource;
import uk.nhs.ciao.fhir.resources.MimeTypes;
import uk.nhs.ciao.fhir.resources.PatientResource;
import uk.nhs.ciao.model.Patient;
import uk.nhs.ciao.spine.HL7PayloadBuilder;
import uk.nhs.ciao.spine.HL7ResponseParser;
import uk.nhs.interoperability.payloads.util.FileLoader;

/**
 * There are two methods to query for FHIR conformance:
 * 
 * GET [base]/metadata {?_format=[mime-type]}
 * OPTIONS [base] {?_format=[mime-type]}
 * 
 * @author Adam Hatherly
 */
public class ConformanceProcessor implements Processor {
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
		
		// Build resource
		String conformanceResource = ConformanceResource.getConformanceResource(responseFormat);
		Message out = exchange.getOut();
		out.setBody(conformanceResource);
	}
}
