package at.tuwien.semanticWeb.abgabe2;

import java.util.Scanner;

public class Main {
	
	private static final String menu = "HotelExpert\n" + "-----------\n" 
		+ "Noch so viel zu tun \n"
		+ "Q Beenden\n"
		+ "Ihre Auswahl:"; 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        boolean exit = false;
        String input;
        do {
            input = printMenu();
            if (input.equals("1")) {
            	System.out.println("los los los!");
            } else if (input.equalsIgnoreCase("q")) {
            	break;
            } else {
            	System.out.println("Falsche eingabe.");
            }
        } while (!exit);
    }

    public static String printMenu() {
        System.out.println(menu);
        return new Scanner(System.in).next();
    }
}
