package at.tuwien.semanticWeb.abgabe2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geonames.utils.Distance;

public class Distances {
    public List<PlaceData> distances(PlaceData event, List<PlaceData> events) {
        List<PlaceData> inDistance = new ArrayList<PlaceData>();
        for (PlaceData placeData : events) {
            double distance = Distance.distanceKM(event.getLatitude(), event.getLongitude(), placeData.getLatitude(),
                placeData.getLongitude());
            System.out.println(distance);
            if (distance <= 100) {
                inDistance.add(placeData);
            }
        }
        return inDistance;
    }
}
