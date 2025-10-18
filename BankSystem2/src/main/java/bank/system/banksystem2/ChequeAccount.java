package bank.system.banksystem2;

public class ChequeAccount extends Account {

    private String employerName;
    private String companyAddress;

    public ChequeAccount(String accountNumber, String branch, double initialBalance,
                         String employerName, String companyAddress) {
        super(accountNumber, branch, initialBalance);
        this.employerName = employerName;
        this.companyAddress = companyAddress;
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
        System.out.println("Cheque Accounts do not earn interest.");
    }
}

