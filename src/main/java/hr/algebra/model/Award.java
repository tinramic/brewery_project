package hr.algebra.model;

import java.util.Objects;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "award")
@XmlAccessorType(XmlAccessType.FIELD)

public class Award {

    private int id;
    private String name;
    private int year;
    private String organization;

    public Award() {}

    public Award(int id, String name, int year, String organization) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.organization = organization;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Award that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + year + ") - " + organization;
    }
}