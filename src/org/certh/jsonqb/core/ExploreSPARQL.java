package org.certh.jsonqb.core;

import java.util.Collections;
import java.util.List;

import org.certh.jsonqb.datamodel.LDResource;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class ExploreSPARQL {
	
	private static String prefix="PREFIX qb: <http://purl.org/linked-data/cube#>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
			+ "PREFIX xkos: <http://rdf-vocabulary.ddialliance.org/xkos#>"
			+ "PREFIX opencube: <http://opencube-project.eu/> ";
	
	private ExploreSPARQL() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}

	// Get all the available cubes of the SPARQLservice
	// Input: The SPARQLservice
	public static List<LDResource> getAllCubes(String sparqlService) {

		String getCubesQuery = prefix
				+ "select  distinct ?res where {"
				+ "?res rdf:type qb:DataSet }";
		TupleQueryResult res = QueryExecutor.executeSelect(getCubesQuery, sparqlService);

		List<LDResource> cubes = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		Collections.sort(cubes);
		return cubes;
	}
	
	
	// Get the cubes that belong to an Aggregation set and have the
	//maximum number of dimensions (for each AggregationSet 1 cube is returned)
	//i.e. get the original cube for each aggregation set
	// Input: The SPARQLservice
	public static List<LDResource> getMaxAggregationSetCubes(String sparqlService) {

		String getmaxCubesQuery = prefix
				//get the cube of the aggregation set that has the max dimension count
				+ "select ?res where{"
				+ "	?res qb:aggregationSet|opencube:aggregationSet ?aggset."
				+ "	?res qb:structure ?dsd."
				+ "	?dsd qb:component ?comp."
				+ "	?comp qb:dimension ?dim."
				//get max dimension count for each aggregation set
				+ "	{select ?aggset (MAX(?dimcount) as ?max) where{"
				+ "	?res qb:aggregationSet|opencube:aggregationSet ?aggset."
				//get dimension count for each cube
				+ "		{SELECT ?res (COUNT(?dim) AS ?dimcount) where {"
				+ "			?res rdf:type qb:DataSet."
				+ "			?res qb:structure ?dsd."
				+ "			?dsd qb:component ?comp."
				+ "			?comp qb:dimension ?dim."
				+ "		}}"
				+ "}}"
				+ "}group by ?res ?max ?label"
				+ " having(count(?dim)=?max)";
		
		TupleQueryResult res = QueryExecutor.executeSelect(getmaxCubesQuery, sparqlService);
		List<LDResource> cubes = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		Collections.sort(cubes);
		return cubes;
	}
	
	
	// Get the cubes that belong to an AggregationSet and have the
	//maximum number of dimensions (for each AggregationSet 1 cube is returned)
	//i.e. get the original cube for each aggregation set
	// AND cubes that do not belong to an AggregationSet
	// Input: The SPARQLservice
	public static List<LDResource> getMaxAggregationSetCubesAndCubesWithoutAggregation(String sparqlService) {
			
		String getCubesQuery = prefix
				+ "select distinct ?res where{"
				//get all cubes that do not have an aggregation set
				+ "	{?res rdf:type qb:DataSet."
				+ "	FILTER NOT EXISTS {?res qb:aggregationSet|opencube:aggregationSet ?aggset}}"
				+ "	UNION"
				+ "	{select ?res where{"
				+ "		?res qb:aggregationSet|opencube:aggregationSet ?aggset."
				+ "		?res qb:structure ?dsd."
				+ "		?dsd qb:component ?comp."
				+ "		?comp qb:dimension ?dim."
				//get max dimension count for each aggregation set
				+ "		{select ?aggset (MAX(?dimcount) as ?max) where{"
				+ "			?res qb:aggregationSet|opencube:aggregationSet ?aggset."
				//get dimension count for each cube
				+ "			{SELECT ?res (COUNT(?dim) AS ?dimcount) where {"
				+ "				?res rdf:type qb:DataSet."
				+ "				?res qb:structure ?dsd."
				+ "				?dsd qb:component ?comp."
				+ "				?comp qb:dimension ?dim."
				+ "			}}"
				+ "		}}"
				+ "	}group by ?res ?max"
				+ "	having(count(?dim)=?max)}"
				//get labels of the returned cubes
				+ "}";
			
		TupleQueryResult res = QueryExecutor.executeSelect(getCubesQuery, sparqlService);
		List<LDResource> cubes = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		Collections.sort(cubes);
		return cubes;
	}
	
	
	//Get cubes that have no computed aggregations
	//Needed by aggregator in order to compute the aggregations
	public static List<LDResource> getCubesWithNoAggregationSet(String sparqlService){
		
		String getCubesWithNoAggregationSetQuery=	prefix+
				"select ?res  where {" +
				"{?res rdf:type qb:DataSet.} " +
				"MINUS{?res opencube:aggregationSet ?set}}";
		
		TupleQueryResult res = QueryExecutor.executeSelect(getCubesWithNoAggregationSetQuery, sparqlService);

		List<LDResource> cumesWithNoAggregation = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		Collections.sort(cumesWithNoAggregation);
		return cumesWithNoAggregation;				
		 
	}	
	

	//Get the cube of an AggregationSet that has only the specified dimensions
	public static LDResource getCubeOfAggregationSet(String aggregationSetURI,
			List<String> dimensions, String sparqlService){
				
		StringBuilder getCubeOfAggregationSetQuery =new StringBuilder(prefix);
		getCubeOfAggregationSetQuery.append("select  distinct ?res where {" 
				+ "?res opencube:aggregationSet ?set."
				+ "?res qb:structure ?dsd." 
				+ "?dsd qb:component ?cs.");
		
		for(String dim:dimensions){
			getCubeOfAggregationSetQuery.append("?cs qb:dimension <"+dim+">.");
		}
		
		getCubeOfAggregationSetQuery.append("{select ?res where{"
						+ "?res opencube:aggregationSet <"+aggregationSetURI+">."
						+ "?res qb:structure ?dsd."
						+ "?dsd qb:component ?comp."
						+ "?comp qb:dimension ?dim."
						+ "}group by ?res "
						+ "having count(?dim)="+dimensions.size()+"}}");

		TupleQueryResult res = QueryExecutor.executeSelect(getCubeOfAggregationSetQuery.toString(),
				sparqlService);

		List<LDResource> cubeOfAggregationSet = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		return cubeOfAggregationSet.get(0);		
				
		
	}

}
