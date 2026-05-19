package hr.algebra.ui;

import hr.algebra.dao.BeerRepository;
import hr.algebra.model.Beer;
import hr.algebra.xml.XmlExportUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class TastingListController {

    @FXML private ListView<Beer> availableBeersListView;
    @FXML private ListView<Beer> tastingListView;

    private final BeerRepository beerRepository = new BeerRepository();
    private final ObservableList<Beer> availableBeers = FXCollections.observableArrayList();
    private final ObservableList<Beer> tastingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadAvailableBeers();
        setupDragAndDrop();
    }

    private void loadAvailableBeers() {
        try {
            List<Beer> beers = beerRepository.findAll();
            availableBeers.setAll(beers);
            availableBeersListView.setItems(availableBeers);
            tastingListView.setItems(tastingList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju piva.");
        }
    }

    private void setupDragAndDrop() {
        // Drag SOURCE - available beers list
        availableBeersListView.setOnDragDetected(event -> {
            Beer selected = availableBeersListView.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            Dragboard dragboard = availableBeersListView.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(selected.getId()));
            dragboard.setContent(content);
            event.consume();
        });

        // Drag TARGET - tasting list
        tastingListView.setOnDragOver(event -> {
            if (event.getGestureSource() != tastingListView &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        tastingListView.setOnDragEntered(event -> {
            if (event.getGestureSource() != tastingListView &&
                    event.getDragboard().hasString()) {
                tastingListView.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2;");
            }
        });

        tastingListView.setOnDragExited(event -> {
            tastingListView.setStyle("");
        });

        tastingListView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasString()) {
                int beerId = Integer.parseInt(dragboard.getString());

                Beer beer = availableBeers.stream()
                        .filter(b -> b.getId() == beerId)
                        .findFirst()
                        .orElse(null);

                if (beer != null && !tastingList.contains(beer)) {
                    tastingList.add(beer);
                    success = true;
                } else if (tastingList.contains(beer)) {
                    showAlert(Alert.AlertType.WARNING, "Upozorenje",
                            beer.getName() + " je već na degustacijskoj listi.");
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    private void handleRemove() {
        Beer selected = tastingListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite pivo za uklanjanje.");
            return;
        }
        tastingList.remove(selected);
    }

    @FXML
    private void handleXmlExport() {
        if (tastingList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Degustacijska lista je prazna.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Spremi XML export");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML datoteke", "*.xml")
        );
        fileChooser.setInitialFileName("degustacijska_lista.xml");

        File file = fileChooser.showSaveDialog(tastingListView.getScene().getWindow());
        if (file != null) {
            try {
                XmlExportUtils.exportTastingList(tastingList, file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Uspjeh",
                        "Degustacijska lista uspješno exportana u XML!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Greška",
                        "Greška pri XML exportu: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear() {
        tastingList.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}