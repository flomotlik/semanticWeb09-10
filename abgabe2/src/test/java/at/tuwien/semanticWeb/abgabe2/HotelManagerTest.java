package at.tuwien.semanticWeb.abgabe2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.tuwien.semanticWeb.abgabe2.HotelManager;

public class HotelManagerTest {
	
	private HotelManager manager;

	@Before
	public void setUp() throws Exception {
		manager = new HotelManager();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadData() {
		System.out.println("Before import:");
		String qry = "SELECT ?x " + 
			         "WHERE { ?kette :name \"Best Hotels\" . " +
			                 " ?hotel :" +  HotelNS.propIstTeilVon + " ?kette . " +
			                 " ?hotel :name ?x }"; 
		manager.printSelectQuery(qry);
	
		System.out.println("After import:");
		manager.loadData();
		manager.printSelectQuery(qry);
		
	}

	@Test
	public void testInverseProperty() {
		System.out.println("Inverse property before import:");
		String qry = "SELECT ?x " + 
			         "WHERE { ?kette :"  + HotelNS.propBestehtAus + " ?hotel  ." +
			         " ?hotel :name ?x . " +
			         "  ?kette :name \"Best Hotels\" }";
		manager.printSelectQuery(qry);
	
		System.out.println("Inverse property after import:");
		manager.loadData();
		manager.printSelectQuery(qry);
		
	}
	
}
