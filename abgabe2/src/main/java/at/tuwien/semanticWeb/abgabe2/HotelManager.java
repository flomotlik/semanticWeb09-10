package at.tuwien.semanticWeb.abgabe2;

import java.util.Scanner;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.resultset.ResultsFormat;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class HotelManager {

	private static HotelManager hm;
	
	private OntModel ontModel;
	
	private Geonames geonames = new Geonames();
	private CSVImporter importer;
	
	public OntModel getOntModel() {
		return ontModel;
	}

	public void setOntModel(OntModel ontModel) {
		this.ontModel = ontModel;
	}

	public OntClass ort;
	public OntClass land;
	public OntClass veranstaltung;
	public Property name;
	public Property datum;
	public Property findetStattIn;
	public Property breitengrad;
	public Property laengengrad;
	public Property zeitzone;
	public Property istIn;
	public Property hatGebiete;
	public Property laenderCode;
	
	public Property dcSubject; 
	
	public static String foafPrefix = "http://pephimon.big.tuwien.ac.at/FOAF_Service/resources/foaf/email/";

    private Model foafModel;
	
	public HotelManager() {
		// hotel.owl laden
		Model model = FileManager.get().loadModel("hotel.owl");
		// wir brauchen eine erweiterte Inferenz Engine, damit (unter anderem) auch funktional inverse properties aufgel�st werden
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		ontModel.add(model);
		model = FileManager.get().loadModel("events.owl");
		ontModel.add(model);

		reloadOwls();
		// Aequivalenzen definieren:
		// Event ~= Veranstaltung
		ontModel.getOntClass(HotelNS.EVENTS_PREFIX + "Event").addProperty(OWL.equivalentClass, veranstaltung);
		// - name
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "name").addProperty(OWL.equivalentProperty, name);
		// - datum
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "datum").addProperty(OWL.equivalentProperty, datum);

		// Place ~= Ort
		ontModel.getOntClass(HotelNS.EVENTS_PREFIX + "Place").addProperty(OWL.equivalentClass, ort);
		// - takesPlaceAt ~= findetStattIn
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "takesPlaceAt").addProperty(OWL.equivalentProperty, findetStattIn);
		// - name: s.o.
		// - breitengrad
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "latitude").addProperty(OWL.equivalentProperty, breitengrad);
		// - laengengrad
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "longitude").addProperty(OWL.equivalentProperty, laengengrad);
		// - zeitzone
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "timezone").addProperty(OWL.equivalentProperty, zeitzone);
		// - land, laendercode, istIn, htaGebiete
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "Country").addProperty(OWL.equivalentClass, land);
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "countryCode").addProperty(OWL.equivalentProperty, laenderCode);
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "contains").addProperty(OWL.equivalentProperty, hatGebiete);
		ontModel.getProperty(HotelNS.EVENTS_PREFIX + "isLocatedIn").addProperty(OWL.equivalentProperty, istIn);
		
		ontModel.setNsPrefix("dct", "http://purl.org/dc/terms/");
		model = FileManager.get().loadModel("interests.owl");
		ontModel.add(model);
		dcSubject = ontModel. getProperty("http://purl.org/dc/terms/subject");
		
	}
	
	private void reloadOwls(){
		veranstaltung = ontModel.getOntClass(HotelNS.prefix + HotelNS.classVeranstaltung);
		ort = ontModel.getOntClass(HotelNS.prefix + HotelNS.classOrt);
		land = ontModel.getOntClass(HotelNS.prefix + HotelNS.classLand);
		
		name = ontModel.getProperty(HotelNS.prefix + HotelNS.propName);
		datum = ontModel.getProperty(HotelNS.prefix + HotelNS.propDatum);
		findetStattIn = ontModel.getProperty(HotelNS.prefix + HotelNS.propFindetStattIn);
		breitengrad = ontModel.getProperty(HotelNS.prefix + HotelNS.propLatitude);
		laengengrad = ontModel.getProperty(HotelNS.prefix + HotelNS.propLongitude);
		zeitzone = ontModel.getProperty(HotelNS.prefix + HotelNS.propTimezone);
		istIn = ontModel.getProperty(HotelNS.prefix + HotelNS.propIstIn);
		hatGebiete = ontModel.getProperty(HotelNS.prefix + HotelNS.propHatGebiete);
		laenderCode = ontModel.getProperty(HotelNS.prefix + HotelNS.propCountryCode);
		
	}
	
	/**
	 * erweitert Ortsinfo mit Hilfe von geonames
	 */
	public void updateOrtsInfo() {
		// TODO: alle Orte auslesen, mit Ortsinfos ergaenzen
		try {
		    System.out.println("Starting update");
		    ResIterator results = ontModel.listSubjectsWithProperty(RDF.type, this.ort);
		    while (results.hasNext()) {
		    	Individual landIndividual = null;
			    Resource ortInstanz = results.next();
			    String name = ontModel.getProperty(ortInstanz, this.name).getString();
			    System.out.println("Ort to update: " + name.toString());
				PlaceData data = geonames.getData(name.toString());
				if(data != null){
					// Check if Land exists
					if(importer.existsLand(data.getCountry(), data.getCountryCode())) {
						landIndividual = importer.getLand(data.getCountry(), data.getCountryCode()).as(Individual.class);
					// If not create new Individual with data from geoservices
					} else {
						landIndividual = this.land.createIndividual();
						landIndividual.addProperty(this.name, data.getCountry());
						landIndividual.addProperty(this.laenderCode, data.getCountryCode());
					}
					// Assign Land to Ort
					ortInstanz.addProperty(this.istIn, landIndividual);
					ortInstanz.addProperty(this.breitengrad, data.getLatitude());
					ortInstanz.addProperty(this.laengengrad, data.getLongitude());
					ortInstanz.addProperty(this.zeitzone, data.getTimezone());
					//ortInstanz.addProperty(this.laenderCode, data.getCountryCode());
				}
			}
		} catch (Exception e) {
			System.out.println("An Orten nichts neues.");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.flush();
			System.err.flush();
		}
		
	}
	public static HotelManager getHotelManager() {
		if (hm == null) {
			hm = new HotelManager();
		}
		return hm;
	}
	
	public void loadData() {
		importer = new CSVImporter();
		importer.importData(this);
	}
	

	/**
	 * f�hrt query aus und gibt das Ergebnis auf std.out aus
	 * - das Ergebnis muss in der Variable ?x bereitgestellt werden!
	 * 
	 * @param queryString
	 */
    public void printSelectQuery(String queryString){
        try {
			ResultSet results = query(queryString);
			if (results != null) {
			    System.out.println("Result:");
			    ResultSetFormatter.out(System.out, results);
//			    while (results.hasNext()) {
//			    	QuerySolution qs = results.next();
//			    	RDFNode rdfNode = qs.get("x");
//			    	if (rdfNode.isLiteral()) {
//			    		Literal l = (Literal)rdfNode.as(Literal.class);
//			    		System.out.println("\t" + l.getString());
//			    	}  else {
//			    		System.out.println("\t" + rdfNode.toString());
//			    	}
//			        
//			    }
			}
		} catch (Exception e) {
			System.out.println("Entschuldigung, beim Verarbeiten der Abfrage ist ein Fehler aufgetreten: " + e.getMessage());
		}
    }
    
    public void printSelectFoafQuery(String query) {
    	try {
    	    System.out.println(query);
			ResultSet results = queryFoaf(query);
			if (results != null) {
			    System.out.println("Result:");
			    ResultSetFormatter.out(System.out, results);
//			    while (results.hasNext()) {
//			    	QuerySolution qs = results.next();
//			    	RDFNode rdfNode = qs.get("x");
//			    	if (rdfNode.isLiteral()) {
//			    		Literal l = (Literal)rdfNode.as(Literal.class);
//			    		System.out.println("\t" + l.getString());
//			    	}  else {
//			    		System.out.println("\t" + rdfNode.toString());
//			    	}
//			        
//			    }
			}
		} catch (Exception e) {
			System.out.println("Entschuldigung, beim Verarbeiten der Abfrage ist ein Fehler aufgetreten: " + e.getMessage());
			e.printStackTrace();
		}
    }
    
	public ResultSet query(String queryString) throws Exception {
    	try {
			String newQueryString = "PREFIX :<" + HotelNS.prefix + "> PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + queryString;
			QueryExecution qe = QueryExecutionFactory.create(newQueryString, ontModel);
			qe.close();
            return qe.execSelect();
		} catch (Throwable e) {
			throw new Exception(e);
		}
    }
	
	public ResultSet queryFoaf(String query) throws Exception {
		try {
			String newQueryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n" + query;
			QueryExecution qe = QueryExecutionFactory.create(newQueryString, foafModel);
			qe.close();
            return qe.execSelect();
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}
	
	public double[] getPlaceOfEvent(String event) throws Exception{
	    String query = "SELECT ?laenge ?breite WHERE {?va :name \"" + event + "\". ?va :findetStattIn ?ort. ?ort :laengenGrad ?laenge. ?ort :breitengrad ?breite }";
	    ResultSet result = query(query);
	    while(result != null && result.hasNext()){
	        QuerySolution qs = result.next();
	        RDFNode laenge = qs.get("laenge");
	        RDFNode breite = qs.get("breite");
	        
	        if(laenge.isLiteral() && breite.isLiteral()){
	            double laengengrad = ((Literal)laenge).getDouble();
	            double breitengrad = ((Literal)breite).getDouble();
	            return new double[]{laengengrad, breitengrad};
	        }
	    }
	    System.out.println("Wrong Return");
	    return new double[2];
	}
	
	/**
	 * Performs an ask query.
	 * @param queryString query to be executed
	 * @return true if there are some results false otherwise
	 * @throws Exception
	 */
	public boolean askQuery(String queryString) throws Exception {
    	try {
			String newQueryString = "PREFIX :<" + HotelNS.prefix + "> PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" + queryString;
			QueryExecution qe = QueryExecutionFactory.create(newQueryString, ontModel);
			qe.close();
            return qe.execAsk();
		} catch (Throwable e) {
			throw new Exception(e);
		}
    }
	
	/**
	 * Reads value for specified parameter from standard input.
	 * @param paramName name of the parameter
	 * @return String
	 */
	private String askParameter(String paramName) {
        System.out.print("Input for: " + paramName);
        return new Scanner(System.in).nextLine();
    }

	/**
	 * Waits until user hits enter.
	 */
	private void waitForUser() {
		System.out.print("Druecken sie <Eingabe> um zum Menue zurueckzukehren");
		new Scanner(System.in).nextLine();
	}
	
	//========
	// QUERIES
	//========
	public void first() throws Exception {
	    Distances distances = new Distances();
		String param = askParameter("<HotelName>");
		ResultSet query = this.query("select ?laenge ?breite Where{?hotel :name \"" + param +"\". ?hotel :niedergelassenIn ?ort. ?ort :breitengrad ?breite. ?ort :laengenGrad ?laenge}");
		if(query != null && query.hasNext()){
		    QuerySolution solutino = query.next();
		    double laenge = ((Literal)solutino.get("laenge")).getDouble();
		    double breite = ((Literal)solutino.get("breite")).getDouble();
		    ResultSet veranstaltungen = this.query("select ?name ?laenge ?breite Where{?va rdf:type :Veranstaltung. ?va :name ?name. ?va :findetStattIn ?ort. ?ort :laengenGrad ?laenge. ?ort :breitengrad ?breite }");
		    while(veranstaltungen.hasNext()){
		        QuerySolution veranstaltung = veranstaltungen.next();
		        double laengeVA = ((Literal)veranstaltung.get("laenge")).getDouble();
		        double breiteVA = ((Literal)veranstaltung.get("breite")).getDouble();
		        String va = ((Literal)veranstaltung.get("name")).getString();
//		        System.out.println(va);
		        double distancesToEvent = distances.distances(breite, laenge, breiteVA, laengeVA);
                        if(distancesToEvent <= 100){
		            System.out.println("In Nähe: " + va + " Entfernung: " + distancesToEvent);
		        }
		    }
		    
		}
		
		waitForUser();
	}
	
	public void second() {
		String param = askParameter("<VeranstaltungsName>");
		double[] placeParams;
		try {
			placeParams = getPlaceOfEvent(param);
			for(Forecast forecast : new WeatherService().getForecast(placeParams[1], placeParams[0])){
	            System.out.println(forecast.toString());
	        }
		} catch (Exception e) {
			System.out.println("Es ist ein Problem bei der Wettervorhersage entstanden.");
			e.printStackTrace();
		}
        
		waitForUser();
	}
	
	public void third() throws Exception {
		String param = askParameter("<VeranstaltungsName>");
		String queryString = "ASK {?event :name \"" + param + "\" ;}";
        boolean exists = askQuery(queryString);
		if(exists){
		    String abstract1 = new DBPedia().getAbstract(param);
		    System.out.println("-----------------------------------------------");
		    System.out.println(abstract1);
		    System.out.println("-----------------------------------------------");
		}else{
		    System.out.println("Doesn't exist");
		}
		waitForUser();
	}
	
	public void fourth() throws Exception {
		String param = askParameter("<GastVorname>");
		String param2 = askParameter("<GastNachname>");
		
		// Search for Gast and his email
		String query = "SELECT ?x " +
				"WHERE { ?gast :vorname  \"" + param + "\" ;" +
				" :nachname \"" + param2 + "\" ;" + 
				" :email ?x ." +
				" ?gast rdf:type :Gast}";
		printSelectQuery(query);
		ResultSet results = null;
		try {
			results = query(query);
		} catch (Exception e) {
			System.out.println("Es ist ein Problem beim Suchen des Gastes entstanden.");
			e.printStackTrace();
		}
		
		if(results.hasNext()) {
			// Extract email from ResultSet as string
			QuerySolution qs = results.next();
	    	RDFNode rdfNode = qs.get("x");
	    	Literal l = (Literal)rdfNode.as(Literal.class);
	    	
	    	ResultSet friendsResultSet = getFriendsResultSet(foafPrefix + l.getString());
			System.out.println("Freunde:");
			while(friendsResultSet.hasNext()){
			 QuerySolution queryS = friendsResultSet.next();
			 RDFNode link = queryS.get("link");
			 String name = ((Literal)queryS.get("name")).getString();
			 System.out.println(name);
			 printNames(getFriendsResultSet(link.toString()));
			}
		} else {
			System.out.println("Kein Gast mit name " + param + " " + param2 + " gefunden.");
		}
		waitForUser();
	}
	
	private void printNames(ResultSet friendsResultSet){
	    while(friendsResultSet.hasNext()){
                QuerySolution queryS = friendsResultSet.next();
                String name = ((Literal)queryS.get("name")).getString();
                System.out.println("    " + name);
               }
	}
	
	private ResultSet getFriendsResultSet(String url) throws Exception{
	    foafModel = ModelFactory.createDefaultModel();
//            System.out.println(url);
            foafModel.read(url);
            
            // TODO: print friends direct + indirect
            String query = "select ?name ?link where { ?person foaf:knows ?x. ?x foaf:name ?name. ?x rdfs:seeAlso ?link}";
            return queryFoaf(query);
	}
	
	public void printFriends(String url){
	    
	}
	
	public void fifth() {
		String param = askParameter("<HotelName>");
		waitForUser();
	}

	
	/**
	 * 6 Welche persoenliche Interessen hat <GastVorname> <GastNachname>? 
	 */
	public void sixth() throws Exception{
		String param = "Max";//askParameter("<GastVorname>");
		String param2 = "Muster"; //askParameter("<GastNachname>");
		
		String queryString = 
			"SELECT DISTINCT ?i" +
			" WHERE { ?p a :Gast . " +
			"         ?p :vorname  \"" + param +"\" . " +
			"         ?p :nachname  \"" + param2 +"\" . " +
			"         ?p :nimmtTeilAn ?i . " + 
			"         ?i :name ?iname} ";
		
		ResultSet results = query(queryString);
		ResultSetFormatter.out(System.out, results);
		while (results.hasNext()){
		}
		
		waitForUser();
	}
	
	public void seventh() {
		String param = askParameter("<GastName>");
		String param2 = askParameter("<Datum>");
		waitForUser();
	}
	
	public void eight() {
		String query = "SELECT DISTINCT ?x ?y " +
		"WHERE { ?event :name ?x ; " +
		":datum ?y ; " +
		":findetStattIn ?ort}";
		printSelectQuery(query);
		waitForUser();
	}
}
