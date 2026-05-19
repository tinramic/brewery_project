package hr.algebra.ui;

import hr.algebra.dao.UserRepository;
import hr.algebra.model.Role;
import hr.algebra.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import hr.algebra.utils.LogUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserRepository userRepository = new UserRepository();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Molimo unesite korisničko ime i lozinku.");
            return;
        }

        try {
            Optional<User> user = userRepository.login(username, password);

            if (user.isPresent()) {
                LogUtils.log(username, "LOGIN", "Korisnik se prijavio u sustav");
                if (user.get().getRole() == Role.ADMINISTRATOR) {
                    openAdminView(user.get());
                } else {
                    openUserView(user.get());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Greška", "Pogrešno korisničko ime ili lozinka.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri prijavi: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Upozorenje", "Molimo unesite korisničko ime i lozinku.");
            return;
        }

        try {
            if (userRepository.findByUsername(username).isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Greška", "Korisničko ime već postoji.");
                return;
            }

            User newUser = new User(0, username, password, Role.USER);
            userRepository.save(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Uspjeh", "Registracija uspješna! Možete se prijaviti.");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri registraciji: " + e.getMessage());
        }
    }

    private void openAdminView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle("Beer Catalog - Administrator");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri otvaranju admin sučelja.");
        }
    }

    private void openUserView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle("Beer Catalog - Korisnik");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Greška", "Greška pri otvaranju korisničkog sučelja.");
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