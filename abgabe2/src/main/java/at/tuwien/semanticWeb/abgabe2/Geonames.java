package at.tuwien.semanticWeb.abgabe2;

import java.util.ArrayList;
import java.util.List;

import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

public class Geonames {
    public List<PlaceData> getData(List<String> places) throws Exception {
        List<PlaceData> data = new ArrayList<PlaceData>();
        for (String place : places) {
            ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
            searchCriteria.setQ(place);
            searchCriteria.setStyle(Style.FULL);
            ToponymSearchResult searchResult = WebService.search(searchCriteria);
            if (searchResult.getToponyms().size() > 0) {
                Toponym top = searchResult.getToponyms().get(0);
                data.add(new PlaceData(top.getCountryName(), top.getLongitude(), top.getLatitude(), top
                    .getCountryCode(), top.getTimezone().getTimezoneId()));
            }
        }
        return data;
    }
}
