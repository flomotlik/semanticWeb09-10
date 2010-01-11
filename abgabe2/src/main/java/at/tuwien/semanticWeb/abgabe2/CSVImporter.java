package at.tuwien.semanticWeb.abgabe2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class CSVImporter {
	
	private OntModel ontModel;
	
	private Map<String, RDFNode> hotelKetten = new HashMap<String, RDFNode>();
	private Map<String, RDFNode> hotels = new HashMap<String, RDFNode>();
	private Map<String, RDFNode> gaeste = new HashMap<String, RDFNode>();
	private Map<String, RDFNode> events = new HashMap<String, RDFNode>();
	
	public CSVImporter() {
		
	}

	/**
	 * importiert csv Daten aus dem Verzeichnis /csv 
	 * 
	 * laut Angabe:
	 * "Gegeben sind Tabellen mit den Daten einer Hotelverwaltungsapplikation in 
	 * Form von CSV-Dateien"
	 * 
	 * Wir gehen also davon aus, dass die neuen Daten nicht bestehende referenzieren, 
	 * sondern in sich abgeschlossen sind.
	 * 
	 * - das kann Probleme geben, wenn wir property stadt zu einer Klasse machen, bzw
	 *   Ort von events.owl verwenden.
	 *   
	 * -> denn dann müssten wir hier alle bestehenden Orte(..) cachen. 
	 * (eine andere Möglichkeit wäre, sie einzeln und on demand per SPARQL query zu laden)  
	 */
	public void importData(OntModel model) {
		ontModel = model;
		
		hotelKetten.clear();
		hotels.clear();
		gaeste.clear();
		events.clear();
		
		try {
			ClassLoader loader = getClass().getClassLoader();
			loadHotelketten(loader.getResourceAsStream("csv/hotelkette.csv"));
			loadGast(loader.getResourceAsStream("csv/gast.csv"));
			loadHotel(loader.getResourceAsStream("csv/hotel.csv"));
			loadBuchung(loader.getResourceAsStream("csv/buchung.csv"));
			loadEvents(loader.getResourceAsStream("csv/events.csv"));
			loadEventTeilnahme(loader.getResourceAsStream("csv/eventteilnahme.csv"));
		} catch (Exception e) {
			System.err.println("Beim Einlesen der csv Daten ist ein Problem aufgetreten: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private void loadHotelketten(InputStream in) throws Exception{
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile überlesen, diese enthält nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 1)) {
			// Owl Klasse erzeugen
			OntClass clazz = ontModel.getOntClass(HotelNS.prefix + HotelNS.classHotelkette);
			while ((line = reader.readNext()) != null) {				
				// pro Zeile eine neue Instanz
				String name = line[0].trim();
				
				if (existsHotelKette(name)) {
					System.out.println("Hotelkette " + name + " schon vorhanden.");
					continue;
				}
				
				Individual ind = clazz.createIndividual();
				ind.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propName), name);
				
				hotelKetten.put(name, ind);
			}
		}
		reader.close();
	}
	
	private void loadGast(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile überlesen, diese enthält nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 3)) {
			// Owl Klasse erzeugen
			OntClass clazz = ontModel.getOntClass(HotelNS.prefix + HotelNS.classGast);
			while ((line = reader.readNext()) != null) {
				// pro Zeile eine neue Instanz
				// Vorname, Nachname, email
				String vorname = line[0].trim();
				String nachname = line[1].trim();
				String email = line[2].trim();
				
				if (existsGast(vorname, nachname, email)) {
					System.out.println("Gast " + vorname + " " + nachname + " " + email + " schon vorhanden.");
					continue;
				}
				
				Individual ind = clazz.createIndividual();
				ind.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propVorname), vorname)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propNachname), nachname)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propEmail), email);
				
				gaeste.put(vorname+" "+nachname, ind);
			}
		}
		reader.close();
		
	}
	
	private void loadHotel(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile überlesen, diese enthält nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 3)) {
			// Owl Klasse erzeugen
			OntClass clazz = ontModel.getOntClass(HotelNS.prefix + HotelNS.classHotel);
			while ((line = reader.readNext()) != null) {
				// pro Zeile eine neue Instanz
				// Name, Stadt, Kette
				String name = line[0].trim();
				String stadt = line[1].trim();
				String kette = line[2].trim();
				
				if (existsHotel(name, stadt)) {
					System.out.println("Hotel " + name + " in " + stadt + " schon vorhanden.");
					continue;
				}
				
				Individual ind = clazz.createIndividual();
				ind.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propName), name)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propStadt), stadt)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propIstTeilVon), getHotelKetteByName(kette));
				
				hotels.put(name, ind);
			}
		}
		reader.close();
	}
	
	private void loadBuchung(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile überlesen, diese enthält nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 4)) {
			// Owl Klasse erzeugen
			OntClass clazz = ontModel.getOntClass(HotelNS.prefix + HotelNS.classBuchung);
			while ((line = reader.readNext()) != null) {
				// pro Zeile eine neue Instanz
				// von, bis, gast, hotel
				String von = line[0].trim();
				String bis = line[1].trim();
				String gast = line[2].trim();
				String hotel = line[3].trim();
				
				Individual ind = clazz.createIndividual();
				ind.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propVon), von)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propBis), bis)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propDurchgefuehrtVon), getGastByName(gast))
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propGehoertZu), getHotelByName(hotel));
			}
		}
		reader.close();
	}
	
	private void loadEvents(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile überlesen, diese enthält nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 3)) {
			// Owl Klasse erzeugen
			OntClass clazz = ontModel.getOntClass(HotelNS.prefix + HotelNS.classVeranstaltung);
			OntClass ortClass = ontModel.getOntClass(HotelNS.prefix + HotelNS.classOrt);
			while ((line = reader.readNext()) != null) {
				String name = line[0].trim();
				String datum = line[1].trim();
				String ort = line[2].trim();
				
				// pro Zeile eine neue Instanz
				Individual ind = clazz.createIndividual();
				// TODO check if ort exists
				Individual ortInstanz = ortClass.createIndividual();
				ortInstanz.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propName), ort);
				
				ind.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propName), name)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propDatum), datum)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propFindetStattIn), ortInstanz);
				
				events.put(line[0], ind);
				
			}
		}
		reader.close();
	}
	
	private void loadEventTeilnahme(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile überlesen, diese enthält nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 3)) {
			while ((line = reader.readNext()) != null) {
				Individual gast = (Individual)getGastByName(line[0].trim());
				gast.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propNimmtTeilAn), getEventByName(line[1].trim()));
				
			}
		}
		reader.close();
	}


	/**
	 * Searches for a HotelKette with specific name in the ontology. 
	 * @param name name of the HotelKette
	 * @return RDFNode
	 */
	private RDFNode getHotelKetteByName(String name) throws Exception{
		/*RDFNode node = hotelKetten.get(name);
		if (node != null) {
			return node;
		} else {
			throw new Exception("Hotelkette nicht gefunden: " + name);
		}
		*/
		String query = "SELECT ?x " +
						"WHERE { ?x :name \"" + name + "\"}";
		
		ResultSet result = HotelManager.getHotelManager().query(query);
		RDFNode node = null;
		if (result != null) {
			QuerySolution qs = result.next();
			node = qs.get("x");
		}
		
		return node;
	}
	/**
	 * Searches for a Gast with specific name in the ontology. 
	 * @param name name of the Gast
	 * @return RDFNode
	 */
	private RDFNode getGastByName(String name) throws Exception{
		RDFNode node = gaeste.get(name);
		if (node != null) {
			return node;
		} else {
			throw new Exception("Gast nicht gefunden: " + name);
		}
		
		/*
		String splitted[] = name.split("\\s+", 2);
		String query = "SELECT ?x " +
		"WHERE { ?x :vorname \"" + splitted[0] + "\" ;" +
				" :nachname \"" + splitted[1] + "\" ;" +
				" :email ?email}";

		ResultSet result = HotelManager.getHotelManager().query(query);
		RDFNode node = null;
		if (result != null) {
			QuerySolution qs = result.next();
			node = qs.get("x");
		}
		return node;	
		*/
	}
	/**
	 * Searches for a hotel with specific name in the ontology. 
	 * @param name name of the hotel
	 * @return RDFNode
	 */
	private RDFNode getHotelByName(String name) throws Exception {
		/*
		RDFNode node = hotels.get(name);
		if (node != null) {
			return node;
		} else {
			throw new Exception("Hotel nicht gefunden: " + name);
		}
		*/
		
		String query = "SELECT ?x " +
		"WHERE { ?x :name \"" + name + "\" ;" +
				" :stadt ?stadt ;" +
				" :istTeilVon ?hotelKette}";
		
		ResultSet result = HotelManager.getHotelManager().query(query);
		RDFNode node = null;
		if (result != null) {
			QuerySolution qs = result.next();
			node = qs.get("x");
		}
		
		return node;
		
	}
	
	/**
	 * Searches for an event with specific name in the ontology. 
	 * @param name name of the event
	 * @return RDFNode
	 */
	private RDFNode getEventByName(String name) throws Exception {
		RDFNode node = events.get(name);
		if (node != null) {
			return node;
		} else {
			throw new Exception("Event nicht gefunden: " + name);
		}
		
		/*
		String query = "SELECT ?x " +
		"WHERE { ?x :name \"" + name + "\" ;" +
				" :datum ?datum ;" +
				" :ort ?ort}";

		ResultSet result = HotelManager.getHotelManager().query(query);
		RDFNode node = null;
		if (result != null) {
			QuerySolution qs = result.next();
			node = qs.get("x");
		}
		
		return node;
		*/
	}
	
	/**
	 * Checks if HotelKette exists already in the ontology.
	 * @param name name of the HotelKette
	 * @return true if exists false otherwise
	 */
	private boolean existsHotelKette(String name) {
		String query = "ASK {?kette :name \"" + name + "\"}";
		
		try {
			return HotelManager.getHotelManager().askQuery(query);
		} catch (Exception e) {
			System.out.println("Problem bei Verarbeitung der query: ");
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean existsGast(String first, String last, String email) {
		// TODO
		return false;
	}
	
	private boolean existsHotel(String name, String city) {
		// TODO
		return false;
	}

}
