package org.certh.jsonqb.serialize;

import java.lang.reflect.Type;

import org.certh.jsonqb.datamodel.LockedDimension;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LockedDimensionSerializer implements JsonSerializer<LockedDimension> {

	@Override
	public JsonElement serialize(LockedDimension lockedDim,  Type typeOfSrc,  JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(SerializationConstants.ID, lockedDim.getURI());
		jsonObject.addProperty(SerializationConstants.LABEL, lockedDim.getURIorLabel());

		if(lockedDim.getOrder()>0){
			jsonObject.addProperty(SerializationConstants.ORDER, lockedDim.getOrder());
		}
		jsonObject.add(SerializationConstants.LOCKED_VALUE,context.serialize(lockedDim.getLockedValue()));
		
		return jsonObject;
	}

}