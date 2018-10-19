package org.certh.jsonqb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Optional;

import org.certh.jsonqb.datamodel.AggregationFunctions;
import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.Label;
import org.certh.jsonqb.util.StringUtil;
import org.certh.jsonqb.util.SPARQLUtil;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class AggregateSPARQL {
	
	private AggregateSPARQL() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}
		
	public static String createNewAggregationSet(String aggregationGraph, String sparqlService) {
		
		String aggregationSetURI=SPARQLUtil.createRandomURI("http://opencube-project.eu/aggregationSet",
				aggregationGraph,sparqlService);
				
		// INSERT NEW AGGREGATION SET
		String insertAggregationSetQuery = SPARQLconstants.PREFIX+ "INSERT DATA {";
		
		if (aggregationGraph != null) {
			insertAggregationSetQuery += "GRAPH <" + aggregationGraph + "> {";
		}

		insertAggregationSetQuery += "<"+aggregationSetURI+ "> rdf:type opencube:AggregationSet.} ";

		if (aggregationGraph != null) {
			insertAggregationSetQuery += "}";
		}
		
		QueryExecutor.executeUPDATE(insertAggregationSetQuery,sparqlService);
		return aggregationSetURI;

	}

	public static void attachCube2AggregationSet(String aggregationSetURI,
			String aggregationSetGraph, String datacubeURI, String sparqlService) {

		// Attach cube 2 aggregation set
		String attachCube2aggregationSetQuery = SPARQLconstants.PREFIX + "INSERT DATA {";
		
		// if aggregation set graph defined
		if (aggregationSetGraph != null) {
			attachCube2aggregationSetQuery += "GRAPH <" + aggregationSetGraph	+ "> {";
		}	
		
		attachCube2aggregationSetQuery += "<"+datacubeURI + "> opencube:aggregationSet <"
				+ aggregationSetURI + "> .} ";

		if (aggregationSetGraph != null) {
			attachCube2aggregationSetQuery += "}";
		}

		QueryExecutor.executeUPDATE(attachCube2aggregationSetQuery,sparqlService);
	}

	//Get ALL the dimensions that appear at ALL the cubes of an AggregationSet
	public static List<LDResource> getAggegationSetDims(String cubeURI, String sparqlService) {
		
		String getAggegationSetDimsQuery = SPARQLconstants.PREFIX 
				+ "select  distinct ?res where {" 
				+ "<"+ cubeURI+ "> opencube:aggregationSet ?set."
				+ "?dataset opencube:aggregationSet ?set."
				+ "?dataset qb:structure ?dsd."
				+ "?dsd qb:component ?comp."
				+ "?comp qb:dimension ?res.}";
		
		TupleQueryResult res = QueryExecutor.executeSelect(getAggegationSetDimsQuery, sparqlService);

		List<LDResource> aggegationSetDims = SPARQLresultTransformer.toLDResourceListWithLabels(res, sparqlService);
		Collections.sort(aggegationSetDims);
		return aggegationSetDims;	
	}

	public static String createCubeForAggregationSet(String originalCubeURI, String originalCubeGraph,
			List<String> dimensions, Map<String, AggregationFunctions> mapMeasureOperation,
			String aggregationSetURI, String sparqlService) {

		
		String aggregatedCubeURI= createDSDForAggregatedCube(originalCubeURI, originalCubeGraph, dimensions,
				mapMeasureOperation, aggregationSetURI, sparqlService);
		
		TupleQueryResult res=getAggregatedObservations(originalCubeURI, dimensions, mapMeasureOperation, sparqlService);
		
		insertAggregatedObservations2Cube(res, aggregatedCubeURI,originalCubeGraph, dimensions,
				mapMeasureOperation.keySet(), sparqlService);
		
		return aggregatedCubeURI;
	}

	private static String createDSDForAggregatedCube(String originalCubeURI, String originalCubeGraph,
			List<String> dimensions, Map<String, AggregationFunctions> mapMeasureOperation,
			String aggregationSetURI, String sparqlService){		
		
		Optional<String> originalcubegraph=Optional.ofNullable(originalCubeGraph);
		
		//New Cube URI = originalCubeURI + _dim1 + _dim2 ...					
		StringBuilder newCubeURI =new StringBuilder(originalCubeURI);
		for(String dim:dimensions){
			newCubeURI.append("_"+dim.substring(Math.max(dim.lastIndexOf('#'),dim.lastIndexOf('/')) + 1, dim.length()));
		}						
		
		// Add new DSD
		StringBuilder createNewDsdQuery =new StringBuilder(SPARQLconstants.PREFIX+" INSERT DATA {");
		
		originalcubegraph.ifPresent(graph->createNewDsdQuery.append("GRAPH <" + graph + "> {"));
				
		//Add labels to new cube
		LDResource originalCube= SPARQLUtil.getLabels(originalCubeURI, sparqlService);	
		for(Label l:originalCube.getLabels()){
			createNewDsdQuery.append("<"+newCubeURI+ "> rdfs:label \""+l.getLabel()+ "(");								
			for(String dim:dimensions){
				 createNewDsdQuery.append(dim.substring(Math.max(dim.lastIndexOf('#'),dim.lastIndexOf('/')) + 1, dim.length())+",");
			}
				
			createNewDsdQuery.append(")\"");
							
			//The lang should be only 2 letters to be valid
			if(l.getLanguage()!=null&&l.getLanguage().length()==2){
				createNewDsdQuery.append("@"+l.getLanguage());
			}	
			createNewDsdQuery.append(".");
		}
		
		// Attach Cube to Aggregation Set
		createNewDsdQuery.append("<"+newCubeURI + "> rdf:type qb:DataSet."
				+ "<"+newCubeURI + "> opencube:aggregationSet <" + aggregationSetURI + "> .");
		
		
		// create random DSD
		String newDsdURI = SPARQLUtil.createRandomURI("http://opencube-project.eu/dsd",
				originalcubegraph.get(),sparqlService);
		
		createNewDsdQuery.append("<"+newCubeURI + "> qb:structure <" + newDsdURI + ">."
				+ "<"+newDsdURI  + "> rdf:type qb:DataStructureDefinition.");

		// Add dimensions to DSD
		// And attach a code list to componentSpecification with the codesUsed
		int i=1;
		for (String dim : dimensions) {
			String newComponentSpecificationURI = newCubeURI+"/componentSpecification_"+ i;
			createNewDsdQuery.append( "<"+newDsdURI + "> qb:component <" + newComponentSpecificationURI + ">."
					+ "<"+newComponentSpecificationURI + "> qb:dimension <"	+ dim + ">.");
			
						
			LDResource codesUsedCodelist=CubeSPARQL.getDimensionCodesUsedCodelist(dim, originalCubeURI, sparqlService);
			//If the original cube has a codesUsed codelist use it at the Aggregated Cube
			if(codesUsedCodelist!=null) {
				createNewDsdQuery.append( "<"+newComponentSpecificationURI+ "> <"+
						SPARQLconstants.CODESUSED_PREDICATE +"> <"+codesUsedCodelist.getURI()+">.");
			}
			
			/*else {
				String newCodesUsedCodelistURI= newCubeURI+"/codes-used_"+i+"/";
				createNewDsdQuery.append( "<"+newComponentSpecificationURI+ "> <"+
							SPARQLconstants.CODESUSED_PREDICATE +"> <"+newCodesUsedCodelistURI+">.");
				createNewDsdQuery.append( "<"+newCodesUsedCodelistURI+ "> a skos:Collection."); 
				
				List<LDResource> dimensionValues=CubeSPARQL.getDimensionAttributeValues(dim, originalCubeURI, sparqlService);
				
				for(LDResource ldr :dimensionValues) {
					createNewDsdQuery.append( "<"+newCodesUsedCodelistURI+ "> skos:member <"+ldr.getURI()+">.");				
				}
			}*/
			
			i++;
		}

		// Add measures to DSD
		for (String m : mapMeasureOperation.keySet()) {			
			String newComponentSpecificationURI = newCubeURI+"/componentSpecification_"+ i;
			createNewDsdQuery.append("<"+newDsdURI + "> qb:component <"+ newComponentSpecificationURI + ">."
						+ "<"+newComponentSpecificationURI + "> qb:measure <" + m+ ">.");
			i++;
		}	
		
		
		originalcubegraph.ifPresent(graph->createNewDsdQuery.append("}"));
		
		createNewDsdQuery.append("}");
		System.out.println(createNewDsdQuery);

		QueryExecutor.executeUPDATE(createNewDsdQuery.toString(),sparqlService);
		
		return newCubeURI.toString(); 
	}
	
	private static TupleQueryResult getAggregatedObservations(String originalCubeURI,
			List<String> dimensions, Map<String, AggregationFunctions> mapMeasureOperation,
			String sparqlService){
		
		// Query to get aggregated observations
		StringBuilder aggregatedObservationsQuery = new StringBuilder();
		
		aggregatedObservationsQuery.append(SPARQLconstants.PREFIX+ "Select ");
		
		
		// Add dimension variables to query
		aggregatedObservationsQuery.append(StringUtil.addVariables("?dim", dimensions.size()));
		
		// Add measures to query
		int i=1;
		for (Entry<String, AggregationFunctions> m : mapMeasureOperation.entrySet()) {
			aggregatedObservationsQuery.append("("+m.getValue().toString()
					+"(xsd:decimal(?measure"+i+"))as ?aggregatedMeasure"+i+") ");
			i++;
		}
		
		aggregatedObservationsQuery.append("where{ ?obs qb:dataSet <" + originalCubeURI+ ">.");

		i = 1;
		for (String dim : dimensions) {
			aggregatedObservationsQuery.append("?obs <" + dim + "> ?dim"	+ i + ".");
			i++;
		}
				
		i=1;
		for (String m : mapMeasureOperation.keySet()) {
			aggregatedObservationsQuery.append("OPTIONAL {?obs <" + m + "> ?measure"+ i + ".}");
			i++;
		}

		/*		 
		For the dimensions not used at the aggregated cube, if the they have many hierarchical levels
		we should aggregate observations from only one level 
		
		*/
		
		//Get all the dimensions of the original cubes
		List<LDResource> originalCubeAllDimensions=CubeSPARQL.getDataCubeDimensions(originalCubeURI, sparqlService);
		
		//contains the dimensions that are not used at the Aggregated cube (allDimensions - usedDimensions)
		List<String> dimensionsNotUsedAtAggregatedCube=new ArrayList<>();
		for(LDResource ldr:originalCubeAllDimensions){
			dimensionsNotUsedAtAggregatedCube.add(ldr.getURI());
		}
		
		dimensionsNotUsedAtAggregatedCube.removeAll(dimensions);
			
				
		//if there is a dimension not used that has levels
		//then aggregate only observations of the same level
		i=1;
		for(String dim:dimensionsNotUsedAtAggregatedCube){
			List<LDResource> dimLevels=CubeSPARQL.getCubeDimensionLevels(dim, originalCubeURI, sparqlService);		
			if(!dimLevels.isEmpty()){
				aggregatedObservationsQuery.append("?obs <" + dim + "> ?levelvalue"+ i + ".");
				aggregatedObservationsQuery.append("<"+dimLevels.get(0).getURI()+"> skos:member ?levelvalue"+ i+".");
				
				i++;
			}
		}				
		
		//Group by dimensions to get aggregated value
		aggregatedObservationsQuery.append("} GROUP BY");
			
		aggregatedObservationsQuery.append(StringUtil.addVariables("?dim", dimensions.size()));
		
		return QueryExecutor.executeSelect(aggregatedObservationsQuery.toString(),sparqlService);
	}

	private static void insertAggregatedObservations2Cube(TupleQueryResult res,
			String aggregatedCubeURI, String graph2Insert, List<String> dimensions,
			Set<String> measures, String sparqlService){
		
		Optional<String> graphInsert=Optional.ofNullable(graph2Insert);
		// Store aggregated results
		// Need to store and process them latter. Otherwise time exceptions occur
		List<BindingSet> bs = new ArrayList<>();

		while (res.hasNext()) {
			bs.add(res.next());
		}

		// Create new observations for the new aggregated cube. Insert cubes in sets of 100
		int count = 0;	
		
		//counter for observation ID 
		int obscount=1;
		
		// Create new cube
		StringBuilder addAggregatedObservations2Cube = new  StringBuilder(SPARQLconstants.PREFIX+ "INSERT DATA  {");
		
		graphInsert.ifPresent(graph->addAggregatedObservations2Cube.append("GRAPH <"+graph+"> { "));
				
		for (BindingSet bindingSet : bs) {
			// Observation URI
			String newObservationURI = "<"+aggregatedCubeURI+"/obs_"+ obscount+">";
			obscount++;
				
			addAggregatedObservations2Cube.append(newObservationURI + " rdf:type qb:Observation." 
						+ newObservationURI 	+ " qb:dataSet <" + aggregatedCubeURI + ">.");
			int i = 1;
			for (String dim : dimensions) {
				String dimValue = bindingSet.getValue("dim" + i).stringValue();
				addAggregatedObservations2Cube.append(newObservationURI + " <" + dim + "> "
						+SPARQLUtil.toTripleValue(dimValue) + ".");				
				i++;
			}

			i=1;
			for (String m : measures) {
				Value measure=bindingSet.getValue("aggregatedMeasure"+i);
				if(measure!=null) {
					addAggregatedObservations2Cube.append(newObservationURI + " <"+ m + "> "
													+SPARQLUtil.toTripleValue(measure.stringValue())+ ".");
				}
				i++;
			}			

			// If |observations|= 100 execute insert
			// Cannot insert all the observations with one query - too long
			if (count == 100) {
				count = 0;
						
				graphInsert.ifPresent(graph->addAggregatedObservations2Cube.append("}"));
								
				addAggregatedObservations2Cube.append("}");
					
				QueryExecutor.executeUPDATE(addAggregatedObservations2Cube.toString(),sparqlService);

				// Initialize query to insert more observations
				addAggregatedObservations2Cube.setLength(0);
				addAggregatedObservations2Cube.append(SPARQLconstants.PREFIX+ "INSERT DATA  {");

				graphInsert.ifPresent(graph->addAggregatedObservations2Cube.append("GRAPH <"+graph+"> { "));
							
			} else {
				count++;
			}
		}	

		// If there are observations not yet inserted
		if (count > 0) {
			
			graphInsert.ifPresent(graph->addAggregatedObservations2Cube.append("}"));
			
			addAggregatedObservations2Cube.append("}");		
			QueryExecutor.executeUPDATE(addAggregatedObservations2Cube.toString(),sparqlService);
		}		
	}
	
		
	
	public static int createRollUpAggregations(String cubeURI, String graph2Insert,
			Map<String, AggregationFunctions> mapMeasureOperation, String sparqlService){
		
		TupleQueryResult res=getAggregatedRollUpObservations(cubeURI,
				 mapMeasureOperation, sparqlService);
		
		return insertAggregatedRollUpObservations2Cube(res, cubeURI, graph2Insert,
				mapMeasureOperation.keySet(), sparqlService);
		
		
	}
	
	//TO DO SUPPORT MULTIPLE ROLLUP DIMENSIONS
	private static TupleQueryResult getAggregatedRollUpObservations(String cubeURI,
			Map<String, AggregationFunctions> mapMeasureOperation, String sparqlService){
		
		List<LDResource> dimensions=CubeSPARQL.getDataCubeDimensions(cubeURI, sparqlService);
		
		List<String> dimensionsWithoutRollUp=new ArrayList<>();
		List<String> rollUpDimensions=new ArrayList<>();
		
		//Separate dimensions with hierarchical structure
		//and without hierarchical structure
		dimensions.forEach(dim-> { 
			List<LDResource> dimLevels=CubeSPARQL.getDimensionLevels(dim.getURI(), sparqlService);						
			if(dimLevels.isEmpty()){
				dimensionsWithoutRollUp.add(dim.getURI());
			}else{
				rollUpDimensions.add(dim.getURI());
			}});
	
		
		//Get original cube graph
		Optional<LDResource> cubegraph=Optional.ofNullable(CubeSPARQL.getCubeGraph(cubeURI, sparqlService));
		
		// Query to get ROLL UP aggregated observations
		StringBuilder rollUpAggregatedObsQuery = new StringBuilder(SPARQLconstants.PREFIX+" Select ");
	
		// Add dimension variables to query
		rollUpAggregatedObsQuery.append(StringUtil.addVariables("?dim", dimensionsWithoutRollUp.size()));
							
		// Add dimension variables to query
		rollUpAggregatedObsQuery.append(StringUtil.addVariables("?rollupnew", rollUpDimensions.size()));
		
		// Add measures to query
		int i=1;
		for (Entry<String, AggregationFunctions> m : mapMeasureOperation.entrySet()) {
			//Different operation for each measure
			rollUpAggregatedObsQuery.append("("+ m.getValue()+"(xsd:decimal(?measure"+i+"))as ?aggregatedMeasure"+i+") ");
			i++;
		}
				
		rollUpAggregatedObsQuery.append("where{");
		
		cubegraph.ifPresent(graph->rollUpAggregatedObsQuery.append("GRAPH <" + graph.getURI() + "> {"));
		
		rollUpAggregatedObsQuery.append("?obs qb:dataSet <" + cubeURI+ ">.");

		i = 1;
		for (String dim : dimensionsWithoutRollUp) {
			rollUpAggregatedObsQuery.append("?obs <" + dim + "> ?dim"	+ i + ".");
			i++;
		}
					
		i = 1;
		for (String rollUpDim : rollUpDimensions) {
			rollUpAggregatedObsQuery.append("?obs <"+rollUpDim+"> ?rollupvalue"+i+".");
			i++;
		}
		
		i=1;
		for (String m : mapMeasureOperation.keySet()) {
			rollUpAggregatedObsQuery.append("?obs <" + m + "> ?measure"+ i + ".");
			i++;
		}
		
		
		cubegraph.ifPresent(graph->rollUpAggregatedObsQuery.append("}"));
		
		if(rollUpDimensions.size()==1){
			String triple="?x xkos:isPartOf ?y.";
			Optional<LDResource> schemaGraph=Optional.ofNullable(SPARQLUtil.getTripleGraph(triple, sparqlService));
			schemaGraph.ifPresent(graph->rollUpAggregatedObsQuery.append("GRAPH <" + graph.getURI() + "> {"));
			rollUpAggregatedObsQuery.append("?rollupvalue"+1+" xkos:isPartOf+ ?rollupnew"+1+".");
			schemaGraph.ifPresent(graph->rollUpAggregatedObsQuery.append("}"));
		}
		
		//TO DO SUPPORT MULTIPLE ROLLUP DIMENSIONS
		/*	
		//	rollUpAggregatedObsQuery.append("{");
			String triple="?x xkos:isPartOf ?y.";
			Optional<LDResource> schemaGraph=Optional.ofNullable(SPARQLUtil.getTripleGraph(triple, sparqlService));
			schemaGraph.ifPresent(graph->rollUpAggregatedObsQuery.append("GRAPH <" + graph.getURI() + "> {"));
			
			for (int j=rollUpDimensions.size();j>=1;j--) {
				rollUpAggregatedObsQuery.append("{");
				for (int k=1;k<=rollUpDimensions.size();k++) {
					if(j==k){
						rollUpAggregatedObsQuery.append("?rollupvalue"+k+" xkos:isPartOf+ ?rollupnew"+k+".");
					}else{
						rollUpAggregatedObsQuery.append("?rollupvalue"+k+" xkos:isPartOf* ?rollupnew"+k+".");
					}
				}
				rollUpAggregatedObsQuery.append("}UNION");
			}			
			
			String tmp=StringUtil.replaceLast(rollUpAggregatedObsQuery.toString(), "UNION", "");
			rollUpAggregatedObsQuery.setLength(0);
			rollUpAggregatedObsQuery.append(tmp);
			
			schemaGraph.ifPresent(graph->rollUpAggregatedObsQuery.append("}"));
		//	rollUpAggregatedObsQuery.append("}");	
		}		*/	

		rollUpAggregatedObsQuery.append("}");

		// Group by dimensions to get aggregated value
		rollUpAggregatedObsQuery.append("GROUP BY ");		
		
		rollUpAggregatedObsQuery.append(StringUtil.addVariables("?dim", dimensionsWithoutRollUp.size()));
		rollUpAggregatedObsQuery.append(StringUtil.addVariables("?rollupnew", rollUpDimensions.size()));
				
		return  QueryExecutor.executeSelect(rollUpAggregatedObsQuery.toString(), sparqlService);
		
	}
	
	private static int insertAggregatedRollUpObservations2Cube(TupleQueryResult res,
			String cubeURI, String graphInsert, Set<String> measures, String sparqlService){		
		
		List<LDResource> dimensions=CubeSPARQL.getDataCubeDimensions(cubeURI, sparqlService);
		
		List<String> dimensionsWithoutRollUp=new ArrayList<>();
		List<String> rollUpDimensions=new ArrayList<>();
		
		//Separate dimensions with hierarchical structure
		//and without hierarchical structure
		dimensions.forEach(dim-> { 
			List<LDResource> dimLevels=CubeSPARQL.getDimensionLevels(dim.getURI(), sparqlService);						
			if(dimLevels.isEmpty()){
				dimensionsWithoutRollUp.add(dim.getURI());
			}else{
				rollUpDimensions.add(dim.getURI());
			}});
		
		
		// Store aggregated results
		List<BindingSet> bs = new ArrayList<>();
		while (res.hasNext()) {
			bs.add(res.next());
		}
		
		StringBuilder addRollUpObservations2Cube = new  StringBuilder(
				SPARQLconstants.PREFIX+ "INSERT DATA  {");

		//Get original cube graph
		Optional<String> cubegraph=Optional.ofNullable(graphInsert);
		cubegraph.ifPresent(graph->addRollUpObservations2Cube.append("GRAPH <"+graph+"> { "));

		int count = 0;
		
		//counter for observation ID 
		int obscount=1;
		
		// Create new observations for the new aggregated cube.
		// Insert observations in sets of 100
		for (BindingSet bindingSet : bs) {
			// Observation URI
			String newObservationURI = "<"+cubeURI+"/rollUpObs_"+ obscount+">";
			obscount++;
							
			addRollUpObservations2Cube.append(newObservationURI + " rdf:type qb:Observation." 
									+ newObservationURI 	+ " qb:dataSet <" + cubeURI + ">.");
			
			int i = 1;
			for (String dim : dimensionsWithoutRollUp) {
				String dimValue = bindingSet.getValue("dim" + i).stringValue();
				addRollUpObservations2Cube.append(newObservationURI + " <"+ dim + "> "
													+SPARQLUtil.toTripleValue(dimValue)+".");
				i++;
			}					
				
			i = 1;
			for (String rollUpDim : rollUpDimensions) {
				String rollUpDimValue = bindingSet.getValue("rollupnew" + i).stringValue();
				addRollUpObservations2Cube.append(newObservationURI + " <"+ rollUpDim + "> "
						+SPARQLUtil.toTripleValue(rollUpDimValue)+".");
				i++;
			}					
				
			i=1;
			//add measures to new aggregated measures to new observation
			for (String m : measures) {
				String measureValue = bindingSet.getValue("aggregatedMeasure"+i).stringValue();
				addRollUpObservations2Cube.append(newObservationURI + " <"+ m + "> "
						+SPARQLUtil.toTripleValue(measureValue)+".");
				i++;
			}
			
			// If |observations|= 100 execute insert
			// Cannot insert all the observations with one query - too long
			if (count == 100) {
				count = 0;
						
				cubegraph.ifPresent(graph->addRollUpObservations2Cube.append("}"));
								
				addRollUpObservations2Cube.append("}");
					
				QueryExecutor.executeUPDATE(addRollUpObservations2Cube.toString(),sparqlService);

				// Initialize query to insert more observations
				addRollUpObservations2Cube.setLength(0);
				addRollUpObservations2Cube.append(SPARQLconstants.PREFIX+ "INSERT DATA  {");

				cubegraph.ifPresent(graph->addRollUpObservations2Cube.append("GRAPH <"+graph+"> { "));
							
			} else {
				count++;
			}
		}	
		
		// If there are observations not yet inserted
		if (count > 0) {			
			cubegraph.ifPresent(graph->addRollUpObservations2Cube.append("}"));			
			addRollUpObservations2Cube.append("}");		
			QueryExecutor.executeUPDATE(addRollUpObservations2Cube.toString(),sparqlService);
		}	
		obscount--;
		
		return obscount;
	}	
}
