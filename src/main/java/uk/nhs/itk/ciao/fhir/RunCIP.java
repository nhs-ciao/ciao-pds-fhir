package uk.nhs.itk.ciao.fhir;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jndi.JndiContext;

/**
 * To test the CIP pointing at TKW use one of the following:
 * 
 * Simple trace, single match:
 * curl http://localhost:8080/fhir/Patient?family=PURVES
 * curl http://localhost:8080/fhir/Patient?family=LEWINGTON
 * curl http://localhost:8080/fhir/Patient?family=STAMBUKDELIFSCHITZ
 * ??? curl http://localhost:8080/fhir/Patient?family=History
 * 
 * No match:
 * curl http://localhost:8080/fhir/Patient?family=NOMATCH
 * 
 * SOAP Fault:
 * curl http://localhost:8080/fhir/Patient?family=SOAPFAULT
 * 
 * Business Fault:
 * curl http://localhost:8080/fhir/Patient?family=MCCI

 * 
 * To send a raw SOAP request to Spine TKW:
 * curl --header "SOAPAction: urn:nhs:names:services:pdsquery/QUPA_IN000005UK01" -X POST --data-binary @/opt/SpineTKW/contrib/SPINE_Test_Messages/MTH_Test_Messages/PDS2008A_Example_Input_Msg/QUPA_IN000005UK01_MCCI_IN010000UK13_noheaders.xml http://localhost:4001/syncservice-pds/pds
 * 
 * 
 * @author adam
 *
 */
public class RunCIP {

	public static void main(String[] args) {
		try {
			// Start camel
			CamelContext context = createCamelContext();
			context.setStreamCaching(true);
			context.setTracing(true);
			context.getProperties().put(Exchange.LOG_DEBUG_BODY_STREAMS, "true");
			context.addRoutes(new CIPRoutes());
			context.start();
			System.out.println("CAMEL STARTED");
			
			// And do a test
			/*Thread.sleep(500);
			System.out.println("Calling route.");
			ProducerTemplate template = context.createProducerTemplate();
			String response = template.requestBody("direct:pretendRequest", "", String.class);
			System.out.println("Result: " + response);*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static CamelContext createCamelContext() throws Exception {	
		// Register our custom beans using JNDI (in the live component this is configured in beans.xml)
		JndiContext jndi = new JndiContext();
		jndi.bind("patientGetProcessor", new PatientGetProcessor());
		jndi.bind("patientPostProcessor", new PatientPostProcessor());
		jndi.bind("patientResponseProcessor", new PatientResponseProcessor());
		jndi.bind("conformanceProcessor", new ConformanceProcessor());
		CamelContext context = new DefaultCamelContext(jndi);
		//CamelContext context = super.createCamelContext();
		
		// Now, configure our jms component to use seda, which is a simple in-memory queue. In the live
		// component, this is configured to use activemq in beans.xml
		context.addComponent("jms", context.getComponent("seda"));
		return context;
	}
}
