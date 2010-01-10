package at.tuwien.semanticWeb.abgabe2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class CSVImporter {
	
	private OntModel ontModel;
	
	private Map<String, RDFNode> hotelKetten = new HashMap<String, RDFNode>();
	private Map<String, RDFNode> hotels = new HashMap<String, RDFNode>();
	private Map<String, RDFNode> gaeste = new HashMap<String, RDFNode>();
	
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
		
		try {
			ClassLoader loader = getClass().getClassLoader();
			loadHotelketten(loader.getResourceAsStream("csv/hotelkette.csv"));
			loadGast(loader.getResourceAsStream("csv/gast.csv"));
			loadHotel(loader.getResourceAsStream("csv/hotel.csv"));
			loadBuchung(loader.getResourceAsStream("csv/buchung.csv"));
		
//			loadEvents(loader.getResourceAsStream("csv/events.csv"));
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
				Individual ind = clazz.createIndividual();
				ind.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propName), name)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propStadt), stadt)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propIstTeilVon), getHotelketteByName(kette));
				
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
			OntClass clazz = ontModel.getOntClass(HotelNS.prefix + HotelNS.classEvent);
			while ((line = reader.readNext()) != null) {
				// pro Zeile eine neue Instanz
				Individual ind = clazz.createIndividual();
				ind.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propVorname), line[0])
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propNachname), line[1])
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propEmail), line[2]);
			}
		}
		reader.close();
	}


	/**
	 * derzeit lookup per Hashmap
	 * - evtentuell on demand mit SPARQL laden?
	 * @param name
	 * @return
	 */
	private RDFNode getHotelketteByName(String name) throws Exception{
		RDFNode node = hotelKetten.get(name);
		if (node != null) {
			return node;
		} else {
			throw new Exception("Hotelkette nicht gefunden: " + name);
		}
	}
	/**
	 * derzeit lookup per Hashmap
	 * - evtentuell on demand mit SPARQL laden?
	 * @param name
	 * @return
	 */
	private RDFNode getGastByName(String name) throws Exception{
		RDFNode node = gaeste.get(name);
		if (node != null) {
			return node;
		} else {
			throw new Exception("Gast nicht gefunden: " + name);
		}
	}
	/**
	 * derzeit lookup per Hashmap
	 * - evtentuell on demand mit SPARQL laden?
	 * @param name
	 * @return
	 */
	private RDFNode getHotelByName(String name) throws Exception {
		RDFNode node = hotels.get(name);
		if (node != null) {
			return node;
		} else {
			throw new Exception("Hotel nicht gefunden: " + name);
		}
		
	}

}
