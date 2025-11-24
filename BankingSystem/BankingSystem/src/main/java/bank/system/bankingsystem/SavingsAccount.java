package bank.system.bankingsystem;

public class SavingsAccount extends Account {
    private static final double MONTHLY_INTEREST_RATE = 0.0005; // 0.05%

    public SavingsAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer, "SAVINGS");
    }

    @Override
    public boolean withdraw(double amount) {
        // Savings accounts do NOT allow withdrawals per spec
        return false;
    }

    @Override
    public void calculateInterest() {
        balance += balance * MONTHLY_INTEREST_RATE;
    }

    @Override
    public String getAccountInfo() {
        return String.format("Savings %s | BWP %.2f | %s %s",
                accountNumber, balance, customer.getFirstName(), customer.getSurname());
    }
}
