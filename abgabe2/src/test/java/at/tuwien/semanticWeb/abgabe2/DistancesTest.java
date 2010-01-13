package at.tuwien.semanticWeb.abgabe2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class DistancesTest {
    @Test
    public void measureDistance() {
        Distances distances = new Distances();
        PlaceData firstPlace = new PlaceData("a", 18, 48, "AT", "Zone");
        List<PlaceData> data = new ArrayList<PlaceData>();
        PlaceData place1 = new PlaceData("a", 19, 48, "AT", "Zone");
        PlaceData place2 = new PlaceData("a", 19, 150, "AT", "Zone");
        data.add(place1);
        data.add(place2);
        double resultPlaces = distances.distances(Double.valueOf(firstPlace.getLatitude()), Double
            .valueOf(firstPlace.getLongitude()), 19, 48);
    }
}
