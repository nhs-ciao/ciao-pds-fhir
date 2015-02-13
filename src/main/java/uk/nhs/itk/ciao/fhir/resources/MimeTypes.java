package uk.nhs.itk.ciao.fhir.resources;

import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.instance.client.ResourceFormat;

/**
 * Utility class to handle mime types. I suspect the FHIR reference implementation may have something similar, but this
 * will do for now.
 * @author Adam Hatherly
 */
public class MimeTypes {
	private static final List<String> xmlTypes = Arrays.asList("xml", "text/xml", "application/xml", "application/xml+fhir");
	private static final List<String> jsonTypes = Arrays.asList("json", "application/json", "application/json+fhir");
	
	/**
	 * Takes a mime type string and returns a FHIR resource format enum entry
	 * @param mimeType String taken from request
	 * @return ResourceFormat enum entry
	 */
	public static ResourceFormat getFormatFromMimeType(String mimeType) {
		if (jsonTypes.contains(mimeType)) {
			return ResourceFormat.RESOURCE_JSON;
		} else if (xmlTypes.contains(mimeType)) {
			return ResourceFormat.RESOURCE_XML;
		} else {
			return null;
		}
	}
}
