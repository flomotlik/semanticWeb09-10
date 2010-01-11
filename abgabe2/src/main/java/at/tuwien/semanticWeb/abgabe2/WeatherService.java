package at.tuwien.semanticWeb.abgabe2;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class WeatherService {
    public List<Forecast> getForecast(double latitude, double longitude) throws IOException, SAXException {
        String content = Helper.getURLContent("http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query="
            + latitude + "," + longitude);
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
