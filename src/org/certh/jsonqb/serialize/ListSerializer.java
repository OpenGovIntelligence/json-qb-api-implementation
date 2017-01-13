package org.certh.jsonqb.serialize;

import java.lang.reflect.Type;
import java.util.List;

import org.certh.jsonqb.datamodel.LDResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ListSerializer implements JsonSerializer<List<LDResource>> {

	private String myClass;
	
	public ListSerializer(String myClass) {
		super();
		this.myClass = myClass;
	}

	@Override
	public JsonElement serialize(List<LDResource> list,  Type typeOfSrc,  JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		JsonArray listOfElements=new JsonArray();
		
		for(LDResource elem:list){
			listOfElements.add(context.serialize(elem));
		}		
		
		jsonObject.add(myClass, listOfElements);		
		return jsonObject; 
	}
	
}
