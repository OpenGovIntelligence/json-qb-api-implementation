package org.certh.jsonqb.util;

import java.util.List;
import java.util.Random;

import org.certh.jsonqb.core.QueryExecutor;
import org.certh.jsonqb.core.SPARQLconstants;
import org.certh.jsonqb.core.SPARQLresultTransformer;
import org.certh.jsonqb.datamodel.LDResource;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class SPARQLUtil {
	
	private SPARQLUtil() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}
		
	//Input a triple value, can be a URI or string
	//Output example: 1) http://example.com -> <http://example.com>
	//                2) Dimitris -> 'Dimitris'
	public static String toTripleValue(String value){
		if (value.contains("http")) {
			return "<" + value + ">";
		} else {
			return "\"" + value + "\"";
		}
	}
	
	public static String createRandomURI(String uriPrefix, String graph,String sparqlService){
		// create random aggregation set
		Random rand = new Random();
				
		boolean uriExist=true;
		String createdURI="";
					
		//Check if the URI already exists
		while(uriExist){
			long rnd = rand.nextLong();
			createdURI = uriPrefix+"_"+ rnd;
			String askURIExist= SPARQLconstants.PREFIX
					+ "SELECT ?exists where{"
					+ "?x a ?y."
					+ "BIND(EXISTS{<"+createdURI+"> rdf:type ?z.} AS ?exists)}"
							+ "LIMIT 1";
			
			TupleQueryResult res = QueryExecutor.executeSelect(askURIExist, sparqlService);
			BindingSet bindingSet = res.next();
			Literal existsStr=(Literal) bindingSet.getValue("exists");
			uriExist=existsStr.booleanValue();
		}
			
		
		return createdURI;
	}		
	
	// Get labels of resource 
	public static LDResource getLabels(String resourceURI, String sparqlService) {
		String getDimensionLevelsOrderedQuery = SPARQLconstants.PREFIX
				+ "select  distinct ?label where {" 
				+ "<" + resourceURI + ">  skos:prefLabel|rdfs:label ?label.}";
		TupleQueryResult res = QueryExecutor.executeSelect(getDimensionLevelsOrderedQuery, sparqlService);
		return SPARQLresultTransformer.toLDResource(resourceURI,res);		
	}
	
	// Get graph of a triple 
	public static LDResource getTripleGraph(String triple, String sparqlService) {
		String getTripleGraphQuery = SPARQLconstants.PREFIX
				+ "select distinct ?res where {"
				+ "graph ?res {"+triple+"}}";
		TupleQueryResult res = QueryExecutor.executeSelect(getTripleGraphQuery, sparqlService);
		List<LDResource> graphs= SPARQLresultTransformer.toLDResourceList(res);
		
		if(graphs.isEmpty()){
			return null;
		}else{
			return graphs.get(0);
		}
	}

}
