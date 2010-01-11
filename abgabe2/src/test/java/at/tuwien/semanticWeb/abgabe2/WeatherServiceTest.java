package at.tuwien.semanticWeb.abgabe2;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

public class WeatherServiceTest {
    @Test
    public void testWeatherService() throws IOException, SAXException {
        WeatherService service = new WeatherService();
        List<Forecast> forecasts = service.getForecast(48, 16);
        for (Forecast forecast : forecasts) {
            System.out.println(forecast.getDay());
            System.out.println(forecast.getMonth());
            System.out.println(forecast.getYear());
            System.out.println(forecast.getHigh());
            System.out.println(forecast.getLow());
            System.out.println(forecast.getConditions());
        }
    }
}
