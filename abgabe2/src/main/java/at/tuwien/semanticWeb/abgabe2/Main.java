package at.tuwien.semanticWeb.abgabe2;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Scanner;

public class Main {
	
	private static final String menu = "HotelExpert\n" + "-----------\n" 
		+ "1 Welche Veranstaltungen finden in der Neahe (im Umkreis von 100km) von <HotelName> statt? \n"
		+ "2 Wie wird das Wetter bei <VeranstaltungsName> sein?\n"
		+ "3 Welche Hintergrundinformationen gibt es fuer <VeranstaltungsName>?\n"
		+ "4 Welche direkte und indirekte Freunde hat <GastVorname> <GastNachname>?\n"
		+ "5 Welche sind die 3 besten Kunden fuer <HotelName>?\n"
		+ "6 Welche persoenliche Interessen hat <GastVorname> <GastNachname>?\n"
		+ "7 Welche interessante Veranstaltungen gibt es fuer <GastVorname> <GastNachname> am <Datum>?\n"
		+ "==========HELPERS==========\n"
		+ "8 Welche Veranstaltungen gibt es?\n"
		+ "Q Beenden\n"
		+ "Ihre Auswahl:"; 

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args){
        boolean exit = false;
        HotelManager manager = HotelManager.getHotelManager();
        System.out.println("Before Load");
        manager.loadData();
        System.out.println("After Load");
        manager.updateOrtsInfo();
        System.out.println("After Update");
        String input;
        
        try {
			manager.getOntModel().write(new FileOutputStream("D:/uni/ESW/semanticWeb09-10/abgabe2/src/test/hotelm.owl"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        do {
            input = printMenu();
            try {
				if (input.equals("1")) {
					manager.first();
				}else if (input.equals("2")) {
				    manager.second();
				} else if (input.equals("3")) {
					manager.third();
				} else if (input.equals("4")) {
					manager.fourth();
				} else if (input.equals("5")) {
					manager.fifth();
				} else if (input.equals("6")) {
					manager.sixth();
				} else if (input.equals("7")) {
					manager.seventh();
				} else if (input.equals("8")) {
					manager.eight();
				}  
				else if (input.equalsIgnoreCase("q")) {
					break;
				} else {
					System.out.println("Falsche eingabe.");
				}
			} catch (Exception e) {
				System.out.println("Bei der Abfrage ist ein Problem aufgetreten: " + e.getMessage());
				e.printStackTrace();
			}
        } while (!exit);
    }

    public static String printMenu() {
        System.out.println(menu);
        return new Scanner(System.in).nextLine();
    }
}
