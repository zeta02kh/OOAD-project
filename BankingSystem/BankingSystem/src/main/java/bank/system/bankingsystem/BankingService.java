package bank.system.bankingsystem;

import java.io.IOException;
import java.util.*;

public class BankingService implements BankOperations {
    private Map<String, Customer> customers;
    private Map<String, Account> accounts;
    private Map<String, List<Account>> customerAccounts;
    private Map<String, String> customerPasswords;
    private FileDataService fileService;

    public BankingService() {
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.customerAccounts = new HashMap<>();
        this.customerPasswords = new HashMap<>();
        this.fileService = new FileDataService();
        initializeData();
    }

    private void initializeData() {
        try {
            // Load customers from file
            List<Customer> loadedCustomers = fileService.loadAllCustomers();
            for (Customer customer : loadedCustomers) {
                customers.put(customer.getCustomerId(), customer);
            }

            // Load accounts from file
            List<Account> loadedAccounts = fileService.loadAllAccounts(loadedCustomers);
            for (Account account : loadedAccounts) {
                accounts.put(account.getAccountNumber(), account);
                addAccountToCustomer(account.getCustomer().getCustomerId(), account);
            }

            // Load passwords
            customerPasswords = fileService.loadAllPasswords();

            // If no data exists, create sample data
            if (customers.isEmpty()) {
                initializeSampleData();
            }
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            initializeSampleData();
        }
    }

