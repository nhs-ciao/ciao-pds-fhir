package uk.nhs.itk.ciao.fhir;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.NamingException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.http.SSLContextParametersSecureProtocolSocketFactory;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jndi.JndiContext;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.itk.ciao.configuration.CIAOConfig;
import uk.nhs.itk.ciao.util.GlobalConstants;

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
public class RunCIP implements GlobalConstants {
	
	private static Logger logger = LoggerFactory.getLogger(RunCIP.class);
	
	public static void main(String[] args) {
		try {
			// Initialise CIP config
			Properties defaultConfig = loadDefaultConfig();
			String version = defaultConfig.get("cip.version").toString();
			String cipName = defaultConfig.get("cip.name").toString();
			CIAOConfig cipConfig = new CIAOConfig(args, cipName, version, defaultConfig);
			// Start camel
			CamelContext context = createCamelContext(cipConfig);
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
	
	private static Properties loadDefaultConfig() {
		InputStream in = null;
		Properties defaultProperties = new Properties();
        try {
        	in = RunCIP.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (in != null) {
            	defaultProperties.load(in);
            	in.close();
            }
        } catch (Exception ex) {
       		logger.error("Default config not found: " + CONFIG_FILE, ex);
       		return null;
        } finally {
            try {
                if (in != null) {
                	in.close();
                }
            } catch (IOException ex) {
            }
        }
        return defaultProperties;
	}
	
	private static CamelContext createCamelContext(CIAOConfig cipConfig) throws Exception {	
		
		JndiContext jndi = new JndiContext();
		
		// Store our CIP config
		jndi.bind("cipConfig", cipConfig);
		
		// Add bean mappings
		populateCamelRegistry(jndi);
		
		// Initialise the TLS configuration for the Spine connection
		initialiseTLS(jndi, cipConfig);
		
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
		
		jndi.bind("QueryActFailedProcessor", new QueryActFailedProcessor());
		jndi.bind("SOAPFaultProcessor", new SOAPFaultProcessor());
		jndi.bind("HTTPErrorProcessor", new HTTPErrorProcessor());
	}
	
	/**
	 * Initialise the Java Secure Socket Extensions for use in Camel Components
	 * @param jndi
	 * @throws Exception 
	 */
	private static void initialiseTLS(JndiContext jndi, CIAOConfig cipConfig) throws Exception {
		
		if (cipConfig.getConfigValue("TLS_ENABLED").equals("true")) {
		
			// Key Store
			KeyStoreParameters ksp = new KeyStoreParameters();
			ksp.setResource(cipConfig.getConfigValue("KEY_STORE"));
			ksp.setPassword(cipConfig.getConfigValue("KEY_STORE_PW"));
			KeyManagersParameters kmp = new KeyManagersParameters();
			kmp.setKeyStore(ksp);
			kmp.setKeyPassword(cipConfig.getConfigValue("KEY_PASSWORD"));
	
			// Trust Store		
			KeyStoreParameters trustStore = new KeyStoreParameters();
			trustStore.setResource(cipConfig.getConfigValue("TRUST_STORE"));
			trustStore.setPassword(cipConfig.getConfigValue("TRUST_STORE_PW"));
			TrustManagersParameters tmgr = new TrustManagersParameters();
			tmgr.setKeyStore(trustStore);
	
			SSLContextParameters scp = new SSLContextParameters();
			scp.setKeyManagers(kmp);
			scp.setTrustManagers(tmgr);
			
			jndi.bind("spineSSLContextParameters", scp);
	
			/*ProtocolSocketFactory factory =
				    new SSLContextParametersSecureProtocolSocketFactory(scp);
				 
			Protocol.registerProtocol("https",
						new Protocol(
				        "https",
				        factory,
				        443));*/
		} else {
			// Bind an empty SSLContext
			jndi.bind("spineSSLContextParameters", new SSLContextParameters());
		}
	}
}
