package bank.system.bankingsystem;

import java.io.*;
import java.util.*;

public class BankingService implements BankOperations {

    private Map<String, Customer> customers;
    private Map<String, Account> accounts;
    private Map<String, List<Account>> customerAccounts;
    private FileDataService fileService;

    public BankingService() {
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.customerAccounts = new HashMap<>();
        this.fileService = new FileDataService();

        initializeData();
    }

    // =====================================================================
    // INITIAL DATA LOAD
    // =====================================================================
    private void initializeData() {
        try {
            // Load customers
            List<Customer> loadedCustomers = fileService.loadAllCustomers();
            for (Customer c : loadedCustomers) {
                customers.put(c.getCustomerId(), c);
            }


            List<Account> loadedAccounts = fileService.loadAllAccounts();

            for (Account ac : loadedAccounts) {
                accounts.put(ac.getAccountNumber(), ac);
                addAccountToCustomer(ac.getCustomer().getCustomerId(), ac);
            }


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
            IndividualCustomer customer1 = new IndividualCustomer("C001", "John", "Doe",
                    "123 Main St", "john.doe@email.com",
                    "123456789", "ID123456");

            IndividualCustomer customer2 = new IndividualCustomer("C002", "Jane", "Smith",
                    "456 Oak Ave", "jane.smith@email.com",
                    "987654321", "ID654321");

            IndividualCustomer customer3 = new IndividualCustomer("C003", "Alice", "Johnson",
                    "789 Pine Rd", "alice.johnson@email.com",
                    "555123456", "ID789012");

            CorporateCustomer customer4 = new CorporateCustomer("C004", "Bob", "Brown",
                    "321 Elm St", "bob.brown@email.com",
                    "555987654", "Brown Enterprises",
                    "123 Business Ave", "REG123456");

            saveCustomer(customer1);
            saveCustomer(customer2);
            saveCustomer(customer3);
            saveCustomer(customer4);

            fileService.saveCustomerPassword("C001", "password1");
            fileService.saveCustomerPassword("C002", "password2");
            fileService.saveCustomerPassword("C003", "password3");
            fileService.saveCustomerPassword("C004", "password4");

            saveAccount(new SavingsAccount("SA001", 1500.0, "Main Branch", customer1));
            saveAccount(new InvestmentAccount("IA001", 2500.0, "Main Branch", customer1));
            saveAccount(new ChequeAccount("CA001", 3000.0, "Main Branch", customer1,
                    "ABC Company", "123 Work St"));

            saveAccount(new SavingsAccount("SA002", 2000.0, "Downtown", customer2));
            saveAccount(new InvestmentAccount("IA002", 3500.0, "Downtown", customer2));
            saveAccount(new ChequeAccount("CA003", 4000.0, "Main Branch", customer3,
                    "XYZ Corp", "456 Office Ave"));

        } catch (IOException e) {
            System.err.println("Error saving sample data: " + e.getMessage());
        }
    }


    public boolean createIndividualCustomer(String customerId, String firstName, String surname,
                                            String address, String email, String phoneNumber,
                                            String idNumber, String password) {
        try {
            if (customers.containsKey(customerId)) return false;

            IndividualCustomer c = new IndividualCustomer(customerId, firstName, surname,
                    address, email, phoneNumber, idNumber);

            saveCustomer(c);
            fileService.saveCustomerPassword(customerId, password);
            return true;

        } catch (IOException e) {
            System.err.println("Error creating individual customer: " + e.getMessage());
            return false;
        }
    }

    public boolean createCorporateCustomer(String customerId, String firstName, String surname,
                                           String address, String email, String phoneNumber,
                                           String companyName, String companyAddress,
                                           String registrationNumber, String password) {
        try {
            if (customers.containsKey(customerId)) return false;

            CorporateCustomer c = new CorporateCustomer(customerId, firstName, surname,
                    address, email, phoneNumber, companyName, companyAddress, registrationNumber);

            saveCustomer(c);
            fileService.saveCustomerPassword(customerId, password);
            return true;

        } catch (IOException e) {
            System.err.println("Error creating corporate customer: " + e.getMessage());
            return false;
        }
    }

