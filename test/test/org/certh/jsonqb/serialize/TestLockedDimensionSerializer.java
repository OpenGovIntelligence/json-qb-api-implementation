package test.org.certh.jsonqb.serialize;

import static org.junit.Assert.assertEquals;

import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.LockedDimension;
import org.certh.jsonqb.serialize.LDResourceSerializer;
import org.certh.jsonqb.serialize.LockedDimensionSerializer;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestLockedDimensionSerializer {

	@Test
	public void testSimpleLockedDimension() {		
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.registerTypeAdapter(LockedDimension.class, new LockedDimensionSerializer());
	    
	    Gson gson = gsonBuilder.create();
	    
	    LDResource lockedValue =new LDResource("http://localhos/lockedValueURI", "locked value");
	    LockedDimension lockedDim=new LockedDimension("http://localhos/lockedDimURI", "locked dim");
	    lockedDim.setLockedValue(lockedValue);
	    
	    // Format to JSON
	    String json = gson.toJson(lockedDim);
	    				
		assertEquals(json, "{\"@id\":\"http://localhos/lockedDimURI\","
					+ "\"label\":\"locked dim\","
					+ "\"locked_value\":{"
						+ "\"@id\":\"http://localhos/lockedValueURI\","
						+ "\"label\":\"locked value\"}}");
	}
	
	@Test
	public void testLDResourceWithOrder() {		
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.registerTypeAdapter(LockedDimension.class, new LockedDimensionSerializer());
	    
	    Gson gson = gsonBuilder.create();
	    
	    LDResource lockedValue =new LDResource("http://localhos/lockedValueURI", "locked value");
	    LockedDimension lockedDim=new LockedDimension("http://localhos/lockedDimURI", "locked dim");
	    lockedDim.setOrder(1);
	    lockedDim.setLockedValue(lockedValue);
	    
	    // Format to JSON
	    String json = gson.toJson(lockedDim);
	    				
		assertEquals(json, "{\"@id\":\"http://localhos/lockedDimURI\","
					+ "\"label\":\"locked dim\","
					+ "\"order\":1,"
					+ "\"locked_value\":{"
						+ "\"@id\":\"http://localhos/lockedValueURI\","
						+ "\"label\":\"locked value\"}}");
	}
}
