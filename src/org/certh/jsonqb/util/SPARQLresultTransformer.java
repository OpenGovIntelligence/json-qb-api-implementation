package org.certh.jsonqb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
/*	public static List<LDResource> toLDResourceOrderedList(TupleQueryResult res) {

		
		Map<Integer,LDResource> mapPositionResource=new HashMap<Integer, LDResource>();
		try {
			while (res.hasNext()) {
				BindingSet bindingSet = res.next();
				LDResource ldr = new LDResource(bindingSet.getValue("res").stringValue());
				Integer position=new Integer(bindingSet.getValue("position").stringValue());

				// Add the resource if not already at the list
				if (!mapPositionResource.containsKey(position)) {
					// check if there is a label (rdfs:label or skos:prefLabel)
					if (bindingSet.getValue("label") != null) {
						ldr.addLabel((Literal) bindingSet.getValue("label"));
					}

				//	if (bindingSet.getValue("level") != null) {
				//		ldr.setLevel(bindingSet.getValue("level").stringValue());
				//	}
					
					mapPositionResource.put(position, ldr);
					
				} else {
					
					LDResource existingLDR = mapPositionResource.get(position);

					// check if there is a label (rdfs:label or skos:prefLabel)
					if (bindingSet.getValue("label") != null) {
						existingLDR.addLabel((Literal) bindingSet.getValue("label"));
					}

				//	if (bindingSet.getValue("level") != null) {
				//		existingLDR.setLevel(bindingSet.getValue("level").stringValue());
				//	}

					mapPositionResource.put(position, existingLDR);
				}
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		
		LDResource[] listOfResources= new LDResource[mapPositionResource.size()];
		for(Integer i: mapPositionResource.keySet()){
			listOfResources[i-1]= mapPositionResource.get(i);
		}
		
		List<LDResource> list= Arrays.asList(listOfResources);
		return list;

	}*/
	

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
		Map<String,List<String>> dimVals=new HashMap<String,List<String>>();
		QBTable qbt=new QBTable();
		try {
			while (res.hasNext()) {
				BindingSet bindingSet = res.next();
				int  i=1;
				//Add number results
				for (String meas : measures) {
					String number=bindingSet.getValue("measure" + i ).stringValue();
					if(!number.equals("")){
						listOfNumbers.add(Double.parseDouble(number));
					}
					i++;
				}	
				
				i=1;
				//Add dimension values used by the result (ordered)
				for(String dim:visualDims){
					String value=bindingSet.getValue("dim"+i).stringValue();
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
			}			
			
			qbt.setMeasures(listOfNumbers);
			qbt.setDimVals(dimVals);
			
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return qbt;

	}
	
	
	
	public static List<Map<String,String>> toMapList(TupleQueryResult res, Map<String,String> mapVariableNameURI) {

		List<Map<String,String>> listOfObservations =new ArrayList<Map<String,String>>();
		try {
			while (res.hasNext()) {
				BindingSet bindingSet = res.next();
				Map<String,String> observation=new HashMap<String, String>();
				for(String varName:mapVariableNameURI.keySet()){
				//	if(bindingSet.getValue(varName)!=null){
						String value=bindingSet.getValue(varName).stringValue();
						observation.put(mapVariableNameURI.get(varName), value);						
				//	}
					
				}
				
				listOfObservations.add(observation);
				
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
