package org.certh.jsonqb.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.Literal;

public class DataCube extends LDResource {
	
	private List<Label> desctiptions=new ArrayList<>();	
	private List<Label> comments=new ArrayList<>();
	private List<String> subjects=new ArrayList<>();
	private String issued;
	private String modified;
	private String publisher;
	private String license;
	
	public DataCube() {
		super();
	}

	public DataCube(String uRI) {
		super(uRI);
	}
	
	
	public List<Label> getDesctiptions() {
		return desctiptions;
	}
	
	public void setDesctiptions(List<Label> desctiptions) {
		this.desctiptions = desctiptions;
	}
	

	public void addDescription(String descriptionstr) {
		this.desctiptions.add(new Label(descriptionstr));	
	}
	
	public void addDescription(Literal literal) {
		this.desctiptions.add(new Label(literal));
	}
	
	public void addDescription(Label label) {
		this.desctiptions.add(label);
	}
	
	public List<Label> getComments() {
		return comments;
	}
	public void setComments(List<Label> comments) {
		this.comments = comments;
	}
	
	public void addComment(String commentstr) {
		this.comments.add(new Label(commentstr));	
	}
	
	public void addComment(Literal literal) {
		this.comments.add(new Label(literal));
	}
	
	public void addComment(Label label) {
		this.comments.add(label);
	}
		
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subject) {
		this.subjects = subject;
	}
	
	public void addSubject(String subjectstr) {
		this.subjects.add(subjectstr);	
	}
	
	public String getIssued() {
		return issued;
	}
	public void setIssued(String issued) {
		this.issued = issued;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	
	
		
}
