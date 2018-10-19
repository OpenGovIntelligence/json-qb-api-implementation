package org.certh.jsonqb.core;

public class SPARQLconstants {
	
	public static final String PREFIX="PREFIX qb: <http://purl.org/linked-data/cube#>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
			+ "PREFIX xkos: <http://rdf-vocabulary.ddialliance.org/xkos#>"
			+ "PREFIX opencube: <http://opencube-project.eu/> "
			+ "PREFIX dct: <http://purl.org/dc/terms/> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	
	public static final String CODESUSED_PREDICATE="http://publishmydata.com/def/qb/codesUsed";
	
	private SPARQLconstants() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}
	
	

}


