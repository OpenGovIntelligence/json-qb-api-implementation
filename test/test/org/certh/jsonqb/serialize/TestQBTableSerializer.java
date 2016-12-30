package test.org.certh.jsonqb.serialize;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.LockedDimension;
import org.certh.jsonqb.datamodel.QBTable;
import org.certh.jsonqb.serialize.LDResourceSerializer;
import org.certh.jsonqb.serialize.LockedDimensionSerializer;
import org.certh.jsonqb.serialize.QBTableSerializer;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestQBTableSerializer {

	@Test
	public void testLockedDims() {
		
		//Create locked dimensions
		LDResource lockedValue1 =new LDResource("http://localhost/lockedValue1URI", "locked value 1");
		LockedDimension lockedDim1=new LockedDimension("http://localhost/lockedDim1URI", "locked dim 1");
		lockedDim1.setLockedValue(lockedValue1);
		    
		LDResource lockedValue2 =new LDResource("http://localhost/lockedValue2URI", "locked value 2");
		LockedDimension lockedDim2=new LockedDimension("http://localhost/lockedDim2URI", "locked dim 2");
		lockedDim2.setLockedValue(lockedValue2);
		
		//Add locked dimensions to QB Table
		QBTable qb=new QBTable();
		qb.addLockedDimension(lockedDim1);
		qb.addLockedDimension(lockedDim2);		
		
		//Create GsonBuilder
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.registerTypeAdapter(LockedDimension.class, new LockedDimensionSerializer());
	    gsonBuilder.registerTypeAdapter(QBTable.class, new QBTableSerializer());
	    
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(qb);
	    				
		assertEquals(json, "{\"structure\":{\"locked_dimensions\":{"
				+ "\"lockedDim1URI\":{\"@id\":\"http://localhost/lockedDim1URI\",\"label\":\"locked dim 1\","
				+ "\"locked_value\":{\"@id\":\"http://localhost/lockedValue1URI\",\"label\":\"locked value 1\"}},"
				+ "\"lockedDim2URI\":{\"@id\":\"http://localhost/lockedDim2URI\",\"label\":\"locked dim 2\","
				+ "\"locked_value\":{\"@id\":\"http://localhost/lockedValue2URI\",\"label\":\"locked value 2\"}}}},"
				+ "\"headers\":{},\"data\":[]}");
	}
	
	@Test
	public void testFreeDims() {
		
		//Create free dimensions
		LDResource freeDim1 =new LDResource("http://localhost/freeDim1URI", "free dim 1");
		LDResource freeDim2 =new LDResource("http://localhost/freeDim2URI", "free dim 2");
		
		//Add free dimensions to QBTable
		QBTable qb=new QBTable();
		qb.addFreeDimension(freeDim1);
		qb.addFreeDimension(freeDim2);
		
		//Create GsonBuilder
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.registerTypeAdapter(QBTable.class, new QBTableSerializer());
	    
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(qb);
	    				
		assertEquals(json, "{\"structure\":{\"free_dimensions\":{"
				+ "\"freeDim1URI\":{\"@id\":\"http://localhost/freeDim1URI\",\"label\":\"free dim 1\"},"
				+ "\"freeDim2URI\":{\"@id\":\"http://localhost/freeDim2URI\",\"label\":\"free dim 2\"}}},"
				+ "\"headers\":{},\"data\":[]}");
	}
	
	
	@Test
	public void testDimensionValues() {
		
		//Create Dimensions and dimension values
		
		//refArea
		LDResource refArea =new LDResource("http://localhost/refArea", "reference area");
		LDResource area1 =new LDResource("http://localhost/area1URI", "area1");
		LDResource area2 =new LDResource("http://localhost/area2URI", "area2");
		List<LDResource> refAreaValues=new ArrayList<>();
		refAreaValues.add(area1);
		refAreaValues.add(area2);
				
		//refPeriod
		LDResource refPeriod =new LDResource("http://localhost/refPeriod", "reference period");
		LDResource time1 =new LDResource("http://localhost/time1URI", "time1");
		LDResource time2 =new LDResource("http://localhost/time2URI", "time2");
		List<LDResource> refPeriodValues=new ArrayList<>();
		refPeriodValues.add(time1);
		refPeriodValues.add(time2);	    
				
		//Add dimension values to QBTable
		QBTable qb=new QBTable();
		qb.putDimensionValues(refArea, refAreaValues);
		qb.putDimensionValues(refPeriod, refPeriodValues);		
		
		//Create GsonBuilder
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.registerTypeAdapter(LockedDimension.class, new LockedDimensionSerializer());
	    gsonBuilder.registerTypeAdapter(QBTable.class, new QBTableSerializer());
	    
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(qb);
	    				
		assertEquals(json, "{\"structure\":{\"dimension_values\":{\"refArea\":{"
				+ "\"area1URI\":{\"@id\":\"http://localhost/area1URI\",\"label\":\"area1\"},"
				+ "\"area2URI\":{\"@id\":\"http://localhost/area2URI\",\"label\":\"area2\"}},"
				+ "\"refPeriod\":{\"time1URI\":{\"@id\":\"http://localhost/time1URI\",\"label\":\"time1\"},"
				+ "\"time2URI\":{\"@id\":\"http://localhost/time2URI\",\"label\":\"time2\"}}}},"
				+ "\"headers\":{},\"data\":[]}");
	}
	
	@Test
	public void testQBStructure() {
		
		
		//Locked dims
		LDResource lockedValue1 =new LDResource("http://localhost/lockedValue1URI", "locked value 1");
		LockedDimension lockedDim1=new LockedDimension("http://localhost/lockedDim1URI", "locked dim 1");
		lockedDim1.setLockedValue(lockedValue1);
		    
		LDResource lockedValue2 =new LDResource("http://localhost/lockedValue2URI", "locked value 2");
		LockedDimension lockedDim2=new LockedDimension("http://localhost/lockedDim2URI", "locked dim 2");
		lockedDim2.setLockedValue(lockedValue2);
		
		//Free dims
		LDResource freeDim1 =new LDResource("http://localhost/freeDim1URI", "free dim 1");
		LDResource freeDim2 =new LDResource("http://localhost/freeDim2URI", "free dim 2");
		
		//Dimension values		
		
		//refArea
		LDResource refArea =new LDResource("http://localhost/refArea", "reference area");
		LDResource area1 =new LDResource("http://localhost/area1URI", "area1");
		LDResource area2 =new LDResource("http://localhost/area2URI", "area2");
		List<LDResource> refAreaValues=new ArrayList<>();
		refAreaValues.add(area1);
		refAreaValues.add(area2);
		
		//refPeriod
		LDResource refPeriod =new LDResource("http://localhost/refPeriod", "reference period");
		LDResource time1 =new LDResource("http://localhost/time1URI", "time1");
		LDResource time2 =new LDResource("http://localhost/time2URI", "time2");
		List<LDResource> refPeriodValues=new ArrayList<>();
		refPeriodValues.add(time1);
		refPeriodValues.add(time2);	    
				
		//Add locked dims, free dims and Dimension values to QBTable
		QBTable qb=new QBTable();
		qb.addLockedDimension(lockedDim1);
		qb.addLockedDimension(lockedDim2);	
		qb.addFreeDimension(freeDim1);
		qb.addFreeDimension(freeDim2);
		qb.putDimensionValues(refArea, refAreaValues);
		qb.putDimensionValues(refPeriod, refPeriodValues);	

		//Create GsonBuilder
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.registerTypeAdapter(LockedDimension.class, new LockedDimensionSerializer());
	    gsonBuilder.registerTypeAdapter(QBTable.class, new QBTableSerializer());
	    
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(qb);
	    	    
	    assertEquals(json, "{\"structure\":{\"free_dimensions\":{"
	    		+ "\"freeDim1URI\":{\"@id\":\"http://localhost/freeDim1URI\",\"label\":\"free dim 1\"},"
	    		+ "\"freeDim2URI\":{\"@id\":\"http://localhost/freeDim2URI\",\"label\":\"free dim 2\"}},"
	    		+ "\"locked_dimensions\":{"
	    		+ "\"lockedDim1URI\":{\"@id\":\"http://localhost/lockedDim1URI\",\"label\":\"locked dim 1\","
	    		+ "\"locked_value\":{\"@id\":\"http://localhost/lockedValue1URI\",\"label\":\"locked value 1\"}},"
	    		+ "\"lockedDim2URI\":{\"@id\":\"http://localhost/lockedDim2URI\",\"label\":\"locked dim 2\","
	    		+ "\"locked_value\":{\"@id\":\"http://localhost/lockedValue2URI\",\"label\":\"locked value 2\"}}},"
	    		+ "\"dimension_values\":{"
	    		+ "\"refArea\":{\"area1URI\":{\"@id\":\"http://localhost/area1URI\",\"label\":\"area1\"},"
	    		+ "\"area2URI\":{\"@id\":\"http://localhost/area2URI\",\"label\":\"area2\"}},"
	    		+ "\"refPeriod\":{\"time1URI\":{\"@id\":\"http://localhost/time1URI\",\"label\":\"time1\"},"
	    		+ "\"time2URI\":{\"@id\":\"http://localhost/time2URI\",\"label\":\"time2\"}}}},"
	    		+ "\"headers\":{},\"data\":[]}");		
	}
	
	@Test
	public void testQBHeadersAndData() {
		
		//Create Dimension values		
		
		//refArea
		LDResource refArea =new LDResource("http://localhost/refArea", "reference area");
		LDResource area1 =new LDResource("http://localhost/area1URI", "area1");
		LDResource area2 =new LDResource("http://localhost/area2URI", "area2");
		LDResource area3 =new LDResource("http://localhost/area3URI", "area3");
		List<LDResource> refAreaValues=new ArrayList<>();
		refAreaValues.add(area1);
		refAreaValues.add(area2);
		refAreaValues.add(area3);
		
		//refperiod
		LDResource refPeriod =new LDResource("http://localhost/refPeriod", "reference period");
		LDResource time1 =new LDResource("http://localhost/time1URI", "time1");
		LDResource time2 =new LDResource("http://localhost/time2URI", "time2");
		List<LDResource> refPeriodValues=new ArrayList<>();
		refPeriodValues.add(time1);
		refPeriodValues.add(time2);	   		
		
		//sex
		LDResource sex =new LDResource("http://localhost/sex", "sex");
		LDResource sex1 =new LDResource("http://localhost/sex1URI", "sex1");
		LDResource sex2 =new LDResource("http://localhost/sex2URI", "sex2");
		List<LDResource> sexValues=new ArrayList<>();
		sexValues.add(sex1);
		sexValues.add(sex2);
		
		//age
		LDResource age =new LDResource("http://localhost/age", "age");
		LDResource age1 =new LDResource("http://localhost/age1URI", "age1");
		LDResource age2 =new LDResource("http://localhost/age2URI", "age2");
		List<LDResource> ageValues=new ArrayList<>();
		ageValues.add(age1);
		ageValues.add(age2);		
		

		//Add dimension values, columns and rows, and data to QBTable
		QBTable qb=new QBTable();
		qb.putDimensionValues(refArea, refAreaValues);
		qb.putDimensionValues(refPeriod, refPeriodValues);
		qb.putDimensionValues(sex, sexValues);
		qb.putDimensionValues(age, ageValues);	
		
		//Columns=[refArea,refPeriod,age]
		//rows = [sex]
		qb.addColumn(refArea);
		qb.addColumn(refPeriod);
		qb.addColumn(age);
		qb.addRow(sex);
        
		//add data to QBTable
		//sexValues*refAreaValues*refPeriodValues*ageValues = 2*3*2*2 = 24 values
        for(int i=1;i<=24;i++){
        	qb.addData(i);
        }
			
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
		gsonBuilder.registerTypeAdapter(LockedDimension.class, new LockedDimensionSerializer());
		gsonBuilder.registerTypeAdapter(QBTable.class, new QBTableSerializer());
	
		Gson gson = gsonBuilder.create();	   
			    
		// Format to JSON
		String json = gson.toJson(qb);
			
		assertEquals(json,"{\"structure\":{\"dimension_values\":{"
				+ "\"age\":{\"age1URI\":{\"@id\":\"http://localhost/age1URI\",\"label\":\"age1\"},"
				+ "\"age2URI\":{\"@id\":\"http://localhost/age2URI\",\"label\":\"age2\"}},"
				+ "\"refArea\":{\"area1URI\":{\"@id\":\"http://localhost/area1URI\",\"label\":\"area1\"},"
				+ "\"area2URI\":{\"@id\":\"http://localhost/area2URI\",\"label\":\"area2\"},"
				+ "\"area3URI\":{\"@id\":\"http://localhost/area3URI\",\"label\":\"area3\"}},"
				+ "\"sex\":{\"sex1URI\":{\"@id\":\"http://localhost/sex1URI\",\"label\":\"sex1\"},"
				+ "\"sex2URI\":{\"@id\":\"http://localhost/sex2URI\",\"label\":\"sex2\"}},"
				+ "\"refPeriod\":{\"time1URI\":{\"@id\":\"http://localhost/time1URI\",\"label\":\"time1\"},"
				+ "\"time2URI\":{\"@id\":\"http://localhost/time2URI\",\"label\":\"time2\"}}}},"
				+ "\"headers\":{\"columns\":{\"refArea\":[\"area1URI\",\"area2URI\",\"area3URI\"],"
				+ "\"refPeriod\":[\"time1URI\",\"time2URI\"],\"age\":[\"age1URI\",\"age2URI\"]},"
				+ "\"column_hierarchy\":[\"refArea\",\"refPeriod\",\"age\"],"
				+ "\"rows\":{\"sex\":[\"sex1URI\",\"sex2URI\"]}},"
				+ "\"data\":[[[[1,2],[3,4]],[[5,6],[7,8]],[[9,10],[11,12]]],"
				+ "[[[13,14],[15,16]],[[17,18],[19,20]],[[21,22],[23,24]]]]}");
		
	}
	

}
