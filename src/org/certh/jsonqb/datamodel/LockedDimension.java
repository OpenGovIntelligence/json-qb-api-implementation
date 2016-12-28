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
	
	@Override
	public boolean equals(Object obj) {
		// if the two objects are equal in reference, they are equal
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof LockedDimension) {
			LockedDimension cust = (LockedDimension) obj;
			if (cust.getURI() != null && cust.getURI().equals(this.getURI())) {
				return true;
			}
		}
	
		return false;
	}
	
	@Override
	public int hashCode(){
		return getURI().hashCode();
		
	}	

}
