package org.certh.jsonqb.datamodel;

import org.eclipse.rdf4j.model.Literal;

public class Label {
	
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
	

}
