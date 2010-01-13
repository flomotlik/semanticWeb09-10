package at.tuwien.semanticWeb.abgabe2;

import org.geonames.utils.Distance;

public class Distances {
    public double distances(double latitude1, double longitude1, double latitude2, double longitude2) {
            double distance = Distance.distanceKM(latitude1, longitude1, latitude2, longitude2);
//            System.out.println(distance);
        return distance;
    }
}
