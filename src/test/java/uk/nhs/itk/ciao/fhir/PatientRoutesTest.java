package uk.nhs.itk.ciao.fhir;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;

public class PatientRoutesTest extends CamelTestSupport {

	/**
	 * For the unit test, this will substitute an in-memory queue for a real
	 * JMS queue so we don't have to have ApacheMQ running to run the test
	 */
	@Override
	protected CamelContext createCamelContext() throws Exception {
		
		// Register our custom beans using JNDI (in the live component this is configured in beans.xml)
		JndiContext jndi = new JndiContext();
		jndi.bind("patientGetProcessor", new PatientGetProcessor());
		jndi.bind("patientPostProcessor", new PatientPostProcessor());
		CamelContext context = new DefaultCamelContext(jndi);
		//CamelContext context = super.createCamelContext();
		
		// Now, configure our jms component to use seda, which is a simple in-memory queue. In the live
		// component, this is configured to use activemq in beans.xml
		context.addComponent("jms", context.getComponent("seda"));
		return context;
	}
	
	/**
	 * Initialise the route we want to test
	 */
	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		RouteBuilder routesToTest = new PatientRoutes();
		return routesToTest;
	}
	
	@Test
	public void testSubmitGetRequest() throws Exception {
		
		Thread.sleep(1000);
		
		// Expected results
		MockEndpoint mock = getMockEndpoint("mock:assert");
		mock.expectedBodiesReceived("Patient GET", "Patient POST");
		
		// Set up notifier to tell us once the matching responses have been received
		NotifyBuilder notify = new NotifyBuilder(context).from("jms:ciao-fhir")
					.whenReceivedSatisfied(mock).create();
		
		// Submit requests
		template.sendBodyAndHeader("jetty:http://0.0.0.0:8080/fhir/Patient", ExchangePattern.InOut, "", Exchange.HTTP_METHOD, "GET");
		template.sendBodyAndHeader("jetty:http://0.0.0.0:8080/fhir/Patient", ExchangePattern.InOut, "", Exchange.HTTP_METHOD, "POST");
		
		boolean matches = notify.matches(5, TimeUnit.SECONDS);
		assertTrue(matches);
	}
}
