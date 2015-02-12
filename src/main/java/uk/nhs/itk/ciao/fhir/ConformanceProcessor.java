package uk.nhs.itk.ciao.fhir;
import java.io.File;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import uk.nhs.interoperability.payloads.util.FileLoader;
import uk.nhs.itk.ciao.fhir.resources.ConformanceResource;
import uk.nhs.itk.ciao.fhir.resources.PatientResource;
import uk.nhs.itk.ciao.model.Patient;
import uk.nhs.itk.ciao.spine.HL7PayloadBuilder;
import uk.nhs.itk.ciao.spine.HL7ResponseParser;

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
		String conformanceResource = ConformanceResource.getConformanceResource();
		Message out = exchange.getOut();
		out.setBody(conformanceResource);
	}
}
