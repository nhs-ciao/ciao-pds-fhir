package uk.nhs.itk.ciao.fhir.resources;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

import org.hl7.fhir.instance.client.ResourceFormat;
import static org.hl7.fhir.instance.client.ResourceFormat.RESOURCE_JSON;
import static org.hl7.fhir.instance.client.ResourceFormat.RESOURCE_XML;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.Conformance;
import org.hl7.fhir.instance.model.Conformance.ConformanceRestComponent;
import org.hl7.fhir.instance.model.Conformance.ConformanceRestResourceComponent;
import org.hl7.fhir.instance.model.Conformance.ConformanceRestResourceOperationComponent;
import org.hl7.fhir.instance.model.Conformance.ConformanceSoftwareComponent;
import org.hl7.fhir.instance.model.Conformance.ConformanceStatementStatus;
import org.hl7.fhir.instance.model.Conformance.RestfulConformanceMode;
import org.hl7.fhir.instance.model.Conformance.TypeRestfulOperation;
import org.hl7.fhir.instance.model.DateAndTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.interoperability.payloads.util.PropertyReader;

public class ConformanceResource {
	
	// Create the resource at startup and store it as a String - no point generating it every time
	private static String resourceXML = makeConformanceResource(RESOURCE_XML);
	private static String resourceJSON = makeConformanceResource(RESOURCE_JSON);
	
	private static Logger logger = LoggerFactory.getLogger(ConformanceResource.class);
	
	public static String getConformanceResource(ResourceFormat format) {
		if (format == RESOURCE_XML)
			return resourceXML;
		else
			return resourceJSON;
	}
	
	private static String makeConformanceResource(ResourceFormat format) {
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
		
		resource.addFormatSimple("xml");
		resource.addFormatSimple("json");
		
		// Specify that we are a server, and that we only support the read operation for the patient resource
		ConformanceRestComponent rest = resource.addRest();
		rest.setModeSimple(RestfulConformanceMode.server);
		ConformanceRestResourceComponent patient = rest.addResource();
		patient.setTypeSimple("Patient");
		ConformanceRestResourceOperationComponent operation = patient.addOperation();
		operation.setCodeSimple(TypeRestfulOperation.read);
		
		return ResourceSerialiser.serialise(resource, format);
	}
	
}
