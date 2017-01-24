package org.certh.jsonqb.serialize;

import java.lang.reflect.Type;

import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.Label;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LDResourceSerializer implements JsonSerializer<LDResource> {

	@Override
	public JsonElement serialize(LDResource ldr,  Type typeOfSrc,  JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(SerializationConstants.ID, ldr.getURI());
		
		jsonObject.addProperty(SerializationConstants.LABEL, ldr.getLabel("en"));

		if(ldr.getOrder()>0){
			jsonObject.addProperty(SerializationConstants.ORDER, ldr.getOrder());
		}
		
		return jsonObject;
	}

}