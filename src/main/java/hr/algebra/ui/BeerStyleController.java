package hr.algebra.ui;

import hr.algebra.dao.BeerStyleRepository;
import hr.algebra.model.BeerStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class BeerStyleController {

    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TableView<BeerStyle> tableView;
    @FXML private TableColumn<BeerStyle, Integer> idColumn;
    @FXML private TableColumn<BeerStyle, String> nameColumn;
    @FXML private TableColumn<BeerStyle, String> descriptionColumn;

    private final BeerStyleRepository beerStyleRepository = new BeerStyleRepository();
    private final ObservableList<BeerStyle> beerStyles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        nameField.setText(newVal.getName());
                        descriptionField.setText(newVal.getDescription());
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
        descriptionColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getDescription()));

        tableView.setItems(beerStyles);
    }

    private void loadData() {
        try {
            List<BeerStyle> list = beerStyleRepository.findAll();
            beerStyles.setAll(list);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju podataka.");
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite naziv stila.");
            return;
        }

        try {
            beerStyleRepository.save(new BeerStyle(0, name, description));
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri dodavanju.");
        }
    }

    @FXML
    private void handleUpdate() {
        BeerStyle selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite stil za ažuriranje.");
            return;
        }

        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite naziv stila.");
            return;
        }

        try {
            selected.setName(name);
            selected.setDescription(description);
            beerStyleRepository.update(selected);
            loadData();
            handleClear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri ažuriranju.");
        }
    }

    @FXML
    private void handleDelete() {
        BeerStyle selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite stil za brisanje.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Jeste li sigurni da želite obrisati " + selected.getName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    beerStyleRepository.delete(selected.getId());
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
        descriptionField.clear();
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