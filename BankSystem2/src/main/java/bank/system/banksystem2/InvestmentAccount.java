package bank.system.banksystem2;

public class InvestmentAccount extends Account {

    private static final double INTEREST_RATE = 0.05; // 5% monthly

    public InvestmentAccount(String accountNumber, String branch, double initialBalance) {
        super(accountNumber, branch, initialBalance);
        if (initialBalance < 500) {
            throw new IllegalArgumentException("Minimum initial deposit is BWP 500.00");
        }
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount + " | New Balance: " + balance);
        } else {
            System.out.println("Invalid withdrawal amount.");
        }
    }

    @Override
    public void payInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
        System.out.println("Interest added: " + interest);
    }
}
