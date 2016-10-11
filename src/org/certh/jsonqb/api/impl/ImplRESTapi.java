package org.certh.jsonqb.api.impl;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
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

	

}



	

	
