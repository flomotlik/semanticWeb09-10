package at.tuwien.semanticWeb.abgabe2;


public class PlaceData {
    private String country;
    private double longitude;
    private double latitude;
    private String countryCode;
    private String timezone;

    public PlaceData(String country, double longitude, double latitude, String countryCode, String timezone) {
        super();
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
        this.countryCode = countryCode;
        this.timezone = timezone;
    }

    public String getCountry() {
        return country;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getTimezone() {
        return timezone;
    }
}
