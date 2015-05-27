package uk.nhs.ciao.fhir;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import uk.nhs.ciao.camel.CamelApplication;
import uk.nhs.ciao.exceptions.CIAOConfigurationException;

import static uk.nhs.ciao.util.GlobalConstants.*;

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
public class RunCIP extends CamelApplication {
	private static final Logger logger = LoggerFactory.getLogger(RunCIP.class);
	
	/**
	 * This is the main class for running CIAO as a simple java process (e.g. within docker)
	 * @param args Command line arguments (e.g. ETCD URL)
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new RunCIP(args).run();
	}
	
	public RunCIP(final String[] args) throws CIAOConfigurationException {
		super(CONFIG_FILE, args);
	}
	
	@Override
	protected StaticApplicationContext createParentApplicationContext()
			throws CIAOConfigurationException {
		final StaticApplicationContext context = super.createParentApplicationContext();
		
		final String sslConfig;
		if (Boolean.parseBoolean(getCIAOConfig().getConfigValue("TLS_ENABLED"))) {
			logger.info("TLS enabled");
			sslConfig = "tls";
		} else {
			logger.info("TLS NOT enabled");
			sslConfig = "vanilla";
		}
		
		final Properties additionalProperties = new Properties();
		additionalProperties.setProperty("sslConfig", sslConfig);
		
		context.getEnvironment().getPropertySources().addFirst(new PropertiesPropertySource(
				"additionalProperties", additionalProperties));
		
		return context;
	}
}
