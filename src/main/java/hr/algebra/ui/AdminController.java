package hr.algebra.ui;

import hr.algebra.utils.DatabaseUtils;
import hr.algebra.utils.LogUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import hr.algebra.utils.DataLoaderService;
import javafx.concurrent.Task;

import java.io.IOException;
import java.util.Optional;

public class AdminController {

    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Dobrodošli, Administrator!");
        statusLabel.setText("Spremni za rad.");
    }

    @FXML
    private void handleBeers() {
        setStatus("Upravljanje pivima");
        loadContent("/fxml/beer.fxml");
    }

    @FXML
    private void handleBreweries() {
        setStatus("Upravljanje pivovarama");
        loadContent("/fxml/brewery.fxml");
    }

    @FXML
    private void handleBeerStyles() {
        setStatus("Upravljanje stilovima piva");
        loadContent("/fxml/beer_style.fxml");
    }

    @FXML
    private void handleBrewmasters() {
        setStatus("Upravljanje pivarima");
        loadContent("/fxml/brewmaster.fxml");
    }

    @FXML
    private void handleCountries() {
        setStatus("Upravljanje zemljama");
        loadContent("/fxml/country.fxml");
    }

    @FXML
    private void handleAwards() {
        setStatus("Upravljanje nagradama");
        loadContent("/fxml/award.fxml");
    }

    @FXML
    private void handleClearData() {
        Optional<ButtonType> result = showConfirmation(
                "Brisanje podataka",
                "Jeste li sigurni da želite obrisati sve podatke? Ova akcija se ne može poništiti."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            DatabaseUtils.clearData();
            LogUtils.log("admin", "CLEAR_DATA", "Administrator obrisao sve podatke");
            setStatus("Svi podaci su obrisani.");
            showAlert(Alert.AlertType.INFORMATION, "Uspjeh", "Svi podaci su uspješno obrisani.");
        }
    }

    @FXML
    private void handleLoadData() {
        Task<Void> task = DataLoaderService.createInitialDataTask(
                () -> {
                    setStatus("Početni podaci uspješno učitani!");
                    showAlert(Alert.AlertType.INFORMATION, "Uspjeh", "Podaci su učitani.");
                },
                () -> showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju podataka.")
        );

        task.messageProperty().addListener((obs, oldMsg, newMsg) ->
                setStatus(newMsg)
        );

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        setStatus("Učitavanje podataka u pozadini...");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 350));
            stage.setTitle("Beer Catalog");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri odjavi.");
        }
    }

    private void loadContent(String fxmlPath) {
        try {
            if (getClass().getResource(fxmlPath) == null) {
                setStatus("Stranica još nije implementirana: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Node content = loader.load();
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            setStatus("Greška pri učitavanju: " + fxmlPath);
        }
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}