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
		+ "Q Beenden\n"
		+ "Ihre Auswahl:"; 

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
        boolean exit = false;
        HotelManager manager = HotelManager.getHotelManager();
        //manager.loadData();
        String input;
        do {
            input = printMenu();
            if (input.equals("1")) {
            	System.out.println("1");
            }else if (input.equals("2")) {
                String place = new Scanner(System.in).nextLine();
                double[] placeParams = manager.getPlaceOfEvent(place);
                for(Forecast forecast : new WeatherService().getForecast(placeParams[1], placeParams[0])){
                    System.out.println(forecast.toString());
                }
            } else if (input.equals("3")) {
            	System.out.println("3");
            } else if (input.equals("4")) {
            	System.out.println("4");
            } else if (input.equals("5")) {
            	System.out.println("5");
            } else if (input.equals("6")) {
            	System.out.println("6");
            } else if (input.equals("7")) {
            	System.out.println("7");
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
