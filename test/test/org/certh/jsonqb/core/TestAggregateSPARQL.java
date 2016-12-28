package test.org.certh.jsonqb.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.certh.jsonqb.core.AggregateSPARQL;
import org.certh.jsonqb.core.CubeSPARQL;
import org.certh.jsonqb.core.QueryExecutor;
import org.certh.jsonqb.datamodel.AggregationFunctions;
import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.util.PropertyFileReader;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAggregateSPARQL {
	
	private String sparqlService;
	private String testAggregationGraph="http://localhost:8890/testaggregation";
	private String testRollUpGraph="http://localhost:8890/testrollup";		
	private String aggregateCubeURI="http://id.vlaanderen.be/statistieken/dq/wonen-sociale-huisvesting-kubus#id";
	private String rollUpCubeURI="http://id.vlaanderen.be/statistieken/dq/Kubus-voorkeursregeling-in-de-ziekteverzekering#id";
	
	@Before
    public void runBeforeTestMethod() {
    	PropertyFileReader pfr=new PropertyFileReader();				
		try {
			sparqlService = pfr.getSPARQLservice();
		} catch (IOException e) {
			e.printStackTrace();
		}		
    }
	
	@Test
	public void testCreateAggregationSet() {		
		
		String aggURI=AggregateSPARQL.createNewAggregationSet(testAggregationGraph, sparqlService);
		AggregateSPARQL.attachCube2AggregationSet(aggURI, testAggregationGraph, aggregateCubeURI, sparqlService);
		
		assertNotNull(aggURI);		
		
		String[] dims={"http://id.vlaanderen.be/statistieken/def#timePeriod",
				"http://id.vlaanderen.be/statistieken/def#verhuringentype"};
		
		List<String> dimsAsList=Arrays.asList(dims);
		
		
		Map<String, AggregationFunctions> measureMap=new HashMap<>();
		
		List<LDResource> measures=CubeSPARQL.getDataCubeMeasures(aggregateCubeURI, sparqlService);
		for(LDResource m:measures){
			measureMap.put(m.getURI(),AggregationFunctions.SUM);
		}
	
				
		String aggCubeURI= AggregateSPARQL.createCubeForAggregationSet(aggregateCubeURI,
				testAggregationGraph, dimsAsList, measureMap, aggURI, sparqlService);	
		
		assertNotNull(aggCubeURI);
		
		String selectCount="Select (count(*) as ?count) where {"
				+ "GRAPH <"+testAggregationGraph+">{?x ?y ?z}}";
		
		TupleQueryResult res = QueryExecutor.executeSelect(selectCount, sparqlService);
		
		assertTrue(res.hasNext());
		BindingSet bindingSet = res.next();
		assertEquals("404", bindingSet.getValue("count").stringValue());		
	}
	
	@Test
	public void testgetAggregatedRollUpObservationsOneDim() {		
			
		Map<String, AggregationFunctions> measureMap=new HashMap<>();
		
		List<LDResource> measures=CubeSPARQL.getDataCubeMeasures(aggregateCubeURI, sparqlService);
		for(LDResource m:measures){
			measureMap.put(m.getURI(),AggregationFunctions.SUM);
		}
				
		int newObs=AggregateSPARQL.createRollUpAggregations(
				aggregateCubeURI, testRollUpGraph, measureMap, sparqlService);
		
		assertEquals(1344, newObs);
		
		String selectCount="Select (count(*) as ?count) where {"
				+ "GRAPH <"+testRollUpGraph+">{?x ?y ?z}}";
		
		TupleQueryResult res = QueryExecutor.executeSelect(selectCount, sparqlService);
		
		assertTrue(res.hasNext());
		BindingSet bindingSet = res.next();
		assertEquals("12096", bindingSet.getValue("count").stringValue());		
	}	
	
	
	@Test
	public void testgetAggregatedRollUpObservationsTwoDim() {		
			
		//TO DO
	/*	Map<String, AggregationFunctions> measureMap=new LinkedHashMap<>();
		
		measureMap.put("http://id.vlaanderen.be/statistieken/def#aantalVoorkeursregelingA", AggregationFunctions.SUM);
		measureMap.put("http://id.vlaanderen.be/statistieken/def#aantalVoorkeursregelingB", AggregationFunctions.SUM);
		measureMap.put("http://id.vlaanderen.be/statistieken/def#aantalVoorkeursregelingC", AggregationFunctions.SUM);
		measureMap.put("http://id.vlaanderen.be/statistieken/def#aantalVoorkeursregelingD", AggregationFunctions.SUM);
		measureMap.put("http://id.vlaanderen.be/statistieken/def#aantalVoorkeursregelingE", AggregationFunctions.SUM);		
				
		int newObs=AggregateSPARQL.createRollUpAggregations(
				aggregateCubeURI, testRollUpGraph, measureMap, sparqlService);*/
		
		
		
	}	
	
	
	@After
	public void emptyTestGraph() {
		String deleteInsert="WITH <"+testAggregationGraph+"> "
				+ "DELETE {?x ?y ?z}"
				+ "WHERE {?x ?y ?z}";
		QueryExecutor.executeUPDATE(deleteInsert, sparqlService);
		
		deleteInsert="WITH <"+testRollUpGraph+"> "
				+ "DELETE {?x ?y ?z}"
				+ "WHERE {?x ?y ?z}";
		QueryExecutor.executeUPDATE(deleteInsert, sparqlService);
	}
	

}
