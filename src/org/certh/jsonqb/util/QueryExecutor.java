package org.certh.jsonqb.util;

import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class QueryExecutor {

	// Execute a SPARQL SELECT using a triple store
	// Input the query to execute and the triple store URI
	public static TupleQueryResult executeSelect(String queryString, String endpointUrl) {
		
//		try {
//			URLEncoder.encode(queryString, "UTF-8");
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
		Repository repo = new SPARQLRepository(endpointUrl);
		repo.initialize();
		RepositoryConnection con = repo.getConnection();
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

		TupleQueryResult res = null;
		try {
			res = tupleQuery.evaluate();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return res;
	}	

	// Execute a SPARQL ASK using a triple store
	// Input the query to execute and the triple store URI
	public static boolean executeASK(String queryString, String endpointUrl) {
		Repository repo = new SPARQLRepository(endpointUrl);
		repo.initialize();
		RepositoryConnection con = repo.getConnection();		
		BooleanQuery booleanQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, queryString);

		boolean result = false;
		try {
			result = booleanQuery.evaluate();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return result;
	}	

}