package at.tuwien.semanticWeb.abgabe2;

import java.util.ArrayList;
import java.util.List;

import org.geonames.utils.Distance;

public class Distances {
    public List<PlaceData> distances(double latitude1, double longitude1, List<PlaceData> events) {
        List<PlaceData> inDistance = new ArrayList<PlaceData>();
        for (PlaceData placeData : events) {
            double distance = Distance.distanceKM(latitude1, longitude1, Double.valueOf(placeData.getLatitude()),
                Double.valueOf(placeData.getLongitude()));
            System.out.println(distance);
            if (distance <= 100) {
                inDistance.add(placeData);
            }
        }
        return inDistance;
    }
}
