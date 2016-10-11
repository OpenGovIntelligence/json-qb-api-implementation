package org.certh.jsonqb.core;

import java.util.Collections;
import java.util.List;

import org.certh.jsonqb.util.LDResource;
import org.certh.jsonqb.util.QueryExecutor;
import org.certh.jsonqb.util.SPARQLresultTransformer;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class CubeSPARQL {

	// Get all the dimensions of a data cube
	// Input: The cubeURI
	public static List<LDResource> getDataCubeDimensions(String dataCubeURI, String SPARQLservice) {

		String getCubeDimensions_query = "PREFIX qb: <http://purl.org/linked-data/cube#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" 
				+ "select  distinct ?res ?label where {" + "<"
				+ dataCubeURI + "> qb:structure ?dsd." 
				+ "?dsd qb:component  ?cs." 
				+ "?cs qb:dimension ?res."
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
				+ "select  distinct ?res ?label where {" + "<"
				+ dataCubeURI + "> qb:structure ?dsd." 
				+ "?dsd qb:component ?cs." 
				+ "?cs qb:measure ?res."
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
				+ "<"+ dataCubeURI + "> qb:structure ?dsd." 
				+ "?dsd qb:component ?comp." 
				+ "?comp qb:attribute ?res. "
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
				+ "OPTIONAL{?res skos:prefLabel|rdfs:label ?label}}";

		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionValues_query, SPARQLservice);
		List<LDResource> dimensionValues = SPARQLresultTransformer.toLDResourceList(res);
		Collections.sort(dimensionValues);
		return dimensionValues;
	}
}
