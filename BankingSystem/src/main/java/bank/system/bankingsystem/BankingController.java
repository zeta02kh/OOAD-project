package bank.system.bankingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class BankingController {
    @FXML private TextArea customerInfoArea;
    @FXML private TextField customerIdField;
    @FXML private ListView<String> accountsListView;
    @FXML private TextField accountNumberField;
    @FXML private TextField amountField;
    @FXML private TextArea transactionInfoArea;

    private BankOperations bankOperations;

    public BankingController() {
        this.bankOperations = new BankingService();
    }

    @FXML
    private void showCreateCustomer() {

        customerInfoArea.setText("Create Customer functionality would be implemented here");
    }

    @FXML
    private void showFindCustomer() {
        String customerId = customerIdField.getText();
        if (!customerId.isEmpty()) {
            Customer customer = bankOperations.findCustomerById(customerId);
            if (customer != null) {
                customerInfoArea.setText(customer.toString());
            } else {
                customerInfoArea.setText("Customer not found");
            }
        }
    }

    @FXML
    private void showOpenAccount() {
        // Implementation for opening account dialog
        customerInfoArea.setText("Open Account functionality would be implemented here");
    }

    @FXML
    private void showCustomerAccounts() {
        String customerId = customerIdField.getText();
        if (!customerId.isEmpty()) {
            List<Account> accounts = bankOperations.getCustomerAccounts(customerId);
            accountsListView.getItems().clear();
            for (Account account : accounts) {
                accountsListView.getItems().add(account.getAccountInfo());
            }
        }
    }

    @FXML
    private void handleDeposit() {
        String accountNumber = accountNumberField.getText();
        String amountText = amountField.getText();

        try {
            double amount = Double.parseDouble(amountText);

            transactionInfoArea.setText(String.format("Deposited BWP%.2f to account %s", amount, accountNumber));
        } catch (NumberFormatException e) {
            transactionInfoArea.setText("Invalid amount format");
        }
    }

    @FXML
    private void handleWithdraw() {
        String accountNumber = accountNumberField.getText();
        String amountText = amountField.getText();

        try {
            double amount = Double.parseDouble(amountText);

            transactionInfoArea.setText(String.format("Withdrawn BWP%.2f from account %s", amount, accountNumber));
        } catch (NumberFormatException e) {
            transactionInfoArea.setText("Invalid amount format");
        }
    }
}