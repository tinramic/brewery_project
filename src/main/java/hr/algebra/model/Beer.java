package hr.algebra.model;

import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "beer")
@XmlAccessorType(XmlAccessType.FIELD)

public class Beer implements Comparable<Beer> {

    private int id;
    private String name;
    private double abv;
    private int ibu;
    private int srm;
    private String flavorProfile;
    private String imagePath;
    private Brewery brewery;
    private BeerStyle beerStyle;
    private Country country;
    private Brewmaster brewmaster;
    private List<Award> awards;

    public Beer() {}

    public Beer(int id, String name, double abv, int ibu, int srm,
                String flavorProfile, String imagePath, Brewery brewery,
                BeerStyle beerStyle, Country country, Brewmaster brewmaster,
                List<Award> awards) {
        this.id = id;
        this.name = name;
        this.abv = abv;
        this.ibu = ibu;
        this.srm = srm;
        this.flavorProfile = flavorProfile;
        this.imagePath = imagePath;
        this.brewery = brewery;
        this.beerStyle = beerStyle;
        this.country = country;
        this.brewmaster = brewmaster;
        this.awards = awards;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAbv() { return abv; }
    public void setAbv(double abv) { this.abv = abv; }

    public int getIbu() { return ibu; }
    public void setIbu(int ibu) { this.ibu = ibu; }

    public int getSrm() { return srm; }
    public void setSrm(int srm) { this.srm = srm; }

    public String getFlavorProfile() { return flavorProfile; }
    public void setFlavorProfile(String flavorProfile) { this.flavorProfile = flavorProfile; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Brewery getBrewery() { return brewery; }
    public void setBrewery(Brewery brewery) { this.brewery = brewery; }

    public BeerStyle getBeerStyle() { return beerStyle; }
    public void setBeerStyle(BeerStyle beerStyle) { this.beerStyle = beerStyle; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public Brewmaster getBrewmaster() { return brewmaster; }
    public void setBrewmaster(Brewmaster brewmaster) { this.brewmaster = brewmaster; }

    public List<Award> getAwards() { return awards; }
    public void setAwards(List<Award> awards) { this.awards = awards; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Beer that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + abv + "% ABV)";
    }

    @Override
    public int compareTo(Beer other) {
        return this.name.compareTo(other.name);
    }
}