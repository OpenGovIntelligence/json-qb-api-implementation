package org.certh.jsonqb.serialize;

import org.certh.jsonqb.datamodel.DataCube;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class DataCubeSerializer implements JsonSerializer<DataCube> {

	@Override
	public JsonElement serialize(DataCube qb,  Type typeOfSrc,  JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty(SerializationConstants.ID, qb.getURI());
		
		if(!qb.getTitles().isEmpty()){
			jsonObject.addProperty(SerializationConstants.LABEL, qb.getTitles().get(0).getLabel());
			
		}
		
		if(!qb.getDesctiptions().isEmpty()){
			jsonObject.addProperty(SerializationConstants.DESCRIPTION, qb.getDesctiptions().get(0).getLabel());
			
		}
		
		if(!qb.getComments().isEmpty()){
			jsonObject.addProperty(SerializationConstants.COMMENT, qb.getComments().get(0).getLabel());
			
		}
		
		if(!qb.getSubjects().isEmpty()){
			jsonObject.addProperty(SerializationConstants.SUBJECT, qb.getSubjects().get(0));
			
		}
		
		if(qb.getIssued()!=null){
			jsonObject.addProperty(SerializationConstants.ISSUED, qb.getIssued());
			
		}
		
		if(qb.getModified()!=null){
			jsonObject.addProperty(SerializationConstants.MODIFIED, qb.getModified());
			
		}
		
		if(qb.getPublisher()!=null){
			jsonObject.addProperty(SerializationConstants.PUBLISHER, qb.getPublisher());
			
		}
		
		if(qb.getLicense()!=null){
			jsonObject.addProperty(SerializationConstants.LICENSE, qb.getLicense());
			
		}		
		
		return jsonObject;
	}

}