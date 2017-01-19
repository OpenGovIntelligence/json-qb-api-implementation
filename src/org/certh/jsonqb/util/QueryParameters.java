package org.certh.jsonqb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public class QueryParameters {

	private String datasetURI;
	private List<String> rowDimensionURIs;
	private List<String> columnDimensionURIs;
	private String measureURI;
	private Map<String, String> fixedValues = new HashMap<>();
	//private List<String> dimensions=new ArrayList<>();

	public QueryParameters(MultivaluedMap<String, String> parameters) {
		datasetURI = parameters.getFirst("dataset");
		rowDimensionURIs = parameters.get("row[]");
		columnDimensionURIs = parameters.get("col[]");
		measureURI = parameters.getFirst("measure");
		for (String param : parameters.keySet()) {
			if (!"dataset".equals(param) && !"col[]".equals(param) && !"row[]".equals(param) && !"measure".equals(param)) {
				fixedValues.put(param, parameters.getFirst(param));
			}
		}
	//	dimensions=parameters.get("dimension");
		
	}

	public String getMeasureURI() {
		return measureURI;
	}

	public String getDatasetURI() {
		return datasetURI;
	}

	public List<String> getRowDimensionURIs() {
		return rowDimensionURIs;
	}

	public List<String> getColumnDimensionURIs() {
		return columnDimensionURIs;
	}

	public Map<String, String> getFixedValues() {
		return fixedValues;
	}
	
	//public List<String> getDimensionURIs() {
	//	return dimensions;
	//}

}
