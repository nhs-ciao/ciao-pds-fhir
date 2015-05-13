package uk.nhs.itk.ciao.fhir;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.component.properties.PropertiesResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.interoperability.payloads.util.FileLoader;
import uk.nhs.itk.ciao.configuration.CIAOConfig;
import uk.nhs.itk.ciao.spine.HL7PayloadBuilder;

/**
 * GET Interactions for FHIR resources defined in the FHIR ReST API:
 * 
 * read			-> GET [base]/[type]/[id] {?_format=[mime-type]}
 * vread		-> GET [base]/[type]/[id]/_history/[vid] {?_format=[mime-type]}
 * search		-> GET [base]?[parameters] {&_format=[mime-type]}
 * history		-> GET [base]/[type]/[id]/_history{?[parameters]&_format=[mime-type]}
 * Get tags		-> GET [base]/_tags
 * 
 * @author adam
 *
 */
public class PatientGetProcessor {
	private static Logger logger = LoggerFactory.getLogger(PatientGetProcessor.class);
	
	public String process(String surname, String gender,
							String dateOfBirth) throws Exception {
		
		// Search examples from FHIR spec
		// GET [base]/Patient?_id=123456789012
		// GET [base]/Patient/123456789012
		// GET [base]/Patient?name=eve
		
		// Search for all the patients with an identifier with 
		// key = "2345" in the system "http://acme.org/patient"
		// GET [base]/Patient?identifier=http://acme.org/patient|2345
		
		// GET [base]/Patient?gender:text=male
		
		HL7PayloadBuilder builder = new HL7PayloadBuilder();
		String requestPayload = builder.buildSimpleTrace(surname, gender, dateOfBirth);
		
		/*
		Message out = exchange.getOut();
		out.setBody(requestPayload);
		// Propagate format from the original request
		out.setHeader("_format", in.getHeader("_format"));
		// Add some additional headers for the Spine call
		out.setHeader("SOAPaction", "urn:nhs:names:services:pdsquery/QUPA_IN000005UK01");
		out.setHeader(Exchange.HTTP_URI, pdsURL);
		*/
		return requestPayload;
		
		//String testVal = FileLoader.loadFile(new File("/opt/SpineTKW/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000005UK01_MCCI_IN010000UK13_noheaders.xml"));
		//out.setBody(testVal);
	}
}
