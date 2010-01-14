package at.tuwien.semanticWeb.abgabe2;

import java.io.InputStream;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class CSVImporter {
	
	private OntModel ontModel;
	private HotelManager hotelM;
	
	private Geonames geonames = new Geonames();
	
	//private Map<String, RDFNode> hotelKetten = new HashMap<String, RDFNode>();
	//private Map<String, RDFNode> hotels = new HashMap<String, RDFNode>();
	//private Map<String, RDFNode> gaeste = new HashMap<String, RDFNode>();
	//private Map<String, RDFNode> events = new HashMap<String, RDFNode>();
	
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
	public void importData(HotelManager m) {
		hotelM = m;
		ontModel = m.getOntModel();
		
		//hotelKetten.clear();
		//hotels.clear();
		//gaeste.clear();
		//events.clear();
		
		try {
			ClassLoader loader = getClass().getClassLoader();
			loadHotelketten(loader.getResourceAsStream("csv/hotelkette.csv"));
			loadGast(loader.getResourceAsStream("csv/gast.csv"));
			loadHotel(loader.getResourceAsStream("csv/hotel.csv"));
			loadBuchung(loader.getResourceAsStream("csv/buchung.csv"));
			loadEvents(loader.getResourceAsStream("csv/events.csv"));
			loadEventTeilnahme(loader.getResourceAsStream("csv/eventteilnahme.csv"));
			loadEventConcepts(loader.getResourceAsStream("csv/eventclassification.csv"));
		} catch (Exception e) {
			System.err.println("Beim Einlesen der csv Daten ist ein Problem aufgetreten: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private void loadHotelketten(InputStream in) throws Exception{
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile ueberlesen, diese enthaelt nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 1)) {
			// Owl Klasse erzeugen
			OntClass clazz = ontModel.getOntClass(HotelNS.prefix + HotelNS.classHotelkette);
			while ((line = reader.readNext()) != null) {				
				// pro Zeile eine neue Instanz
				String name = line[0].trim();
				
				if (existsHotelKette(name)) {
					System.out.println("Hotelkette " + name + " bereits vorhanden.");
					continue;
				}
				
				Individual ind = clazz.createIndividual();
				ind.addProperty(hotelM.name, name);
				
				//hotelKetten.put(name, ind);
			}
		}
		reader.close();
	}
	
	private void loadGast(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile ueberlesen, diese enthaelt nur eine Beschreibung der Spalten
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
					System.out.println("Gast " + vorname + " " + nachname + " " + email + " bereits vorhanden.");
					continue;
				}
				
				Individual ind = clazz.createIndividual();
				ind.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propVorname), vorname)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propNachname), nachname)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propEmail), email);
				
				//gaeste.put(vorname+" "+nachname, ind);
			}
		}
		reader.close();
		
	}
	
	private void loadHotel(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile ueberlesen, diese enthaelt nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 3)) {
			// Owl Klasse erzeugen
			OntClass clazz = ontModel.getOntClass(HotelNS.prefix + HotelNS.classHotel);
			OntClass ortClass = ontModel.getOntClass(HotelNS.prefix + HotelNS.classOrt);
			while ((line = reader.readNext()) != null) {
				// pro Zeile eine neue Instanz
				// Name, Stadt, Kette
				String name = line[0].trim();
				String stadt = line[1].trim();
				String kette = line[2].trim();
				
				Individual ortInstanz;
				if(existsOrt(stadt)) {
					ortInstanz = (Individual)getOrtByName(stadt).as(Individual.class);
				} else {
					ortInstanz = ortClass.createIndividual();
					ortInstanz.addProperty(hotelM.name, stadt);
				}
				
				if (existsHotel(name, stadt)) {
					System.out.println("Hotel " + name + " in " + stadt + " bereits vorhanden.");
					continue;
				}
				
				Individual ind = clazz.createIndividual();
				ind.addProperty(hotelM.name, name)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propNiedergelassenIn), ortInstanz)
				.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propIstTeilVon), getHotelKetteByName(kette));
				
				//hotels.put(name, ind);
			}
		}
		reader.close();
	}
	
	private void loadBuchung(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile ueberlesen, diese enthaelt nur eine Beschreibung der Spalten
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
				
				if (existsBuchung(von, bis, gast, hotel)) {
					System.out.println("Buchung von " + von + " bis " + bis + " fuer " + gast + " im " + hotel + " bereits vorhanden.");
					continue;
				}
				
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
		// erste Zeile ueberlesen, diese enthaelt nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 3)) {
			// Owl Klasse erzeugen
			OntClass clazz = ontModel.getOntClass(HotelNS.prefix + HotelNS.classVeranstaltung);
			OntClass ortClass = ontModel.getOntClass(HotelNS.prefix + HotelNS.classOrt);
			Individual ortInstanz;
			Individual ind;
			while ((line = reader.readNext()) != null) {
				String name = line[0].trim();
				String datum = line[1].trim();
				String ort = line[2].trim();
				
				if (existsEvent(name, datum, ort)) {
					System.out.println("Veranstaltung " + name + " am " + datum + " in " + ort + " bereits vorhanden.");
					continue;
				}				
				
				if(existsOrt(ort)) {
					ortInstanz = (Individual)getOrtByName(ort).as(Individual.class);
				} else {
					ortInstanz = ortClass.createIndividual();
					ortInstanz.addProperty(hotelM.name, ort);
				}
				// hier wird wirklich nur das Event geladen
				
				// pro Zeile eine neue Instanz
				ind = clazz.createIndividual();
				
				ind.addProperty(hotelM.name, name)
				.addProperty(hotelM.datum, datum)
				.addProperty(hotelM.findetStattIn, ortInstanz);
				
				//events.put(line[0], ind);
				
			}
		}
		reader.close();
	}
	
	private void loadEventTeilnahme(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile ueberlesen, diese enthaelt nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 3)) {
			while ((line = reader.readNext()) != null) {
				String person = line[0].trim();
				String event = line[1].trim();
				String datum = line[2].trim();
				
				if (existsEventTeilnahme(person, event, datum)) {
					System.out.println("Teilnahme von " + person + " an " + event + " am " + datum + " bereits vorhanden.");
					continue;
				}
				
				Individual gast = (Individual)getGastByName(line[0].trim()).as(Individual.class);
				gast.addProperty(ontModel.getProperty(HotelNS.prefix + HotelNS.propNimmtTeilAn), getEvent(event, datum));
				
			}
		}
		reader.close();
	}

	private void loadEventConcepts(InputStream in) throws Exception {
		CSVReader reader  = new CSVReader(new InputStreamReader(in));
		// erste Zeile ueberlesen, diese enthaelt nur eine Beschreibung der Spalten
		String[] line = reader.readNext();
		if ((line != null) && (line.length == 2)) {
			while ((line = reader.readNext()) != null) {
				String eventname = line[0].trim();
				String conceptname = line[1].trim();

				Individual concept = ontModel.getIndividual(HotelNS.CONCEPT_PREFIX + conceptname);

				if (concept != null) {
					String query = "SELECT ?event " +
					"WHERE { ?event rdf:type :Veranstaltung ." +
					       " ?event :name \"" + eventname + "\" }";
	
					ResultSet results = HotelManager.getHotelManager().query(query);

					RDFNode node = null;
					if ((results != null) && results.hasNext()) {
						QuerySolution qs = results.next();
						node = qs.get("event");
						Individual event = (Individual)node.as(Individual.class);
						event.addProperty(hotelM.dcSubject, concept);
					}
				}else {
					System.out.println("Konzept " + conceptname + " nicht vorhanden.");
				}
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
		/*RDFNode node = gaeste.get(name);
		if (node != null) {
			return node;
		} else {
			throw new Exception("Gast nicht gefunden: " + name);
		}
		*/
		
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
				" :niedergelassenIn ?stadt ;" +
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
	 * Searches for an event with specific name and date in the ontology. 
	 * @param name name of the event
	 * @param date date of the event
	 * @return RDFNode
	 */
	private RDFNode getEvent(String name, String date) throws Exception {
		/*RDFNode node = events.get(name);
		if (node != null) {
			return node;
		} else {
			throw new Exception("Event nicht gefunden: " + name);
		}
		*/
		
		String query = "SELECT ?x " +
		"WHERE { ?x :name \"" + name + "\" ;" +
				" :datum \"" + date + "\"}";

		ResultSet result = HotelManager.getHotelManager().query(query);
		RDFNode node = null;
		if ((result != null) && (result.hasNext())) {
			QuerySolution qs = result.next();
			node = qs.get("x");
		}
		
		return node;
		
	}
	
	/**
	 * Searches for an Ort with specific name in the ontology. 
	 * @param name name of the Ort
	 * @return RDFNode
	 */
	private RDFNode getOrtByName(String name) throws Exception {
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
	 * Searches for an Land with specific name and countryCode in the ontology. 
	 * @param name name of the Land
	 * @param countryCode country code of the Land
	 * @return RDFNode
	 * @throws Exception
	 */
	public RDFNode getLand(String name, String countryCode) throws Exception {
		String query = "SELECT ?x " +
		"WHERE { ?x :name \"" + name + "\" ;" +
				" :laenderCode \"" + countryCode + "\"}";

		ResultSet result = HotelManager.getHotelManager().query(query);
		RDFNode node = null;
		if (result != null) {
			QuerySolution qs = result.next();
			node = qs.get("x");
		}
		
		return node;
	}
	
	/**
	 * Checks if HotelKette already exists in the ontology.
	 * @param name name of the HotelKette
	 * @return true if exists, false otherwise
	 */
	private boolean existsHotelKette(String name) {
		String query = "ASK {?kette :name \"" + name + "\" }";
		
		try {
			return HotelManager.getHotelManager().askQuery(query);
		} catch (Exception e) {
			System.out.println("Problem bei Verarbeitung der query: ");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if Gast already exists in the ontology.
	 * @param first first name of the Gast
	 * @param last last name of the Gast
	 * @param email email of the Gast
	 * @return true if exists, false otherwise
	 */
	private boolean existsGast(String first, String last, String email) {
		String query = "ASK {?gast :vorname \"" + first + "\" ;" +
							" :nachname \"" + last + "\" ;" +
							" :email \"" + email + "\"}";
		
		try {
			return HotelManager.getHotelManager().askQuery(query);
		} catch (Exception e) {
			System.out.println("Problem bei Verarbeitung der query: ");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if Hotel already exists in the ontology.
	 * @param name name of the Hotel
	 * @param city city where the Hotel is
	 * @return true if exists, false otherwise
	 */
	private boolean existsHotel(String name, String city) {
		String query = "ASK {?hotel :name \"" + name + "\" ;" +
		" :niedergelassenIn ?ort ." +
		" ?ort :name \"" + city + "\"}";

		try {
			return HotelManager.getHotelManager().askQuery(query);
		} catch (Exception e) {
			System.out.println("Problem bei Verarbeitung der query: ");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if Event already exists in the ontology.
	 * @param name name of the Event
	 * @param date date of the Event
	 * @param place of the Event
	 * @return true if exists, false otherwise
	 */
	private boolean existsEvent(String name, String date, String place) {
		String query = "ASK {?event :name \"" + name + "\" ;" +
		" :datum \"" + date + "\" ;" +
		" :findetStattIn ?ort ." +
		" ?ort :name \"" + place + "\"}";

		try {
			return HotelManager.getHotelManager().askQuery(query);
		} catch (Exception e) {
			System.out.println("Problem bei Verarbeitung der query: ");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if Ort already exists in the ontology.
	 * @param name name of the Ort
	 * @return true if exists, false otherwise
	 */
	private boolean existsOrt(String name) {
		String query = "ASK {?ort :name \"" + name + "\"}";

		try {
			return HotelManager.getHotelManager().askQuery(query);
		} catch (Exception e) {
			System.out.println("Problem bei Verarbeitung der query: ");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if EventTeilnahme already exists in the ontology.
	 * @param person person of the EventTeilnahme
	 * @param event event of the EventTeilnahme
	 * @param date date of the EventTeilnahme
	 * @return true if exists, false otherwise
	 */
	private boolean existsEventTeilnahme(String person, String event, String date) {
		String splitted[] = person.split("\\s+", 2);
		String query = "ASK { {?event :name \"" + event + "\" ;" +
		" :datum \"" + date + "\" ;" +
		" :wirdBesuchtVon ?gast .} " +
		" { ?gast :vorname \"" + splitted[0] + "\" ;" +
		" :nachname \"" + splitted[1] + "\"}}";

		try {
			return HotelManager.getHotelManager().askQuery(query);
		} catch (Exception e) {
			System.out.println("Problem bei Verarbeitung der query: ");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if Buchung already exists in the ontology.
	 * @param start start date of the Buchung
	 * @param end end date of the Buchung
	 * @param customer name of the custome of the Buchung
	 * @param hotel name of the hotel of the Buchung
	 * @return true if exists, false otherwise
	 */
	private boolean existsBuchung(String start, String end, String customer, String hotel) {
		String splitted[] = customer.split("\\s+", 2);
		String query = "ASK {?buchung :von \"" + start + "\" ;" +
		" :bis \"" + end + "\" ;" +
		" :durchgefuehrtVon ?gast ;" +
		" :gehoertZu ?hotel ." +
		" ?gast :vorname \"" + splitted[0] + "\" ;" +
		" :nachname \"" + splitted[1] + "\" ." +
		" ?hotel :name \"" + hotel + "\"}";

		try {
			return HotelManager.getHotelManager().askQuery(query);
		} catch (Exception e) {
			System.out.println("Problem bei Verarbeitung der query: ");
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks if Land already exists in the ontology.
	 * @param name name of the Land
	 * @param countryCode code of the Land
	 * @return true if exists, false otherwise
	 */
	public boolean existsLand(String name, String countryCode) {
		String query = "ASK {?land :name \"" + name + "\" ;" +
		" :laenderCode \"" + countryCode + "\"}";

		try {
			return HotelManager.getHotelManager().askQuery(query);
		} catch (Exception e) {
			System.out.println("Problem bei Verarbeitung der query: ");
			e.printStackTrace();
		}
		return false;
	}

}
