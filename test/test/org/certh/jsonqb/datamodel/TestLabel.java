package test.org.certh.jsonqb.datamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.certh.jsonqb.datamodel.Label;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Test;

public class TestLabel {

	private String labelstr1="label1";
	private String labelstr2="label2";
	private String language1="en";
	private String language2="de";
	private ValueFactory factory = SimpleValueFactory.getInstance();
	private Label label1=new Label(factory.createLiteral(labelstr1,language1));
	private Label label2=new Label(factory.createLiteral(labelstr1,language1));
	private Label label3=new Label(factory.createLiteral(labelstr1,language2));
	private Label label4=new Label(factory.createLiteral(labelstr2,language1));
	
	@Test
	public void testLabelEquality() {
		assertEquals(label1,label2);
		assertNotEquals(label1,label3);
		assertNotEquals(label1,label4);			
	}
	
	@Test
	public void testLabelHashCode() {
		assertEquals(label1.hashCode(),label2.hashCode());
		assertNotEquals(label1.hashCode(),label3.hashCode());
		assertNotEquals(label1.hashCode(),label4.hashCode());		
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(label1.compareTo(label2)==0);	
		assertTrue(label1.compareTo(label3)>0);
		assertTrue(label1.compareTo(label4)<0);
	}

}



