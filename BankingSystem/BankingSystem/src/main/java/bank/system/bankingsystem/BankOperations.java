package bank.system.bankingsystem;

import java.util.List;

public interface BankOperations {
    boolean openAccount(Customer customer, String accountType, double initialDeposit);
    boolean closeAccount(String accountNumber);
    List<Account> getCustomerAccounts(String customerId);
    Customer findCustomerById(String customerId);
    boolean authenticateUser(String username, String password);
}