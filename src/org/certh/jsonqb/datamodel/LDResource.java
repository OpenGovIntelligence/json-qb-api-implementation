package org.certh.jsonqb.datamodel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.eclipse.rdf4j.model.Literal;

public class LDResource implements Comparable<LDResource> {
	
	private String URI;
	private List<Label> labels=new ArrayList<>();
	private int order=-1;
	
	//Comparator that compares LDResource by their label
	public static final Comparator<LDResource> labelComparator =
			(LDResource ldr1,LDResource ldr2)->	ldr1.getURIorLabel().compareTo(ldr2.getURIorLabel());
	
	public LDResource() {
		super();
	}

	public LDResource(String uRI) {
		URI = uRI;
	}
	
	public LDResource(String uRI,String label) {
		URI = uRI;
		if(label!=null){
			this.labels.add(new Label(label));	
		}
	}
	
	public LDResource(String uRI,Literal literal) {
		URI = uRI;
		if(literal!=null){
			this.labels.add(new Label(literal));			
		}
	}	
	
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}


	
	public List<Label> getLabels(){
		return labels;
	}
	
	//default label = "en"
	public String getLabel(String lang){
		String enLabel=null;
		String labelWithLanguage=null;
		String otherLabel=getLastPartOfURI();
		if(!labels.isEmpty()){
			otherLabel=labels.get(0).getLabel();
		}
		for(Label l:getLabels()){
			if(l.getLanguage()!=null){
				if(l.getLanguage().equals(lang)){
					labelWithLanguage=l.getLabel();
				}else if(l.getLanguage().equals("en")){
					enLabel=l.getLabel();
				}
			}else{
				otherLabel=l.getLabel();
			}
		}
		
		if(labelWithLanguage!=null){
			return labelWithLanguage;
		}else if(enLabel!=null){
			return enLabel;
		}else{
			return otherLabel;
		}
	}
	
	
	

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}
	
	public void addLabel(String labelstr) {
		this.labels.add(new Label(labelstr));	
	}
	
	public void addLabel(Literal literal) {
		this.labels.add(new Label(literal));
	}
	
	public void addLabel(Label label) {
		this.labels.add(label);
	}
	
	
	

	// If labels exists return the 1st label 
	// else return the last part of the URI (either after last '#' or after last '/')
	public String getURIorLabel()  {
		
		if (!labels.isEmpty() && getLabel("en")!=null && !"".equals(getLabel("en"))){
			return getLabel("en");
		
		//if (!labels.isEmpty() && labels.get(0) != null && labels.get(0).getLabel()!=null &&
		//		! "".equals(labels.get(0).getLabel())) {
		//	return labels.get(0).getLabel();			
		} else{ 
			return getLastPartOfURI();
		}

	}
	
	
	// Get the last part of the URI (either after last '#' or after last '/')
	public String getLastPartOfURI()  {
		if (URI.contains("#")) {
			String val=URI.substring(URI.lastIndexOf('#') + 1, URI.length());
			if("id".equals(val)){
				
				return URI.substring(URI.lastIndexOf('/') + 1, URI.lastIndexOf('#'));
			}else{
				return val;
			}
			
		} else if(URI.contains("/")) {
			return URI.substring(URI.lastIndexOf('/') + 1, URI.length());
		} else{
			return URI;	
		}

	}

	@Override
	public boolean equals(Object obj) {
		// if the two objects are equal in reference, they are equal
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof LDResource) {
			LDResource cust = (LDResource) obj;
			if (cust.getURI() != null && cust.getURI().equals(this.getURI())) {
				return true;
			}
		}
	
		return false;
	}
	
	@Override
	public int hashCode(){
		return URI.hashCode();
		
	}	

	@Override
	public int compareTo(LDResource otherResource) {		
		return  this.getURIorLabel().compareTo((otherResource).getURIorLabel());
	}	
	
	
	
	
}
