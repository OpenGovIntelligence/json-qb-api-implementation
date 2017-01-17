package org.certh.jsonqb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public class QueryParameters {

	private String datasetURI;
	private String rowDimensionURI;
	private String columnDimensionURI;
	private String measureURI;
	private Map<String, String> fixedValues = new HashMap<>();

	public QueryParameters(MultivaluedMap<String, String> parameters) {
		datasetURI = parameters.getFirst("dataset");
		rowDimensionURI = parameters.getFirst("row");
		columnDimensionURI = parameters.getFirst("col");
		measureURI = parameters.getFirst("measure");
		for (String param : parameters.keySet()) {
			if (!"dataset".equals(param) && !"col".equals(param) && !"row".equals(param) && !"measure".equals(param)) {
				fixedValues.put(param, parameters.getFirst(param));
			}
		}
	}

	public String getMeasureURI() {
		return measureURI;
	}

	public String getDatasetURI() {
		return datasetURI;
	}

	public String getRowDimensionURI() {
		return rowDimensionURI;
	}

	public String getColumnDimensionURI() {
		return columnDimensionURI;
	}

	public Map<String, String> getFixedValues() {
		return fixedValues;
	}

	

}
