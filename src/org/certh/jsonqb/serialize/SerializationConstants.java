package org.certh.jsonqb.serialize;

public class SerializationConstants {

	//*** Structure ***//
	
	public static final String STRUCTURE="structure";
	
	public static final String FREE_DIMENSIONS="free_dimensions";
	
	public static final String LOCKED_DIMENSIONS="locked_dimensions";
	
	public static final String DIMENSION_VALUES="dimension_values";
	
	public static final String LOCKED_VALUE="locked_value";
	
	
	//*** HEADERS ***//
	
	public static final String HEADERS="headers";
	
	public static final String COLUMNS="columns";
	
	public static final String ROWS="rows";
	
	public static final String COLUMN_HIERARCHY="column_hierarchy";
	
	public static final String ROW_HIERARCHY="row_hierarchy";
	
	
	//*** DATA ***//
	public static final String DATA="data";
	
	
	//*** Resource ***//
	
	public static final String LABEL="label";
	
	public static final String ID="@id";
	
	public static final String ORDER="order";	
	
	
	//*** LISTS ***//
	
	public static final String CUBES="cubes";
	
	public static final String DIMENSIONS="dimensions";
	
	public static final String ATTRIBUTES="attributes";
	
	public static final String MEASURES="measures";
	
	public static final String VALUES="values";
	
	public static final String OBSERVATIONS="observations";
	
	private SerializationConstants() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}
			
			
			
			
			
			
			
}
