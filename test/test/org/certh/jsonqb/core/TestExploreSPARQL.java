package test.org.certh.jsonqb.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.certh.jsonqb.core.ExploreSPARQL;
import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.util.PropertyFileReader;
import org.junit.Before;
import org.junit.Test;

public class TestExploreSPARQL {

	private String SPARQLservice;
	
    @Before
    public void runBeforeTestMethod() {
    	PropertyFileReader pfr=new PropertyFileReader();				
		try {
			SPARQLservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			e.printStackTrace();
		}		
    }
    
    @Test
	public void testGetAllCubes() {
		 List<LDResource> cubes= ExploreSPARQL.getAllCubes(SPARQLservice); 
		 assertEquals(230,cubes.size());
	}
    
    @Test
	public void testGetCubesWithNoAggregationSet() {
		 List<LDResource> cubesWithNoAggregationSet= ExploreSPARQL.getCubesWithNoAggregationSet(SPARQLservice); 
		 assertEquals(0,cubesWithNoAggregationSet.size());
	}
    
    @Test
	public void testMaxAggregationSetCubes() {
		 List<LDResource> maxAggregationSetCubes= ExploreSPARQL.getMaxAggregationSetCubes(SPARQLservice); 
		 assertEquals(14,maxAggregationSetCubes.size());
	}
    
    @Test
   	public void testMaxAggregationSetCubesAndCubesWithoutAggregation() {
   		 List<LDResource> maxAggregationSetCubesAndCubesWithoutAggregation= 
   				 ExploreSPARQL.getMaxAggregationSetCubesAndCubesWithoutAggregation(SPARQLservice); 
   		 assertEquals(14,maxAggregationSetCubesAndCubesWithoutAggregation.size());
   	}
    
    @Test
   	public void testGetCubeOfAggregationSet() {
    	String aggsetURI="http://opencube-project.eu/aggregationSet_3402807365941127147";
    	List<String> d=new ArrayList<String>();
    	d.add("http://id.vlaanderen.be/statistieken/def#refArea");
    	LDResource cubeOfAggregationSet= ExploreSPARQL.getCubeOfAggregationSet(aggsetURI, d, SPARQLservice);
    	System.out.println(cubeOfAggregationSet.getURI());
   		assertNotNull(cubeOfAggregationSet);
   		String expectedCubeURI="http://id.vlaanderen.be/statistieken/dq/kubus-studieniveau-nwwz#id_refArea_6447896132624084171";
   		assertEquals(expectedCubeURI, cubeOfAggregationSet.getURI());
   	}


}






