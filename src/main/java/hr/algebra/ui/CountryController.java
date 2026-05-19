package hr.algebra.ui;

import hr.algebra.dao.CountryRepository;
import hr.algebra.model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class CountryController {

    @FXML private TextField nameField;
    @FXML private TableView<Country> tableView;
    @FXML private TableColumn<Country, Integer> idColumn;
    @FXML private TableColumn<Country, String> nameColumn;

    private final CountryRepository countryRepository = new CountryRepository();
    private final ObservableList<Country> countries = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        nameField.setText(newVal.getName());
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

        tableView.setItems(countries);
    }

    private void loadData() {
        try {
            List<Country> list = countryRepository.findAll();
            countries.setAll(list);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju podataka.");
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite naziv zemlje.");
            return;
        }

        try {
            countryRepository.save(new Country(0, name));
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri dodavanju.");
        }
    }

    @FXML
    private void handleUpdate() {
        Country selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite zemlju za ažuriranje.");
            return;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite naziv zemlje.");
            return;
        }

        try {
            selected.setName(name);
            countryRepository.update(selected);
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri ažuriranju.");
        }
    }

    @FXML
    private void handleDelete() {
        Country selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite zemlju za brisanje.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Jeste li sigurni da želite obrisati " + selected.getName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    countryRepository.delete(selected.getId());
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