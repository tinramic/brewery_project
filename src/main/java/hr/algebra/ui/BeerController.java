package hr.algebra.ui;

import hr.algebra.dao.*;
import hr.algebra.model.*;
import hr.algebra.utils.LogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BeerController {

    @FXML private TextField nameField;
    @FXML private TextField abvField;
    @FXML private TextField ibuField;
    @FXML private TextField srmField;
    @FXML private TextField flavorProfileField;
    @FXML private ComboBox<Brewery> breweryComboBox;
    @FXML private ComboBox<BeerStyle> beerStyleComboBox;
    @FXML private ComboBox<Country> countryComboBox;
    @FXML private ComboBox<Brewmaster> brewmasterComboBox;
    @FXML private Label imagePathLabel;
    @FXML private TableView<Beer> tableView;
    @FXML private TableColumn<Beer, Integer> idColumn;
    @FXML private TableColumn<Beer, String> nameColumn;
    @FXML private TableColumn<Beer, Double> abvColumn;
    @FXML private TableColumn<Beer, Integer> ibuColumn;
    @FXML private TableColumn<Beer, Integer> srmColumn;
    @FXML private TableColumn<Beer, String> breweryColumn;
    @FXML private TableColumn<Beer, String> styleColumn;
    @FXML private TableColumn<Beer, String> countryColumn;
    @FXML private TableColumn<Beer, String> brewmasterColumn;

    private final BeerRepository beerRepository = new BeerRepository();
    private final BreweryRepository breweryRepository = new BreweryRepository();
    private final BeerStyleRepository beerStyleRepository = new BeerStyleRepository();
    private final CountryRepository countryRepository = new CountryRepository();
    private final BrewmasterRepository brewmasterRepository = new BrewmasterRepository();

    private final ObservableList<Beer> beers = FXCollections.observableArrayList();
    private String selectedImagePath = null;

    private static final String ASSETS_DIR = "assets/beers/";

    @FXML
    public void initialize() {
        createAssetsDirectory();
        setupTableColumns();
        loadComboBoxes();
        loadData();

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        populateFields(newVal);
                    }
                }
        );
    }

    private void createAssetsDirectory() {
        File dir = new File(ASSETS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getName()));
        abvColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(
                        cell.getValue().getAbv()).asObject());
        ibuColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getIbu()).asObject());
        srmColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getSrm()).asObject());
        breweryColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getBrewery() != null ?
                                cell.getValue().getBrewery().getName() : ""));
        styleColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getBeerStyle() != null ?
                                cell.getValue().getBeerStyle().getName() : ""));
        countryColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getCountry() != null ?
                                cell.getValue().getCountry().getName() : ""));
        brewmasterColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getBrewmaster() != null ?
                                cell.getValue().getBrewmaster().getName() : ""));

        tableView.setItems(beers);
    }

    private void loadComboBoxes() {
        try {
            breweryComboBox.setItems(FXCollections.observableArrayList(breweryRepository.findAll()));
            beerStyleComboBox.setItems(FXCollections.observableArrayList(beerStyleRepository.findAll()));
            countryComboBox.setItems(FXCollections.observableArrayList(countryRepository.findAll()));
            brewmasterComboBox.setItems(FXCollections.observableArrayList(brewmasterRepository.findAll()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju podataka.");
        }
    }

    private void loadData() {
        try {
            List<Beer> list = beerRepository.findAll();
            beers.setAll(list);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju piva.");
        }
    }

    private void populateFields(Beer beer) {
        nameField.setText(beer.getName());
        abvField.setText(String.valueOf(beer.getAbv()));
        ibuField.setText(String.valueOf(beer.getIbu()));
        srmField.setText(String.valueOf(beer.getSrm()));
        flavorProfileField.setText(beer.getFlavorProfile());
        breweryComboBox.setValue(beer.getBrewery());
        beerStyleComboBox.setValue(beer.getBeerStyle());
        countryComboBox.setValue(beer.getCountry());
        brewmasterComboBox.setValue(beer.getBrewmaster());
        selectedImagePath = beer.getImagePath();
        imagePathLabel.setText(beer.getImagePath() != null ? beer.getImagePath() : "Nema slike");
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberi sliku etikete");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Slike", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(nameField.getScene().getWindow());
        if (file != null) {
            try {
                Path destination = Paths.get(ASSETS_DIR + file.getName());
                Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                selectedImagePath = destination.toString();
                imagePathLabel.setText(file.getName());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri kopiranju slike.");
            }
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateFields()) return;

        try {
            Beer beer = buildBeerFromFields(0);
            beerRepository.save(beer);
            LogUtils.log("admin", "ADD_BEER", "Dodano pivo: " + beer.getName());
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri dodavanju piva.");
        }
    }

    @FXML
    private void handleUpdate() {
        Beer selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite pivo za ažuriranje.");
            return;
        }

        if (!validateFields()) return;

        try {
            Beer beer = buildBeerFromFields(selected.getId());
            beerRepository.update(beer);
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri ažuriranju piva.");
        }
    }

    @FXML
    private void handleDelete() {
        Beer selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite pivo za brisanje.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Jeste li sigurni da želite obrisati " + selected.getName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    deleteImage(selected.getImagePath());
                    beerRepository.delete(selected.getId());
                    loadData();
                    handleClear();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri brisanju piva.");
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        abvField.clear();
        ibuField.clear();
        srmField.clear();
        flavorProfileField.clear();
        breweryComboBox.setValue(null);
        beerStyleComboBox.setValue(null);
        countryComboBox.setValue(null);
        brewmasterComboBox.setValue(null);
        imagePathLabel.setText("Nema slike");
        selectedImagePath = null;
        tableView.getSelectionModel().clearSelection();
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite naziv piva.");
            return false;
        }
        if (breweryComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite pivovaru.");
            return false;
        }
        if (beerStyleComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite stil piva.");
            return false;
        }
        if (countryComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite zemlju.");
            return false;
        }
        if (brewmasterComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite pivara.");
            return false;
        }
        try {
            Double.parseDouble(abvField.getText().trim());
            Integer.parseInt(ibuField.getText().trim());
            Integer.parseInt(srmField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "ABV mora biti decimalni broj, IBU i SRM moraju biti cijeli brojevi.");
            return false;
        }
        return true;
    }

    private Beer buildBeerFromFields(int id) {
        return new Beer(
                id,
                nameField.getText().trim(),
                Double.parseDouble(abvField.getText().trim()),
                Integer.parseInt(ibuField.getText().trim()),
                Integer.parseInt(srmField.getText().trim()),
                flavorProfileField.getText().trim(),
                selectedImagePath,
                breweryComboBox.getValue(),
                beerStyleComboBox.getValue(),
                countryComboBox.getValue(),
                brewmasterComboBox.getValue(),
                new ArrayList<>()
        );
    }

    private void deleteImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}