package org.certh.jsonqb.serialize;

import java.lang.reflect.Type;
import java.util.List;

import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.LockedDimension;
import org.certh.jsonqb.datamodel.QBTable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class QBTableSerializer implements JsonSerializer<QBTable> {
	
	@Override
	public JsonElement serialize(QBTable qbtable,  Type typeOfSrc,  JsonSerializationContext context) {
		
		//****  create overall json object ****//
		JsonObject jsonObject = new JsonObject();
		
		//Add structure to overall json object
		jsonObject.add(SerializationConstants.STRUCTURE, createStructure(qbtable, context));
		
		//Add headers to the overall json object
		jsonObject.add(SerializationConstants.HEADERS, createHeader(qbtable));
		
		//Add data to the overall json object
		jsonObject.add(SerializationConstants.DATA, createData(qbtable));
		
		return jsonObject;
	}
	
	private JsonObject createStructure(QBTable qbtable,  JsonSerializationContext context){
		
		/***************************
		 *   Create structure      *
		 ***************************/
		
		JsonObject structure = new JsonObject();		
		
		//Add free dimensions
		if(!qbtable.getFreeDimensions().isEmpty()){
			JsonObject freeDims = new JsonObject();
			
			for(LDResource fDim: qbtable.getFreeDimensions()){
				freeDims.add(fDim.getLastPartOfURI(),context.serialize(fDim));
			}
			
			structure.add(SerializationConstants.FREE_DIMENSIONS, freeDims);
		}
		
		//Add locked dimensions
		if(!qbtable.getLockedDimensions().isEmpty()){		
			JsonObject lockedDims = new JsonObject();
			
			for(LockedDimension lockedDim: qbtable.getLockedDimensions()){
				lockedDims.add(lockedDim.getLastPartOfURI(),context.serialize(lockedDim));
			}
			
			structure.add(SerializationConstants.LOCKED_DIMENSIONS, lockedDims);
		}
		
		//Add dimension values
		if(!qbtable.getDimensionValues().isEmpty()){
			JsonObject allDimensionValues = new JsonObject();
			
			for(LDResource dim: qbtable.getDimensionValues().keySet()){
				JsonObject dimValues = new JsonObject();
				List<LDResource> values=qbtable.getDimensionValues().get(dim);
				for(LDResource val:values){
					dimValues.add(val.getLastPartOfURI(), context.serialize(val));
				}
				allDimensionValues.add(dim.getLastPartOfURI(), dimValues);
			}
			
			structure.add(SerializationConstants.DIMENSION_VALUES, allDimensionValues);
		}
		
		return structure;
		
	}
	
	private JsonObject createHeader(QBTable qbtable){
		
		/***************************
		 *   Create headers        *
		 ***************************/
		
		JsonObject headers = new JsonObject();

		if(!qbtable.getColumnHierarchy().isEmpty()){
			JsonArray columnHierarchy=new JsonArray();
			JsonObject columns = new JsonObject();
			for(LDResource col :qbtable.getColumnHierarchy()){
				JsonArray colValuesArray = new JsonArray();
				List<LDResource> values=qbtable.getDimensionValues().get(col);
				for(LDResource val: values){
					JsonPrimitive jsonValue = new JsonPrimitive(val.getLastPartOfURI());
					colValuesArray.add(jsonValue);
				}
				columns.add(col.getLastPartOfURI(), colValuesArray);
				
				JsonPrimitive colName = new JsonPrimitive(col.getLastPartOfURI());
				columnHierarchy.add(colName);
			}		
			headers.add(SerializationConstants.COLUMNS, columns);
			if(columnHierarchy.size()>1){
				headers.add(SerializationConstants.COLUMN_HIERARCHY, columnHierarchy);
			}
		}
		
		if(!qbtable.getRowHierarchy().isEmpty()){
			JsonArray rowHierarchy=new JsonArray();
			JsonObject rows = new JsonObject();
			for(LDResource row :qbtable.getRowHierarchy()){
				JsonArray rowValuesArray = new JsonArray();
				List<LDResource> values=qbtable.getDimensionValues().get(row);
				for(LDResource val: values){
					JsonPrimitive jsonValue = new JsonPrimitive(val.getLastPartOfURI());
					rowValuesArray.add(jsonValue);
				}
				rows.add(row.getLastPartOfURI(), rowValuesArray);
				
				JsonPrimitive rowName = new JsonPrimitive(row.getLastPartOfURI());
				rowHierarchy.add(rowName);
			}		
			headers.add(SerializationConstants.ROWS, rows);
			
			if(rowHierarchy.size()>1){
				headers.add(SerializationConstants.ROW_HIERARCHY, rowHierarchy);
			}
		}
		
		return headers;
	}
	
	
	private int arrayIndex=0;
	
	private JsonArray createData(QBTable qbtable){
		JsonArray data=new JsonArray();
		
		if(!qbtable.getRowHierarchy().isEmpty()&&!qbtable.getColumnHierarchy().isEmpty()){
			//1 dim at ROWS
			LDResource rowDim =qbtable.getRowHierarchy().get(0);
			List<LDResource> rowValues=qbtable.getDimensionValues().get(rowDim);
			//For each row value create row data
			for(LDResource val:rowValues){
				data.add(createRow(qbtable, 0));
			}
		}
		
		return data;
		
	}
	
	
	private JsonArray createRow(QBTable qbtable, int dimIndex){
		if(dimIndex==qbtable.getColumnHierarchy().size()-1){
			JsonArray dataRow=new JsonArray();
			LDResource dimensionUsed=qbtable.getColumnHierarchy().get(dimIndex);
			List<LDResource> valuesOfDimUsed= qbtable.getDimensionValues().get(dimensionUsed);
			for(int i=0;i<valuesOfDimUsed.size();i++){
				Number cell=qbtable.getData().get(arrayIndex);
				if(cell==null){
					dataRow.add(JsonNull.INSTANCE);
				}else{
					JsonPrimitive jsoncell = new JsonPrimitive(cell);
					dataRow.add(jsoncell);
				}
				
				arrayIndex++;
				
			}
			return dataRow;
		}else{
			JsonArray dataRow=new JsonArray();
			LDResource dimensionUsed=qbtable.getColumnHierarchy().get(dimIndex);
			List<LDResource> valuesOfDimUsed= qbtable.getDimensionValues().get(dimensionUsed);
			int tmpIndex=dimIndex+1;
			for(int i=0;i<valuesOfDimUsed.size();i++){
				dataRow.add(createRow(qbtable, tmpIndex));
			}
			return dataRow;
		}
	} 

}



