package org.certh.jsonqb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.certh.jsonqb.datamodel.LDResource;

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
	
	public static List<String> ldResourceSet2StringList(Set<LDResource> ldrSet){
		List<String> strList=new ArrayList<>();
		for(LDResource ldr: ldrSet){
			strList.add(ldr.getURI());
		}		
		return strList;
	}
}
