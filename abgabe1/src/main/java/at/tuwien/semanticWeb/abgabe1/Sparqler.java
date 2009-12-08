package at.tuwien.semanticWeb.abgabe1;

import java.net.URL;
import java.util.Scanner;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class Sparqler {

    public void first() {
        String number = this.askParameter("Anzahl");
        String query = "SELECT ?Hotel WHERE {  ?Hotel trsm:hatSterne ?sterne FILTER(?sterne >= " + number + ")}";
        printQuery(query);
    }

    public void second() {
        String hotel = this.askParameter("Hotel");
        String query = "SELECT ?inNaehe WHERE {  ?hotel trsm:hatName \"" + hotel + "\" . ?hotel trsm:istInNaeheVon ?inNaehe}";
        printQuery(query);
    }

    public void third() {
        String name = this.askParameter("Gastname");
        String hotel = this.askParameter("Hotelname");
        String query = "SELECT ?hotel WHERE { ?guest trsm:hatName "
            + "\"" + name + "\"" 
            + ". ?guest trsm:bucht ?buchung. ?buchung trsm:beinhaltetZimmer ?zimmer. ?zimmer trsm:gehoertZuHotel ?hotel.?hotel trsm:hatName "
            + "\"" + hotel + "\"}";
        printYesNoQuery(query);
    }

    public void fourth() {
        String query = "SELECT ?guest WHERE { ?guest trsm:bucht ?buchung. ?buchung trsm:beinhaltetZimmer ?zimmer. ?zimmer trsm:istRaucherZimmer true }";
        printQuery(query);
    }

    public void fifth() {
        String query = "SELECT ?angebot WHERE { ?guest trsm:bucht ?buchung. ?buchung trsm:beinhaltetZimmer ?zimmer. ?zimmer trsm:istRaucherZimmer true.?guest trsm:nutzt ?angebot}";
        printQuery(query);
    }

    public void sixth() {
        String number1 = this.askParameter("Zimmernummer 1");
        String number2 = this.askParameter("Zimmernummer 2");
        String query = "SELECT ?zimmer2 WHERE { ?zimmer trsm:liegtNeben ?zimmer2. ?zimmer trsm:hatNummer ?nr1.?zimmer2 trsm:hatNummer ?nr2 FILTER(?nr1 = "
            + number1 + " && ?nr2 = " + number2 + ")}";
        printYesNoQuery(query);
    }

    public void seventh() {
        String guest1 = this.askParameter("Gast 1");
        String guest2 = this.askParameter("Guest 2");
        String query = "SELECT ?guest1 ?guest2 WHERE {?guest1 trsm:hatName \"" + guest1 + "\" . ?guest2 trsm:hatName "
            + "\"" + guest2 + "\""
            + ".{{?guest1 trsm:istAllgemeinVerwandt ?guest2} UNION {?guest1 trsm:istFamilienVerwandt ?guest2}}}";
        printYesNoQuery(query);
    }

    public void eigth() {
        String query = "SELECT ?guest1 ?guest2 WHERE { "
            + "{{?guest1 trsm:istAllgemeinVerwandt ?guest2} UNION {?guest1 trsm:istFamilienVerwandt ?guest2}}. "
            + "?guest1 trsm:bucht ?buchung1. ?buchung1 trsm:beinhaltetZimmer ?zimmer1. "
            + "?guest2 trsm:bucht ?buchung2. ?buchung2 trsm:beinhaltetZimmer ?zimmer2. "
            + "?zimmer1 trsm:liegtNeben ?zimmer2}";
        printQuery(query);
    }

    private String askParameter(String paramName) {
        System.out.print("Input for: " + paramName);
        return new Scanner(System.in).nextLine();
    }
    
    private void printQuery(String queryString){
        ResultSet results = this.query(queryString);
        if (results != null) {
            System.out.println("Result:");
            while (results.hasNext()) {
                System.out.println("   " + results.next());
            }
            System.out.print("Dr�cken sie <Eingabe> um zum Men� zur�ckzukehren");
            new Scanner(System.in).nextLine();
        }
    }
    
    private void printYesNoQuery(String queryString){
        ResultSet results = this.query(queryString);
        if(results.hasNext()){
            System.out.println("True");
        }else{
            System.out.println("False");
        }
        System.out.print("Dr�cken sie <Eingabe> um zum Men� zur�ckzukehren");
        new Scanner(System.in).nextLine();
    }

    private ResultSet query(String queryString) {
    	// TODO: m�ssen wir wirklich jedes Mal das Model neu laden, oder d�rfen wir das wiederverwenden? - wahrscheinlich schon
    	try {
			Model model = FileManager.get().loadModel("tourismus.owl");
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
}
