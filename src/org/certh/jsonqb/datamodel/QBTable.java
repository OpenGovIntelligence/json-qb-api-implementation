package org.certh.jsonqb.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class QBTable {

	//The free dimensions of the QBTable
	private List<LDResource> freeDimensions = new ArrayList<>();
	
	//The locked dimensions of the QBTable
	private List<LockedDimension> lockedDimensions = new ArrayList<>();
	
	//The order of free dimensions to be used as columns
	private List<LDResource> columnHierarchy=new ArrayList<>();
	
	//The order of free dimensions to be used as rows
	private List<LDResource> rowHierarchy=new ArrayList<>();

	//The values of the free?? dimensions that appear at the QBTable
	private Map<LDResource, List<LDResource>> dimensionValues = new HashMap<>();
	
	private List<Number> data=new ArrayList<>();

	public Map<LDResource, List<LDResource>> getDimensionValues() {
		return dimensionValues;
	}

	public void setDimensionValues(Map<LDResource, List<LDResource>> dimensionValues) {
		this.dimensionValues = dimensionValues;
	}

	public void putDimensionValues(LDResource dim, List<LDResource> values) {
		this.dimensionValues.put(dim, values);
	}

	public List<LDResource> getFreeDimensions() {
		return freeDimensions;
	}

	public void setFreeDimensions(List<LDResource> freeDimensions) {
		this.freeDimensions = freeDimensions;
	}
	
	public void addFreeDimension(LDResource ldr) {
		freeDimensions.add(ldr);
	}

	public List<LockedDimension> getLockedDimensions() {
		return lockedDimensions;
	}

	public void setLockedDimensions(List<LockedDimension> lockedDimensions) {
		this.lockedDimensions = lockedDimensions;
	}	

	public void addLockedDimension(LockedDimension loc) {
		lockedDimensions.add(loc);
	}
	
	public List<LDResource> getColumnHierarchy() {
		return columnHierarchy;
	}

	public void setColumnHierarchy(List<LDResource> columnHierarchy) {
		this.columnHierarchy = columnHierarchy;
	}
	
	public void addColumn(LDResource col) {
		columnHierarchy.add(col);
	}

	public List<LDResource> getRowHierarchy() {
		return rowHierarchy;
	}

	public void setRowHierarchy(List<LDResource> rowHierarchy) {
		this.rowHierarchy = rowHierarchy;
	}
	
	public void addRow(LDResource row) {
		rowHierarchy.add(row);
	}
	
	public List<Number> getData() {
		return data;
	}

	public void setData(List<Number> data) {
		this.data = data;
	}
	
	public void addData(Number num) {
		data.add(num);
	}

}
