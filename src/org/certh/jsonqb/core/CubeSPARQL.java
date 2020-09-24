//Creator Dimitris Zeginis
package org.certh.jsonqb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.certh.jsonqb.datamodel.DataCube;
import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.QBTable;
import org.certh.jsonqb.datamodel.QBTableJsonStat;
import org.certh.jsonqb.util.ObservationList;
import org.certh.jsonqb.util.SPARQLUtil;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class CubeSPARQL {
	
	private CubeSPARQL() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}
	
	
	public static LDResource getCubeGraph(String dataCubeURI, String sparqlService) {
		String triple="<"+dataCubeURI+"> rdf:type qb:DataSet.";
		return SPARQLUtil.getTripleGraph(triple, sparqlService);
		
	}
	
	public static LDResource getAggregationSetOfCube(String dataCubeURI, String sparqlService) {
		String getCubeAggregationSetQuery = SPARQLconstants.PREFIX
				+ "select distinct ?res  where {" 
				+ "<"+dataCubeURI+"> opencube:aggregationSet ?res.}";

		TupleQueryResult res = QueryExecutor.executeSelect(getCubeAggregationSetQuery, sparqlService);

		List<LDResource> set = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);

		//Only one aggregation set exists
		if(set.isEmpty()){
			return null;
		}else{
			return set.get(0);
		}
	}
	
	// Get all the dimensions of a data cube
	// Input: The cubeURI, SPARQLservice
	public static List<LDResource> getDataCubeDimensions(String dataCubeURI, String sparqlService) {

		String getCubeDimensionsQuery = SPARQLconstants.PREFIX
				+ "select distinct ?res  where {" 
				+ "<"+ dataCubeURI + "> qb:structure ?dsd." 
				+ "?dsd qb:component  ?cs." + "?cs qb:dimension ?res.}";

		TupleQueryResult res = QueryExecutor.executeSelect(getCubeDimensionsQuery, sparqlService);

		List<LDResource> cumeDimensions = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		Collections.sort(cumeDimensions);
		return cumeDimensions;
	}

	// Get all the measure of a data cube
	// Input: The cubeURI, SPARQL service
	public static List<LDResource> getDataCubeMeasures(String dataCubeURI, String sparqlService) {

		String getCubeMeasureQuery = SPARQLconstants.PREFIX
				+ "select  distinct ?res where {" 
				+ "<"+ dataCubeURI + "> qb:structure ?dsd." 
				+ "?dsd qb:component ?cs." 
				+ "?cs qb:measure ?res.}";

		TupleQueryResult res = QueryExecutor.executeSelect(getCubeMeasureQuery, sparqlService);
		List<LDResource> cumeMeasures = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		Collections.sort(cumeMeasures);
		return cumeMeasures;
	}

	public static List<LDResource> getDataCubeAttributes(String dataCubeURI, String sparqlService) {

		String getCubeAttributesQuery = SPARQLconstants.PREFIX
				+ "select  distinct ?res  where {" 
				+ "<"+ dataCubeURI + "> qb:structure ?dsd." 
				+ "?dsd qb:component ?comp." 
				+ "?comp qb:attribute ?res. }";

		TupleQueryResult res = QueryExecutor.executeSelect(getCubeAttributesQuery, sparqlService);
		List<LDResource> cubeAttributes = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		Collections.sort(cubeAttributes);
		return cubeAttributes;
	}

	
	public static List<LDResource> getDimensionAttributeValues(String dimensionURI, String cubeURI,
			String sparqlService) {

			String getDimensionValuesQuery = SPARQLconstants.PREFIX
				+ "select  distinct ?res  ?label where {"
				+ "?observation qb:dataSet <" + cubeURI + ">."
				+ "?observation <" + dimensionURI + "> ?res."
				+ "OPTIONAL{?res  skos:prefLabel|rdfs:label ?label.}"
				+ "}";			

		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionValuesQuery, sparqlService);
		List<LDResource> dimensionValuesWithLables=SPARQLresultTransformer.toLDResourceListWithLabels(res,sparqlService);
		
		Collections.sort(dimensionValuesWithLables, LDResource.labelComparator);
		return dimensionValuesWithLables;
	}
	
	public static LDResource getDimensionCodesUsedCodelist(String dimensionURI, String cubeURI,
			String sparqlService) {

			String getDimensionCodesUsedCodelistQuery = SPARQLconstants.PREFIX
				+ "select  ?codesUsedList where {"
				+ "<"+cubeURI+"> qb:structure/qb:component ?comp."
				+ "?comp qb:dimension <"+dimensionURI+">."
				+ "?comp <"+SPARQLconstants.CODESUSED_PREDICATE+"> ?codesUsedList."
				+ "}";			

		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionCodesUsedCodelistQuery, sparqlService);
		LDResource ldr =null;
		if(res.hasNext()) {
			BindingSet bindingSet = res.next();
			ldr = new LDResource(bindingSet.getValue("codesUsedList").stringValue());	
		}		
		
		return ldr;
	}

	// Ordered List of ALL Dimension Levels
	public static List<LDResource> getDimensionLevels(String dimensionURI, String sparqlService) {

		String getDimensionLevelsOrderedQuery = SPARQLconstants.PREFIX
				+ "select  distinct ?res ?position  where {" 
				+ "<" + dimensionURI + ">  qb:codeList ?codelist."
				+ "?codelist xkos:levels ?levellist." 
				+ "?levellist rdf:rest*/rdf:first ?res."
				+ "?res xkos:depth ?position.}group by ?res ?label";

		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionLevelsOrderedQuery, sparqlService);
		return SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		
	}

	// ASK if a cube contains data at a specific dimension level
	// (check if exists an observation with this value)
	public static boolean cubeContainsDimensionLevel(String cubeURI, String dimensionLevel, String sparqlService) {
		String askDimensionLevelInDataCubeQuery = SPARQLconstants.PREFIX
			+ "SELECT ?exists where{"
			+ "?obs qb:dataSet <" + cubeURI+ ">."
			+ "?obs ?dim ?value."
			+ "BIND(EXISTS{ <" + dimensionLevel + "> skos:member ?value.})}";
					
		TupleQueryResult res = QueryExecutor.executeSelect(askDimensionLevelInDataCubeQuery, sparqlService);
		BindingSet bindingSet = res.next();
		Literal existsStr=(Literal) bindingSet.getValue("exists");
		return existsStr.booleanValue();			
	}

	// Ordered List of Cube Dimension Levels
	public static List<LDResource> getCubeDimensionLevels(String dimensionURI, String cubeURI, String sparqlService) {
		List<LDResource> allDimensionLevelsOrdered = getDimensionLevels(dimensionURI, sparqlService);
		List<LDResource> cubeDimensionLevels = new ArrayList<>();
		for (LDResource dimLevel : allDimensionLevelsOrdered) {
			if (cubeContainsDimensionLevel(cubeURI, dimLevel.getURI(), sparqlService)) {
				cubeDimensionLevels.add(dimLevel);
			}
		}
		return cubeDimensionLevels;
	}
	
	
	
	
	
	// Get labels of resource 
	public static DataCube getCubeMetaData(String cubeURI, String sparqlService) {
		String getCubeMetadataQuery = SPARQLconstants.PREFIX
				+ "select ?label ?title  ?description ?comment ?issued ?modified"
				+ "?subject ?publisher ?license where {" 
				+ "<" + cubeURI + ">  rdf:type qb:DataSet. "
				+ "OPTIONAL{<" + cubeURI + "> skos:prefLabel|rdfs:label ?label}"
				+ "OPTIONAL{<" + cubeURI + "> dct:title ?title}"
				+ "OPTIONAL{<" + cubeURI + "> dct:description ?description}"
				+ "OPTIONAL{<" + cubeURI + "> rdfs:comment ?comment}"
				+ "OPTIONAL{<" + cubeURI + "> dct:issued ?issued}"
				+ "OPTIONAL{<" + cubeURI + "> dct:modified ?modified}"
				+ "OPTIONAL{<" + cubeURI + "> dct:subject ?subject}"
				+ "OPTIONAL{<" + cubeURI + "> dct:publisher ?publisher}"
				+ "OPTIONAL{<" + cubeURI + "> dct:license ?license}"
				+ "}";

		TupleQueryResult res = QueryExecutor.executeSelect(getCubeMetadataQuery, sparqlService);
		return SPARQLresultTransformer.toDataCube(cubeURI,res);		
	}	
	

	public static ObservationList getSlice(List<String> visualDims, Map<String, String> fixedDims,
			List<String> selectedMeasures, String cubeURI, String mode,
			int limit, String sparqlService) {

		Map<String, String> mapVariableNameURI = new HashMap<>();
		mapVariableNameURI.put("obs", "id");

		StringBuilder getSliceQuery=new StringBuilder(SPARQLconstants.PREFIX);
		getSliceQuery.append("Select distinct ?obs ");

		List<LDResource> dimensions=CubeSPARQL.getDataCubeDimensions(cubeURI, sparqlService);
		
		int i = 1;
		// Add dimensions ?dim to SPARQL query
		for (String vDim : visualDims) {
			getSliceQuery.append("?val" + i+" ");
			
			if("label".equals(mode)){
				getSliceQuery.append("?dim"+i+" ");
			}
			
			//Find the LDResource dimension
			for(LDResource dim:dimensions){
				if(dim.getURI().equals(vDim)){
					//if mode = URI use URIs as names of the dimensions
					if("URI".equals(mode)){
						mapVariableNameURI.put("val" + i, dim.getURI());
					//else use labels as names of the dimensions
					}else{
						mapVariableNameURI.put("val" + i, dim.getURIorLabel());
					}
				}
			}			
			i++;
		}

		i = 1;
		
		List<LDResource> measures=CubeSPARQL.getDataCubeMeasures(cubeURI, sparqlService);
		// Add measures ?meas to SPARQL query
		for (String meas : selectedMeasures) {
			getSliceQuery.append("?measure" + i + " ");
			
			//Find the LDResource dimension
			for(LDResource measLDR:measures){
				if(measLDR.getURI().equals(meas)){
					//if mode = URI use URIs as names of the dimensions
					if("URI".equals(mode)){
						mapVariableNameURI.put("measure" + i, measLDR.getURI());
					//else use labels as names of the dimensions
					}else{
						mapVariableNameURI.put("measure" + i, measLDR.getURIorLabel());
					}
				}
			}
						
			i++;
		}

		// Select observations of a specific cube (cubeURI)
		getSliceQuery.append(" where { ?obs qb:dataSet <" + cubeURI + ">.");

		
		i = 1;
			
		// Add fixed dimensions to where clause
		for (Entry<String, String> fDimEntry : fixedDims.entrySet()) {
			getSliceQuery.append("?obs <" + fDimEntry.getKey() + "> ");
			if (fDimEntry.getValue().contains("http")) {
				getSliceQuery.append("<" + fDimEntry.getValue() + ">.");
			} else {
				getSliceQuery.append("?value" + i + "." + "FILTER(STR(?value" + i + ")='" + fDimEntry.getValue() + "')");
			}
			i++;
		}		

		i = 1;
		// Add free dimensions to where clause
		for (String vDim : visualDims) {
			if("URI".equals(mode)){
				getSliceQuery.append("OPTIONAL{?obs <" + vDim + "> " + "?val" + i + "}");
			}else{
				getSliceQuery.append("OPTIONAL{?obs <" + vDim + "> " + "?dim" + i + "} "
						+ "OPTIONAL{?dim"+ i +" skos:prefLabel|rdfs:label ?val"+i+". }");
			}
			
			i++;
		}

		i = 1;
		for (String meas : selectedMeasures) {
			getSliceQuery.append("OPTIONAL{?obs  <" + meas + "> ?measure" + i + ".}");
			i++;
		}

		getSliceQuery.append("} ");
		
		//getSliceQuery.append("ORDER BY ");
		 
		//for (int j=1;j<=visualDims.size();j++) {
		//	getSliceQuery.append("?val" + j + " ");			
		//}
		
		if(limit!=-1){
			getSliceQuery.append("LIMIT "+limit);
		}
				
		TupleQueryResult res = QueryExecutor.executeSelect(getSliceQuery.toString(), sparqlService);
		return SPARQLresultTransformer.toObservationList(res, mapVariableNameURI);
	}
	
	public static QBTableJsonStat getJsonStatTable(List<String> visualDims, Map<String, String> fixedDims,
			List<String> selectedMeasures, String cubeURI, String sparqlService) {
		
		StringBuilder getTableQuery=new StringBuilder(SPARQLconstants.PREFIX);
		
		getTableQuery.append("Select distinct ");
		
		
		//Add visual dims to SPARQL query
		for (int j=1;j<=visualDims.size();j++) {
			getTableQuery.append("?dim" + j + " ");			
		}		

	    // Add measures ?meas to SPARQL query
		for (int j=1;j<=selectedMeasures.size();j++) {
			getTableQuery.append("?measure" + j + " ");			
		}

		// Select observations of a specific cube (cubeURI)
		getTableQuery.append(" where {" + "?obs qb:dataSet <" + cubeURI + ">.");

		// Add fixed dimensions to where clause
		int i = 1;
		
		for (Map.Entry<String, String> entry : fixedDims.entrySet()) {
			getTableQuery.append("?obs <" + entry.getKey() + "> ");
			if (entry.getValue().contains("http")) {
				getTableQuery.append("<" + entry.getValue() + ">.");
			} else {
				getTableQuery.append("?value" + i + "." + "FILTER(STR(?value" + i + ")='" + entry.getValue() + "')");
			}
			i++;
		}		
		

		i = 1;
		
		// Add free dimensions to where clause
		for (String vDim : visualDims) {
			getTableQuery.append("?obs <" + vDim + "> " + "?dim" + i + ". ");
			i++;
		}

		i = 1;
		for (String meas : selectedMeasures) {
			getTableQuery.append("?obs  <" + meas + "> ?measure" + i + ".");
			i++;
		}

		getTableQuery.append("}");
		
		TupleQueryResult res = QueryExecutor.executeSelect(getTableQuery.toString(), sparqlService);
		return SPARQLresultTransformer.toQBJsonStatTable(res, selectedMeasures, visualDims,sparqlService);	

	}
	
	public static QBTable getTable(List<String> rowDimensions,List<String> colDimensions,
			Map<String, String> fixedDims, List<String> selectedMeasures,
			String cubeURI, String sparqlService) {
		
		StringBuilder getTableQuery=new StringBuilder(SPARQLconstants.PREFIX);
		
		getTableQuery.append("Select distinct ");
		
		//add row dims to SPARQL query
		for (int j=1;j<=rowDimensions.size();j++) {
				getTableQuery.append("?row" + j + " ");			
		}	
		
		
		//add col dims to SPARQL query
		if(colDimensions!=null){
			for (int j=1;j<=colDimensions.size();j++) {
				getTableQuery.append("?col" + j + " ");			
			}
		}

	    // Add measures ?meas to SPARQL query
		for (int j=1;j<=selectedMeasures.size();j++) {
			getTableQuery.append("?measure" + j + " ");			
		}

		// Select observations of a specific cube (cubeURI)
		getTableQuery.append(" where {" + "?obs qb:dataSet <" + cubeURI + ">.");

		// Add fixed dimensions to where clause
		int i = 1;
		
		for (Map.Entry<String, String> entry : fixedDims.entrySet()) {
			getTableQuery.append("?obs <" + entry.getKey() + "> ");
			if (entry.getValue().contains("http")) {
				getTableQuery.append("<" + entry.getValue() + ">.");
			} else {
				getTableQuery.append("?value" + i + "." + "FILTER(STR(?value" + i + ")='" + entry.getValue() + "')");
			}
			i++;
		}		
		

		//i = 1;
		
		// Add free dimensions to where clause
		//for (String vDim : visualDims) {
		//	getTableQuery.append("?obs <" + vDim + "> " + "?dim" + i + ". ");
		//	i++;
		//}
		
		//add row dims to SPARQL query
		for (int j=0;j<rowDimensions.size();j++) {
			getTableQuery.append("?obs <" + rowDimensions.get(j) + "> " + "?row" + (j+1) + ". ");			
		}	
						
		//add visual dims to SPARQL query
		if(colDimensions!=null){
			for (int j=0;j<colDimensions.size();j++) {
				getTableQuery.append("?obs <" + colDimensions.get(j) + "> " + "?col" + (j+1) + ". ");			
			}
		}
		

		i = 1;
		for (String meas : selectedMeasures) {
			getTableQuery.append("?obs  <" + meas + "> ?measure" + i + ".");
			i++;
		}

		getTableQuery.append("}");
		
		
		
		TupleQueryResult res = QueryExecutor.executeSelect(getTableQuery.toString(), sparqlService);
		return SPARQLresultTransformer.toQBTable(rowDimensions,colDimensions,fixedDims,selectedMeasures,res, sparqlService);	

		
		
	}
}
