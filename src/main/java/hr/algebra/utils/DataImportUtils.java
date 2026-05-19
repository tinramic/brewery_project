package hr.algebra.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.dao.BreweryRepository;
import hr.algebra.dao.CountryRepository;
import hr.algebra.model.Brewery;
import hr.algebra.model.Country;
import hr.algebra.xml.BreweryData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DataImportUtils {

    private static final String BREWERY_API_URL =
            "https://api.openbrewerydb.org/v1/breweries?per_page=20";

    private DataImportUtils() {}

    public static void importBreweriesFromApi() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BREWERY_API_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        BreweryData[] breweries = mapper.readValue(response.body(), BreweryData[].class);

        CountryRepository countryRepository = new CountryRepository();
        BreweryRepository breweryRepository = new BreweryRepository();

        List<Country> existingCountries = countryRepository.findAll();
        Set<String> existingCountryNames = existingCountries.stream()
                .map(Country::getName)
                .collect(Collectors.toSet());

        List<Brewery> existingBreweries = breweryRepository.findAll();
        Set<String> existingBreweryNames = existingBreweries.stream()
                .map(Brewery::getName)
                .collect(Collectors.toSet());

        for (BreweryData data : breweries) {
            if (data.getName() == null || data.getCountry() == null) continue;

            // Dodaj zemlju ako ne postoji
            if (!existingCountryNames.contains(data.getCountry())) {
                countryRepository.save(new Country(0, data.getCountry()));
                existingCountryNames.add(data.getCountry());
                System.out.println("Added country: " + data.getCountry());
            }

            // Dodaj pivovaru ako ne postoji
            if (!existingBreweryNames.contains(data.getName())) {
                Optional<Country> country = countryRepository.findAll()
                        .stream()
                        .filter(c -> c.getName().equals(data.getCountry()))
                        .findFirst();

                country.ifPresent(c -> {
                    try {
                        breweryRepository.save(new Brewery(0, data.getName(), c));
                        existingBreweryNames.add(data.getName());
                        System.out.println("Added brewery: " + data.getName());
                    } catch (SQLException e) {
                        System.err.println("Failed to save brewery: " + data.getName());
                    }
                });
            }
        }

        System.out.println("Import completed!");
    }
}