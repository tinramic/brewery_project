package hr.algebra.xml;

import hr.algebra.dao.*;
import hr.algebra.model.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.*;

import java.io.File;
import java.util.List;

public class XmlBackupUtils {

    private XmlBackupUtils() {}

    public static void createBackup(String filePath) throws Exception {
        BackupData backup = new BackupData();

        CountryRepository countryRepo = new CountryRepository();
        BeerStyleRepository styleRepo = new BeerStyleRepository();
        BrewmasterRepository brewmasterRepo = new BrewmasterRepository();
        BreweryRepository breweryRepo = new BreweryRepository();
        AwardRepository awardRepo = new AwardRepository();
        BeerRepository beerRepo = new BeerRepository();

        backup.setCountries(countryRepo.findAll());
        backup.setBeerStyles(styleRepo.findAll());
        backup.setBrewmasters(brewmasterRepo.findAll());
        backup.setBreweries(breweryRepo.findAll());
        backup.setAwards(awardRepo.findAll());
        backup.setBeers(beerRepo.findAll());

        JAXBContext context = JAXBContext.newInstance(BackupData.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(backup, new File(filePath));

        System.out.println("Backup created: " + filePath);
    }

    @XmlRootElement(name = "backup")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BackupData {

        @XmlElementWrapper(name = "countries")
        @XmlElement(name = "country")
        private List<Country> countries;

        @XmlElementWrapper(name = "beerStyles")
        @XmlElement(name = "beerStyle")
        private List<BeerStyle> beerStyles;

        @XmlElementWrapper(name = "brewmasters")
        @XmlElement(name = "brewmaster")
        private List<Brewmaster> brewmasters;

        @XmlElementWrapper(name = "breweries")
        @XmlElement(name = "brewery")
        private List<Brewery> breweries;

        @XmlElementWrapper(name = "awards")
        @XmlElement(name = "award")
        private List<Award> awards;

        @XmlElementWrapper(name = "beers")
        @XmlElement(name = "beer")
        private List<Beer> beers;

        public void setCountries(List<Country> countries) { this.countries = countries; }
        public void setBeerStyles(List<BeerStyle> beerStyles) { this.beerStyles = beerStyles; }
        public void setBrewmasters(List<Brewmaster> brewmasters) { this.brewmasters = brewmasters; }
        public void setBreweries(List<Brewery> breweries) { this.breweries = breweries; }
        public void setAwards(List<Award> awards) { this.awards = awards; }
        public void setBeers(List<Beer> beers) { this.beers = beers; }
    }
}