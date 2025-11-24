package bank.system.bankingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class BankingController {

    @FXML private TextArea customerInfoArea;
    @FXML private ListView<String> accountsListView;
    @FXML private TextArea transactionInfoArea;
    @FXML private TextField customerIdField;
    @FXML private TextField accountNumberField;
    @FXML private TextField amountField;

    private BankingService bankingService;

    public void setBankingService(BankingService bankingService) {
        this.bankingService = bankingService;
        refreshAllData();
    }

    // CUSTOMER MANAGEMENT

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

        TextField customerIdField = new TextField();
        TextField firstNameField = new TextField();
        TextField surnameField = new TextField();
        TextField addressField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField idNumberField = new TextField();
        PasswordField passwordField = new PasswordField();

        VBox content = new VBox(10,
                new HBox(10, new Label("Customer ID:"), customerIdField),
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
                        customerIdField.getText(),
                        firstNameField.getText(),
                        surnameField.getText(),
                        addressField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        idNumberField.getText(),
                        passwordField.getText()
                );

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Individual customer created!");
                    refreshAllData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer! Maybe ID exists.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showCreateCorporateCustomerDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Create Corporate Customer");

        TextField customerIdField = new TextField();
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
                new HBox(10, new Label("Customer ID:"), customerIdField),
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
                        customerIdField.getText(),
                        firstNameField.getText(),
                        surnameField.getText(),
                        addressField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        companyNameField.getText(),
                        companyAddressField.getText(),
                        registrationField.getText(),
                        passwordField.getText()
                );

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Corporate customer created!");
                    refreshAllData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer! Maybe ID exists.");
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
            Customer customer = bankingService.findCustomerById(customerId);

            if (customer != null) {
                customerInfoArea.setText(customer.toString());

                List<Account> accounts = bankingService.getCustomerAccounts(customerId);
                StringBuilder info = new StringBuilder("\n\nAccounts:\n");
                for (Account a : accounts) info.append(a.getAccountInfo()).append("\n");
                customerInfoArea.appendText(info.toString());

            } else {
                customerInfoArea.setText("Customer not found!");
            }
        });
    }

    // ACCOUNT MANAGEMENT

    @FXML
    private void showOpenAccount() {
        String custId = customerIdField.getText().trim();
        if (custId.isEmpty()) { showAlert(Alert.AlertType.WARNING, "Warning", "Enter a Customer ID first!"); return; }
        Customer customer = bankingService.findCustomerById(custId);
        if (customer == null) { showAlert(Alert.AlertType.ERROR, "Error", "Customer not found!"); return; }

        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("SAVINGS", "SAVINGS", "INVESTMENT", "CHEQUE");
        typeDialog.setTitle("Open Account");
        typeDialog.setHeaderText("Select Account Type");

        Optional<String> res = typeDialog.showAndWait();
        if (!res.isPresent()) return;
        String type = res.get();

        // get opening amount
        TextInputDialog amountDialog = new TextInputDialog();
        amountDialog.setTitle("Initial Deposit");
        amountDialog.setHeaderText("Enter opening balance");
        amountDialog.setContentText("Amount:");
        Optional<String> amtRes = amountDialog.showAndWait();
        if (!amtRes.isPresent()) return;

        double amount;
        try { amount = Double.parseDouble(amtRes.get()); }
        catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount!"); return; }

        if ("INVESTMENT".equalsIgnoreCase(type) && amount < 500) {
            showAlert(Alert.AlertType.ERROR, "Error", "Investment accounts require at least BWP 500.00");
            return;
        }

        if ("CHEQUE".equalsIgnoreCase(type)) {
            if (!(customer instanceof IndividualCustomer)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Only individual customers can open cheque accounts.");
                return;
            }
            // ask for employer info
            Dialog<ButtonType> empDialog = new Dialog<>();
            empDialog.setTitle("Employment Info (Required for Cheque Account)");
            TextField empField = new TextField();
            TextField empAddrField = new TextField();
            VBox v = new VBox(10, new HBox(10, new Label("Employer:"), empField), new HBox(10, new Label("Employer Addr:"), empAddrField));
            empDialog.getDialogPane().setContent(v);
            empDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Optional<ButtonType> empRes = empDialog.showAndWait();
            if (!empRes.isPresent() || empRes.get() != ButtonType.OK) return;

            String emp = empField.getText().trim();
            String empAddr = empAddrField.getText().trim();
            if (emp.isEmpty() || empAddr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Employer details required for cheque account.");
                return;
            }

            boolean success = bankingService.openChequeAccount(customer, amount, emp, empAddr);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "CHEQUE account opened!");
                refreshAllData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to open cheque account!");
            }
            return;
        }

        boolean success = bankingService.openAccount(customer, type, amount);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", type + " account opened!");
            refreshAllData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open account!");
        }
    }

    @FXML
    private void showCustomerAccounts() {
        String id = customerIdField.getText().trim();
        if (id.isEmpty()) { showAlert(Alert.AlertType.WARNING, "Warning", "Enter a Customer ID!"); return; }
        List<Account> list = bankingService.getCustomerAccounts(id);
        accountsListView.getItems().clear();
        if (list.isEmpty()) accountsListView.getItems().add("No accounts found.");
        else list.forEach(a -> accountsListView.getItems().add(a.getAccountInfo()));
    }

    // TRANSACTIONS

    @FXML
    private void handleDeposit() {
        try {
            String acc = accountNumberField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            Account a = bankingService.findAccountByNumber(acc);
            if (a == null) { showAlert(Alert.AlertType.ERROR, "Error", "Account not found!"); return; }
            bankingService.depositToAccount(acc, amount);
            transactionInfoArea.setText("Deposit successful!\nNew Balance: BWP" + a.getBalance());
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
            if (a == null) { showAlert(Alert.AlertType.ERROR, "Error", "Account not found!"); return; }
            if (a instanceof SavingsAccount) {
                showAlert(Alert.AlertType.ERROR, "Error", "Savings accounts do NOT allow withdrawals!");
                return;
            }
            boolean ok = bankingService.withdrawFromAccount(acc, amount);
            if (ok) {
                transactionInfoArea.setText("Withdrawal successful!\nNew Balance: BWP" + a.getBalance());
                refreshAllData();
            } else {
                transactionInfoArea.setText("Withdrawal failed! Insufficient funds.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid input!");
        }
    }

    @FXML
    private void calculateAllInterest() {
        bankingService.calculateAllInterest();
        transactionInfoArea.setText("Interest applied.");
        refreshAllData();
    }

    private void refreshAllData() {
        if (!customerIdField.getText().trim().isEmpty()) showCustomerAccounts();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
