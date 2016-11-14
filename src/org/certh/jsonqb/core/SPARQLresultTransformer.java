package org.certh.jsonqb.core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.Observation;
import org.certh.jsonqb.datamodel.QBTable;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class SPARQLresultTransformer {

	public static List<LDResource> toLDResourceList(TupleQueryResult res) {

		List<LDResource> listOfResources = new ArrayList<LDResource>();
		try {
			while (res.hasNext()) {
				BindingSet bindingSet = res.next();
				LDResource ldr = new LDResource(bindingSet.getValue("res").stringValue());

				// Add the resource if not already at the list
				if (!listOfResources.contains(ldr)) {
					// check if there is a label (rdfs:label or skos:prefLabel)
					if (bindingSet.getValue("label") != null) {
						ldr.addLabel((Literal) bindingSet.getValue("label"));
					}

					if (bindingSet.getValue("position") != null) {
						ldr.setOrder(Integer.valueOf(bindingSet.getValue("position").stringValue()));
					}
					
					listOfResources.add(ldr);
				} else {
					int index = listOfResources.indexOf(ldr);
					LDResource existingLDR = listOfResources.get(index);

					// check if there is a label (rdfs:label or skos:prefLabel)
					if (bindingSet.getValue("label") != null) {
						existingLDR.addLabel((Literal) bindingSet.getValue("label"));
					}

					if (bindingSet.getValue("position") != null) {
						ldr.setOrder(Integer.valueOf(bindingSet.getValue("position").stringValue()));
					}

					listOfResources.set(index, existingLDR);
				}
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return listOfResources;
	}	

	public static List<String> toStringList(TupleQueryResult res) {

		List<String> listOfString = new ArrayList<String>();
		try {
			while (res.hasNext()) {
				BindingSet bindingSet = res.next();
				String str=bindingSet.getValue("str").stringValue();
				if(!str.equals("")){
					listOfString.add(str);
				}
				
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return listOfString;
	}
	
	public static List<Number> toNumberLists(TupleQueryResult res, List<String> measures) {

		List<Number> listOfNumbers = new ArrayList<Number>();
		try {
			while (res.hasNext()) {
				BindingSet bindingSet = res.next();
				int  i=1;
				for (String meas : measures) {
					String number=bindingSet.getValue("measure" + i ).stringValue();
					if(!number.equals("")){
						listOfNumbers.add(Double.parseDouble(number));
					}
					i++;
				}			
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return listOfNumbers;

	}
	
	
	public static QBTable toQBTable(TupleQueryResult res,
			List<String> measures, List<String> visualDims) {

		List<Number> listOfNumbers = new ArrayList<Number>();
		List<Observation> listOfObservations=new ArrayList<Observation>();
		Map<String,List<String>> dimVals=new HashMap<String,List<String>>();
		QBTable qbt=new QBTable();
		try {
			//create a list of observations
			while (res.hasNext()) {				
				Observation obs=new Observation();
				BindingSet bindingSet = res.next();
				
				int  i=1;
				
				//Add measure to observation
				for (String meas : measures) {
					String number=bindingSet.getValue("measure" + i ).stringValue();
					obs.putObservationValue(meas, number);
					i++;
				}	
				
				i=1;
				
				//Add dimension values to observation 
				for(String dim:visualDims){
					String value=bindingSet.getValue("dim"+i).stringValue();
					obs.putObservationValue(dim, value);
					
					//collect dimension values of result
					if(!dimVals.keySet().contains(dim)){
						List<String> tmpdimvals=new ArrayList<String>();
						tmpdimvals.add(value);
						dimVals.put(dim, tmpdimvals);
					}else{
						List<String> tmpdimvals=dimVals.get(dim);
						if(!tmpdimvals.contains(value)){
							tmpdimvals.add(value);
							dimVals.put(dim, tmpdimvals);
						}
					}
					i++;
				}	
				listOfObservations.add(obs);				
			}	
			
			//Sort dimension values
			for(String dim:dimVals.keySet()){
				List<String> values=dimVals.get(dim);
				Collections.sort(values);
				dimVals.put(dim, values);				
			}
			
			String rowDim=visualDims.get(0);
			String colDim=visualDims.get(1);
			List<String> rowDimValues=dimVals.get(rowDim);
			List<String> colDimValues=dimVals.get(colDim);
					
			int obsIndex=0;
			//Order of observations are in row major order 
			for(String rowVal:rowDimValues){
				for(String colVal:colDimValues){
					if(listOfObservations.size()>obsIndex){
						Observation currentObs=listOfObservations.get(obsIndex);
						String obsRowVal=currentObs.getObservationValues().get(rowDim);
						String obsColVal=currentObs.getObservationValues().get(colDim);
						if(rowVal.equals(obsRowVal)&&colVal.equals(obsColVal)){
							for (String meas : measures) {
								listOfNumbers.add(Double.parseDouble(currentObs.getObservationValues().get(meas)));
							}	
							obsIndex++;
						}else{
							for (String meas : measures) {
								listOfNumbers.add(null);
							}
						}		
					}else{
						for (String meas : measures) {
							listOfNumbers.add(null);
							
						}
					}								
				}
			}
			
			qbt.setMeasures(listOfNumbers);
			qbt.setDimVals(dimVals);
			
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return qbt;

	}
	
	
	
	public static List<Observation> toObservationList(TupleQueryResult res, Map<String,String> mapVariableNameURI) {

	//	List<Map<String,String>> listOfObservations =new ArrayList<Map<String,String>>();
		List<Observation> listOfObservations=new ArrayList<Observation>();
		try {
			while (res.hasNext()) {
				BindingSet bindingSet = res.next();
				Observation obs=new Observation();
				for(String varName:mapVariableNameURI.keySet()){
					String value=bindingSet.getValue(varName).stringValue();
						obs.putObservationValue(mapVariableNameURI.get(varName), value);						
				}
				
				listOfObservations.add(obs);
				
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return listOfObservations;
	}
	
	
	public static LDResource toLDResource(String resURI,TupleQueryResult res) {
		LDResource ldr = new LDResource(resURI);
		while (res.hasNext()) {
			BindingSet bindingSet = res.next();
			ldr.addLabel((Literal) bindingSet.getValue("label"));
			

		}
		return ldr;
	}	
}
