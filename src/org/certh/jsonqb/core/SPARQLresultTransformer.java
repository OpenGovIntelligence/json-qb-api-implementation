package org.certh.jsonqb.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.util.CharArrayMap.EntrySet;
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
			listOfResources.add(ldr);
		}				
		return listOfResources;
	}
	
	public static List<LDResource> toLDResourceListWithLabels(TupleQueryResult res, String sparqlService){
		List<LDResource> listOfResources=toLDResourceList(res);
		List<LDResource> listofLabeledLDResourceList=new  ArrayList<>();
		
		for(LDResource ldr: listOfResources){
			if (ldr.getLabels().isEmpty()){
				LDResource labeledLdr=SPARQLUtil.getLabels(ldr.getURI(), sparqlService);
				listofLabeledLDResourceList.add(labeledLdr);
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
	public static QBTable toQBTable(TupleQueryResult res, List<String> measures,
			List<String> visualDims,Map<String, String> fixedDims,	String sparqlService) {
			
			List<LDResource> visualDimsLD=new ArrayList<>();
			for(String vDim:visualDims){
				visualDimsLD.add(SPARQLUtil.getLabels(vDim, sparqlService));
			}
			
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
			
			Map<LDResource, List<LDResource>> dimValsLDR=new HashMap<>();
			
			for(String dim: dimVals.keySet()){
				LDResource dimLDR=SPARQLUtil.getLabels(dim, sparqlService);
				dimValsLDR.put(dimLDR, dimVals.get(dim));
			}
			
			qbt.setData(Arrays.asList(listOfNumbers));	
			qbt.setFreeDimensions(visualDimsLD);
			qbt.addColumn(visualDimsLD.get(1));
			qbt.addRow(visualDimsLD.get(0));
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
				String value = bindingSet.getValue(entry.getKey()).stringValue();
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
			
			if(bindingSet.getValue("title")!=null){
				Label title=new Label((Literal) bindingSet.getValue("title"));
				if(!qb.getTitles().contains(title)){
					qb.addTitle(title);
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
