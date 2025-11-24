package bank.system.bankingsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private BankingService bankingService;

    public LoginController() {
        this.bankingService = new BankingService();
    }

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

        if (bankingService.authenticateUser(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/system/bankingsystem/banking_system.fxml"));
                Parent root = loader.load();

                BankingController mainController = loader.getController();
                mainController.setBankingService(bankingService);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(root, 1000, 650);
                stage.setScene(scene);

                if ("admin".equals(username)) {
                    stage.setTitle("Banking System - Admin Dashboard");
                } else {
                    stage.setTitle("Banking System - Customer Dashboard (" + username + ")");
                }

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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
