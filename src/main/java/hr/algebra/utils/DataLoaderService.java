package hr.algebra.utils;

import hr.algebra.dao.BeerRepository;
import hr.algebra.dao.BeerStyleRepository;
import hr.algebra.dao.BreweryRepository;
import hr.algebra.dao.CountryRepository;
import hr.algebra.dao.BrewmasterRepository;
import hr.algebra.dao.AwardRepository;
import hr.algebra.model.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import hr.algebra.utils.DataImportUtils;

import java.util.List;

public class DataLoaderService {

    private DataLoaderService() {}

    public static Task<List<Beer>> createBeerLoadTask() {
        return new Task<>() {
            @Override
            protected List<Beer> call() throws Exception {
                updateMessage("Učitavanje piva...");
                BeerRepository repo = new BeerRepository();
                List<Beer> beers = repo.findAll();
                updateMessage("Učitano " + beers.size() + " piva.");
                return beers;
            }
        };
    }

    public static Task<Void> createInitialDataTask(Runnable onSuccess, Runnable onError) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Dohvaćanje podataka s API-ja...");
                updateProgress(0, 3);

                updateMessage("Učitavanje pivovara iz online izvora...");
                DataImportUtils.importBreweriesFromApi();
                updateProgress(1, 3);

                updateMessage("Završeno!");
                updateProgress(3, 3);

                Platform.runLater(onSuccess);
                return null;
            }
        };
    }
}