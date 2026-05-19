package hr.algebra.ui;

import hr.algebra.dao.BrewmasterRepository;
import hr.algebra.model.Brewmaster;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class BrewmasterController {

    @FXML private TextField nameField;
    @FXML private TextField bioField;
    @FXML private TextField brewingPhilosophyField;
    @FXML private TableView<Brewmaster> tableView;
    @FXML private TableColumn<Brewmaster, Integer> idColumn;
    @FXML private TableColumn<Brewmaster, String> nameColumn;
    @FXML private TableColumn<Brewmaster, String> bioColumn;
    @FXML private TableColumn<Brewmaster, String> brewingPhilosophyColumn;

    private final BrewmasterRepository brewmasterRepository = new BrewmasterRepository();
    private final ObservableList<Brewmaster> brewmasters = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        nameField.setText(newVal.getName());
                        bioField.setText(newVal.getBio());
                        brewingPhilosophyField.setText(newVal.getBrewingPhilosophy());
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
        bioColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getBio()));
        brewingPhilosophyColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getBrewingPhilosophy()));

        tableView.setItems(brewmasters);
    }

    private void loadData() {
        try {
            List<Brewmaster> list = brewmasterRepository.findAll();
            brewmasters.setAll(list);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju podataka.");
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String bio = bioField.getText().trim();
        String philosophy = brewingPhilosophyField.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite ime pivara.");
            return;
        }

        try {
            brewmasterRepository.save(new Brewmaster(0, name, bio, philosophy));
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri dodavanju.");
        }
    }

    @FXML
    private void handleUpdate() {
        Brewmaster selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite pivara za ažuriranje.");
            return;
        }

        String name = nameField.getText().trim();
        String bio = bioField.getText().trim();
        String philosophy = brewingPhilosophyField.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite ime pivara.");
            return;
        }

        try {
            selected.setName(name);
            selected.setBio(bio);
            selected.setBrewingPhilosophy(philosophy);
            brewmasterRepository.update(selected);
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri ažuriranju.");
        }
    }

    @FXML
    private void handleDelete() {
        Brewmaster selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite pivara za brisanje.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Jeste li sigurni da želite obrisati " + selected.getName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    brewmasterRepository.delete(selected.getId());
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
        bioField.clear();
        brewingPhilosophyField.clear();
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