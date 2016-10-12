package org.certh.jsonqb.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.certh.jsonqb.api.RESTapi;
import org.certh.jsonqb.core.CubeSPARQL;
import org.certh.jsonqb.util.LDResource;
import org.certh.jsonqb.util.PropertyFileReader;

import com.google.gson.Gson;

@Path("/")
public class ImplRESTapi implements RESTapi {

	@Override
	public Response getDimensions(String datasetURI) {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			List<LDResource> dimensions= CubeSPARQL.getDataCubeDimensions(datasetURI, SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(dimensions);
			
			return Response.ok(json).build();
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}

	@Override
	public Response getMeasures(String datasetURI) {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			List<LDResource> measures= CubeSPARQL.getDataCubeMeasures(datasetURI, SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(measures);
			
			return Response.ok(json).build();
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}

	@Override
	public Response getAttributes(String datasetURI) {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			List<LDResource> attributes= CubeSPARQL.getDataCubeAttributes(datasetURI, SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(attributes);
			
			return Response.ok(json).build();
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}

	@Override
	public Response getDimensionValues(String datasetURI, String dimensionURI) {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			List<LDResource> dimensionValues= CubeSPARQL.getDimensionAttributeValues(dimensionURI,
					datasetURI, SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(dimensionValues);
			
			return Response.ok(json).build();
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}

	@Override
	public Response getAttributeValues(String datasetURI, String attributeURI) {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			List<LDResource> attributeValues= CubeSPARQL.getDimensionAttributeValues(attributeURI,
					datasetURI, SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(attributeValues);
			
			return Response.ok(json).build();
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}

	@Override
	public Response getDimensionLevels(String datasetURI, String dimensionURI) {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			List<LDResource> dimensionLevels= CubeSPARQL.getCubeDimensionLevels(dimensionURI,datasetURI,SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(dimensionLevels);			
			return Response.ok(json).build();
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}

	@Override
	public Response getSlice(UriInfo info) {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			MultivaluedMap<String, String> params=info.getQueryParameters();
			
			Map<String,String> fixedDims=new HashMap<String,String>();
			String datasetURI="";
			for(String param:params.keySet()){
				if(param.equals("dataset")){
					datasetURI=params.getFirst(param);
				}else{
					fixedDims.put(param, params.getFirst(param));
				}
				
			}
			
			List<LDResource> dimensions= CubeSPARQL.getDataCubeDimensions(datasetURI, SPARQLservice);
			List<String> visualDims=new ArrayList<String>();
			for(LDResource dim:dimensions){
				if(!fixedDims.keySet().contains(dim.getURI())){
					visualDims.add(dim.getURI());
				}
			}
			
			List<LDResource> measures= CubeSPARQL.getDataCubeMeasures(datasetURI, SPARQLservice);
			List<String> selectedMeasures=new ArrayList<String>();
			for(LDResource meas:measures){
				selectedMeasures.add(meas.getURI());				
			}
			
			
			List<Map<String,String>> slice=CubeSPARQL.getSlice(visualDims, fixedDims, selectedMeasures, datasetURI, SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(slice);			
			return Response.ok(json).build();
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}
}



	

	
