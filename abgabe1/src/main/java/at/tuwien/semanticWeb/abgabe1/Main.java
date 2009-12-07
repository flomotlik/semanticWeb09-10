package at.tuwien.semanticWeb.abgabe1;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        boolean exit = false;
        Sparqler sparqler = new Sparqler();
        do {
            switch (main.printMenu()) {
                case 1:
                    sparqler.first();
                    break;
                case 2:
                    sparqler.second();
                    break;
                case 3:
                    sparqler.third();
                    break;
                case 4:
                    sparqler.fourth();
                    break;
                case 5:
                    sparqler.fifth();
                    break;
                case 6:
                    sparqler.sixth();
                    break;
                case 7:
                    sparqler.seventh();
                    break;
                case 8:
                    sparqler.eigth();
                    break;
                case 9:
                    exit = true;
                    break;
            }
        } while (!exit);
    }

    public int printMenu() {
        System.out.println("HotelExpert\n" + "-----------\n" + "1 Welche Hotels haben mindestens <Anzahl> Sterne?\n"
            + "2 Welche Attraktionen oder Hotels sind in der Naehe von <Hotelname>?\n"
            + "3 Hat Person <Gastname> schon in Hotel <Hotelname> gewohnt (ja/nein)?\n"
            + "4 Welche Personen haben ein Raucherzimmer gebucht?\n"
            + "5 Welche Wellnessangebote werden von Gaesten genutzt, die ein Raucherzimmer gebucht haben?\n"
            + "6 Ist das Zimmer <Zimmernummer1> neben dem Zimmer <Zimmernummer2> (ja/nein)?\n"
            + "7 Ist <Gastname> verwandt mit <Gastname> (ja/nein)?\n"
            + "8 Welche Gaeste, die verwandt sind, haben neben einander ein Zimmer gebucht?\n" + "9 Beenden\n"
            + "Ihre Auswahl:");
        return new Scanner(System.in).nextInt();
    }
}
