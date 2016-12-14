package org.certh.jsonqb.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileReader {

	public String getSPARQLservice() throws IOException {
		
		Properties prop = new Properties();
		String propFileName = "config.prop";

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		String sparqlservice = prop.getProperty("SPARQLservice");
		inputStream.close();
		return sparqlservice;
	}

}
