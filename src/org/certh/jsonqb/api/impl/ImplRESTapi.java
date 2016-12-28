package org.certh.jsonqb.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.certh.jsonqb.api.RESTapi;
import org.certh.jsonqb.core.CubeSPARQL;
import org.certh.jsonqb.core.ExploreSPARQL;
import org.certh.jsonqb.datamodel.DataCube;
import org.certh.jsonqb.datamodel.DimensionValues;
import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.Observation;
import org.certh.jsonqb.datamodel.QBTableJsonStat;
import org.certh.jsonqb.util.JsonStatUtil;
import org.certh.jsonqb.util.PropertyFileReader;
import org.certh.jsonqb.util.QueryParameters;
import org.certh.jsonqb.util.SPARQLUtil;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import no.ssb.jsonstat.v2.Dataset;
import no.ssb.jsonstat.v2.Dimension;

@Path("/")
public class ImplRESTapi implements RESTapi {

	private static String allowOrigin = "Access-Control-Allow-Origin";
	private static final Logger LOGGER = Logger.getLogger(ImplRESTapi.class.getName());

	@Override
	public Response getAllCubes() {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		List<LDResource> cubes = ExploreSPARQL.getAllCubes(sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(cubes);
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	@Override
	public Response getMaxAggregationSetCubes() {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		List<LDResource> cubes = ExploreSPARQL.getMaxAggregationSetCubes(sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(cubes);
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	@Override
	public Response getMaxAggregationSetCubesAndCubesWithoutAggregation() {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		List<LDResource> cubes = ExploreSPARQL.getMaxAggregationSetCubesAndCubesWithoutAggregation(sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(cubes);
		return Response.ok(json).header(allowOrigin, "*").build();

	}
	
	@Override
	public Response getDataCubeMetadata(String datasetURI) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		DataCube qb = CubeSPARQL.getCubeMetaData(datasetURI, sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(qb);
		return Response.ok(json).header(allowOrigin, "*").build();
	}

	@Override
	public Response getDimensions(String datasetURI) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		List<LDResource> dimensions = CubeSPARQL.getDataCubeDimensions(datasetURI, sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(dimensions);
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	@Override
	public Response getMeasures(String datasetURI) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		List<LDResource> measures = CubeSPARQL.getDataCubeMeasures(datasetURI, sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(measures);
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	@Override
	public Response getAttributes(String datasetURI) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		List<LDResource> attributes = CubeSPARQL.getDataCubeAttributes(datasetURI, sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(attributes);
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	@Override
	public Response getDimensionValues(String datasetURI, String dimensionURI) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		List<LDResource> dimensionValues = CubeSPARQL.getDimensionAttributeValues(dimensionURI, datasetURI,
				sparqlservice);

		DimensionValues jsonDimVal = new DimensionValues();
		LDResource dimension = SPARQLUtil.getLabels(dimensionURI, sparqlservice);
		jsonDimVal.setDimension(dimension);
		jsonDimVal.setValues(dimensionValues);
		Gson g = new Gson();
		String json = g.toJson(jsonDimVal);
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	@Override
	public Response getAttributeValues(String datasetURI, String attributeURI) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		List<LDResource> attributeValues = CubeSPARQL.getDimensionAttributeValues(attributeURI, datasetURI,
				sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(attributeValues);
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	@Override
	public Response getDimensionLevels(String datasetURI, String dimensionURI) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		List<LDResource> dimensionLevels = CubeSPARQL.getCubeDimensionLevels(dimensionURI, datasetURI, sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(dimensionLevels);
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	@Override
	public Response getSlice(UriInfo info) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}
		
		QueryParameters qp=new QueryParameters(info.getQueryParameters());
		String datasetURI = qp.getDatasetURI();
		String measure = qp.getMeasureURI();
		Map<String, String> fixedDims = qp.getFixedDims();
		
		List<LDResource> dimensions = CubeSPARQL.getDataCubeDimensions(datasetURI, sparqlservice);
		List<String> visualDims = new ArrayList<>();
		for (LDResource dim : dimensions) {
			if (!fixedDims.keySet().contains(dim.getURI())) {
				visualDims.add(dim.getURI());
			}
		}

		List<String> selectedMeasures = new ArrayList<>();
		if (!"".equals(measure)) {
			selectedMeasures.add(measure);
		} else {
			List<LDResource> measures = CubeSPARQL.getDataCubeMeasures(datasetURI, sparqlservice);
			for (LDResource meas : measures) {
				selectedMeasures.add(meas.getURI());
			}
		}

		List<Observation> slice = CubeSPARQL.getSlice(visualDims, fixedDims, selectedMeasures, datasetURI,
				sparqlservice);
		Gson g = new Gson();
		String json = g.toJson(slice);		
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	@Override
	public Response getTable(UriInfo info) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
	
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}

		QueryParameters qp=new QueryParameters(info.getQueryParameters());
				
		String datasetURI=qp.getDatasetURI();
		String rowDimensionURI = qp.getRowDimensionURI();
		String columnDimensionURI =qp.getColumnDimensionURI();
		String measure = qp.getMeasureURI();		
		Map<String, String> fixedDims = qp.getFixedDims();		

		List<String> visualDims = new ArrayList<>();
		visualDims.add(rowDimensionURI);
		visualDims.add(columnDimensionURI);

		List<String> selectedMeasures = new ArrayList<>();
		List<LDResource> measures = CubeSPARQL.getDataCubeMeasures(datasetURI, sparqlservice);

		Map<String, String> measureURILabelMap = new TreeMap<>();
		for (LDResource meas : measures) {
			// if there is a selected measure
			if (!"".equals(measure)) {
				if (meas.getURI().equals(measure)) {
					selectedMeasures.add(meas.getURI());
					measureURILabelMap.put(meas.getURI(), meas.getURIorLabel());
				}
			// if there is no selected measure, assume all measures are selected
			} else {
				selectedMeasures.add(meas.getURI());
				measureURILabelMap.put(meas.getURI(), meas.getURIorLabel());
			}
		}

		QBTableJsonStat table = CubeSPARQL.getTable(visualDims, fixedDims, selectedMeasures, datasetURI, sparqlservice);
		Dataset.Builder jsonStatBuilder = Dataset.create();

		for (String dim : visualDims) {
			Map<String, String> dimURILabelMap = new LinkedHashMap<>();
			List<LDResource> tabledimValues = table.getDimVals().get(dim);		
			for (LDResource ldr : tabledimValues) {				
				dimURILabelMap.put(ldr.getURI(), ldr.getURIorLabel());			
			}

			LDResource dimLDR = SPARQLUtil.getLabels(dim, sparqlservice);
			jsonStatBuilder.withDimension(Dimension.create(dim).withLabel(dimLDR.getURIorLabel())
					.withIndexedLabels(ImmutableMap.copyOf(dimURILabelMap)));
	
		}		

		Dataset jsonstatDataset = jsonStatBuilder.withValues(table.getMeasures()).build();
		Gson g = new Gson();
		String jsonStat = JsonStatUtil.cleanJsonStat(g.toJson(jsonstatDataset));
		jsonStat = JsonStatUtil.jsonStatAddClass(jsonStat);
		return Response.ok(jsonStat).header(allowOrigin, "*").build();

	}

	
}
