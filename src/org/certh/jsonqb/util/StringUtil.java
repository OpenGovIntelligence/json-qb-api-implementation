package org.certh.jsonqb.util;

public class StringUtil {
	
	private StringUtil() {
		 // Throw an exception if this ever *is* called
	    throw new AssertionError("Instantiating utility class.");
	}

	public static  String replaceLast(String string, String substring, String replacement){
	  int index = string.lastIndexOf(substring);
	  
	  if (index == -1){
	    return string;
	  }
	  
	  return string.substring(0, index) + replacement
	          + string.substring(index+substring.length());
	}
	
	public static String addVariables(String prefix, int number){
		StringBuilder result=new StringBuilder();
		for(int i=1;i<=number;i++){
			result.append(prefix+i+" ");
		}
		return result.toString();
	}
}
