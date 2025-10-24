package bank.system.bankingsystem;

import java.util.*;

public class BankingService implements BankOperations {
    private Map<String, Customer> customers;
    private Map<String, Account> accounts;
    private Map<String, List<Account>> customerAccounts;

    public BankingService() {
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.customerAccounts = new HashMap<>();
        initializeSampleData();
    }

    private void initializeSampleData() {

        IndividualCustomer customer1 = new IndividualCustomer("C001", "John", "Doe",
                "123 Main St", "john.doe@email.com", "123456789", "ID123456");
        customers.put("C001", customer1);

        SavingsAccount savings = new SavingsAccount("SA001", 1000.0, "Main Branch", customer1);
        accounts.put("SA001", savings);
        addAccountToCustomer("C001", savings);
    }

    @Override
    public boolean openAccount(Customer customer, String accountType, double initialDeposit) {

        return true;
    }

    @Override
    public boolean closeAccount(String accountNumber) {

        return true;
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
        // Simple authentication - in real application, this would connect to database
        return "admin".equals(username) && "password".equals(password);
    }

    private void addAccountToCustomer(String customerId, Account account) {
        customerAccounts.computeIfAbsent(customerId, k -> new ArrayList<>()).add(account);
    }
}