package org.certh.jsonqb.api.impl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.certh.jsonqb.api.RESTapi;
import org.certh.jsonqb.core.AggregateSPARQL;
import org.certh.jsonqb.core.CubeSPARQL;
import org.certh.jsonqb.core.ExploreSPARQL;
import org.certh.jsonqb.datamodel.AggregationFunctions;
import org.certh.jsonqb.datamodel.DataCube;
import org.certh.jsonqb.datamodel.DimensionAttributeValues;
import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.LockedDimension;
import org.certh.jsonqb.datamodel.Observation;
import org.certh.jsonqb.datamodel.QBTable;
import org.certh.jsonqb.datamodel.QBTableJsonStat;
import org.certh.jsonqb.serialize.DataCubeSerializer;
import org.certh.jsonqb.serialize.LDResourceSerializer;
import org.certh.jsonqb.serialize.ListSerializer;
import org.certh.jsonqb.serialize.LockedDimensionSerializer;
import org.certh.jsonqb.serialize.ObservationListSerializer;
import org.certh.jsonqb.serialize.ObservationSerializer;
import org.certh.jsonqb.serialize.QBTableSerializer;
import org.certh.jsonqb.serialize.SerializationConstants;
import org.certh.jsonqb.util.JsonStatUtil;
import org.certh.jsonqb.util.ObservationList;
import org.certh.jsonqb.util.OrderedPowerSet;
import org.certh.jsonqb.util.PropertyFileReader;
import org.certh.jsonqb.util.QueryParameters;
import org.certh.jsonqb.util.SPARQLUtil;
import org.certh.jsonqb.util.StringUtil;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ArrayList.class, new ListSerializer(SerializationConstants.CUBES));
		gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    	   
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();	   
	    		    
	    return Response.ok(gson.toJson(cubes)).header(allowOrigin, "*").build();
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
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ArrayList.class, new ListSerializer(SerializationConstants.CUBES));
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.setPrettyPrinting();
	    
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(cubes);
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
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ArrayList.class, new ListSerializer(SerializationConstants.CUBES));
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(cubes);
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
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(DataCube.class, new DataCubeSerializer());
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();
				
		String json = gson.toJson(qb);
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
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ArrayList.class, new ListSerializer(SerializationConstants.DIMENSIONS));
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(dimensions);
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
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ArrayList.class, new ListSerializer(SerializationConstants.MEASURES));
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(measures);
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
		
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ArrayList.class, new ListSerializer(SerializationConstants.ATTRIBUTES));
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(attributes);
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

		DimensionAttributeValues jsonDimVal = new DimensionAttributeValues();
		LDResource dimension = SPARQLUtil.getLabels(dimensionURI, sparqlservice);
		jsonDimVal.setDimension(dimension);
		jsonDimVal.setValues(dimensionValues);
		
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ArrayList.class, new ListSerializer(SerializationConstants.VALUES));
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(jsonDimVal);
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
			

		DimensionAttributeValues jsonDimVal = new DimensionAttributeValues();
		LDResource attribute = SPARQLUtil.getLabels(attributeURI, sparqlservice);
		jsonDimVal.setDimension(attribute);
		jsonDimVal.setValues(attributeValues);
		
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ArrayList.class, new ListSerializer(SerializationConstants.VALUES));
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();	   
	    
	    // Format to JSON
	    String json = gson.toJson(jsonDimVal);
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
		
		DimensionAttributeValues jsonDimVal = new DimensionAttributeValues();
		
		LDResource dimension = SPARQLUtil.getLabels(dimensionURI, sparqlservice);
		jsonDimVal.setDimension(dimension);
		jsonDimVal.setValues(dimensionLevels);
		
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ArrayList.class, new ListSerializer(SerializationConstants.VALUES));
	    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();	   
		    
	    // Format to JSON
	    String json = gson.toJson(jsonDimVal);
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
		Map<String, String> fixedDims = qp.getFixedValues();
		
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

		ObservationList slice = CubeSPARQL.getSlice(visualDims, fixedDims, selectedMeasures, datasetURI,
				sparqlservice);
		
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ObservationList.class, new ObservationListSerializer());
	    gsonBuilder.registerTypeAdapter(Observation.class, new ObservationSerializer());
	    gsonBuilder.setPrettyPrinting();
		
	    Gson gson = gsonBuilder.create();	
		
		String json = gson.toJson(slice);		
		return Response.ok(json).header(allowOrigin, "*").build();

	}

	//MERGE WITH getTable
	//Take format as parameter = [jsonstat,jsonqb]
	@Override
	public Response getJsonStatTable(UriInfo info) {
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
		String rowDimensionURIs = qp.getRowDimensionURIs().get(0);
		String columnDimensionURIs =qp.getColumnDimensionURIs().get(0);
		String measure = qp.getMeasureURI();		
		Map<String, String> fixedDims = qp.getFixedValues();		

		List<String> visualDims = new ArrayList<>();
		visualDims.add(rowDimensionURIs);
		visualDims.add(columnDimensionURIs);

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

		QBTableJsonStat table = CubeSPARQL.getJsonStatTable(visualDims, fixedDims, selectedMeasures, datasetURI, sparqlservice);
		Dataset.Builder jsonStatBuilder = Dataset.create();

		for (String dim : visualDims) {
			Map<String, String> dimURILabelMap = new LinkedHashMap<>();
			List<LDResource> tabledimValues = table.getDimVals().get(dim);		
			for (LDResource ldr : tabledimValues) {				
				dimURILabelMap.put(ldr.getURI(), ldr.getLabel("en"));			
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
			List<String> rowDimensions = qp.getRowDimensionURIs();
			List<String> columnDimensions =qp.getColumnDimensionURIs();
			String measure = qp.getMeasureURI();		
			Map<String, String> fixedDims = qp.getFixedValues();		

			//List<String> visualDims = new ArrayList<>();
			//visualDims.add(rowDimensionURIs.get(0));
			//visualDims.add(columnDimensionURIs.get(0));

			List<String> selectedMeasures = new ArrayList<>();
			List<LDResource> measures = CubeSPARQL.getDataCubeMeasures(datasetURI, sparqlservice);

		//	Map<String, String> measureURILabelMap = new TreeMap<>();
			for (LDResource meas : measures) {
				// if there is a selected measure
				if (!"".equals(measure)) {
					if (meas.getURI().equals(measure)) {
						selectedMeasures.add(meas.getURI());
				//		measureURILabelMap.put(meas.getURI(), meas.getURIorLabel());
					}
				// if there is no selected measure, assume all measures are selected
				} else {
					selectedMeasures.add(meas.getURI());
				//	measureURILabelMap.put(meas.getURI(), meas.getURIorLabel());
				}
			}

			QBTable table = CubeSPARQL.getTable(rowDimensions,columnDimensions, fixedDims, selectedMeasures, datasetURI, sparqlservice);
			
			//Create GsonBuilder
			GsonBuilder gsonBuilder = new GsonBuilder();
		    gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
		    gsonBuilder.registerTypeAdapter(LockedDimension.class, new LockedDimensionSerializer());
		    gsonBuilder.registerTypeAdapter(QBTable.class, new QBTableSerializer());
		    gsonBuilder.setPrettyPrinting();
		    Gson gson = gsonBuilder.create();	   
		    
		    // Format to JSON
		    String json = gson.toJson(table);
		    return Response.ok(json).header(allowOrigin, "*").build();
			
	}

	@Override
	public Response createAggregations(UriInfo info) {
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
		Map<String, String> tempMapMeasureToAggregationFunction = qp.getFixedValues();	
		
		
		Map<String, AggregationFunctions> mapMeasureToAggregationFunction=new HashMap<>();
		for(Entry<String, String> entry:tempMapMeasureToAggregationFunction.entrySet()){
			mapMeasureToAggregationFunction.put(entry.getKey(), AggregationFunctions.valueOf(entry.getValue()));
		}
		
		
		List<LDResource> cubeDimensions=CubeSPARQL.getDataCubeDimensions(datasetURI, sparqlservice);
		LDResource cubeGraph=CubeSPARQL.getCubeGraph(datasetURI, sparqlservice);
		// Create new aggregation set
		String aggregationSetURI = AggregateSPARQL.createNewAggregationSet(
				cubeGraph.getURI(),sparqlservice);

		// Attach original cube to aggregation set
		AggregateSPARQL.attachCube2AggregationSet(aggregationSetURI, 
				cubeGraph.getURI(), datasetURI,sparqlservice);

		OrderedPowerSet<LDResource> ops = new OrderedPowerSet<>((ArrayList<LDResource>) cubeDimensions);

		// calculate all dimension combinations
		for (int j = 1; j < cubeDimensions.size(); j++) {
			List<LinkedHashSet<LDResource>> dimensionPermutations = ops.getPermutationsList(j);
			for (Set<LDResource> perm : dimensionPermutations) {
				List<String> listOfDims=StringUtil.ldResourceSet2StringList(perm) ;
				// create new cube of aggregation set
				String newCubeURI = AggregateSPARQL.createCubeForAggregationSet(datasetURI,
						cubeGraph.getURI(), listOfDims, mapMeasureToAggregationFunction, 
						aggregationSetURI,sparqlservice);		
				LOGGER.log(Level.INFO, "Aggregated cube created "+ newCubeURI);
			}			
		
		}
		
		
		return Response.ok().header(allowOrigin, "*").build();
	}

	@Override
	public Response getCubeOfAggregationSet(String datasetURI, List<String> dimension) {
		PropertyFileReader pfr = new PropertyFileReader();
		String sparqlservice;
		try {
			sparqlservice = pfr.getSPARQLservice();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return Response.serverError().build();
		}		
		
		LDResource cubeOfAggSet =ExploreSPARQL.getCubeOfAggregationSet(datasetURI, dimension, sparqlservice);
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LDResource.class, new LDResourceSerializer());
	    	   
	    gsonBuilder.setPrettyPrinting();
	    Gson gson = gsonBuilder.create();	   
	    		    
	    return Response.ok(gson.toJson(cubeOfAggSet)).header(allowOrigin, "*").build();
	}

	
}
