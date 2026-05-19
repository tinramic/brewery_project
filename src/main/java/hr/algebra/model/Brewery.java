package hr.algebra.model;

import java.util.Objects;

public class Brewery {

    private int id;
    private String name;
    private Country country;

    public Brewery() {}

    public Brewery(int id, String name, Country country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Brewery that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}