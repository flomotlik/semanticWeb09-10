package at.tuwien.semanticWeb.abgabe2;

import java.util.Scanner;

public class Main {
	
	private static final String menu = "HotelExpert\n" + "-----------\n" 
		+ "1 Welche Veranstaltungen finden in der Neahe (im Umkreis von 100km) von <HotelName> statt? \n"
		+ "2 Wie wird das Wetter bei <VeranstaltungsName> sein?\n"
		+ "3 Welche Hintergrundinformationen gibt es für <VeranstaltungsName>?\n"
		+ "4 Welche direkte und indirekte Freunde hat <GastName>?\n"
		+ "5 Welche sind die 3 besten Kunden für <HotelName>?\n"
		+ "6 Welche persönliche Interessen hat <GastName>?\n"
		+ "7 Welche interessante Veranstaltungen gibt es fuer <GastName> am <Datum>?\n"
		+ "==========HELPERS==========\n"
		+ "8 Welche Veranstaltungen gibt es?\n"
		+ "Q Beenden\n"
		+ "Ihre Auswahl:"; 

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
        boolean exit = false;
        HotelManager manager = HotelManager.getHotelManager();
        manager.loadData();
        String input;
        do {
            input = printMenu();
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
        } while (!exit);
    }

    public static String printMenu() {
        System.out.println(menu);
        return new Scanner(System.in).nextLine();
    }
}
