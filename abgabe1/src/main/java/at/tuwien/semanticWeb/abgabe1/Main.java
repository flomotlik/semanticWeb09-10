package at.tuwien.semanticWeb.abgabe1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        boolean exit = false;
        Sparqler sparqler = new Sparqler();
        String input;
        do {
            input = main.printMenu();
            if (input.equals("1")) 
            	sparqler.first();
            else if (input.equals("2"))
            	sparqler.second();
            else if (input.equals("3"))
            	sparqler.third();
            else if (input.equals("4"))
            	sparqler.fourth();
            else if (input.equals("5"))
            	sparqler.fifth();
            else if (input.equals("6"))
            	sparqler.sixth();
            else if (input.equals("7"))
            	sparqler.seventh();
            else if (input.equals("8"))
            	sparqler.eigth();
            else if (input.equalsIgnoreCase("q")) {
            	break;
            } else
            	System.out.println("Falsche eingabe.");
        } while (!exit);
    }

    public String printMenu() {
        System.out.println("HotelExpert\n" + "-----------\n" + "1 Welche Hotels haben mindestens <Anzahl> Sterne?\n"
            + "2 Welche Attraktionen oder Hotels sind in der Naehe von <Hotelname>?\n"
            + "3 Hat Person <Gastname> schon in Hotel <Hotelname> gewohnt (ja/nein)?\n"
            + "4 Welche Personen haben ein Raucherzimmer gebucht?\n"
            + "5 Welche Wellnessangebote werden von Gaesten genutzt, die ein Raucherzimmer gebucht haben?\n"
            + "6 Ist das Zimmer <Zimmernummer1> neben dem Zimmer <Zimmernummer2> (ja/nein)?\n"
            + "7 Ist <Gastname> verwandt mit <Gastname> (ja/nein)?\n"
            + "8 Welche Gaeste, die verwandt sind, haben neben einander ein Zimmer gebucht?\n" 
            + "Q Beenden\n"
            + "Ihre Auswahl:");
        return new Scanner(System.in).next();
    }
}