    private void saveCustomer(Customer customer) throws IOException {
        fileService.saveCustomer(customer);
        customers.put(customer.getCustomerId(), customer);
    }


    public void saveAccount(Account account) {
        try {
            fileService.saveAccount(account);
            accounts.put(account.getAccountNumber(), account);
            addAccountToCustomer(account.getCustomer().getCustomerId(), account);

        } catch (IOException e) {
            System.err.println("Error saving account: " + e.getMessage());
        }
    }


    // OPEN ACCOUNT

    @Override
    public boolean openAccount(Customer customer, String accountType,
                               double initialDeposit, String... extra) {

        try {
            String number = generateAccountNumber(accountType);
            Account newAccount = null;

            switch (accountType.toUpperCase()) {
                case "SAVINGS":
                    newAccount = new SavingsAccount(number, initialDeposit,
                            "Main Branch", customer);
                    break;

                case "INVESTMENT":
                    if (!InvestmentAccount.isValidOpeningBalance(initialDeposit)) return false;
                    newAccount = new InvestmentAccount(number, initialDeposit,
                            "Main Branch", customer);
                    break;

                case "CHEQUE":
                    if (extra.length < 2) return false;
                    newAccount = new ChequeAccount(number, initialDeposit,
                            "Main Branch", customer, extra[0], extra[1]);
                    break;
            }

            if (newAccount != null) {
                saveAccount(newAccount);
                return true;
            }

        } catch (Exception e) {
            System.err.println("Error opening account: " + e.getMessage());
        }

        return false;
    }


    // CLOSE ACCOUNT

    @Override
    public boolean closeAccount(String accountNumber) {
        try {
            Account removed = accounts.remove(accountNumber);
            if (removed != null) {
                String cid = removed.getCustomer().getCustomerId();
                List<Account> list = customerAccounts.get(cid);
                if (list != null) list.removeIf(a -> a.getAccountNumber().equals(accountNumber));

                persistAllAccounts();
                return true;
            }

        } catch (Exception e) {
            System.err.println("Error closing account: " + e.getMessage());
        }

        return false;
    }


    // LOOKUP METHODS

    @Override
    public List<Account> getCustomerAccounts(String customerId) {
        return new ArrayList<>(customerAccounts.getOrDefault(customerId, new ArrayList<>()));
    }

    @Override
    public Customer findCustomerById(String customerId) {
        return customers.get(customerId);
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        try {
            return fileService.authenticateUser(username, password);
        } catch (IOException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }

    public Account findAccountByNumber(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public void logTransaction(String transaction) {
        try {
            fileService.saveTransaction(transaction);
        } catch (IOException e) {
            System.err.println("Error logging transaction: " + e.getMessage());
        }
    }


    // ACCOUNT NUMBER GENERATOR

    public String generateAccountNumber(String type) {
        String prefix = switch (type.toUpperCase()) {
            case "SAVINGS" -> "SA";
            case "INVESTMENT" -> "IA";
            case "CHEQUE" -> "CA";
            default -> "AC";
        };

        int num = accounts.size() + 1;
        String candidate;

        do {
            candidate = prefix + String.format("%06d", num++);
        } while (accounts.containsKey(candidate));

        return candidate;
    }

    private void addAccountToCustomer(String customerId, Account account) {
        customerAccounts.computeIfAbsent(customerId, k -> new ArrayList<>()).add(account);
    }


    // WRITE ALL ACCOUNTS BACK TO FILE

    public void persistAllAccounts() {
        try {
            File file = new File(System.getProperty("user.dir") + "/data/accounts.dat");
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(new ArrayList<>(accounts.values()));
            }
        } catch (IOException e) {
            System.err.println("Error persisting accounts: " + e.getMessage());
        }
    }
}
