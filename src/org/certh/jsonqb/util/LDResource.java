package org.certh.jsonqb.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.rdf4j.model.Literal;



public class LDResource implements Comparable<LDResource> {
	
	private String URI;
	private List<Label> labels=new ArrayList<Label>();
	//private String level;
	
	public LDResource() {
		super();
	}

	public LDResource(String uRI) {
		URI = uRI;
	}
	
	public LDResource(String uRI,String label) {
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

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

//	public String getLevel() {
//		return level;
//	}

//	public void setLevel(String level) {
//		 this.level= level;
//	}
	
	//public String getLabel() {
	//	if(labelLiteral!=null){
	//		return labelLiteral.getLabel();
	//	}else{
	//		return null;
	//	}
	//}
	
	public List<Label> getLabels(){
		return labels;
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
	

	// If labels exists return the 1st label 
	// else return the last part of the URI (either after last '#' or after last '/')
	public String getURIorLabel()  {
		
		if (labels.size()>0&& labels.get(0) != null && labels.get(0).getLabel()!=null &&
				!labels.get(0).getLabel().equals("")) {
			return labels.get(0).getLabel();			
		} else{ 
			return getLastPartOfURI();
		}

	}
	
	
	// Get the last part of the URI (either after last '#' or after last '/')
	public String getLastPartOfURI()  {
		if (URI.contains("#")) {
			return URI.substring(URI.lastIndexOf("#") + 1, URI.length());
		} else {
			return URI.substring(URI.lastIndexOf("/") + 1, URI.length());
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
			if (cust.getURI() != null && cust.getURI().equals(URI)) {
				return true;
			}
		}
	
		return false;
	}
	
	public int hashCode(){
		return URI.hashCode();
		
	}	

	public int compareTo(LDResource otherResource) {
		
		return  this.getURIorLabel().compareTo((otherResource).getURIorLabel());
	}	
	
}
