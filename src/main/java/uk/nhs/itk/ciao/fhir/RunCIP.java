package uk.nhs.itk.ciao.fhir;

import javax.naming.NamingException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jndi.JndiContext;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;

import uk.nhs.itk.ciao.util.PropertyReader;

/**
 * To test the CIP pointing at TKW use one of the following:
 * 
 * Simple trace, single match:
 * curl http://localhost:8080/fhir/Patient?family=PURVES
 * curl http://localhost:8080/fhir/Patient?family=LEWINGTON
 * curl http://localhost:8080/fhir/Patient?family=STAMBUKDELIFSCHITZ
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
 * @author Adam Hatherly
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static CamelContext createCamelContext() throws Exception {	
		
		JndiContext jndi = new JndiContext();
		populateCamelRegistry(jndi);
		
		// Initialise the TLS configuration for the Spine connection
		initialiseTLS(jndi);
		
		CamelContext context = new DefaultCamelContext(jndi);

		// Now, configure our jms component to use seda, which is a simple in-memory queue. In the live
		// component, this would be configured to use activemq
		context.addComponent("jms", context.getComponent("seda"));
		return context;
	}
	
	/**
	 * Register our custom beans using JNDI (in an OSGi deployment this is configured in beans.xml) 
	 * @param jndi
	 * @throws NamingException
	 */
	private static void populateCamelRegistry(JndiContext jndi) throws NamingException {
		jndi.bind("patientGetProcessor", new PatientGetProcessor());
		jndi.bind("patientPostProcessor", new PatientPostProcessor());
		jndi.bind("patientResponseProcessor", new PatientResponseProcessor());
		jndi.bind("conformanceProcessor", new ConformanceProcessor());
	}
	
	/**
	 * Initialise the Java Secure Socket Extensions for use in Camel Components
	 * @param jndi
	 * @throws NamingException 
	 */
	private static void initialiseTLS(JndiContext jndi) throws NamingException {
		
		if (PropertyReader.getProperty("TLS_ENABLED").equals("true")) {
		
			// Key Store
			KeyStoreParameters ksp = new KeyStoreParameters();
			ksp.setResource(PropertyReader.getProperty("KEY_STORE"));
			ksp.setPassword(PropertyReader.getProperty("KEY_STORE_PASSWORD"));
	
			KeyManagersParameters kmp = new KeyManagersParameters();
			kmp.setKeyStore(ksp);
			kmp.setKeyPassword(PropertyReader.getProperty("KEY_PASSWORD"));
	
			//FilterParameters filter = new FilterParameters();
			//filter.getInclude().add(".*");
			//SSLContextClientParameters sccp = new SSLContextClientParameters();
			//sccp.setCipherSuitesFilter(filter);
			
			// Trust Store
			KeyStoreParameters trustStore = new KeyStoreParameters();
			ksp.setResource(PropertyReader.getProperty("TRUST_STORE"));
			ksp.setPassword(PropertyReader.getProperty("TRUST_STORE_PASSWORD"));
			
			TrustManagersParameters tmgr = new TrustManagersParameters();
			tmgr.setKeyStore(trustStore);
			
	
			SSLContextParameters scp = new SSLContextParameters();
			//scp.setClientParameters(sccp);
			scp.setKeyManagers(kmp);
			scp.setTrustManagers(tmgr);
			
			jndi.bind("spineSSLContextParameters", scp);
	
			//SSLContext context = scp.createSSLContext();
			//SSLEngine engine = scp.createSSLEngine();
		} else {
			// Bind an empty SSLContext
			jndi.bind("spineSSLContextParameters", new SSLContextParameters());
		}
	}
}
