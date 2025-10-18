package bank.system.banksystem2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BankSystemApp extends Application {

    private final List<Customer> customers = new ArrayList<>();
    private ComboBox<Account> comboAccounts;
    private Label lblBalance;
    private TextArea txtLog;
    private TextField txtAmount;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Bank System GUI");

        // Populate sample customers and accounts
        initSampleData();

        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
                createCustomerTab(),
                createAccountTab()
        );

        Scene scene = new Scene(tabs, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void initSampleData() {
        Customer c1 = new Customer("John", "Doe", "Gaborone");
        c1.addAccount(new SavingsAccount("S001", "Main", 1000));
        c1.addAccount(new InvestmentAccount("I001", "Main", 10000));

        Customer c2 = new StudentCustomer("Alice", "Smith", "Francistown", "UB", "ST123", "CS");
        c2.addAccount(new SavingsAccount("S002", "City", 500));

        Customer c3 = new BusinessCustomer("Bob", "Johnson", "Molepolole", "TechCorp", "TX001", "IT");
        c3.addAccount(new ChequeAccount("C001", "Main", 2000, "TechCorp", "123 Tech St"));

        customers.add(c1);
        customers.add(c2);
        customers.add(c3);
    }

    private Tab createCustomerTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        ListView<Customer> lvCustomers = new ListView<>();
        lvCustomers.getItems().addAll(customers);

        layout.getChildren().add(lvCustomers);

        return new Tab("Customers", layout);
    }

    private Tab createAccountTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        comboAccounts = new ComboBox<>();
        customers.forEach(c -> comboAccounts.getItems().addAll(c.getAccounts()));

        lblBalance = new Label("Balance: ");
        txtAmount = new TextField();
        txtAmount.setPromptText("Enter amount");

        Button btnDeposit = new Button("Deposit");
        btnDeposit.setOnAction(e -> handleDeposit());

        Button btnWithdraw = new Button("Withdraw");
        btnWithdraw.setOnAction(e -> handleWithdraw());

        Button btnInterest = new Button("Pay Interest");
        btnInterest.setOnAction(e -> handleInterest());

        txtLog = new TextArea();
        txtLog.setPrefHeight(200);
        txtLog.setEditable(false);

        HBox hBox = new HBox(10, txtAmount, btnDeposit, btnWithdraw);

        layout.getChildren().addAll(new Label("Select Account:"), comboAccounts,
                lblBalance, hBox, btnInterest, txtLog);

        comboAccounts.setOnAction(e -> updateBalance());

        return new Tab("Accounts", layout);
    }

    private void updateBalance() {
        Account acc = comboAccounts.getValue();
        if (acc != null) {
            lblBalance.setText("Balance: " + acc.getBalance());
        }
    }

    private void handleDeposit() {
        Account acc = comboAccounts.getValue();
        if (acc != null) {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                acc.deposit(amount);
                txtLog.appendText("Deposited " + amount + " to " + acc.getAccountNumber() + "\n");
                updateBalance();
            } catch (NumberFormatException ex) {
                txtLog.appendText("Invalid amount!\n");
            }
        }
    }

    private void handleWithdraw() {
        Account acc = comboAccounts.getValue();
        if (acc != null) {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                acc.withdraw(amount);
                txtLog.appendText("Withdrew " + amount + " from " + acc.getAccountNumber() + "\n");
                updateBalance();
            } catch (NumberFormatException ex) {
                txtLog.appendText("Invalid amount!\n");
            }
        }
    }

    private void handleInterest() {
        Account acc = comboAccounts.getValue();
        if (acc != null) {
            acc.payInterest();
            txtLog.appendText("Interest applied to " + acc.getAccountNumber() + "\n");
            updateBalance();
        }
    }
}
