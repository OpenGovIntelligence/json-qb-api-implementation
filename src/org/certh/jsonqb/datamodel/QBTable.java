package org.certh.jsonqb.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QBTable {
	
	List<Number> measures=new ArrayList<>();
	
	//Map dimension URI with the values of the dimension used at the QBTable
	Map<String,List<LDResource>> dimVals=new HashMap<>();
	
	
	public Map<String, List<LDResource>> getDimVals() {
		return dimVals;
	}
	public void setDimVals(Map<String, List<LDResource>> dimVals) {
		this.dimVals = dimVals;
	}
	public List<Number> getMeasures() {
		return measures;
	}
	public void setMeasures(List<Number> measures) {
		this.measures = measures;
	}

	

}
