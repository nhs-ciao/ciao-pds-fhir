package uk.nhs.ciao.fhir.resources;

import static org.hl7.fhir.instance.client.ResourceFormat.RESOURCE_XML;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.hl7.fhir.instance.client.ResourceFormat;
import org.hl7.fhir.instance.formats.JsonComposer;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceSerialiser {
	private static Logger logger = LoggerFactory.getLogger(ResourceSerialiser.class);
	
	public static String serialise(Resource resource, ResourceFormat type) {
		try {
			if (type == RESOURCE_XML) {
				XmlComposer composer = new XmlComposer();
				OutputStream out = new ByteArrayOutputStream();
				composer.compose(out, resource, true);
				return out.toString();
			} else {
				JsonComposer composer = new JsonComposer();
				OutputStream out = new ByteArrayOutputStream();
				composer.compose(out, resource, true);
				return out.toString();
			}
		} catch (Exception e) {
			logger.error("Unable to serialise FHIR resource", e);
		}
		return null;
	}
	
	public static String serialise(AtomFeed feed, ResourceFormat type) {
		try {
			if (type == RESOURCE_XML) {
				XmlComposer composer = new XmlComposer();
				OutputStream out = new ByteArrayOutputStream();
				composer.compose(out, feed, true);
				return out.toString();
			} else {
				JsonComposer composer = new JsonComposer();
				OutputStream out = new ByteArrayOutputStream();
				composer.compose(out, feed, true);
				return out.toString();
			}
		} catch (Exception e) {
			logger.error("Unable to serialise FHIR Atom Feed", e);
		}
		return null;
	}
}
