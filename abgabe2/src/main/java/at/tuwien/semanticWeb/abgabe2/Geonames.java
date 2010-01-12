package at.tuwien.semanticWeb.abgabe2;

import org.geonames.Style;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

public class Geonames {
	public PlaceData getData(String place) throws Exception {
	    //System.out.println(place);
		ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
		searchCriteria.setQ(place);
		searchCriteria.setStyle(Style.FULL);
		ToponymSearchResult searchResult = WebService.search(searchCriteria);
		Toponym top = searchResult.getToponyms().get(0);
		PlaceData data = new PlaceData(top.getCountryName(), top.getLongitude(), top
				.getLatitude(), top.getCountryCode(), top.getTimezone()
				.getTimezoneId());
		//System.out.println(data);
		return data;
	}
}
