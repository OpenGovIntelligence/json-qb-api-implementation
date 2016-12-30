package test.org.certh.jsonqb.serialize;

import static org.junit.Assert.assertEquals;

import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.serialize.LDResourceSerializer;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestLDResourceSerializer {

	@Test
	public void testSimpleLDResource() {		
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    
	    Gson gson = gsonBuilder.create();
	    
	    LDResource ldr =new LDResource("http://localhos/testURI", "test label");

	    // Format to JSON
	    String json = gson.toJson(ldr);
	    				
		assertEquals(json, "{\"@id\":\"http://localhos/testURI\","
				+ "\"label\":\"test label\"}");
	}
	
	@Test
	public void testLDResourceWithOrder() {		
		GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    
	    Gson gson = gsonBuilder.create();
	    
	    LDResource ldr =new LDResource("http://localhos/testURI", "test label");
	    ldr.setOrder(1);
	    // Format to JSON
	    String json = gson.toJson(ldr);
	    		
		assertEquals(json, "{\"@id\":\"http://localhos/testURI\","
				+ "\"label\":\"test label\",\"order\":1}");
	}
	
}
