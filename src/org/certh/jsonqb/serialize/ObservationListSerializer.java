package org.certh.jsonqb.serialize;

import java.lang.reflect.Type;

import org.certh.jsonqb.datamodel.Observation;
import org.certh.jsonqb.util.ObservationList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ObservationListSerializer implements JsonSerializer<ObservationList>{
	
	@Override
	public JsonElement serialize(ObservationList obslist,  Type typeOfSrc,  JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		JsonArray jsonListOfObsElements=new JsonArray();
		
		for(Observation obs:obslist.getListOfObservations()){
			jsonListOfObsElements.add(context.serialize(obs));
		}		
		
		jsonObject.add(SerializationConstants.OBSERVATIONS, jsonListOfObsElements);		
		return jsonObject;
	}
}
