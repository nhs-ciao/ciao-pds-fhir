package uk.nhs.ciao.fhir;

import uk.nhs.ciao.camel.CamelApplication;
import uk.nhs.ciao.camel.CamelApplicationRunner;
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
	/**
	 * This is the main class for running CIAO as a simple java process (e.g. within docker)
	 * @param args Command line arguments (e.g. ETCD URL)
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final CamelApplication application = new RunCIP(args);
		CamelApplicationRunner.runApplication(application);
	}
	
	public RunCIP(final String[] args) throws CIAOConfigurationException {
		super(CONFIG_FILE, args);
	}
}
