package hr.algebra.xml;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "tastingList")
@XmlAccessorType(XmlAccessType.FIELD)
public class TastingListXml {

    @XmlElement(name = "beer")
    private List<BeerXml> beers;

    public TastingListXml() {}

    public TastingListXml(List<BeerXml> beers) {
        this.beers = beers;
    }
}