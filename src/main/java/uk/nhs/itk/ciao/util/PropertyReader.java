/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package uk.nhs.itk.ciao.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class to read configuration from a property file
 * @author Adam Hatherly
 */
public class PropertyReader implements GlobalConstants {
	
    private static Properties defaultProperties;
    private static Logger logger = LoggerFactory.getLogger(PropertyReader.class);
    
    static {
    	initialise();
    }

    /**
     * This loads the config into memory
     */
    private static void initialise() {
    	if (defaultProperties == null) {
	    	defaultProperties = new Properties();
    	}

    	InputStream in = null;
        
        try {
        	in = PropertyReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (in != null) {
            	defaultProperties.load(in);
            	in.close();
            }
        } catch (Exception ex) {
       		logger.info("Config file not found: " + CONFIG_FILE, ex);
        } finally {
            try {
                if (in != null) {
                	in.close();
                }
            } catch (IOException ex) {
            }
        }
    }
    
    /**
     * Retrieve the value of the property with the specified name
     * @param propertyName Name of property to retrieve
     * @return Value of property
     */
    public static String getProperty(String propertyName) {
    	return defaultProperties.getProperty(propertyName);
    }
}
