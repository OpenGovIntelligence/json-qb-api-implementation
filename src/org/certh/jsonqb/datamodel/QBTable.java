package org.certh.jsonqb.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QBTable {
	
	List<Number> measures=new ArrayList<Number>();
	Map<String,List<String>> dimVals=new HashMap<String,List<String>>();
	
	
	public Map<String, List<String>> getDimVals() {
		return dimVals;
	}
	public void setDimVals(Map<String, List<String>> dimVals) {
		this.dimVals = dimVals;
	}
	public List<Number> getMeasures() {
		return measures;
	}
	public void setMeasures(List<Number> measures) {
		this.measures = measures;
	}

	

}
