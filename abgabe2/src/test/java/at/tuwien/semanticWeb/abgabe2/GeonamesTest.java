package at.tuwien.semanticWeb.abgabe2;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class GeonamesTest {
    @Test
    public void testGeonames() throws Exception {
        Geonames webService = new Geonames();
        List<String> countries = Arrays.asList(new String[] {"wien"});
        List<PlaceData> data = webService.getData(countries);
        for (PlaceData placeData : data) {
            System.out.println(placeData.getCountry());
            System.out.println(placeData.getLongitude());
            System.out.println(placeData.getLatitude());
            System.out.println(placeData.getCountryCode());
            System.out.println(placeData.getTimezone());
        }
    }
}
