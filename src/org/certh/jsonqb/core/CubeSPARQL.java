package org.certh.jsonqb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.certh.jsonqb.datamodel.DataCube;
import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.Observation;
import org.certh.jsonqb.datamodel.QBTable;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class CubeSPARQL {

	private static String prefix="PREFIX qb: <http://purl.org/linked-data/cube#>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
			+ "PREFIX xkos: <http://rdf-vocabulary.ddialliance.org/xkos#>"
			+ "PREFIX opencube: <http://opencube-project.eu/> "
			+ "PREFIX dct: <http://purl.org/dc/terms/> ";
	
	private CubeSPARQL() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}
	
	
	// Get all the dimensions of a data cube
	// Input: The cubeURI, SPARQLservice
	public static List<LDResource> getDataCubeDimensions(String dataCubeURI, String sparqlService) {

		String getCubeDimensionsQuery = prefix
				+ "select  distinct ?res  where {" 
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

		String getCubeMeasureQuery = prefix
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

		String getCubeAttributesQuery = prefix
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

			String getDimensionValuesQuery = prefix
				+ "select  distinct ?res  where {"
				+ "?observation qb:dataSet <" + cubeURI + ">."
				+ "?observation <" + dimensionURI + "> ?res."
				+ "}";			

		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionValuesQuery, sparqlService);
		List<LDResource> dimensionValuesWithLables=SPARQLresultTransformer.toLDResourceListWithLabels(res,sparqlService);
		
		Collections.sort(dimensionValuesWithLables, LDResource.labelComparator);
		return dimensionValuesWithLables;
	}

	// Ordered List of ALL Dimension Levels
	public static List<LDResource> getDimensionLevels(String dimensionURI, String sparqlService) {

		String getDimensionLevelsOrderedQuery = prefix
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
		String askDimensionLevelInDataCubeQuery = prefix
				+ "ASK where{?obs qb:dataSet <" + cubeURI+ ">." 
				+ "?obs ?dim ?value." 
				+ "<" + dimensionLevel + "> skos:member ?value.}";
		return QueryExecutor.executeASK(askDimensionLevelInDataCubeQuery, sparqlService);
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
	public static LDResource getLabels(String resourceURI, String sparqlService) {
		String getDimensionLevelsOrderedQuery = prefix
				+ "select  distinct ?label where {" 
				+ "<" + resourceURI + ">  skos:prefLabel|rdfs:label ?label.}";

		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionLevelsOrderedQuery, sparqlService);
		return SPARQLresultTransformer.toLDResource(resourceURI,res);		
	}
	
	
	// Get labels of resource 
	public static DataCube getCubeMetaData(String cubeURI, String sparqlService) {
		String getCubeMetadataQuery = prefix
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
	
	
	

	
	
	

	public static List<Observation> getSlice(List<String> visualDims, Map<String, String> fixedDims,
			List<String> selectedMeasures, String cubeURI, String sparqlService) {

		Map<String, String> mapVariableNameURI = new HashMap<>();
		mapVariableNameURI.put("obs", "id");

		StringBuilder getSliceQuery=new StringBuilder(prefix);
		getSliceQuery.append("Select distinct ?obs ");

		int i = 1;
		// Add dimensions ?dim to SPARQL query
		for (String vDim : visualDims) {
			getSliceQuery.append("?dim" + i + " ");
			mapVariableNameURI.put("dim" + i, vDim);
			i++;
		}

		i = 1;
		// Add measures ?meas to SPARQL query
		for (String meas : selectedMeasures) {
			getSliceQuery.append("?measure" + i + " ");
			mapVariableNameURI.put("measure" + i, meas);
			i++;
		}

		// Select observations of a specific cube (cubeURI)
		getSliceQuery.append(" where { ?obs qb:dataSet <" + cubeURI + ">.");

		// Add fixed dimensions to where clause
		i = 1;
		for (Map.Entry<String, String> entry : fixedDims.entrySet()) {
			getSliceQuery.append("?obs <" + entry.getKey() + "> ");
			if (entry.getValue().contains("http")) {
				getSliceQuery.append("<" + entry.getValue() + ">.");
			} else {
				getSliceQuery.append("?value" + i + "." + "FILTER(STR(?value" + i + ")='" + entry.getValue() + "')");
			}
			i++;
		}
		
		
		for (String fDim : fixedDims.keySet()) {
			getSliceQuery.append("?obs <" + fDim + "> ");
			if (fixedDims.get(fDim).contains("http")) {
				getSliceQuery.append("<" + fixedDims.get(fDim) + ">.");
			} else {
				getSliceQuery.append("?value" + i + "." + "FILTER(STR(?value" + i + ")='" + fixedDims.get(fDim) + "')");
			}
			i++;
		}

		i = 1;
		// Add free dimensions to where clause
		for (String vDim : visualDims) {
			getSliceQuery.append("?obs <" + vDim + "> " + "?dim" + i + ". ");
			i++;
		}

		i = 1;
		for (String meas : selectedMeasures) {
			getSliceQuery.append("?obs  <" + meas + "> ?measure" + i + ".");
			i++;
		}

		getSliceQuery.append("} ORDER BY ");
		
		i = 1;
		for (String vDim : visualDims) {
			getSliceQuery.append("?dim" + i + " ");
			i++;
		}
				
		TupleQueryResult res = QueryExecutor.executeSelect(getSliceQuery.toString(), sparqlService);
		return SPARQLresultTransformer.toObservationList(res, mapVariableNameURI);
	}
	
	public static QBTable getTable(List<String> visualDims, Map<String, String> fixedDims,
			List<String> selectedMeasures, String cubeURI, String sparqlService) {
		
		StringBuilder getTableQuery=new StringBuilder(prefix);
		
		getTableQuery.append("Select distinct ");
		
		int i = 1;
		//Ádd visual dims to SPARQL query
		for (String vDim : visualDims) {
			getTableQuery.append("?dim" + i + " ");
			i++;
		}		

	    i = 1;
		// Add measures ?meas to SPARQL query
		for (String meas : selectedMeasures) {
			getTableQuery.append("?measure" + i + " ");
			i++;
		}

		// Select observations of a specific cube (cubeURI)
		getTableQuery.append(" where {" + "?obs qb:dataSet <" + cubeURI + ">.");

		// Add fixed dimensions to where clause
		i = 1;
		
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
		return SPARQLresultTransformer.toQBTable(res, selectedMeasures, visualDims,sparqlService);	

	}
}
