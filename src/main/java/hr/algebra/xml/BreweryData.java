package hr.algebra.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BreweryData {

    @JsonProperty("id")
    private String apiId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("country")
    private String country;

    @JsonProperty("brewery_type")
    private String breweryType;

    public BreweryData() {}

    public String getApiId() { return apiId; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getBreweryType() { return breweryType; }
}