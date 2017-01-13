package test.org.certh.jsonqb.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.certh.jsonqb.core.CubeSPARQL;
import org.certh.jsonqb.datamodel.DataCube;
import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.Observation;
import org.certh.jsonqb.datamodel.QBTableJsonStat;
import org.certh.jsonqb.util.ObservationList;
import org.certh.jsonqb.util.PropertyFileReader;
import org.junit.Before;
import org.junit.Test;

public class TestCubeSPARQL {

	//private String SPARQLservice="http://195.251.218.39:8890/sparql";
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
	 public void testGetDataCubeMetadata() {
		 String dataCubeURI="http://id.vlaanderen.be/statistieken/dq/kubus-arbeidsmarkt-swse#id";
		 DataCube qb= CubeSPARQL.getCubeMetaData(dataCubeURI,SPARQLservice); 
	
		 assertEquals(2,qb.getComments().size());
		 assertEquals(2,qb.getDesctiptions().size());
		 assertEquals(2,qb.getTitles().size());
		 assertEquals(2,qb.getLabels().size());
		 assertEquals(2,qb.getSubjects().size());
		 assertNotNull(qb.getIssued());
		 assertNotNull(qb.getModified());
		 assertNotNull(qb.getPublisher());
		 assertNotNull(qb.getLicense());		 
	 }	 
	 
	 @Test
	 public void testGetDataCubeMeasures() {
		 String dataCubeURI="http://id.vlaanderen.be/statistieken/dq/kubus-arbeidsmarkt-swse#id";
		 List<LDResource> cubeMeasures= CubeSPARQL.getDataCubeMeasures(dataCubeURI,SPARQLservice); 
		 assertEquals(8,cubeMeasures.size());
	 }
	 
	 @Test
	 public void testGetDataCubeDimensions() {
		 String dataCubeURI="http://id.vlaanderen.be/statistieken/dq/kubus-arbeidsmarkt-swse#id";
		 List<LDResource> cubeMeasures= CubeSPARQL.getDataCubeDimensions(dataCubeURI,SPARQLservice); 
		 assertEquals(4,cubeMeasures.size());
	 }
	 
	 @Test
	 public void testGetDimensionValues() {
		 String dataCubeURI="http://id.vlaanderen.be/statistieken/dq/kubus-arbeidsmarkt-swse#id";
		 String dimensionURI="http://id.vlaanderen.be/statistieken/def#refArea";
		 List<LDResource> dimensionValues= CubeSPARQL.getDimensionAttributeValues(
				 dimensionURI,dataCubeURI,SPARQLservice); 
		 assertEquals(336,dimensionValues.size());
	 }
	 
	 @Test
	 public void testGetCubeDimensionLevels() {
		 String dataCubeURI="http://id.vlaanderen.be/statistieken/dq/kubus-arbeidsmarkt-swse#id";
		 String dimensionURI="http://id.vlaanderen.be/statistieken/def#refArea";
		 List<LDResource> dimensionValues= CubeSPARQL.getCubeDimensionLevels(
				 dimensionURI,dataCubeURI,SPARQLservice); 
		 assertEquals(4,dimensionValues.size());
	 }
	 
	 @Test
	 public void testGetSlice() {
		 String dataCubeURI="http://id.vlaanderen.be/statistieken/dq/kubus-arbeidsmarkt-swse#id";
		
		 List<String> visualDims=new ArrayList<String>();
		 visualDims.add("http://id.vlaanderen.be/statistieken/def#leeftijdsgroep");
		 visualDims.add("http://purl.org/linked-data/sdmx/2009/dimension#sex");
		 
		 Map<String, String> fixedDims=new HashMap<String, String>();
		 fixedDims.put("http://id.vlaanderen.be/statistieken/def#refArea", "http://id.fedstats.be/nis/44001#id");
		 fixedDims.put("http://id.vlaanderen.be/statistieken/def#timePeriod", "http://id.vlaanderen.be/statistieken/concept/jaar_2004#id");
		 
		 List<String> selectedMeasures=new ArrayList<String>();
		 selectedMeasures.add("http://id.vlaanderen.be/statistieken/def#aantalnwwz");
		 
		 
		 ObservationList observations= CubeSPARQL.getSlice(
				 visualDims, fixedDims, selectedMeasures, dataCubeURI, SPARQLservice); 
		 
		 assertEquals(42,observations.getListOfObservations().size());
	 } 
	 
	 @Test
	 public void testGetTable() {
		 String dataCubeURI="http://id.vlaanderen.be/statistieken/dq/kubus-arbeidsmarkt-swse#id";
		 String dim1="http://id.vlaanderen.be/statistieken/def#leeftijdsgroep";
		 String dim2="http://purl.org/linked-data/sdmx/2009/dimension#sex";
		 
		 List<String> visualDims=new ArrayList<String>();
		 visualDims.add(dim1);
		 visualDims.add(dim2);
		 
		 Map<String, String> fixedDims=new HashMap<String, String>();
		 fixedDims.put("http://id.vlaanderen.be/statistieken/def#refArea", "http://id.fedstats.be/nis/44001#id");
		 fixedDims.put("http://id.vlaanderen.be/statistieken/def#timePeriod", "http://id.vlaanderen.be/statistieken/concept/jaar_2004#id");
		 
		 List<String> selectedMeasures=new ArrayList<String>();
		 selectedMeasures.add("http://id.vlaanderen.be/statistieken/def#aantalnwwz");
		 
		 
		 QBTableJsonStat table= CubeSPARQL.getJsonStatTable(visualDims, 
				 fixedDims, selectedMeasures, dataCubeURI, SPARQLservice); 
		 
		 assertEquals(42,table.getMeasures().size());
		 assertEquals(21,table.getDimVals().get(dim1).size());
		 assertEquals(2,table.getDimVals().get(dim2).size());
	 } 
}
