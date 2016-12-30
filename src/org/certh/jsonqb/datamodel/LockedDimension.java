package org.certh.jsonqb.datamodel;

import org.eclipse.rdf4j.model.Literal;

public class LockedDimension extends LDResource {
	
	private LDResource lockedValue;
	
	
	public LockedDimension() {
		super();
	}

	public LockedDimension(String uRI) {
		super(uRI);		
	}
	
	public LockedDimension(String uRI,String label) {
		super(uRI, label);		
	}
	
	public LockedDimension(String uRI,Literal literal) {
		super(uRI, literal);
	}	
	

	public LDResource getLockedValue() {
		return lockedValue;
	}

	public void setLockedValue(LDResource lockedValue) {
		this.lockedValue = lockedValue;
	}			
	

}
