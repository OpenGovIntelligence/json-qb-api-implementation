package test.org.certh.jsonqb.util;

import static org.junit.Assert.assertEquals;

import org.certh.jsonqb.util.StringUtil;
import org.junit.Test;

public class TestStringUtil {
	
	@Test
	public void testSingleReplaceLast() {
		String str="This is a test";
		str=StringUtil.replaceLast(str, "test", "demo");
		assertEquals(str, "This is a demo");
	}
	
	@Test
	public void testMultipleReplaceLast() {
		String str="This is a test test test";
		str=StringUtil.replaceLast(str, "test", "demo");
		assertEquals(str, "This is a test test demo");
	}

}
