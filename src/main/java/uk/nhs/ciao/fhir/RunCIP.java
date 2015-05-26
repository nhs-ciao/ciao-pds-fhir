package uk.nhs.ciao.fhir;

import org.apache.camel.CamelContext;
import org.apache.camel.util.jndi.JndiContext;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ciao.properties.CiaoPropertyResolver;
import uk.nhs.ciao.spine.HL7PayloadBuilder;
import uk.nhs.ciao.util.GlobalConstants;

/**
 * To test the CIP pointing at TKW use one of the following:
 * 
 * Simple trace, single match:
 * curl http://localhost:8080/fhir/Patient?family=PURVES
 * curl http://localhost:8080/fhir/Patient?family=LEWINGTON
 * curl http://localhost:8080/fhir/Patient?family=STAMBUKDELIFSCHITZ
 * 
 * Specifying XML format:
 * curl -H "Accept: application/xml+fhir" http://localhost:8080/fhir/Patient?family=PURVES
 * curl http://localhost:8080/fhir/Patient?family=PURVES&_format=xml
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
public class RunCIP extends uk.nhs.ciao.RunCIP implements GlobalConstants {
	
	private static Logger logger = LoggerFactory.getLogger(RunCIP.class);
	
	/**
	 * This is the main class for running CIAO as a simple java process (e.g. within docker)
	 * @param args Command line arguments (e.g. ETCD URL)
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Initialise Camel context
		new RunCIP().initialiseCamel(args);
	}
	
	/**
	 * Initialise the camel context and start Camel
	 * @param args
	 * @throws Exception
	 */
	private void initialiseCamel(String[] args) throws Exception {
		CamelContext context = super.init(args, CONFIG_FILE);
		
		// Point our JMS component at an in-memory queue
		context.addComponent("jms", context.getComponent("seda"));
		
		context.addRoutes(new FHIRRoutes());
		super.startCamel(context);
	}
	
	/**
	 * Register our custom beans using JNDI (in an OSGi deployment this is configured in beans.xml) 
	 * @param jndi
	 * @throws Exception 
	 */
	@Override
	protected void populateCamelRegistry(JndiContext jndi) throws Exception {
		jndi.bind("payloadBuilder", new HL7PayloadBuilder());
		jndi.bind("patientPostProcessor", new PatientPostProcessor());
		jndi.bind("patientResponseProcessor", new PatientResponseProcessor());
		jndi.bind("conformanceProcessor", new ConformanceProcessor());
		
		jndi.bind("QueryActFailedProcessor", new QueryActFailedProcessor());
		jndi.bind("SOAPFaultProcessor", new SOAPFaultProcessor());
		jndi.bind("HTTPErrorProcessor", new HTTPErrorProcessor());
		
		// Initialise the TLS configuration for the Spine connection
		initialiseTLS(jndi);
	}

	/**
	 * Initialise the Java Secure Socket Extensions for use in Camel Components
	 * @param jndi
	 * @throws Exception 
	 */
	private static void initialiseTLS(JndiContext jndi) throws Exception {
		
		if (CiaoPropertyResolver.getConfigValue("TLS_ENABLED").equals("true")) {
		
			// Key Store
			KeyStoreParameters ksp = new KeyStoreParameters();
			ksp.setResource(CiaoPropertyResolver.getConfigValue("KEY_STORE"));
			ksp.setPassword(CiaoPropertyResolver.getConfigValue("KEY_STORE_PW"));
			KeyManagersParameters kmp = new KeyManagersParameters();
			kmp.setKeyStore(ksp);
			kmp.setKeyPassword(CiaoPropertyResolver.getConfigValue("KEY_PASSWORD"));
	
			// Trust Store		
			KeyStoreParameters trustStore = new KeyStoreParameters();
			trustStore.setResource(CiaoPropertyResolver.getConfigValue("TRUST_STORE"));
			trustStore.setPassword(CiaoPropertyResolver.getConfigValue("TRUST_STORE_PW"));
			TrustManagersParameters tmgr = new TrustManagersParameters();
			tmgr.setKeyStore(trustStore);
	
			SSLContextParameters scp = new SSLContextParameters();
			scp.setKeyManagers(kmp);
			scp.setTrustManagers(tmgr);
			
			jndi.bind("spineSSLContextParameters", scp);
			logger.info("TLS enabled");
		} else {
			// Bind an empty SSLContext
			jndi.bind("spineSSLContextParameters", new SSLContextParameters());
			logger.info("TLS NOT enabled");
		}
	}
}
