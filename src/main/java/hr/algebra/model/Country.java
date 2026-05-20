package hr.algebra.model;

import java.util.Objects;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "country")
@XmlAccessorType(XmlAccessType.FIELD)

public class Country {

    private int id;
    private String name;

    public Country() {}

    public Country(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;
        Country country = (Country) o;
        return id == country.id;
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