    private void initializeSampleData() {
        try {
            // Individual Customers
            IndividualCustomer customer1 = new IndividualCustomer("C001", "John", "Doe",
                    "123 Main St", "john.doe@email.com", "123456789", "ID123456");
            IndividualCustomer customer2 = new IndividualCustomer("C002", "Jane", "Smith",
                    "456 Oak Ave", "jane.smith@email.com", "987654321", "ID654321");
            IndividualCustomer customer3 = new IndividualCustomer("C003", "Alice", "Johnson",
                    "789 Pine Rd", "alice.johnson@email.com", "555123456", "ID789012");

            // Corporate Customers
            CorporateCustomer customer4 = new CorporateCustomer("C004", "Bob", "Brown",
                    "321 Elm St", "bob.brown@email.com", "555987654",
                    "Brown Enterprises", "123 Business Ave", "REG123456");
            CorporateCustomer customer5 = new CorporateCustomer("C005", "TechCorp", "Ltd",
                    "Tech Park", "info@techcorp.com", "555444333",
                    "TechCorp Ltd", "123 Business Ave", "REG123456");
            CorporateCustomer customer6 = new CorporateCustomer("C006", "BuildIt", "Construction",
                    "Industrial Zone", "contact@buildit.com", "555666777",
                    "BuildIt Construction", "456 Factory Rd", "REG789012");

            // Add customers
            saveCustomer(customer1);
            saveCustomer(customer2);
            saveCustomer(customer3);
            saveCustomer(customer4);
            saveCustomer(customer5);
            saveCustomer(customer6);

            // Set passwords
            fileService.saveCustomerPassword("C001", "password1");
            fileService.saveCustomerPassword("C002", "password2");
            fileService.saveCustomerPassword("C003", "password3");
            fileService.saveCustomerPassword("C004", "password4");
            fileService.saveCustomerPassword("C005", "password5");
            fileService.saveCustomerPassword("C006", "password6");

            // Create sample accounts
            SavingsAccount savings1 = new SavingsAccount("SA001", 1500.0, "Main Branch", customer1);
            InvestmentAccount investment1 = new InvestmentAccount("IA001", 2500.0, "Main Branch", customer1);
            ChequeAccount cheque1 = new ChequeAccount("CA001", 3000.0, "Main Branch", customer1, "ABC Company", "123 Work St");

            SavingsAccount savings2 = new SavingsAccount("SA002", 2000.0, "Downtown Branch", customer2);
            InvestmentAccount investment2 = new InvestmentAccount("IA002", 3500.0, "Downtown Branch", customer2);

            ChequeAccount cheque3 = new ChequeAccount("CA003", 4000.0, "Main Branch", customer3, "XYZ Corp", "456 Office Ave");

            ChequeAccount cheque4 = new ChequeAccount("CA004", 8000.0, "Business Branch", customer4, "Brown Enterprises", "123 Business Ave");

            ChequeAccount cheque5 = new ChequeAccount("CA005", 8000.0, "Business Branch", customer5, "TechCorp Ltd", "123 Business Ave");
            InvestmentAccount investment5 = new InvestmentAccount("IA005", 10000.0, "Business Branch", customer5);

            ChequeAccount cheque6 = new ChequeAccount("CA006", 6000.0, "Business Branch", customer6, "BuildIt Construction", "456 Factory Rd");

            // Save accounts
            saveAccount(savings1);
            saveAccount(investment1);
            saveAccount(cheque1);
            saveAccount(savings2);
            saveAccount(investment2);
            saveAccount(cheque3);
            saveAccount(cheque4);
            saveAccount(cheque5);
            saveAccount(investment5);
            saveAccount(cheque6);

            // Reload data from files
            initializeData();

        } catch (IOException e) {
            System.err.println("Error saving sample data: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) throws IOException {
        fileService.saveCustomer(customer);
        customers.put(customer.getCustomerId(), customer);
    }

    private void saveAccount(Account account) throws IOException {
        fileService.saveAccount(account);
        accounts.put(account.getAccountNumber(), account);
        addAccountToCustomer(account.getCustomer().getCustomerId(), account);
    }

    @Override
    public boolean openAccount(Customer customer, String accountType, double initialDeposit) {
        try {
            // Generate account number
            String accountNumber = generateAccountNumber(accountType);

            Account newAccount = null;
            switch (accountType.toUpperCase()) {
                case "SAVINGS":
                    newAccount = new SavingsAccount(accountNumber, initialDeposit, "Main Branch", customer);
                    break;
                case "INVESTMENT":
                    if (InvestmentAccount.isValidOpeningBalance(initialDeposit)) {
                        newAccount = new InvestmentAccount(accountNumber, initialDeposit, "Main Branch", customer);
                    } else {
                        return false;
                    }
                    break;
                case "CHEQUE":
                    // For cheque account, you'd need employer details
                    newAccount = new ChequeAccount(accountNumber, initialDeposit, "Main Branch",
                            customer, "Unknown", "Unknown");
                    break;
            }

            if (newAccount != null) {
                saveAccount(newAccount);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error saving account: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean closeAccount(String accountNumber) {
        try {
            Account account = accounts.remove(accountNumber);
            if (account != null) {
                String customerId = account.getCustomer().getCustomerId();
                List<Account> customerAccts = customerAccounts.get(customerId);
                if (customerAccts != null) {
                    customerAccts.remove(account);
                }
                // Note: In a file-based system, we'd need to rewrite the files
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error closing account: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Account> getCustomerAccounts(String customerId) {
        return customerAccounts.getOrDefault(customerId, new ArrayList<>());
    }

    @Override
    public Customer findCustomerById(String customerId) {
        return customers.get(customerId);
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        // Admin authentication
        if ("admin".equals(username) && "admin123".equals(password)) {
            return true;
        }

        // Customer authentication
        try {
            return fileService.verifyCustomerPassword(username, password);
        } catch (IOException e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public void logTransaction(String transaction) {
        try {
            fileService.saveTransaction(transaction);
            System.out.println("TRANSACTION LOG: " + transaction);
        } catch (IOException e) {
            System.err.println("Error logging transaction: " + e.getMessage());
        }
    }

    public Account findAccountByNumber(String accountNumber) {
        return accounts.get(accountNumber);
    }

    private String generateAccountNumber(String accountType) {
        String prefix = "";
        switch (accountType.toUpperCase()) {
            case "SAVINGS": prefix = "SA"; break;
            case "INVESTMENT": prefix = "IA"; break;
            case "CHEQUE": prefix = "CA"; break;
        }
        return prefix + String.format("%06d", accounts.size() + 1);
    }

    private void addAccountToCustomer(String customerId, Account account) {
        customerAccounts.computeIfAbsent(customerId, k -> new ArrayList<>()).add(account);
    }
}