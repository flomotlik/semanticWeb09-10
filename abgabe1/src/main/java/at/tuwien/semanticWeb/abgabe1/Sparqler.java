package at.tuwien.semanticWeb.abgabe1;

import java.util.Scanner;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;

public class Sparqler {
	
	private Model model;
	
	public Sparqler() {
		model = FileManager.get().loadModel("tourismus.owl");
	}

    public void first() {
        String number = this.askParameter("Anzahl");
        String query = "SELECT ?x " +
        				"WHERE {?hotel trsm:hatSterne ?sterne ; " +
        						"trsm:hatName ?x " +
        						"FILTER(?sterne >= " + number + ")}";
        printSelectQuery(query);
    }

    public void second() {
        String hotel = this.askParameter("Hotel");
        String query = "SELECT ?x " +
        				"WHERE {?hotel trsm:hatName \"" + hotel + "\" ; " +
        						"trsm:istInNaeheVon ?inNaehe ." +
        						"{?inNaehe trsm:hatBezeichnung ?x} " +
        						"UNION " + 
        						"{?inNaehe trsm:hatName ?x}}";
        printSelectQuery(query);
    }

    public void third() {
        String name = this.askParameter("Gastname");
        String hotel = this.askParameter("Hotelname");
        String query = "ASK {?guest trsm:hatName " + "\"" + name + "\"" +
            ". ?guest trsm:bucht ?buchung . " +
            "?buchung trsm:beinhaltetZimmer ?zimmer . " +
            "?zimmer trsm:gehoertZuHotel ?hotel . " +
            "?hotel trsm:hatName " + "\"" + hotel + "\"}";
        printAskQuery(query);
    }

    public void fourth() {
        String query = "SELECT ?x " +
        				"WHERE {?guest trsm:bucht ?buchung . " +
        				"?buchung trsm:beinhaltetZimmer ?zimmer . " +
        				"?zimmer trsm:istRaucherZimmer true . " +
        				"?guest trsm:hatName ?x}";
        printSelectQuery(query);
    }

    public void fifth() {
        String query = "SELECT ?x " +
        				"WHERE { ?guest trsm:bucht ?buchung . " +
        				"?buchung trsm:beinhaltetZimmer ?zimmer . " +
        				"?zimmer trsm:istRaucherZimmer true . " +
        				"?guest trsm:nutzt ?angebot . " +
        				"?angebot trsm:hatBezeichnung ?x}";
        printSelectQuery(query);
    }

    public void sixth() {
        String number1 = this.askParameter("Zimmernummer 1");
        String number2 = this.askParameter("Zimmernummer 2");
        String query = "ASK {?zimmer trsm:liegtNeben ?zimmer2 . " +
        				"?zimmer trsm:hatNummer ?nr1 . " +
        				"?zimmer2 trsm:hatNummer ?nr2 FILTER(?nr1 = "
            + number1 + " && ?nr2 = " + number2 + ")}";
        printAskQuery(query);
    }

    public void seventh() {
        String guest1 = this.askParameter("Gast 1");
        String guest2 = this.askParameter("Guest 2");
        String query = "ASK {?guest1 trsm:hatName \"" + guest1 + "\" . " +
        				"?guest2 trsm:hatName " + "\"" + guest2 + "\"" +
        				".{{?guest1 trsm:istAllgemeinVerwandt ?guest2} " +
        				"UNION " +
        				"{?guest1 trsm:istFamilienVerwandt ?guest2}}}";
        printAskQuery(query);
    }

    public void eigth() {
        String query = "SELECT ?x " +
        				"WHERE { " +
        				"{?guest1 trsm:istAllgemeinVerwandt ?guest2} " +
        				"UNION " +
        				"{?guest1 trsm:istFamilienVerwandt ?guest2} . " +
        				"?guest1 trsm:bucht ?buchung1 . " +
        				"?buchung1 trsm:beinhaltetZimmer ?zimmer1 . " +
        				"?guest2 trsm:bucht ?buchung2 . " +
        				"?buchung2 trsm:beinhaltetZimmer ?zimmer2 . " +
        				"?zimmer1 trsm:liegtNeben ?zimmer2 . " +
        				"?guest1 trsm:hatName ?x}";
        printSelectQuery(query);
    }

    private String askParameter(String paramName) {
        System.out.print("Input for: " + paramName);
        return new Scanner(System.in).nextLine();
    }
    
    private void printSelectQuery(String queryString){
        ResultSet results = this.query(queryString);
        if (results != null) {
            System.out.println("Result:");
            while (results.hasNext()) {
            	QuerySolution qs = results.next();
            	RDFNode rdfNode = qs.get("x");
            	if (rdfNode.isLiteral())
            		System.out.println("\t" + rdfNode.as(Literal.class).getString());
            	else
            		System.out.println("\t" + rdfNode.toString());
                
            }
            System.out.print("Druecken sie <Eingabe> um zum Menue zurueckzukehren");
            new Scanner(System.in).nextLine();
        }
    }
    
    private void printAskQuery(String queryString){
        try {
        	if (this.askQuery(queryString))
        		System.out.println("Ja");
        	else
        		System.out.println("Nein");
		} catch (Exception e) {
			System.out.println("Entschuldigung, beim Verarbeiten der Abfrage ist ein Fehler aufgetreten: " + e.getMessage());
		}
        System.out.print("Druecken sie <Eingabe> um zum Menue zurueckzukehren");
        new Scanner(System.in).nextLine();
    }
	
    private ResultSet query(String queryString) {
    	try {
			String newQueryString = "PREFIX trsm:<http://www.owl-ontologies.com/tourism.owl#> PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" + queryString;
			QueryExecution qe = QueryExecutionFactory.create(newQueryString, model);
			qe.close();
            System.out.flush();
            return qe.execSelect();
		} catch (Throwable e) {
			System.out.println("Entschuldigung, beim Verarbeiten der Abfrage ist ein Fehler aufgetreten: " + e.getMessage());
			return null;
		}
    }
    
    private boolean askQuery(String queryString) throws Exception {
    	try {
			String newQueryString = "PREFIX trsm:<http://www.owl-ontologies.com/tourism.owl#> PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" + queryString;
			QueryExecution qe = QueryExecutionFactory.create(newQueryString, model);
			qe.close();
            System.out.flush();
            return qe.execAsk();
		} catch (Throwable e) {
			throw new Exception(e.getMessage());
		}
    }
}
