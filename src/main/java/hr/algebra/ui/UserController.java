package hr.algebra.ui;

import hr.algebra.dao.BeerRepository;
import hr.algebra.dao.BeerStyleRepository;
import hr.algebra.model.Beer;
import hr.algebra.model.BeerStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import hr.algebra.utils.BeerFilterUtils;
import java.util.function.Predicate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserController {

    @FXML private ComboBox<BeerStyle> styleFilterComboBox;
    @FXML private TextField minAbvField;
    @FXML private TextField maxAbvField;
    @FXML private TableView<Beer> tableView;
    @FXML private TableColumn<Beer, Integer> idColumn;
    @FXML private TableColumn<Beer, String> nameColumn;
    @FXML private TableColumn<Beer, Double> abvColumn;
    @FXML private TableColumn<Beer, Integer> ibuColumn;
    @FXML private TableColumn<Beer, Integer> srmColumn;
    @FXML private TableColumn<Beer, String> breweryColumn;
    @FXML private TableColumn<Beer, String> styleColumn;
    @FXML private TableColumn<Beer, String> countryColumn;
    @FXML private TableColumn<Beer, String> flavorColumn;
    @FXML private Label statusLabel;
    @FXML private StackPane contentArea;

    private final BeerRepository beerRepository = new BeerRepository();
    private final BeerStyleRepository beerStyleRepository = new BeerStyleRepository();
    private final ObservableList<Beer> beers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadStyleFilter();
        loadAllBeers();
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
        flavorColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getFlavorProfile()));

        tableView.setItems(beers);
    }

    private void loadStyleFilter() {
        try {
            List<BeerStyle> styles = beerStyleRepository.findAll();
            styleFilterComboBox.setItems(FXCollections.observableArrayList(styles));
        } catch (SQLException e) {
            statusLabel.setText("Greška pri učitavanju stilova.");
        }
    }

    private void loadAllBeers() {
        try {
            List<Beer> list = beerRepository.findAll();
            beers.setAll(list);
            statusLabel.setText("Ukupno piva: " + list.size());
        } catch (SQLException e) {
            statusLabel.setText("Greška pri učitavanju piva.");
        }
    }

    @FXML
    private void handleAllBeers() {
        styleFilterComboBox.setValue(null);
        minAbvField.clear();
        maxAbvField.clear();
        loadAllBeers();
        contentArea.getChildren().setAll(tableView);
    }

    @FXML
    private void handleSearch() {
        BeerStyle selectedStyle = styleFilterComboBox.getValue();
        String minAbvText = minAbvField.getText().trim();
        String maxAbvText = maxAbvField.getText().trim();

        try {
            List<Beer> allBeers = beerRepository.findAll();
            List<Beer> result;

            Predicate<Beer> predicate = beer -> true;

            if (selectedStyle != null) {
                predicate = predicate.and(BeerFilterUtils.byStyle(selectedStyle.getId()));
            }

            if (!minAbvText.isEmpty() && !maxAbvText.isEmpty()) {
                double minAbv = Double.parseDouble(minAbvText);
                double maxAbv = Double.parseDouble(maxAbvText);
                predicate = predicate.and(BeerFilterUtils.byMinAbv(minAbv))
                        .and(BeerFilterUtils.byMaxAbv(maxAbv));
            }

            result = BeerFilterUtils.filter(allBeers, predicate);

            // Consumer - ispis u konzolu za demonstraciju
            result.forEach(BeerFilterUtils.printBeerInfo());

            beers.setAll(result);
            statusLabel.setText("Pronađeno piva: " + result.size());

        } catch (NumberFormatException e) {
            statusLabel.setText("ABV vrijednosti moraju biti decimalni brojevi.");
        } catch (SQLException e) {
            statusLabel.setText("Greška pri pretraživanju.");
        }
    }

    @FXML
    private void handleTastingList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tasting_list.fxml"));
            javafx.scene.Node content = loader.load();
            contentArea.getChildren().setAll(content);
            statusLabel.setText("Degustacijska lista");
        } catch (IOException e) {
            statusLabel.setText("Greška pri učitavanju degustacijske liste.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) tableView.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 350));
            stage.setTitle("Beer Catalog");
        } catch (IOException e) {
            statusLabel.setText("Greška pri odjavi.");
        }
    }
}