package bank.system.bankingsystem;

public class InvestmentAccount extends Account {
    private static final double MONTHLY_INTEREST_RATE = 0.05; // 5%
    private static final double MIN_OPENING_BALANCE = 500.00;

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer, "Investment");
    }

    public static boolean isValidOpeningBalance(double amount) {
        return amount >= MIN_OPENING_BALANCE;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public void calculateInterest() {
        double interest = balance * MONTHLY_INTEREST_RATE;
        balance += interest;
    }

    @Override
    public String getAccountInfo() {
        return String.format("Investment Account: %s, Balance: BWP%.2f, Customer: %s %s",
                accountNumber, balance, customer.getFirstName(), customer.getSurname());
    }
}