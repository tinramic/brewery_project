package hr.algebra.xml;

import hr.algebra.model.Beer;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "beer")
@XmlAccessorType(XmlAccessType.FIELD)
public class BeerXml {

    @XmlAttribute
    private int id;

    @XmlElement
    private String name;

    @XmlElement
    private double abv;

    @XmlElement
    private int ibu;

    @XmlElement
    private int srm;

    @XmlElement
    private String flavorProfile;

    @XmlElement
    private String brewery;

    @XmlElement
    private String style;

    @XmlElement
    private String country;

    @XmlElement
    private String brewmaster;

    public BeerXml() {}

    public BeerXml(Beer beer) {
        this.id = beer.getId();
        this.name = beer.getName();
        this.abv = beer.getAbv();
        this.ibu = beer.getIbu();
        this.srm = beer.getSrm();
        this.flavorProfile = beer.getFlavorProfile();
        this.brewery = beer.getBrewery() != null ? beer.getBrewery().getName() : "";
        this.style = beer.getBeerStyle() != null ? beer.getBeerStyle().getName() : "";
        this.country = beer.getCountry() != null ? beer.getCountry().getName() : "";
        this.brewmaster = beer.getBrewmaster() != null ? beer.getBrewmaster().getName() : "";
    }
}