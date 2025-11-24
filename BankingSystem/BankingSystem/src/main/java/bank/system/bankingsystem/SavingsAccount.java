package bank.system.bankingsystem;

public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    private static final double MONTHLY_INTEREST_RATE = 0.0005; // 0.05%

    public SavingsAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer, "Savings");
    }

    @Override
    public boolean withdraw(double amount) {
        // Per assignment: Savings accounts do NOT allow withdrawals
        return false;
    }

    @Override
    public void calculateInterest() {
        double interest = balance * MONTHLY_INTEREST_RATE;
        balance += interest;
    }

    @Override
    public String getAccountInfo() {
        return String.format("Savings Account: %s, Balance: BWP%.2f, Customer: %s %s",
                accountNumber, balance, customer.getFirstName(), customer.getSurname());
    }
}
