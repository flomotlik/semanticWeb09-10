package at.tuwien.semanticWeb.abgabe2;

import java.util.Scanner;

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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;

public class HotelManager {

	private static HotelManager hm;
	
	private OntModel ontModel;
	
	public OntModel getOntModel() {
		return ontModel;
	}

	public void setOntModel(OntModel ontModel) {
		this.ontModel = ontModel;
	}

	
	public HotelManager() {
		// hotel.owl laden
		Model model = FileManager.get().loadModel("hotel.owl");
		// wir brauchen eine erweiterte Inferenz Engine, damit (unter anderem) auch funktional inverse properties aufgeloest werden
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		ontModel.add(model);
		model = FileManager.get().loadModel("events.owl");
		//model.write(System.out);
		ontModel.add(model);
		//ontModel.write(System.out);
		// Aequivalenzen definieren: 
		OntClass event = ontModel.getOntClass(HotelNS.EVENTS_PREFIX + "Event");
		OntClass veranstaltung = ontModel.getOntClass(HotelNS.prefix + HotelNS.classVeranstaltung);
		event.addProperty(OWL.equivalentClass, veranstaltung);
		// TODO: equivalentProperty		
	}
	
	public static HotelManager getHotelManager() {
		if (hm == null) {
			hm = new HotelManager();
		}
		return hm;
	}
	
	public void loadData() {
		CSVImporter importer = new CSVImporter();
		importer.importData(ontModel);
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
	
	public double[] getPlaceOfEvent(String event) throws Exception{
	    System.out.println(event);
	    String query = "SELECT ?laenge ?breite WHERE {?va :name \"" + event + "\". ?va :findetStattIn ?ort. ?ort :laengenGrad ?laenge. ?ort :breitengrad ?breite }";
	    ResultSet result = query(query);
	    System.out.println(result);
	    while(result != null && result.hasNext()){
	        System.out.println("In While");
	        QuerySolution qs = result.next();
	        RDFNode laenge = qs.get("laenge");
	        RDFNode breite = qs.get("breite");
	        
	        if(laenge.isLiteral() && breite.isLiteral()){
	            System.out.println("Is Literal");
	            double laengengrad = ((Literal)laenge).getDouble();
	            double breitengrad = ((Literal)breite).getDouble();
	            return new double[]{laengengrad, breitengrad};
	        }
	    }
	    System.out.println("Wrong Return");
	    return new double[0];
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
	public void first() {
		String param = askParameter("<HotelName>");
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
	
	public void third() {
		String param = askParameter("<VeranstaltungsName>");
		waitForUser();
	}
	
	public void fourth() {
		String param = askParameter("<GastName>");
		waitForUser();
	}
	
	public void fifth() {
		String param = askParameter("<HotelName>");
		waitForUser();
	}
	
	public void sixth() {
		String param = askParameter("<GastName>");
		waitForUser();
	}
	
	public void seventh() {
		String param = askParameter("<GastName>");
		String param2 = askParameter("<Datum>");
		waitForUser();
	}
}
