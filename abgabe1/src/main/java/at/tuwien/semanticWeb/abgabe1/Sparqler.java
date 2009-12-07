package at.tuwien.semanticWeb.abgabe1;

import java.util.Scanner;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.OWLMiniReasonerFactory;
import com.hp.hpl.jena.util.FileManager;

public class Sparqler {

    public void first() {
        String number = this.askParameter("Anzahl");
        String query = "SELECT ?Hotel WHERE {  ?Hotel trsm:hatSterne ?sterne FILTER(?sterne >= " + number + ")}";
    }

    public void second() {
        String hotel = this.askParameter("Hotel");
        String query = "SELECT ?inNähe WHERE {  ?Hotel trsm:hatName " + hotel + ". ?Hotel trsm:istInNäheVon ?inNähe}";
    }

    public void third() {
        String name = this.askParameter("Gastname");
        String hotel = this.askParameter("Hotelname");
        String query = "SELECT ?hotel WHERE { ?guest trsm:hatName "
            + name
            + ". ?guest trsm:bucht ?buchung. ?buchung trsm:beinhaltetZimmer ?zimmer. ?zimmer trsm:gehörtZuHotel ?hotel.?hotel trsm:hatName "
            + hotel + "}";
    }

    public void fourth() {
        String query = "SELECT ?guest WHERE { ?guest trsm:bucht ?buchung. ?buchung trsm:beinhaltetZimmer ?zimmer. ?zimmer trsm:istRaucherZimmer true }";
    }

    public void fifth() {
        // TODO subclassOf Wellnessangebot geht noch nicht
        String query = "SELECT ?angebot WHERE { ?guest trsm:bucht ?buchung. ?buchung trsm:beinhaltetZimmer ?zimmer. ?zimmer trsm:istRaucherZimmer true.?guest trsm:nutzt ?angebot.}";
    }

    public void sixth() {
        String number1 = this.askParameter("Zimmernummer 1");
        String number2 = this.askParameter("Zimmernummer 2");
        String query = "SELECT ?zimmer2 WHERE { ?zimmer trsm:liegtNeben ?zimmer2. ?zimmer trsm:hatNummer ?nr1.?zimmer2 trsm:hatNummer ?nr2 FILTER(?nr1 = "
            + number1 + " && ?nr2 = " + number2 + ")}";
    }

    public void seventh() {
        String guest1 = this.askParameter("Gast 1");
        String guest2 = this.askParameter("Guest 2");
        String query = "SELECT ?guest1 ?guest2 WHERE {?guest1 trsm:hatName " + guest1 + ".?guest2 trsm:hatName "
            + guest2
            + ".{{?guest1 trsm:istAllgemeinVerwandt ?guest2} UNION {?guest1 trsm:istFamilienVerwandt ?guest2}}}";
    }

    public void eigth() {
        String query = "SELECT ?guest1 ?guest2 WHERE { "
            + "{{?guest1 trsm:istAllgemeinVerwandt ?guest2} UNION {?guest1 trsm:istFamilienVerwandt ?guest2}}. "
            + "?guest1 trsm:bucht ?buchung1. ?buchung1 trsm:beinhaltetZimmer ?zimmer1. "
            + "?guest2 trsm:bucht ?buchung2. ?buchung2 trsm:beinhaltetZimmer ?zimmer2. "
            + "?zimmer1 trsm:liegtNeben ?zimmer2}";
    }

    private String askParameter(String paramName) {
        System.out.print("Input for: " + paramName);
        return new Scanner(System.in).nextLine();
    }

    private void query(String queryString) {
        Model model = FileManager.get().loadModel("src/main/resources/tourism.owl");
        InfModel infModel = ModelFactory.createRDFSModel(model);
        Reasoner reasoner = OWLMiniReasonerFactory.theInstance().create(null);
    }
}
