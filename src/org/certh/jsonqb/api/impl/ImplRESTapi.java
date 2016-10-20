package org.certh.jsonqb.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.certh.jsonqb.api.RESTapi;
import org.certh.jsonqb.core.CubeSPARQL;
import org.certh.jsonqb.util.JsonStatUtil;
import org.certh.jsonqb.util.LDResource;
import org.certh.jsonqb.util.PropertyFileReader;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import no.ssb.jsonstat.JsonStatSerializer;
import no.ssb.jsonstat.v2.Dataset;
import no.ssb.jsonstat.v2.Dimension;

@Path("/")
public class ImplRESTapi implements RESTapi {
	
	@Override
	public Response getCubes() {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			List<LDResource> cubes= CubeSPARQL.getCubes(SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(cubes);	
			System.out.println(json);
			return Response.ok(json).build();
			
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}
	

	@Override
	public Response getDimensions(String datasetURI) {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			List<LDResource> dimensions= CubeSPARQL.getDataCubeDimensions(datasetURI, SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(dimensions);
			System.out.println(json);
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
			System.out.println(json);
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
			System.out.println(json);
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
			System.out.println(json);
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
			System.out.println(json);
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
			System.out.println(json);
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
			
			//All measures
			List<LDResource> measures= CubeSPARQL.getDataCubeMeasures(datasetURI, SPARQLservice);
			List<String> selectedMeasures=new ArrayList<String>();
			for(LDResource meas:measures){
				selectedMeasures.add(meas.getURI());				
			}
			
			
			List<Map<String,String>> slice=CubeSPARQL.getSlice(visualDims, fixedDims, selectedMeasures, datasetURI, SPARQLservice);
			Gson g=new Gson();
			String json = g.toJson(slice);	
			System.out.println(json);
			return Response.ok(json).build();
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}

	@Override
	public Response getTable(UriInfo info) {
		PropertyFileReader pfr=new PropertyFileReader();
		String SPARQLservice;
		try {
			SPARQLservice = pfr.getSPARQLservice();
			MultivaluedMap<String, String> params=info.getQueryParameters();
			
			Map<String,String> fixedDims=new HashMap<String,String>();
			String datasetURI="";
			String rowDimensionURI="";
			String columnDimensionURI="";
			for(String param:params.keySet()){
				if(param.equals("dataset")){
					datasetURI=params.getFirst(param);
				}else if(param.equals("col")){
					columnDimensionURI=params.getFirst(param);
				}else if(param.equals("row")){
					rowDimensionURI=params.getFirst(param);
				}else{
					fixedDims.put(param, params.getFirst(param));
				}				
			}			
			
			List<String> visualDims=new ArrayList<String>();
			visualDims.add(rowDimensionURI);
			visualDims.add(columnDimensionURI);
			
			//All measures 
			List<LDResource> measures= CubeSPARQL.getDataCubeMeasures(datasetURI, SPARQLservice);
			List<String> selectedMeasures=new ArrayList<String>();
			Map<String,String> measureURILabelMap=new TreeMap<String,String>();
			for(LDResource meas:measures){
				selectedMeasures.add(meas.getURI());	
				measureURILabelMap.put(meas.getURI(), meas.getURIorLabel());
			}
			
			
			List<Number> table=CubeSPARQL.getTable(visualDims, fixedDims, selectedMeasures, datasetURI, SPARQLservice);
			
			Dataset.Builder jsonStatBuilder = Dataset.create();
			for(String dim:visualDims){
				List<LDResource> dimValues=CubeSPARQL.getDimensionAttributeValues(dim, datasetURI, SPARQLservice);
				Map<String,String> dimURILabelMap=new TreeMap<String,String>();
			
				for(LDResource ldr:dimValues){
					dimURILabelMap.put(ldr.getURI(), ldr.getURIorLabel());					
				}
				jsonStatBuilder.withDimension(Dimension.create(dim)
			                .withIndexedLabels(ImmutableMap.copyOf(dimURILabelMap)));		
			}
		
			jsonStatBuilder.withDimension(Dimension.create("measure")
					.withIndexedLabels(ImmutableMap.copyOf(measureURILabelMap)));	
			
			
			Dataset jsonstatDataset = jsonStatBuilder.withValues(table).build();
				
			Gson g=new Gson();
			String jsonStat = JsonStatUtil.cleanJsonStat(g.toJson(jsonstatDataset));	
			jsonStat=JsonStatUtil.jsonStatAddClass(jsonStat);
			System.out.println(jsonStat);			
			return Response.ok(jsonStat).build();
			
		} catch (IOException e) {
			return Response.serverError().build();
		}
	}
}



	

	
