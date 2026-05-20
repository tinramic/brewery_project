package hr.algebra.model;

import java.util.Objects;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "brewmaster")
@XmlAccessorType(XmlAccessType.FIELD)

public class Brewmaster {

    private int id;
    private String name;
    private String bio;
    private String brewingPhilosophy;

    public Brewmaster() {}

    public Brewmaster(int id, String name, String bio, String brewingPhilosophy) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.brewingPhilosophy = brewingPhilosophy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getBrewingPhilosophy() { return brewingPhilosophy; }
    public void setBrewingPhilosophy(String brewingPhilosophy) { this.brewingPhilosophy = brewingPhilosophy; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Brewmaster that)) return false;
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