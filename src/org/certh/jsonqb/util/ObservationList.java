package org.certh.jsonqb.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.certh.jsonqb.datamodel.LDResource;
import org.certh.jsonqb.datamodel.Observation;

public class ObservationList {

	private List<Observation> listOfObservations;

	public ObservationList() {
		listOfObservations=new ArrayList<>();
	}

	public ObservationList(List<Observation> listOfObservations) {
		this.listOfObservations = listOfObservations;
	}

	public List<Observation> getListOfObservations() {
		return listOfObservations;
	}

	public void setListOfObservations(List<Observation> listOfObservations) {
		this.listOfObservations = listOfObservations;
	}

	public void addObservation(Observation obs) {
		listOfObservations.add(obs);
	}

	public Map<String, List<String>> getDimensionValues(List<String> dimensions) {
		Map<String, List<String>> dimVals = new HashMap<>();

		for (Observation obs : listOfObservations) {
			for (String dim : dimensions) {
				// collect dimension values of result
				List<String> currentDimVals = dimVals.get(dim);
				if (currentDimVals == null) {
					currentDimVals = new ArrayList<>();
				}
				Set<String> currentDimValSet = new HashSet<>(currentDimVals);
				currentDimValSet.add(obs.getObservationValues().get(dim));
				dimVals.put(dim, new ArrayList<>(currentDimValSet));

			}
		}

		for (Map.Entry<String, List<String>> entry : dimVals.entrySet()) {
			List<String> values = new ArrayList<>(entry.getValue());
			Collections.sort(values);
			dimVals.put(entry.getKey(), values);
		}

		return dimVals;

	}
	
	public Map<String, List<LDResource>> getDimensionValuesWithLabels(List<String> dimensions, String sparqlService) {
		Map<String, List<String>> dimVals = getDimensionValues(dimensions);

		Map<String, List<LDResource>> dimValsWithLables = new HashMap<>();
		
		for (Map.Entry<String, List<String>> entry : dimVals.entrySet()) {
			List<String> values = new ArrayList<>(entry.getValue());
			
			List<LDResource> valsWithLabels = new ArrayList<>();
			
			for(String val:values){
				LDResource ldr=new LDResource(val);
				//if it is a URI then get labels
				if(val.startsWith("http://")){
					ldr = SPARQLUtil.getLabels(val, sparqlService);
				}				
				valsWithLabels.add(ldr);
			}
			Collections.sort(valsWithLabels,LDResource.labelComparator);
			dimValsWithLables.put(entry.getKey(), valsWithLabels);
			
		}	

		return dimValsWithLables;

	}
	
	

}
