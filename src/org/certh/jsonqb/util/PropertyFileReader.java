package org.certh.jsonqb.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileReader {

	InputStream inputStream;
	
	public String getSPARQLservice() throws IOException {
		
		String SPARQLservice="";
		
		try {
			Properties prop = new Properties();
			String propFileName = "config.prop";
 
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			} 
			
			SPARQLservice= prop.getProperty("SPARQLservice");
					
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return SPARQLservice;
	}
	
}
