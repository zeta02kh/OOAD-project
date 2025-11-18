package bank.system.bankingsystem;

public interface AccountOperations {
    void deposit(double amount);
    boolean withdraw(double amount);
    double getBalance();
    void calculateInterest();
    String getAccountInfo();
}