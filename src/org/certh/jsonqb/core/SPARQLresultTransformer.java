package org.certh.jsonqb.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.certh.jsonqb.datamodel.DataCube;
import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.Label;
import org.certh.jsonqb.datamodel.LockedDimension;
import org.certh.jsonqb.datamodel.Observation;
import org.certh.jsonqb.datamodel.QBTable;
import org.certh.jsonqb.datamodel.QBTableJsonStat;
import org.certh.jsonqb.util.ObservationList;
import org.certh.jsonqb.util.SPARQLUtil;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;



public class SPARQLresultTransformer {
	
	private SPARQLresultTransformer() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}

	public static List<LDResource> toLDResourceList(TupleQueryResult res) {
	
		String positionVal = "position";
		List<LDResource> listOfResources = new ArrayList<>();
		while (res.hasNext()) {
			BindingSet bindingSet = res.next();
			LDResource ldr = new LDResource(bindingSet.getValue("res").stringValue());	
			if (bindingSet.getValue(positionVal) != null) {
				ldr.setOrder(Integer.valueOf(bindingSet.getValue(positionVal).stringValue()));
			}
			if(bindingSet.getValue("label")!=null){
				ldr.addLabel((Literal) bindingSet.getValue("label"));
			}
			//remoe the previous LDResource (if exists)
			listOfResources.remove(ldr);
			//Add the new one with the updated labels, position
			listOfResources.add(ldr);
		}				
		return listOfResources;
	}
	
	public static List<LDResource> toLDResourceListWithLabels(TupleQueryResult res, String sparqlService){
		List<LDResource> listOfResources=toLDResourceList(res);
		List<LDResource> listofLabeledLDResourceList=new  ArrayList<>();
		
		for(LDResource ldr: listOfResources){
			if(ldr.getURI().contains("http://")&&ldr.getLabels().isEmpty()){
				LDResource labeledLdr=SPARQLUtil.getLabels(ldr.getURI(), sparqlService);
				listofLabeledLDResourceList.add(labeledLdr);
			}else{
				listofLabeledLDResourceList.add(ldr);
			}			
		}
		
		return listofLabeledLDResourceList;
		
	}

	public static List<String> toStringList(TupleQueryResult res) {
		List<String> listOfString = new ArrayList<>();
		while (res.hasNext()) {
			BindingSet bindingSet = res.next();
			String str = bindingSet.getValue("str").stringValue();
			if (!"".equals(str)) {
				listOfString.add(str);
			}
		}
		return listOfString;
	}

	public static List<Number> toNumberLists(TupleQueryResult res, List<String> measures) {
		List<Number> listOfNumbers = new ArrayList<>();
		while (res.hasNext()) {
			BindingSet bindingSet = res.next();
			for (int i=1;i<=measures.size();i++) {
				String number = bindingSet.getValue("measure" + i).stringValue();
				if (!"".equals(number)) {
					listOfNumbers.add(Double.parseDouble(number));
				}				
			}
		}
		return listOfNumbers;

	}

	//NEED TO SUPPORT MULTIPLE MEASURE
	public static QBTableJsonStat toQBJsonStatTable(TupleQueryResult res, List<String> measures, List<String> visualDims,
			String sparqlService) {
		
		ObservationList observationList =new ObservationList();
		
		QBTableJsonStat qbt = new QBTableJsonStat();

		// create a list of observations
		while (res.hasNext()) {
			Observation obs = new Observation();
			BindingSet bindingSet = res.next();

			int i = 1;

			// Add measure to observation
			for (String meas : measures) {
				String number = bindingSet.getValue("measure" + i).stringValue();
				obs.putObservationValue(meas, number);
				i++;
			}

			i = 1;

			// Add dimension values to observation
			for (String dim : visualDims) {
				String value = bindingSet.getValue("dim" + i).stringValue();
				obs.putObservationValue(dim, value);

				i++;
			}
			observationList.addObservation(obs);
		}
		
		//get the dimension values used by the cube observations 
		Map<String, List<LDResource>> dimVals = observationList.getDimensionValuesWithLabels(visualDims,sparqlService);
			
		String rowDim = visualDims.get(0);
		String colDim = visualDims.get(1);
		List<LDResource> rowDimValues = dimVals.get(rowDim);
		List<LDResource> colDimValues = dimVals.get(colDim);
		
		Number[] listOfNumbers = new Number[rowDimValues.size()*colDimValues.size()];
		//initialize list with empty values
		for(int i=0;i<rowDimValues.size()*colDimValues.size();i++){
			listOfNumbers[i]=null;
		}
		
		for(Observation obs:observationList.getListOfObservations()){
			LDResource obsRowVal =new LDResource( obs.getObservationValues().get(rowDim));
			LDResource obsColVal = new LDResource(obs.getObservationValues().get(colDim));

			//Row major order
			int obsIndex=rowDimValues.indexOf(obsRowVal)*colDimValues.size()+colDimValues.indexOf(obsColVal);
			
			//NEED TO SUPPORT MULTIPLE MEASURES
			for (String meas : measures) {
				listOfNumbers[obsIndex]= Double.parseDouble(obs.getObservationValues().get(meas));
			}
			
		}

		qbt.setMeasures(Arrays.asList(listOfNumbers));
		qbt.setDimVals(dimVals);
		return qbt;
	}
	
	
	
	
	//NEED TO SUPPORT MULTIPLE MEASURE
	public static QBTable toQBTable(List<String> rowDimensions,List<String> colDimensions,
			Map<String, String> fixedDims, List<String> measures, TupleQueryResult res,
			String sparqlService) {
			
		    List<LDResource> rowDimsLD=new ArrayList<>();
		    List<LDResource> colDimsLD=new ArrayList<>();
		    List<LDResource> freeDimsLD=new ArrayList<>();
		    
		    List<String> freeDims=new ArrayList<>();
		    freeDims.addAll(rowDimensions);
		    
		
		    for(String rowDim:rowDimensions){
				rowDimsLD.add(SPARQLUtil.getLabels(rowDim, sparqlService));
			}
		    
			
		    if(colDimensions!=null){
		    	freeDims.addAll(colDimensions);
				for(String colDim:colDimensions){
					colDimsLD.add(SPARQLUtil.getLabels(colDim, sparqlService));
				}
		    }
			
			freeDimsLD.addAll(rowDimsLD);
			freeDimsLD.addAll(colDimsLD);
			
			List<LockedDimension> lockedDims=new ArrayList<>();
			for(String fDim:fixedDims.keySet()){
				String fDimVal=fixedDims.get(fDim);
				LDResource ldr=SPARQLUtil.getLabels(fDim, sparqlService);
				LockedDimension lock=new LockedDimension(fDim);
				lock.setLabels(ldr.getLabels());
				lock.setLockedValue(SPARQLUtil.getLabels(fDimVal, sparqlService));
				lockedDims.add(lock);
			}
		
			ObservationList observationList =new ObservationList();
			
			QBTable qbt = new QBTable();

			// create a list of observations
			while (res.hasNext()) {
				Observation obs = new Observation();
				BindingSet bindingSet = res.next();

				int i = 1;

				// Add measure to observation
				for (String meas : measures) {
					String number = bindingSet.getValue("measure" + i).stringValue();
					obs.putObservationValue(meas, number);
					i++;
				}

				i = 1;

				// Add dimension values to observation
				for (String rowDim : rowDimensions) {
					String value = bindingSet.getValue("row" + i).stringValue();
					obs.putObservationValue(rowDim, value);
					i++;
				}
								
				i = 1;

				// Add dimension values to observation
				if(colDimensions!=null){
					for (String colDim : colDimensions) {
						String value = bindingSet.getValue("col" + i).stringValue();
						obs.putObservationValue(colDim, value);
	
						i++;
					}
				}
				
				observationList.addObservation(obs);
			}
			
			//get the dimension values used by the cube observations 
			Map<String, List<LDResource>> dimVals = observationList.getDimensionValuesWithLabels(freeDims,sparqlService);
				
			String rowDim = rowDimensions.get(0);
			String colDim=null;
			if(colDimensions!=null){
				colDim=colDimensions.get(0);
			}
			
			List<LDResource> rowDimValues = dimVals.get(rowDim);
			List<LDResource> colDimValues=null;
			Number[] listOfNumbers;
			if(colDimensions!=null){
				colDimValues = dimVals.get(colDim);			
			
				listOfNumbers = new Number[rowDimValues.size()*colDimValues.size()];
				//initialize list with empty values
				for(int i=0;i<rowDimValues.size()*colDimValues.size();i++){
					listOfNumbers[i]=null;
				}
			}else{			
				listOfNumbers = new Number[rowDimValues.size()];
				//initialize list with empty values
				for(int i=0;i<rowDimValues.size();i++){
					listOfNumbers[i]=null;
				}
			}
			
			for(Observation obs:observationList.getListOfObservations()){
				LDResource obsRowVal =new LDResource( obs.getObservationValues().get(rowDim));
				int obsIndex;
				if(colDim!=null){
					LDResource obsColVal = new LDResource(obs.getObservationValues().get(colDim));
					//Row major order
					obsIndex=rowDimValues.indexOf(obsRowVal)*colDimValues.size()+colDimValues.indexOf(obsColVal);
				}else{
					//Row major order
					obsIndex=rowDimValues.indexOf(obsRowVal);
				}
				
				
				//NEED TO SUPPORT MULTIPLE MEASURES
				for (String meas : measures) {
					listOfNumbers[obsIndex]= Double.parseDouble(obs.getObservationValues().get(meas));
				}
				
			}	
			
			Map<LDResource, List<LDResource>> dimValsLDR=new HashMap<>();
			
			for(String dim: dimVals.keySet()){
				LDResource dimLDR=SPARQLUtil.getLabels(dim, sparqlService);
				dimValsLDR.put(dimLDR, dimVals.get(dim));
			}
			
			qbt.setData(Arrays.asList(listOfNumbers));	
			qbt.setFreeDimensions(freeDimsLD);
			if(colDimensions!=null){
				qbt.addColumn(colDimsLD.get(0));
			}
			qbt.addRow(rowDimsLD.get(0));
			qbt.setLockedDimensions(lockedDims);
			qbt.setDimensionValues(dimValsLDR);
			return qbt;
		}
	

	public static ObservationList toObservationList(TupleQueryResult res, Map<String, String> mapVariableNameURI) {

		ObservationList listOfObservations = new ObservationList();
		while (res.hasNext()) {
			BindingSet bindingSet = res.next();
			Observation obs = new Observation();
			for (Map.Entry<String, String> entry : mapVariableNameURI.entrySet()) {
				//The SPARQL variable name e.g. val1, val2, measure1
				String key=entry.getKey();
				Value v=bindingSet.getValue(key);
				String value="";
				if(v!=null){
					value=v.stringValue();

				//if valX is empty check dimX e.g. for predicates that are String -> sdmx-dimension:refPeriod  "2015"^^xsd:gYear ;
				}else if(key.startsWith("val")){
					String dimval="dim"+key.substring(key.length() - 1);
					v=bindingSet.getValue(dimval);
					if(v!=null){
						LDResource dimldr=new LDResource(v.stringValue());
						//value=v.stringValue();
						value=dimldr.getLabel(null);
					}
				}
				
				obs.putObservationValue(entry.getValue(), value);
			}			
	
			listOfObservations.addObservation(obs);
		}
		return listOfObservations;
	}

	public static LDResource toLDResource(String resURI, TupleQueryResult res) {
		LDResource ldr = new LDResource(resURI);
		while (res.hasNext()) {
			BindingSet bindingSet = res.next();
			if(bindingSet.getValue("label")!=null){
				ldr.addLabel((Literal) bindingSet.getValue("label"));
			}

		}
		return ldr;
	}
	
	
	public static DataCube toDataCube(String cubeURI, TupleQueryResult res) {
		DataCube qb = new DataCube(cubeURI);
		while (res.hasNext()) {
			BindingSet bindingSet = res.next();
			
			if(bindingSet.getValue("label")!=null){
				Label label=new Label((Literal) bindingSet.getValue("label"));
				if(!qb.getLabels().contains(label)){
					qb.addLabel(label);
				}
			}
			
			
			
			if(bindingSet.getValue("description")!=null){
				Label description=new Label((Literal) bindingSet.getValue("description"));
				if(!qb.getDesctiptions().contains(description)){
					qb.addDescription(description);
				}			
			}
			
			if(bindingSet.getValue("comment")!=null){
				Label comment=new Label((Literal) bindingSet.getValue("comment"));
				if(!qb.getComments().contains(comment)){
					qb.addComment(comment);
				}				
			}
			
			if(bindingSet.getValue("issued")!=null){
				qb.setIssued(bindingSet.getValue("issued").stringValue());
			}
			
			if(bindingSet.getValue("modified")!=null){
				qb.setModified(bindingSet.getValue("modified").stringValue());
			}
			
			if(bindingSet.getValue("subject")!=null){
				String subject= bindingSet.getValue("subject").stringValue();
				if(!qb.getSubjects().contains(subject)){
					qb.addSubject(subject);
				}				
			}
			
			if(bindingSet.getValue("publisher")!=null){
				qb.setPublisher(bindingSet.getValue("publisher").stringValue());
			}
			
			if(bindingSet.getValue("license")!=null){
				qb.setLicense(bindingSet.getValue("license").stringValue());
			}
		}
		return qb;
	}
}
