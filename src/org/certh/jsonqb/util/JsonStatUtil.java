package org.certh.jsonqb.util;

public class JsonStatUtil {
	
	//Remove no needed information from serialization of no.ssb.jsonstat.v2.Dataset
	public static String cleanJsonStat (String jsonStat){
		String cleanJsonStat=jsonStat;
		cleanJsonStat=cleanJsonStat.replaceAll(",\"version\":\"TWO\",\"clazz\":\"DIMENSION\"", "");
		cleanJsonStat=cleanJsonStat.replaceAll(",\"version\":\"TWO\",\"clazz\":\"DATASET\"", "");
		return cleanJsonStat;
	}
	
	//Add class:dataset to json-stat
	public static String jsonStatAddClass (String jsonStat){
		String newJsonStat=jsonStat;
		newJsonStat="{\"class\" : \"dataset\","+newJsonStat.substring(1, newJsonStat.length());
		return newJsonStat;
	}
}
