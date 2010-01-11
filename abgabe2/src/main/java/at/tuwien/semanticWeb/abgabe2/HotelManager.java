package at.tuwien.semanticWeb.abgabe2;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;

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
		// wir brauchen eine erweiterte Inferenz Engine, damit (unter anderem) auch funktional inverse properties aufgelöst werden
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		ontModel.add(model);
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
	 * führt query aus und gibt das Ergebnis auf std.out aus
	 * - das Ergebnis muss in der Variable ?x bereitgestellt werden!
	 * 
	 * @param queryString
	 */
    public void printSelectQuery(String queryString){
        try {
			ResultSet results = query(queryString);
			if (results != null) {
			    System.out.println("Result:");
			    while (results.hasNext()) {
			    	QuerySolution qs = results.next();
			    	RDFNode rdfNode = qs.get("x");
			    	if (rdfNode.isLiteral()) {
			    		Literal l = (Literal)rdfNode.as(Literal.class);
			    		System.out.println("\t" + l.getString());
			    	}  else {
			    		System.out.println("\t" + rdfNode.toString());
			    	}
			        
			    }
			}
		} catch (Exception e) {
			System.out.println("Entschuldigung, beim Verarbeiten der Abfrage ist ein Fehler aufgetreten: " + e.getMessage());
		}
    }
	public ResultSet query(String queryString) throws Exception {
    	try {
			String newQueryString = "PREFIX :<" + HotelNS.prefix + "> PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" + queryString;
			QueryExecution qe = QueryExecutionFactory.create(newQueryString, ontModel);
			qe.close();
            return qe.execSelect();
		} catch (Throwable e) {
			throw new Exception(e);
		}
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

}
