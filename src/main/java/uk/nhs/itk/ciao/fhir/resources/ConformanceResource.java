package uk.nhs.itk.ciao.fhir.resources;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.BooleanType;
import org.hl7.fhir.instance.model.Conformance;
import org.hl7.fhir.instance.model.Conformance.ConformanceSoftwareComponent;
import org.hl7.fhir.instance.model.Conformance.ConformanceStatementStatus;
import org.hl7.fhir.instance.model.DateAndTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.interoperability.payloads.util.PropertyReader;

public class ConformanceResource {
	
	// Create the resource at startup and store it as a String - no point generating it every time
	private static String resourceValue = makeConformanceResource();
	private static Logger logger = LoggerFactory.getLogger(ConformanceResource.class);
	
	public static String getConformanceResource() {
		return resourceValue;
	}
	
	private static String makeConformanceResource() {
		
		try {
			Conformance resource = new Conformance();
			
			resource.setPublisherSimple("Health and Social Care Information Centre");
			resource.setStatusSimple(ConformanceStatementStatus.draft);
			resource.setDateSimple(new DateAndTime(new Date()));
			
			ConformanceSoftwareComponent software = new ConformanceSoftwareComponent();
			software.setNameSimple("nhs-ciao");
			software.setVersionSimple(PropertyReader.getProperty("cip.version"));
			resource.setSoftware(software);
			
			resource.setFhirVersionSimple("0.4.0");
			resource.setAcceptUnknownSimple(false);
			
			XmlComposer composer = new XmlComposer();
			OutputStream out = new ByteArrayOutputStream();
			composer.compose(out, resource, true);
			return out.toString();
			
		} catch (Exception e) {
			logger.error("Unable to build conformance resource", e);
		}
		return null;
	}
	
}
