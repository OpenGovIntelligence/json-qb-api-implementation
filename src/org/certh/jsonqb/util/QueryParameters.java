package org.certh.jsonqb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public class QueryParameters {

	private String datasetURI;
	private List<String> rowDimensionURIs;
	private List<String> columnDimensionURIs;
	private List<String> measureURIs;
	private Map<String, String> fixedValues = new HashMap<>();
	private int limit;
	private String mode;


	public QueryParameters(MultivaluedMap<String, String> parameters) {
		datasetURI = parameters.getFirst("dataset");
		rowDimensionURIs = parameters.get("row[]");
		columnDimensionURIs = parameters.get("col[]");
		measureURIs = parameters.get("measure[]");
		try{
			limit=Integer.parseInt(parameters.getFirst("limit"));
		}catch (NumberFormatException e) {
			limit=-1;
		}
		mode=parameters.getFirst("mode");
		if(mode==null||!mode.equals("URI")){
			mode="label";
		}
		for (String param : parameters.keySet()) {
			if (!"dataset".equals(param) && !"col[]".equals(param) && !"row[]".equals(param) && 
					!"measure[]".equals(param)&& !"limit".equals(param) && !"mode".equals(param)) {
				fixedValues.put(param, parameters.getFirst(param));
			}
		}			
	}

	public List<String> getMeasureURIs() {
		return measureURIs;
	}

	public String getDatasetURI() {
		return datasetURI;
	}
	
	public String getMode() {
		return mode;
	}
	
	public int getLimit() {
		return limit;
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
