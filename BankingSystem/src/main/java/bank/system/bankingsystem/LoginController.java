package bank.system.bankingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final String VALID_USERNAME = "admin";
    private final String VALID_PASSWORD = "password";

    @FXML
    private void initialize() {
        messageLabel.setText("");
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password");
            return;
        }

        if (authenticate(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/system/bankingsystem/banking_system.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(root, 800, 600);
                stage.setScene(scene);
                stage.setTitle("Banking System - Main Dashboard");
                stage.centerOnScreen();

            } catch (Exception e) {
                showError("Error loading application", "Failed to load the banking system interface.");
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Invalid username or password");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    private boolean authenticate(String username, String password) {
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}