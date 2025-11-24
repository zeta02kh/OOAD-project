package bank.system.bankingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class BankingController {

    @FXML private TextArea customerInfoArea;
    @FXML private ListView<String> accountsListView;
    @FXML private TextArea transactionInfoArea;
    @FXML private TextField customerIdField;
    @FXML private TextField accountNumberField;
    @FXML private TextField amountField;

    private BankingService bankingService;

    // Called from LoginController
    public void setBankingService(BankingService bankingService) {
        this.bankingService = bankingService;
        refreshAllData();
    }

    // ============================================================
    // CUSTOMER MANAGEMENT
    // ============================================================

    @FXML
    private void showCreateCustomer() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create New Customer");
        dialog.setHeaderText("Choose Customer Type");

        ButtonType individualButton = new ButtonType("Individual", ButtonBar.ButtonData.OK_DONE);
        ButtonType corporateButton = new ButtonType("Corporate", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(individualButton, corporateButton, cancelButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == individualButton) {
                showCreateIndividualCustomerDialog();
            } else if (dialogButton == corporateButton) {
                showCreateCorporateCustomerDialog();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showCreateIndividualCustomerDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Create Individual Customer");

        TextField dlgCustomerId = new TextField();
        TextField firstNameField = new TextField();
        TextField surnameField = new TextField();
        TextField addressField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField idNumberField = new TextField();
        PasswordField passwordField = new PasswordField();

        VBox content = new VBox(10,
                new HBox(10, new Label("Customer ID:"), dlgCustomerId),
                new HBox(10, new Label("First Name:"), firstNameField),
                new HBox(10, new Label("Surname:"), surnameField),
                new HBox(10, new Label("Address:"), addressField),
                new HBox(10, new Label("Email:"), emailField),
                new HBox(10, new Label("Phone:"), phoneField),
                new HBox(10, new Label("ID Number:"), idNumberField),
                new HBox(10, new Label("Password:"), passwordField)
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                boolean success = bankingService.createIndividualCustomer(
                        dlgCustomerId.getText().trim(),
                        firstNameField.getText().trim(),
                        surnameField.getText().trim(),
                        addressField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim(),
                        idNumberField.getText().trim(),
                        passwordField.getText()
                );

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Individual customer created!");
                    refreshAllData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer! ID may already exist or invalid data.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showCreateCorporateCustomerDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Create Corporate Customer");

        TextField dlgCustomerId = new TextField();
        TextField firstNameField = new TextField();
        TextField surnameField = new TextField();
        TextField addressField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField companyNameField = new TextField();
        TextField companyAddressField = new TextField();
        TextField registrationField = new TextField();
        PasswordField passwordField = new PasswordField();

        VBox content = new VBox(10,
                new HBox(10, new Label("Customer ID:"), dlgCustomerId),
                new HBox(10, new Label("First Name:"), firstNameField),
                new HBox(10, new Label("Surname:"), surnameField),
                new HBox(10, new Label("Address:"), addressField),
                new HBox(10, new Label("Email:"), emailField),
                new HBox(10, new Label("Phone:"), phoneField),
                new HBox(10, new Label("Company Name:"), companyNameField),
                new HBox(10, new Label("Company Address:"), companyAddressField),
                new HBox(10, new Label("Registration:"), registrationField),
                new HBox(10, new Label("Password:"), passwordField)
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                boolean success = bankingService.createCorporateCustomer(
                        dlgCustomerId.getText().trim(),
                        firstNameField.getText().trim(),
                        surnameField.getText().trim(),
                        addressField.getText().trim(),
                        emailField.getText().trim(),
                        phoneField.getText().trim(),
                        companyNameField.getText().trim(),
                        companyAddressField.getText().trim(),
                        registrationField.getText().trim(),
                        passwordField.getText()
                );

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Corporate customer created!");
                    refreshAllData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer! ID may already exist or invalid data.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void showFindCustomer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find Customer");
        dialog.setHeaderText("Enter Customer ID:");

        dialog.showAndWait().ifPresent(customerId -> {
            Customer customer = bankingService.findCustomerById(customerId.trim());

            if (customer != null) {
                customerInfoArea.setText(customer.toString());

                List<Account> accounts = bankingService.getCustomerAccounts(customerId.trim());
                StringBuilder info = new StringBuilder("\n\nAccounts:\n");
                for (Account a : accounts) info.append(a.getAccountInfo()).append("\n");
                customerInfoArea.appendText(info.toString());

            } else {
                customerInfoArea.setText("Customer not found!");
            }
        });
    }

    // ============================================================
    // ACCOUNT MANAGEMENT
    // ============================================================

    @FXML
    private void showOpenAccount() {
        String customerId = customerIdField.getText().trim();

        if (customerId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter a Customer ID first!");
            return;
        }

        Customer customer = bankingService.findCustomerById(customerId);
        if (customer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Customer not found!");
            return;
        }

        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("SAVINGS",
                "SAVINGS", "INVESTMENT", "CHEQUE");
        typeDialog.setTitle("Open Account");
        typeDialog.setHeaderText("Select Account Type");

        typeDialog.showAndWait().ifPresent(type -> {
            TextInputDialog amountDialog = new TextInputDialog();
            amountDialog.setTitle("Initial Deposit");
            amountDialog.setHeaderText("Enter opening balance");
            amountDialog.setContentText("Amount:");

            amountDialog.showAndWait().ifPresent(amountStr -> {
                try {
                    double amount = Double.parseDouble(amountStr);

                    if ("INVESTMENT".equalsIgnoreCase(type) && amount < 500) {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Investment accounts require at least BWP 500.00");
                        return;
                    }

                    if ("CHEQUE".equalsIgnoreCase(type)) {
                        // request employer info
                        TextInputDialog empDialog = new TextInputDialog();
                        empDialog.setTitle("Employer Info");
                        empDialog.setHeaderText("Enter Employer Name:");
                        empDialog.showAndWait().ifPresent(employer -> {
                            TextInputDialog empAddr = new TextInputDialog();
                            empAddr.setTitle("Employer Address");
                            empAddr.setHeaderText("Enter Employer Address:");
                            empAddr.showAndWait().ifPresent(employerAddress -> {
                                boolean success = bankingService.openAccount(customer, type, amount, employer, employerAddress);
                                if (success) {
                                    showAlert(Alert.AlertType.INFORMATION, "Success", type + " account opened!");
                                    refreshAllData();
                                } else {
                                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to open account!");
                                }
                            });
                        });
                    } else {
                        boolean success = bankingService.openAccount(customer, type, amount);
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", type + " account opened!");
                            refreshAllData();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open account!");
                        }
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount!");
                }
            });
        });
    }

    @FXML
    private void showCustomerAccounts() {
        String id = customerIdField.getText().trim();
        if (id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Enter a Customer ID!");
            return;
        }

        List<Account> accounts = bankingService.getCustomerAccounts(id);
        accountsListView.getItems().clear();

        if (accounts.isEmpty()) {
            accountsListView.getItems().add("No accounts found.");
        } else {
            for (Account a : accounts) accountsListView.getItems().add(a.getAccountInfo());
        }
    }

    // ============================================================
    // TRANSACTIONS
    // ============================================================

    @FXML
    private void handleDeposit() {
        try {
            String acc = accountNumberField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());

            Account a = bankingService.findAccountByNumber(acc);
            if (a == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Account not found!");
                return;
            }

            a.deposit(amount);
            bankingService.saveAccount(a);
            bankingService.logTransaction("DEPOSIT: " + amount + " to " + acc);

            transactionInfoArea.setText("Deposit successful!\nNew Balance: BWP" + String.format("%.2f", a.getBalance()));
            refreshAllData();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount!");
        }
    }

    @FXML
    private void handleWithdraw() {
        try {
            String acc = accountNumberField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());

            Account a = bankingService.findAccountByNumber(acc);
            if (a == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Account not found!");
                return;
            }

            if (a instanceof SavingsAccount) {
                showAlert(Alert.AlertType.ERROR, "Error", "Savings accounts do NOT allow withdrawals!");
                return;
            }

            if (a.withdraw(amount)) {
                bankingService.saveAccount(a);
                bankingService.logTransaction("WITHDRAW: " + amount + " from " + acc);
                transactionInfoArea.setText("Withdrawal successful!\nNew Balance: BWP" + String.format("%.2f", a.getBalance()));
                refreshAllData();
            } else {
                transactionInfoArea.setText("Withdrawal failed! Insufficient funds or invalid amount.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid input!");
        }
    }

    // ============================================================
    // INTEREST
    // ============================================================

    @FXML
    private void calculateAllInterest() {
        List<Account> accounts = bankingService.getAllAccounts();
        int count = 0;

        for (Account a : accounts) {
            if (a instanceof SavingsAccount || a instanceof InvestmentAccount) {
                a.calculateInterest();
                bankingService.saveAccount(a);
                count++;
            }
        }

        transactionInfoArea.setText("Interest applied to: " + count + " accounts.");
        bankingService.persistAllAccounts();
        refreshAllData();
    }

    private void refreshAllData() {
        if (!customerIdField.getText().trim().isEmpty()) {
            showCustomerAccounts();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
