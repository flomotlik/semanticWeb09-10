package at.tuwien.semanticWeb.abgabe2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.tuwien.semanticWeb.abgabe2.HotelManager;

public class HotelManagerTest {
	
	private HotelManager manager;

	@Before
	public void setUp() throws Exception {
		manager = HotelManager.getHotelManager();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testEquivalentClassEvent() {
		System.out.println("List all Veranstaltungen:");
		
		String qry = "SELECT ?name ?x " + 
			         "WHERE { ?x rdf:type :Veranstaltung  . " +
			                  " ?x :name  ?name }";
		manager.printSelectQuery(qry);
	}
	
	@Test
	public void testLoadData() throws Exception {
		System.out.println("Before import:");
		String qry = "SELECT ?x " + 
			         "WHERE { ?kette :name \"Best Hotels\" . " +
			                 " ?hotel :" +  HotelNS.propIstTeilVon + " ?kette . " +
			                 " ?hotel :name ?x }"; 
//		manager.printSelectQuery(qry);
	
		System.out.println("After import:");
		manager.loadData();
//		manager.printSelectQuery(qry);
		manager.printSelectQuery("SELECT ?x WHERE { ?ort :laenderCode ?x.}");
		manager.printSelectQuery("Select ?x Where { ?ort :breitengrad ?x}");
		manager.printSelectQuery("Select ?x Where { ?va :name \"Skispringen\". ?va :findetStattIn ?ort.?ort :name ?x}");
		//double[] place = manager.getPlaceOfEvent("Skispringen");
		//System.out.println("Laenge: " + place[0] + " Breite: " + place[1]);
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
		//manager.loadData();
		manager.printSelectQuery(qry);
		
	}
	@Test
	public void testLaender() {
		manager.loadData();
		manager.updateOrtsInfo();
		
		String qry = "SELECT DISTINCT ?ort ?land ?code " + 
        	"WHERE { ?o :name ?ort ;" +
        	" :istIn ?l . " +
        	" ?l :name ?land ;" + 
        	" :laenderCode ?code}";
		
		manager.printSelectQuery(qry);
	}
	
	@Test
	public void testDates() {
		manager.loadData();
		
		String qry = "SELECT ?event ?datum " + 
    	"WHERE { ?e :name ?event ;" +
    	" :datum ?datum . " +
    	" ?e rdf:type :Veranstaltung}";
	
		manager.printSelectQuery(qry);
		System.out.println("Nach Filter:");
	
		qry = "SELECT ?event ?d " + 
    	"WHERE { ?e :name ?event ." +
    	" ?e :datum ?d . " +
    	" FILTER (?d > \"2009-12-16\")}"; 
    	
		manager.printSelectQuery(qry);
	}
	
	@Test
	public void testBuchungsDatum() {
		manager.loadData();
		
		String qry = "SELECT ?gast ?hotel ?von ?bis " + 
    	"WHERE { ?b :durchgefuehrtVon ?g ; " +
    	" :von ?von ;" +
    	" :bis ?bis ;" +
    	" :gehoertZu ?h . " +
    	" ?h :name ?hotel ." + 
    	" ?g :nachname ?gast }";
	
		manager.printSelectQuery(qry);
		System.out.println("==========MAX MUSTER============");
		
		qry = "SELECT ?hotel ?von ?bis " + 
    	"WHERE { ?b :durchgefuehrtVon ?g ; " +
    	" :von ?von ;" +
    	" :bis ?bis ;" +
    	" :gehoertZu ?h . " +
    	" ?h :name ?hotel ." + 
    	" ?g :nachname \"Muster\" ; " +
    	" :vorname \"Max\"}";
	
		manager.printSelectQuery(qry);
	}
	
}
