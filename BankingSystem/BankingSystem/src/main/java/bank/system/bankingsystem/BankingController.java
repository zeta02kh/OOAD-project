package bank.system.bankingsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Date;
import java.util.List;

public class BankingController {
    @FXML private TextArea customerInfoArea;
    @FXML private TextField customerIdField;
    @FXML private ListView<String> accountsListView;
    @FXML private TextField accountNumberField;
    @FXML private TextField amountField;
    @FXML private TextArea transactionInfoArea;

    private BankingService bankingService;

    public BankingController() {
        this.bankingService = new BankingService();
    }

    @FXML
    private void showCreateCustomer() {
        customerInfoArea.setText("Create Customer functionality with database integration\n\n" +
                "Sample customers are pre-loaded in the database:\n" +
                "- C001: John Doe\n" +
                "- C002: Jane Smith\n" +
                "- C003: Alice Johnson\n" +
                "- C004: Bob Brown\n" +
                "- C005: TechCorp Ltd\n" +
                "- C006: BuildIt Construction");
    }

    @FXML
    private void showFindCustomer() {
        String customerId = customerIdField.getText();
        if (!customerId.isEmpty()) {
            Customer customer = bankingService.findCustomerById(customerId);
            if (customer != null) {
                customerInfoArea.setText(customer.toString());

                // Also show accounts
                List<Account> accounts = bankingService.getCustomerAccounts(customerId);
                if (!accounts.isEmpty()) {
                    customerInfoArea.appendText("\n\nAccounts:");
                    for (Account account : accounts) {
                        customerInfoArea.appendText("\n- " + account.getAccountInfo());
                    }
                }
            } else {
                customerInfoArea.setText("Customer not found. Try: C001, C002, C003, C004, C005, or C006");
            }
        }
    }

    @FXML
    private void showOpenAccount() {
        customerInfoArea.setText("Open Account functionality with database integration\n\n" +
                "This would open a dialog to create new accounts for customers.");
    }

    @FXML
    private void showCustomerAccounts() {
        String customerId = customerIdField.getText();
        if (!customerId.isEmpty()) {
            List<Account> accounts = bankingService.getCustomerAccounts(customerId);
            accountsListView.getItems().clear();
            if (accounts.isEmpty()) {
                accountsListView.getItems().add("No accounts found for customer " + customerId);
            } else {
                for (Account account : accounts) {
                    accountsListView.getItems().add(account.getAccountInfo());
                }
            }
        }
    }

    @FXML
    private void handleDeposit() {
        String accountNumber = accountNumberField.getText();
        String amountText = amountField.getText();

        try {
            double amount = Double.parseDouble(amountText);
            // Find account and deposit
            Account account = findAccount(accountNumber);
            if (account != null) {
                account.deposit(amount);
                String transaction = String.format("DEPOSIT|%s|%.2f|%s",
                        accountNumber, amount, new Date());
                bankingService.logTransaction(transaction);
                transactionInfoArea.setText(String.format("Deposited BWP%.2f to account %s", amount, accountNumber));

                // Refresh account display
                showCustomerAccounts();
            } else {
                transactionInfoArea.setText("Account not found. Try: SA001, IA001, CA001, etc.");
            }
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
            Account account = findAccount(accountNumber);
            if (account != null) {
                boolean success = account.withdraw(amount);
                if (success) {
                    String transaction = String.format("WITHDRAW|%s|%.2f|%s",
                            accountNumber, amount, new Date());
                    bankingService.logTransaction(transaction);
                    transactionInfoArea.setText(String.format("Withdrawn BWP%.2f from account %s", amount, accountNumber));

                    // Refresh account display
                    showCustomerAccounts();
                } else {
                    transactionInfoArea.setText("Withdrawal failed. Insufficient funds or invalid amount.");
                }
            } else {
                transactionInfoArea.setText("Account not found. Try: SA001, IA001, CA001, etc.");
            }
        } catch (NumberFormatException e) {
            transactionInfoArea.setText("Invalid amount format");
        }
    }

    private Account findAccount(String accountNumber) {
        return bankingService.findAccountByNumber(accountNumber);
    }


}