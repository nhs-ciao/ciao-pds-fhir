package uk.nhs.itk.ciao.fhir;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPErrorProcessor implements Processor {
	private static Logger logger = LoggerFactory.getLogger(HTTPErrorProcessor.class);

	public void process(Exchange exchange) throws Exception {
		Message out = exchange.getOut();
		Message in = exchange.getIn();
		out.setBody("HTTP Error\n\n" + in.toString());
	}
}
