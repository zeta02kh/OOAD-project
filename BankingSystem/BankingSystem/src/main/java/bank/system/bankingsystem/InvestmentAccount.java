package bank.system.bankingsystem;

public class InvestmentAccount extends Account {
    private static final double MONTHLY_INTEREST_RATE = 0.05; // 5%
    private static final double MIN_OPENING_BALANCE = 500.00;

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer, "INVESTMENT");
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
        balance += balance * MONTHLY_INTEREST_RATE;
    }

    @Override
    public String getAccountInfo() {
        return String.format("Investment %s | BWP %.2f | %s %s",
                accountNumber, balance, customer.getFirstName(), customer.getSurname());
    }
}
