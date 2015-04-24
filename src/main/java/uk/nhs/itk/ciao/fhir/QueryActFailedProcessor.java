package uk.nhs.itk.ciao.fhir;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryActFailedProcessor implements Processor {
	private static Logger logger = LoggerFactory.getLogger(QueryActFailedProcessor.class);

	public void process(Exchange exchange) throws Exception {
		Message out = exchange.getOut();
		Message in = exchange.getIn();
		out.setBody("QueryActFailed\n\n" + in.toString());
	}
}
