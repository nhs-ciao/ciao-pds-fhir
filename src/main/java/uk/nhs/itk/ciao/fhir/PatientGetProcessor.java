package uk.nhs.itk.ciao.fhir;
import java.io.File;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import uk.nhs.interoperability.payloads.util.FileLoader;
import uk.nhs.itk.ciao.spine.HL7PayloadBuilder;
import uk.nhs.itk.ciao.util.PropertyReader;

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
public class PatientGetProcessor implements Processor {
	public void process(Exchange exchange) throws Exception {
		
		// Search examples from FHIR spec
		// GET [base]/Patient?_id=123456789012
		// GET [base]/Patient/123456789012
		// GET [base]/Patient?name=eve
		
		// Search for all the patients with an identifier with 
		// key = "2345" in the system "http://acme.org/patient"
		// GET [base]/Patient?identifier=http://acme.org/patient|2345
		
		// GET [base]/Patient?gender:text=male
		
		String pdsURL = PropertyReader.getProperty("PDSURL");
		
		Message in = exchange.getIn();
		
		String surname = null;
		if (in.getHeader("family") != null) {
			surname = in.getHeader("family").toString();
		}
		
		String gender = null;
		if (in.getHeader("gender") != null) {
			gender = in.getHeader("gender").toString();
		}
		
		String dateOfBirth = null;
		if (in.getHeader("birthdate") != null) {
			dateOfBirth = in.getHeader("birthdate").toString();
		}
		
		String requestPayload = HL7PayloadBuilder.buildSimpleTrace(surname, gender, dateOfBirth);
		
		Message out = exchange.getOut();
		out.setBody(requestPayload);
		// Propagate format from the original request
		out.setHeader("_format", in.getHeader("_format"));
		// Add some additional headers for the Spine call
		out.setHeader("SOAPaction", "urn:nhs:names:services:pdsquery/QUPA_IN000005UK01");
		out.setHeader(Exchange.HTTP_URI, pdsURL);
		
		//String testVal = FileLoader.loadFile(new File("/opt/SpineTKW/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000005UK01_MCCI_IN010000UK13_noheaders.xml"));
		//out.setBody(testVal);
	}
}
