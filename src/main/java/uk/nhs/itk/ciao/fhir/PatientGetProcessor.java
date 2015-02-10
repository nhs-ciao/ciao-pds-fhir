package uk.nhs.itk.ciao.fhir;
import java.io.File;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import uk.nhs.interoperability.payloads.util.FileLoader;
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
		
		Message in = exchange.getIn();
		String surname = in.getHeader("family").toString();
		
		String requestPayload = HL7PayloadBuilder.buildSimpleTrace(surname);
		
		Message out = exchange.getOut();
		out.setBody(requestPayload);
		out.setHeader("SOAPaction", "urn:nhs:names:services:pdsquery/QUPA_IN000005UK01");
		out.setHeader(Exchange.HTTP_URI, "http://127.0.0.1:4001/syncservice-pds/pds");
		
		//String testVal = FileLoader.loadFile(new File("/opt/SpineTKW/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000005UK01_MCCI_IN010000UK13_noheaders.xml"));
		//out.setBody(testVal);
	}
}
