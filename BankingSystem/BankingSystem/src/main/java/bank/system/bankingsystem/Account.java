package bank.system.bankingsystem;

public abstract class Account {
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

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public abstract boolean withdraw(double amount);
    public abstract void calculateInterest();
    public abstract String getAccountInfo();

    // Serialization-friendly line format for accounts file:
    // ACCOUNTTYPE|ACCNUM|BALANCE|BRANCH|CUSTOMERID|EMPLOYER|EMPLOYER_ADDR
    // EMPLOYER fields are optional (only for Cheque accounts).
}
