package at.tuwien.semanticWeb.abgabe2;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class WeatherService {
    public List<Forecast> getForecast(double latitude, double longitude) throws IOException, SAXException {
        URL wunderground = new URL("http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query=" + latitude
            + "," + longitude);
        URLConnection connection = wunderground.openConnection();
        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuffer buffer = new StringBuffer();
        while (scanner.hasNextLine()) {
            buffer.append(scanner.nextLine());
        }
        String content = buffer.toString();
        System.out.println(content);
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("forecast/simpleforecast", ArrayList.class);
        digester.addObjectCreate("forecast/simpleforecast/forecastday", Forecast.class.getName());
        digester.addCallMethod("forecast/simpleforecast/forecastday/date/month", "setMonth", 0);
        digester.addCallMethod("forecast/simpleforecast/forecastday/date/year", "setYear", 0);
        digester.addCallMethod("forecast/simpleforecast/forecastday/date/day", "setDay", 0);
        digester.addCallMethod("forecast/simpleforecast/forecastday/high/celsius", "setHigh", 0);
        digester.addCallMethod("forecast/simpleforecast/forecastday/low/celsius", "setLow", 0);
        digester.addCallMethod("forecast/simpleforecast/forecastday/conditions", "setConditions", 0);
        digester.addSetNext("forecast/simpleforecast/forecastday", "add");
        return (List<Forecast>) digester.parse(new StringReader(content));
    }
}
