package hr.algebra.ui;

import hr.algebra.dao.AwardRepository;
import hr.algebra.model.Award;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class AwardController {

    @FXML private TextField nameField;
    @FXML private TextField yearField;
    @FXML private TextField organizationField;
    @FXML private TableView<Award> tableView;
    @FXML private TableColumn<Award, Integer> idColumn;
    @FXML private TableColumn<Award, String> nameColumn;
    @FXML private TableColumn<Award, Integer> yearColumn;
    @FXML private TableColumn<Award, String> organizationColumn;

    private final AwardRepository awardRepository = new AwardRepository();
    private final ObservableList<Award> awards = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        nameField.setText(newVal.getName());
                        yearField.setText(String.valueOf(newVal.getYear()));
                        organizationField.setText(newVal.getOrganization());
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
        yearColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(
                        cell.getValue().getYear()).asObject());
        organizationColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getOrganization()));

        tableView.setItems(awards);
    }

    private void loadData() {
        try {
            List<Award> list = awardRepository.findAll();
            awards.setAll(list);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri učitavanju podataka.");
        }
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String yearText = yearField.getText().trim();
        String organization = organizationField.getText().trim();

        if (name.isEmpty() || yearText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite naziv i godinu nagrade.");
            return;
        }

        try {
            int year = Integer.parseInt(yearText);
            awardRepository.save(new Award(0, name, year, organization));
            loadData();
            handleClear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Godina mora biti broj.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri dodavanju.");
        }
    }

    @FXML
    private void handleUpdate() {
        Award selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite nagradu za ažuriranje.");
            return;
        }

        String name = nameField.getText().trim();
        String yearText = yearField.getText().trim();
        String organization = organizationField.getText().trim();

        if (name.isEmpty() || yearText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Unesite naziv i godinu nagrade.");
            return;
        }

        try {
            int year = Integer.parseInt(yearText);
            selected.setName(name);
            selected.setYear(year);
            selected.setOrganization(organization);
            awardRepository.update(selected);
            loadData();
            handleClear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Godina mora biti broj.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri ažuriranju.");
        }
    }

    @FXML
    private void handleDelete() {
        Award selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Odaberite nagradu za brisanje.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Jeste li sigurni da želite obrisati " + selected.getName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    awardRepository.delete(selected.getId());
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
        yearField.clear();
        organizationField.clear();
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