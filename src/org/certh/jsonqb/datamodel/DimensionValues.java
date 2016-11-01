package org.certh.jsonqb.datamodel;

import java.util.List;

public class DimensionValues {

	List<LDResource> values;
	LDResource dimension;
	
	public List<LDResource> getValues() {
		return values;
	}
	public void setValues(List<LDResource> values) {
		this.values = values;
	}
	public LDResource getDimension() {
		return dimension;
	}
	public void setDimension(LDResource dimension) {
		this.dimension = dimension;
	}
}
