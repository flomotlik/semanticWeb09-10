package at.tuwien.semanticWeb.abgabe2;

import org.junit.Test;

public class GeonamesTest {
    @Test
    public void testGeonames() throws Exception {
        Geonames webService = new Geonames();
        PlaceData placeData = webService.getData("wien");
        System.out.println(placeData.getCountry());
        System.out.println(placeData.getLongitude());
        System.out.println(placeData.getLatitude());
        System.out.println(placeData.getCountryCode());
        System.out.println(placeData.getTimezone());
    }
}
