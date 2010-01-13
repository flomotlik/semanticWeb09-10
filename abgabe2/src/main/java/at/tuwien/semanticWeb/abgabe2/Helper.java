package at.tuwien.semanticWeb.abgabe2;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Helper {
    public static String getURLContent(String url) throws IOException {
//        System.out.println(url);
        URL wunderground = new URL(url);
        URLConnection connection = wunderground.openConnection();
        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuffer buffer = new StringBuffer();
        while (scanner.hasNextLine()) {
            buffer.append(scanner.nextLine());
        }
//        System.out.println(buffer.toString());
        return buffer.toString();
    }
}
