package hr.algebra.xml;

import hr.algebra.model.Beer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.io.File;
import java.util.List;

public class XmlExportUtils {

    private XmlExportUtils() {}

    public static void exportTastingList(List<Beer> beers, String filePath) throws Exception {
        List<BeerXml> beerXmls = beers.stream()
                .map(BeerXml::new)
                .toList();

        TastingListXml tastingListXml = new TastingListXml(beerXmls);

        JAXBContext context = JAXBContext.newInstance(TastingListXml.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(tastingListXml, new File(filePath));
    }
}