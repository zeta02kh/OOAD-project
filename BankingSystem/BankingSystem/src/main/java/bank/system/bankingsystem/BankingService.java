package bank.system.bankingsystem;

import java.io.IOException;
import java.util.*;

/**
 * Service layer that keeps in-memory maps and persists using FileDataService.
 */
public class BankingService {
    private Map<String, Customer> customers = new LinkedHashMap<>();
    private Map<String, Account> accounts = new LinkedHashMap<>();
    private Map<String, List<Account>> customerAccounts = new HashMap<>();
    private Map<String, String> customerPasswords = new HashMap<>();
    private FileDataService fileService;

    public BankingService() {
        fileService = new FileDataService();
        initializeData();
    }

    private void initializeData() {
        try {
            //Load customers
            List<Customer> loadedCustomers = fileService.loadAllCustomers();
            for (Customer c : loadedCustomers) {
                customers.put(c.getCustomerId(), c);
            }

            // Load passwords
            customerPasswords = fileService.loadAllPasswords();

            // Load accounts (needs customers map to be ready)
            List<Account> loadedAccounts = fileService.loadAllAccounts(customers);
            for (Account a : loadedAccounts) {
                accounts.put(a.getAccountNumber(), a);
                addAccountToCustomer(a.getCustomer().getCustomerId(), a);
            }

            // If no customers loaded, initialize sample data (only for first run)
            if (customers.isEmpty()) initializeSampleData();

        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            try { initializeSampleData(); } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    private void initializeSampleData() throws IOException {
        IndividualCustomer customer1 = new IndividualCustomer("C001", "John", "Doe",
                "123 Main St", "john.doe@email.com", "123456789", "ID123456");
        IndividualCustomer customer2 = new IndividualCustomer("C002", "Jane", "Smith",
                "456 Oak Ave", "jane.smith@email.com", "987654321", "ID654321");
        IndividualCustomer customer3 = new IndividualCustomer("C003", "Alice", "Johnson",
                "789 Pine Rd", "alice.johnson@email.com", "555123456", "ID789012");

        CorporateCustomer customer4 = new CorporateCustomer("C004", "Bob", "Brown",
                "321 Elm St", "bob.brown@email.com", "555987654",
                "Brown Enterprises", "123 Business Ave", "REG123456");

        customers.put(customer1.getCustomerId(), customer1);
        customers.put(customer2.getCustomerId(), customer2);
        customers.put(customer3.getCustomerId(), customer3);
        customers.put(customer4.getCustomerId(), customer4);

        // Save passwords
        customerPasswords.put("C001", "password1");
        customerPasswords.put("C002", "password2");
        customerPasswords.put("C003", "password3");
        customerPasswords.put("C004", "password4");

        // Sample accounts
        saveNewAccount(new SavingsAccount(generateAccountNumber("SAVINGS"), 1500.0, "Main Branch", customer1));
        saveNewAccount(new InvestmentAccount(generateAccountNumber("INVESTMENT"), 2500.0, "Main Branch", customer1));
        saveNewAccount(new ChequeAccount(generateAccountNumber("CHEQUE"), 3000.0, "Main Branch", customer1, "ABC Company", "123 Work St"));

        saveNewAccount(new SavingsAccount(generateAccountNumber("SAVINGS"), 2000.0, "Downtown Branch", customer2));
        saveNewAccount(new InvestmentAccount(generateAccountNumber("INVESTMENT"), 3500.0, "Downtown Branch", customer2));
        saveNewAccount(new ChequeAccount(generateAccountNumber("CHEQUE"), 4000.0, "Main Branch", customer3, "XYZ Corp", "456 Office Ave"));

        // persist initial data to files
        persistAll();
    }

    // Create customer methods
    public boolean createIndividualCustomer(String customerId, String firstName, String surname,
                                            String address, String email, String phoneNumber,
                                            String idNumber, String password) {
        if (customers.containsKey(customerId)) return false;
        IndividualCustomer c = new IndividualCustomer(customerId, firstName, surname, address, email, phoneNumber, idNumber);
        customers.put(customerId, c);
        customerPasswords.put(customerId, password);
        addCustomerToFiles();
        return true;
    }

    public boolean createCorporateCustomer(String customerId, String firstName, String surname,
                                           String address, String email, String phoneNumber,
                                           String companyName, String companyAddress,
                                           String registrationNumber, String password) {
        if (customers.containsKey(customerId)) return false;
        CorporateCustomer c = new CorporateCustomer(customerId, firstName, surname, address, email, phoneNumber,
                companyName, companyAddress, registrationNumber);
        customers.put(customerId, c);
        customerPasswords.put(customerId, password);
        addCustomerToFiles();
        return true;
    }

    private void addCustomerToFiles() {
        try {
            fileService.saveCustomerMap(customers);
            fileService.saveCustomerPassword("dummy", "dummy"); // noop but ensures file exists - we'll rewrite properly below
            // write passwords properly
            for (Map.Entry<String, String> e : customerPasswords.entrySet()) {
                fileService.saveCustomerPassword(e.getKey(), e.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error saving customers/passwords: " + e.getMessage());
        }
    }

    // Account creation helpers
    public boolean openAccount(Customer customer, String accountType, double initialDeposit) {
        if (customer == null) return false;
        if ("INVESTMENT".equalsIgnoreCase(accountType) && !InvestmentAccount.isValidOpeningBalance(initialDeposit)) {
            return false;
        }
        if ("CHEQUE".equalsIgnoreCase(accountType)) {
            // Cheque needs employer details - use openChequeAccount for that
            return false;
        }
        String accNum = generateAccountNumber(accountType);
        Account acc = null;
        switch (accountType.toUpperCase()) {
            case "SAVINGS":
                acc = new SavingsAccount(accNum, initialDeposit, "Main Branch", customer);
                break;
            case "INVESTMENT":
                acc = new InvestmentAccount(accNum, initialDeposit, "Main Branch", customer);
                break;
        }
        if (acc != null) {
            saveNewAccount(acc);
            persistAll();
            return true;
        }
        return false;
    }

    // Overload for cheque with employer info
    public boolean openChequeAccount(Customer customer, double initialDeposit, String employer, String employerAddress) {
        if (customer == null) return false;
        if (!(customer instanceof IndividualCustomer)) return false; // only individuals
        String accNum = generateAccountNumber("CHEQUE");
        ChequeAccount acc = new ChequeAccount(accNum, initialDeposit, "Main Branch", customer, employer, employerAddress);
        saveNewAccount(acc);
        persistAll();
        return true;
    }

    private void saveNewAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
        addAccountToCustomer(account.getCustomer().getCustomerId(), account);
        try {
            fileService.saveAccountMap(accounts);
        } catch (IOException e) {
            System.err.println("Error saving account file: " + e.getMessage());
        }
    }

    public boolean closeAccount(String accountNumber) {
        Account a = accounts.remove(accountNumber);
        if (a != null) {
            List<Account> list = customerAccounts.get(a.getCustomer().getCustomerId());
            if (list != null) list.removeIf(x -> x.getAccountNumber().equals(accountNumber));
            try {
                fileService.saveAccountMap(accounts);
                return true;
            } catch (IOException e) {
                System.err.println("Error updating accounts file: " + e.getMessage());
            }
        }
        return false;
    }

    public List<Account> getCustomerAccounts(String customerId) {
        return customerAccounts.getOrDefault(customerId, new ArrayList<>());
    }

    public Customer findCustomerById(String customerId) {
        return customers.get(customerId);
    }

    public boolean authenticateUser(String username, String password) {
        try {
            return fileService.authenticateUser(username, password);
        } catch (IOException e) {
            System.err.println("Auth error: " + e.getMessage());
            return false;
        }
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public Account findAccountByNumber(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public void depositToAccount(String accountNumber, double amount) {
        Account a = findAccountByNumber(accountNumber);
        if (a != null && amount > 0) {
            a.deposit(amount);
            try { fileService.saveAccountMap(accounts); } catch (IOException e) { e.printStackTrace(); }
            logTransaction(String.format("DEPOSIT: %.2f to %s", amount, accountNumber));
        }
    }

    public boolean withdrawFromAccount(String accountNumber, double amount) {
        Account a = findAccountByNumber(accountNumber);
        if (a != null) {
            boolean ok = a.withdraw(amount);
            if (ok) {
                try { fileService.saveAccountMap(accounts); } catch (IOException e) { e.printStackTrace(); }
                logTransaction(String.format("WITHDRAW: %.2f from %s", amount, accountNumber));
                return true;
            }
        }
        return false;
    }

    public void calculateAllInterest() {
        int count = 0;
        for (Account a : accounts.values()) {
            if (a instanceof SavingsAccount || a instanceof InvestmentAccount) {
                a.calculateInterest();
                count++;
            }
        }
        try { fileService.saveAccountMap(accounts); } catch (IOException e) { e.printStackTrace(); }
        logTransaction("Interest applied to " + count + " accounts.");
    }

    public void logTransaction(String transaction) {
        try {
            fileService.saveTransaction(transaction);
        } catch (IOException e) {
            System.err.println("Error logging transaction: " + e.getMessage());
        }
    }

    public String generateAccountNumber(String accountType) {
        String prefix;
        switch (accountType.toUpperCase()) {
            case "SAVINGS": prefix = "SA"; break;
            case "INVESTMENT": prefix = "IA"; break;
            case "CHEQUE": prefix = "CA"; break;
            default: prefix = "AC"; break;
        }
        // find highest existing numeric suffix for this prefix
        int max = 0;
        for (String acc : accounts.keySet()) {
            if (acc.startsWith(prefix)) {
                String numPart = acc.substring(prefix.length()).replaceFirst("^0+", "");
                try {
                    int n = Integer.parseInt(numPart);
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        int next = max + 1;
        return prefix + String.format("%06d", next);
    }

    private void addAccountToCustomer(String customerId, Account account) {
        customerAccounts.computeIfAbsent(customerId, k -> new ArrayList<>()).add(account);
    }

    private void persistAll() {
        try {
            fileService.saveCustomerMap(customers);
            fileService.saveAccountMap(accounts);
            // save passwords map
            for (Map.Entry<String, String> e : customerPasswords.entrySet()) {
                fileService.saveCustomerPassword(e.getKey(), e.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error persisting data: " + e.getMessage());
        }
    }
}
