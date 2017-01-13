package org.certh.jsonqb.serialize;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.certh.jsonqb.datamodel.Observation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ObservationSerializer implements JsonSerializer<Observation> {

	@Override
	public JsonElement serialize(Observation obs,  Type typeOfSrc,  JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();					
		for(Entry<String, String> obsValue: obs.getObservationValues().entrySet() ){
			if("id".equals(obsValue.getKey())){
				jsonObject.addProperty(SerializationConstants.ID,obsValue.getValue());
			}else{
				jsonObject.addProperty(obsValue.getKey(),obsValue.getValue());
			}
		}				
			
		return jsonObject;
	}
	
}




	
	
