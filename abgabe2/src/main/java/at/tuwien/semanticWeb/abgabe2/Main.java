package at.tuwien.semanticWeb.abgabe2;

import java.util.Scanner;

public class Main {
	
	private static final String menu = "HotelExpert\n" + "-----------\n" 
		+ "Noch so viel zu tun \n"
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
            	System.out.println("los los los!");
            }else if (input.equals("2")) {
                String place = new Scanner(System.in).nextLine();
                double[] placeParams = manager.getPlaceOfEvent(place);
                for(Forecast forecast : new WeatherService().getForecast(placeParams[1], placeParams[0])){
                    System.out.println(forecast.toString());
                }
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
