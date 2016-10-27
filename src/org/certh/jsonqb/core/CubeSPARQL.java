package org.certh.jsonqb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.certh.jsonqb.util.LDResource;
import org.certh.jsonqb.util.QBTable;
import org.certh.jsonqb.util.QueryExecutor;
import org.certh.jsonqb.util.SPARQLresultTransformer;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class CubeSPARQL {

	
	
	// Get all the available cubes of the SPARQLservice
	// Input: The SPARQLservice
	public static List<LDResource> getCubes(String SPARQLservice) {

		String getCubes_query = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" 
				+ "select  distinct ?res ?label where {" 
				+ "?res rdf:type qb:DataSet "
				+ "OPTIONAL{?res skos:prefLabel|rdfs:label ?label.}}";
		TupleQueryResult res = QueryExecutor.executeSelect(getCubes_query, SPARQLservice);

		List<LDResource> cubes = SPARQLresultTransformer.toLDResourceList(res);
		Collections.sort(cubes);
		return cubes;
	}
	
	// Get all the dimensions of a data cube
	// Input: The cubeURI, SPARQLservice
	public static List<LDResource> getDataCubeDimensions(String dataCubeURI, String SPARQLservice) {

		String getCubeDimensions_query = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" 
				+ "select  distinct ?res ?label where {" 
				+ "<"+ dataCubeURI + "> qb:structure ?dsd." 
				+ "?dsd qb:component  ?cs." + "?cs qb:dimension ?res."
				+ "OPTIONAL{?res skos:prefLabel|rdfs:label ?label.}}";

		TupleQueryResult res = QueryExecutor.executeSelect(getCubeDimensions_query, SPARQLservice);

		List<LDResource> cumeDimensions = SPARQLresultTransformer.toLDResourceList(res);
		Collections.sort(cumeDimensions);
		return cumeDimensions;
	}

	// Get all the measure of a data cube
	// Input: The cubeURI, SPARQL service
	public static List<LDResource> getDataCubeMeasures(String dataCubeURI, String SPARQLservice) {

		String getCubeMeasure_query = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" 
				+ "select  distinct ?res ?label where {" 
				+ "<"+ dataCubeURI + "> qb:structure ?dsd." + "?dsd qb:component ?cs." + "?cs qb:measure ?res."
				+ "OPTIONAL{?res skos:prefLabel|rdfs:label ?label.}}";

		TupleQueryResult res = QueryExecutor.executeSelect(getCubeMeasure_query, SPARQLservice);
		List<LDResource> cumeMeasures = SPARQLresultTransformer.toLDResourceList(res);
		Collections.sort(cumeMeasures);
		return cumeMeasures;
	}

	public static List<LDResource> getDataCubeAttributes(String dataCubeURI, String SPARQLservice) {

		String getCubeAttributes_query = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" 
				+ "select  distinct ?res ?label where {" 
				+ "<"+ dataCubeURI + "> qb:structure ?dsd." + "?dsd qb:component ?comp." + "?comp qb:attribute ?res. "
				+ "OPTIONAL{?res skos:prefLabel|rdfs:label ?label.}}";

		TupleQueryResult res = QueryExecutor.executeSelect(getCubeAttributes_query, SPARQLservice);
		List<LDResource> cubeAttributes = SPARQLresultTransformer.toLDResourceList(res);
		Collections.sort(cubeAttributes);
		return cubeAttributes;
	}

	public static List<LDResource> getDimensionAttributeValues(String dimensionURI, String cubeURI,
			String SPARQLservice) {

		String getDimensionValues_query = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" 
				+ "select  distinct ?res ?label where {"
				+ "?observation qb:dataSet <" + cubeURI + ">."
				+ "?observation <" + dimensionURI + "> ?res."
				+ "OPTIONAL{?res skos:prefLabel|rdfs:label ?label}} ORDER BY ?res";

		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionValues_query, SPARQLservice);
		List<LDResource> dimensionValues = SPARQLresultTransformer.toLDResourceList(res);
		Collections.sort(dimensionValues);
		return dimensionValues;
	}

	// Ordered List of ALL Dimension Levels
	public static List<LDResource> getDimensionLevels(String dimensionURI, String SPARQLservice) {

		String getDimensionLevelsOrdered_query = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX xkos: <http://rdf-vocabulary.ddialliance.org/xkos#>"
				+ "select  distinct ?res ?position ?label where {" 
				+ "<" + dimensionURI + ">  qb:codeList ?codelist."
				+ "?codelist xkos:levels ?levellist." 
				+ "?levellist rdf:rest*/rdf:first ?res."
				+ "?res xkos:depth ?position." + "OPTIONAL{?res skos:prefLabel|rdfs:label ?label.}"
				+ "}group by ?res ?label";

		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionLevelsOrdered_query, SPARQLservice);
		List<LDResource> dimensionValues = SPARQLresultTransformer.toLDResourceList(res);
		return dimensionValues;
	}

	// ASK if a cube contains data at a specific dimension level
	// (check if exists an observation with this value)
	public static boolean cubeContainsDimensionLevel(String cubeURI, String dimensionLevel, String SPARQLservice) {
		String askDimensionLevelInDataCube_query = "PREFIX  qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" 
				+ "ASK where{?obs qb:dataSet <" + cubeURI+ ">." 
				+ "?obs ?dim ?value." + "<" + dimensionLevel + "> skos:member ?value.}";
		return QueryExecutor.executeASK(askDimensionLevelInDataCube_query, SPARQLservice);
	}

	// Ordered List of Cube Dimension Levels
	public static List<LDResource> getCubeDimensionLevels(String dimensionURI, String cubeURI, String SPARQLservice) {
		List<LDResource> allDimensionLevelsOrdered = getDimensionLevels(dimensionURI, SPARQLservice);
		List<LDResource> cubeDimensionLevels = new ArrayList<LDResource>();
		for (LDResource dimLevel : allDimensionLevelsOrdered) {
			if (cubeContainsDimensionLevel(cubeURI, dimLevel.getURI(), SPARQLservice)) {
				cubeDimensionLevels.add(dimLevel);
			}
		}
		return cubeDimensionLevels;
	}
	
	
	// Get labels of resource 
	public static LDResource getLabels(String resourceURI, String SPARQLservice) {
		String getDimensionLevelsOrdered_query = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX xkos: <http://rdf-vocabulary.ddialliance.org/xkos#>"
				+ "select  distinct ?label where {" 
				+ "<" + resourceURI + ">  skos:prefLabel|rdfs:label ?label.}";

		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionLevelsOrdered_query, SPARQLservice);
		LDResource dimensionLDR = SPARQLresultTransformer.toLDResource(resourceURI,res);
		return dimensionLDR;
	}
	

	public static List<Map<String, String>> getSlice(List<String> visualDims, Map<String, String> fixedDims,
			List<String> selectedMeasures, String cubeURI, String SPARQLservice) {

		Map<String, String> mapVariableNameURI = new HashMap<String, String>();
		mapVariableNameURI.put("obs", "id");

		String getSlice_query = "PREFIX  qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" 
				+ "Select ?obs ";

		int i = 1;
		// Add dimensions ?dim to SPARQL query
		for (String vDim : visualDims) {
			getSlice_query += "?dim" + i + " ";
			mapVariableNameURI.put("dim" + i, vDim);
			i++;
		}

		i = 1;
		// Add measures ?meas to SPARQL query
		for (String meas : selectedMeasures) {
			getSlice_query += "?measure" + i + " ";
			mapVariableNameURI.put("measure" + i, meas);
			i++;
		}

		// Select observations of a specific cube (cubeURI)
		getSlice_query += " where {" + "?obs qb:dataSet <" + cubeURI + ">.";

		// Add fixed dimensions to where clause
		i = 1;
		for (String fDim : fixedDims.keySet()) {
			getSlice_query += "?obs <" + fDim + "> ";
			if (fixedDims.get(fDim).contains("http")) {
				getSlice_query += "<" + fixedDims.get(fDim) + ">.";
			} else {
				getSlice_query += "?value" + i + "." + "FILTER(STR(?value" + i + ")='" + fixedDims.get(fDim) + "')";
			}
			i++;
		}

		i = 1;
		// Add free dimensions to where clause
		for (String vDim : visualDims) {
			getSlice_query += "?obs <" + vDim + "> " + "?dim" + i + ". ";
			i++;
		}

		i = 1;
		for (String meas : selectedMeasures) {
			getSlice_query += "?obs  <" + meas + "> ?measure" + i + ".";
			i++;
		}

		getSlice_query += "} ORDER BY ";
		
		i = 1;
		for (String vDim : visualDims) {
			getSlice_query += "?dim" + i + " ";
			i++;
		}
		
		System.out.println(getSlice_query);
		TupleQueryResult res = QueryExecutor.executeSelect(getSlice_query, SPARQLservice);
		List<Map<String, String>> listOfObservations = SPARQLresultTransformer.toMapList(res, mapVariableNameURI);

		return listOfObservations;

	}
	
	public static QBTable getTable(List<String> visualDims, Map<String, String> fixedDims,
			List<String> selectedMeasures, String cubeURI, String SPARQLservice) {

	//	Map<String, String> mapVariableNameURI = new HashMap<String, String>();
	//	mapVariableNameURI.put("obs", "id");

		String getSlice_query = "PREFIX  qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" 
				+ "Select ";
		
		int i = 1;
		//Ádd visual dims to SPARQL query
		for (String vDim : visualDims) {
			getSlice_query += "?dim" + i + " ";
			i++;
		}
		

	    i = 1;
		// Add measures ?meas to SPARQL query
		for (String meas : selectedMeasures) {
			getSlice_query += "?measure" + i + " ";
	//		mapVariableNameURI.put("measure" + i, meas);
			i++;
		}

		// Select observations of a specific cube (cubeURI)
		getSlice_query += " where {" + "?obs qb:dataSet <" + cubeURI + ">.";

		// Add fixed dimensions to where clause
		i = 1;
		for (String fDim : fixedDims.keySet()) {
			getSlice_query += "?obs <" + fDim + "> ";
			if (fixedDims.get(fDim).contains("http")) {
				getSlice_query += "<" + fixedDims.get(fDim) + ">.";
			} else {
				getSlice_query += "?value" + i + "." + "FILTER(STR(?value" + i + ")='" + fixedDims.get(fDim) + "')";
			}
			i++;
		}

		i = 1;
		// Add free dimensions to where clause
		for (String vDim : visualDims) {
			getSlice_query += "?obs <" + vDim + "> " + "?dim" + i + ". ";
			i++;
		}

		i = 1;
		for (String meas : selectedMeasures) {
			getSlice_query += "?obs  <" + meas + "> ?measure" + i + ".";
			i++;
		}

		getSlice_query += "} ORDER BY ";
		
		i = 1;
		for (String vDim : visualDims) {
			getSlice_query += "?dim" + i + " ";
			i++;
		}
		
		System.out.println(getSlice_query);
		TupleQueryResult res = QueryExecutor.executeSelect(getSlice_query, SPARQLservice);
		//List<Number> listOfONumbers = SPARQLresultTransformer.toQBTable(res, selectedMeasures, visualDims);
		QBTable qbt=SPARQLresultTransformer.toQBTable(res, selectedMeasures, visualDims);

		return qbt;

	}
}
