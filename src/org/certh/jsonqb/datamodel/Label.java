package org.certh.jsonqb.datamodel;

import org.eclipse.rdf4j.model.Literal;

public class Label implements Comparable<Label>{
	
	private String label;
	private String language;
	
	public  Label(String label){
		this.label=label;
	}
	
	public  Label(Literal literal){
		if (literal.getLabel()!=null){
			this.label=literal.getLabel();
		}
		if (literal.getLanguage().isPresent()){
			this.language=literal.getLanguage().get();
		}
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// if the two objects are equal in reference, they are equal
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof Label) {
			Label cust = (Label) obj;
			if (cust.getLabel() != null && cust.getLanguage()!=null
					&& cust.getLabel().equals(this.getLabel()) 
				&& cust.getLanguage().equals(this.getLanguage())){
				return true;
			}
		}
	
		return false;
	}
	
	@Override
	public int hashCode(){
		return (getLabel()+"@"+getLanguage()).hashCode();
		
	}	

	@Override
	public int compareTo(Label otherResource) {		
		return  (getLabel()+"@"+getLanguage()).compareTo(
				otherResource.getLabel()+"@"+otherResource.getLanguage());
		

	}	
	

}
