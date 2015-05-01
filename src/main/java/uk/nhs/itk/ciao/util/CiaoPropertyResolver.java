package uk.nhs.itk.ciao.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.component.properties.PropertiesResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.itk.ciao.configuration.CIAOConfig;
import uk.nhs.itk.ciao.fhir.RunCIP;

public class CiaoPropertyResolver implements PropertiesResolver {
	
	private static Logger logger = LoggerFactory.getLogger(CiaoPropertyResolver.class);

	public Properties resolveProperties(CamelContext arg0, boolean arg1,
			String... arg2) throws Exception {
		
		
		
		
		return null;
	}
	
	public static void createPropertiesComponent(String defaultConfigFileName, String[] args, CamelContext context) throws Exception {
		Properties defaultConfig = loadDefaultConfig(defaultConfigFileName);
		String version = defaultConfig.get("cip.version").toString();
		String cipName = defaultConfig.get("cip.name").toString();
		CIAOConfig cipConfig = new CIAOConfig(args, cipName, version, defaultConfig);
		
		PropertiesComponent pc = new PropertiesComponent();
		pc.setLocation("");
		context.addComponent("properties", pc);
	}
	
	private static Properties loadDefaultConfig(String defaultConfigFileName) {
		InputStream in = null;
		Properties defaultProperties = new Properties();
        try {
        	in = RunCIP.class.getClassLoader().getResourceAsStream(defaultConfigFileName);
            if (in != null) {
            	defaultProperties.load(in);
            	in.close();
            }
        } catch (Exception ex) {
       		logger.error("Default config not found: " + defaultConfigFileName, ex);
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
}
