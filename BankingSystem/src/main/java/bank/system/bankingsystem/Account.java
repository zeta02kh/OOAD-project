package bank.system.bankingsystem;

public abstract class Account implements AccountOperations {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected Customer customer;
    protected String accountType;

    public Account(String accountNumber, double balance, String branch, Customer customer, String accountType) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.customer = customer;
        this.accountType = accountType;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public Customer getCustomer() { return customer; }
    public String getAccountType() { return accountType; }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    @Override
    public abstract boolean withdraw(double amount);

    @Override
    public abstract void calculateInterest();

    @Override
    public abstract String getAccountInfo();
}