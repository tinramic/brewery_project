package hr.algebra.ui;

import hr.algebra.dao.BreweryRepository;
import hr.algebra.dao.CountryRepository;
import hr.algebra.model.Brewery;
import hr.algebra.model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class BreweryController {

    @FXML private TextField nameField;
    @FXML private ComboBox<Country> countryComboBox;
    @FXML private TableView<Brewery> tableView;
    @FXML private TableColumn<Brewery, Integer> idColumn;
    @FXML private TableColumn<Brewery, String> nameColumn;
    @FXML private TableColumn<Brewery, String> countryColumn;

    private final BreweryRepository breweryRepository = new BreweryRepository();
    private final CountryRepository countryRepository = new CountryRepository();
    private final ObservableList<Brewery> breweries = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadCountries();
        loadData();

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        nameField.setText(newVal.getName());
                        countryComboBox.setValue(newVal.getCountry());
                    }
                }
        );
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getName()));
        countryColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getCountry() != null ?
                                cell.getValue().getCountry().getName() : ""));

        tableView.setItems(breweries);
    }

    private void loadCountries() {
        try {
            List<Country> countries = countryRepository.findAll();
            countryComboBox.setItems(FXCollections.observableArrayList(countries));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju zemalja.");
        }
    }

    private void loadData() {
        try {
            List<Brewery> list = breweryRepository.findAll();
            breweries.setAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju podataka: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        Country country = countryComboBox.getValue();

        if (name.isEmpty() || country == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite naziv pivovare i odaberite zemlju.");
            return;
        }

        try {
            breweryRepository.save(new Brewery(0, name, country));
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri dodavanju.");
        }
    }

    @FXML
    private void handleUpdate() {
        Brewery selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite pivovaru za ažuriranje.");
            return;
        }

        String name = nameField.getText().trim();
        Country country = countryComboBox.getValue();

        if (name.isEmpty() || country == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite naziv pivovare i odaberite zemlju.");
            return;
        }

        try {
            selected.setName(name);
            selected.setCountry(country);
            breweryRepository.update(selected);
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri ažuriranju.");
        }
    }

    @FXML
    private void handleDelete() {
        Brewery selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite pivovaru za brisanje.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Jeste li sigurni da želite obrisati " + selected.getName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    breweryRepository.delete(selected.getId());
                    loadData();
                    handleClear();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri brisanju.");
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        countryComboBox.setValue(null);
        tableView.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}