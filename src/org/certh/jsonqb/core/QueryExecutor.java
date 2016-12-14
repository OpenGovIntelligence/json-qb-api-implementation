package org.certh.jsonqb.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class QueryExecutor {
	
	private static final Logger LOGGER = Logger.getLogger(QueryExecutor.class.getName());
	
	private QueryExecutor() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}

	// Execute a SPARQL SELECT using a triple store
	// Input the query to execute and the triple store URI
	public static TupleQueryResult executeSelect(String queryString, String endpointUrl) {
			
		Repository repo = new SPARQLRepository(endpointUrl);
		repo.initialize();
		RepositoryConnection con = repo.getConnection();
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		LOGGER.log(Level.INFO, queryString);
		
		TupleQueryResult res = null;
		try {
			res = tupleQuery.evaluate();
		} catch (RepositoryException|MalformedQueryException|QueryEvaluationException e) {
			LOGGER.log( Level.SEVERE, e.toString(), e);
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
		} catch (RepositoryException|MalformedQueryException|QueryEvaluationException e) {
			LOGGER.log( Level.SEVERE, e.toString(), e);
		} 
		return result;
	}	
	
	// Execute a SPARQL UPDATE using the native IWB triple store
	// Input the query to execute and the triple store URI
	public static void executeUPDATE(String queryString, String endpointUrl) {
		Repository repo = new SPARQLRepository(endpointUrl);
		repo.initialize();
		RepositoryConnection con = repo.getConnection();		
		Update updateQuery = con.prepareUpdate(QueryLanguage.SPARQL, queryString);
				
		try {
			updateQuery.execute();
		} catch (RepositoryException|MalformedQueryException|QueryEvaluationException e) {
			LOGGER.log( Level.SEVERE, e.toString(), e);
		} 	
	}

}