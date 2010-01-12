package at.tuwien.semanticWeb.abgabe2;

public class PlaceData {
    private String country;
    private String longitude;
    private String latitude;
    private String countryCode;
    private String timezone;

    public PlaceData(String country, double longitude, double latitude, String countryCode, String timezone) {
        super();
        this.country = country;
        this.longitude = Double.toString(longitude);
        this.latitude = Double.toString(latitude);
        this.countryCode = countryCode;
        this.timezone = timezone;
    }

    public String getCountry() {
        return country;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getTimezone() {
        return timezone;
    }

    @Override
    public String toString() {
        return "PlaceData [country=" + country + ", countryCode=" + countryCode + ", latitude=" + latitude
            + ", longitude=" + longitude + ", timezone=" + timezone + "]";
    }

}
