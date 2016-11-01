package org.certh.jsonqb.datamodel;

import java.util.Map;
import java.util.TreeMap;

public class Observation {
	
	private Map<String,String> observationValues=new TreeMap<String,String>();

	public Map<String, String> getObservationValues() {
		return observationValues;
	}

	public void setObservationValues(Map<String, String> observationValues) {
		this.observationValues = observationValues;
	}
	
	public void putObservationValue(String key, String value){
		observationValues.put(key, value);
	}

}
