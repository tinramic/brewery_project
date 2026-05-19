package hr.algebra;

import hr.algebra.utils.DatabaseUtils;
import hr.algebra.dao.UserRepository;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;
import hr.algebra.utils.ConfigUtils;

import java.sql.SQLException;

public class MainApp extends Application {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), ConfigUtils.getWindowWidth(), ConfigUtils.getWindowHeight());
        primaryStage.setTitle(ConfigUtils.getWindowTitle());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        ConfigUtils.load();
        DatabaseUtils.initialize();
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        try {
            if (userRepository.findByUsername(ConfigUtils.getAdminUsername()).isEmpty()) {
                userRepository.createAdmin(ConfigUtils.getAdminUsername(), ConfigUtils.getAdminPassword());
                System.out.println("Default admin created");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create default admin", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}