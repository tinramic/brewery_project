package hr.algebra.utils;

import hr.algebra.model.Beer;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BeerFilterUtils {

    private BeerFilterUtils() {}

    // Predicate - filter po ABV
    public static Predicate<Beer> byMinAbv(double minAbv) {
        return beer -> beer.getAbv() >= minAbv;
    }

    public static Predicate<Beer> byMaxAbv(double maxAbv) {
        return beer -> beer.getAbv() <= maxAbv;
    }

    // Predicate - filter po stilu
    public static Predicate<Beer> byStyle(int styleId) {
        return beer -> beer.getBeerStyle() != null &&
                beer.getBeerStyle().getId() == styleId;
    }

    // Predicate - filter po zemlji
    public static Predicate<Beer> byCountry(int countryId) {
        return beer -> beer.getCountry() != null &&
                beer.getCountry().getId() == countryId;
    }

    // Function - pretvaranje Beer u String za prikaz
    public static Function<Beer, String> toDisplayString() {
        return beer -> beer.getName() + " (" + beer.getAbv() + "% ABV) - " +
                (beer.getBeerStyle() != null ? beer.getBeerStyle().getName() : "");
    }

    // Consumer - ispis beer info u konzolu
    public static Consumer<Beer> printBeerInfo() {
        return beer -> System.out.println(
                "Beer: " + beer.getName() +
                        " | ABV: " + beer.getAbv() +
                        " | IBU: " + beer.getIbu() +
                        " | Style: " + (beer.getBeerStyle() != null ? beer.getBeerStyle().getName() : "N/A")
        );
    }

    // Filter - filtriranje liste piva
    public static List<Beer> filter(List<Beer> beers, Predicate<Beer> predicate) {
        return beers.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    // Kombiniranje Predicatea
    public static List<Beer> filterByAbvRange(List<Beer> beers, double minAbv, double maxAbv) {
        return beers.stream()
                .filter(byMinAbv(minAbv).and(byMaxAbv(maxAbv)))
                .sorted()
                .collect(Collectors.toList());
    }

    // Optional - traženje piva po nazivu
    public static Optional<Beer> findByName(List<Beer> beers, String name) {
        return beers.stream()
                .filter(beer -> beer.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    // Streams napredne metode - I3
    public static List<String> getDistinctStyles(List<Beer> beers) {
        return beers.stream()
                .filter(beer -> beer.getBeerStyle() != null)
                .map(beer -> beer.getBeerStyle().getName())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static Optional<Beer> getStrongestBeer(List<Beer> beers) {
        return beers.stream()
                .max((b1, b2) -> Double.compare(b1.getAbv(), b2.getAbv()));
    }

    public static Optional<Beer> getLightestBeer(List<Beer> beers) {
        return beers.stream()
                .min((b1, b2) -> Double.compare(b1.getAbv(), b2.getAbv()));
    }

    public static boolean anyBeerAbove(List<Beer> beers, double abv) {
        return beers.stream().anyMatch(byMinAbv(abv));
    }

    public static boolean allBeersAbove(List<Beer> beers, double abv) {
        return beers.stream().allMatch(byMinAbv(abv));
    }

    public static long countByStyle(List<Beer> beers, int styleId) {
        return beers.stream()
                .filter(byStyle(styleId))
                .count();
    }
}