package at.tuwien.semanticWeb.abgabe2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;

public class HotelManager {

	private Model model;
	
	public HotelManager() {
		// hotel.owl laden
		
		// Kommen wir mit einem RDF Model aus, müssten wir hier nicht ein 
		// OntModel erzeugen und befüllen ?
		Model loadedModel = FileManager.get().loadModel("hotel.owl");
		model = ModelFactory.createInfModel(ReasonerRegistry.getOWLMiniReasoner(), loadedModel);
		// Daten aus csv hinzufügen
		loadData();
	}
	
	public void loadData() {
		try {
			loadBuchungen(getClass().getResourceAsStream("/csv/Buchungen.csv"));
		} catch (IOException e) {
			System.err.println("Beim Einlesen der csv Daten ist ein Problem aufgetreten: ");
			e.printStackTrace();
		}
	}
	
	private void loadBuchungen(InputStream in) throws IOException {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile lesen, diese enthält nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if (line != null) {
			// Owl Klasse erzeugen
			
			while ((line = reader.readNext()) != null) {
				
				
			}
		}
	}
	
	// ...

}
