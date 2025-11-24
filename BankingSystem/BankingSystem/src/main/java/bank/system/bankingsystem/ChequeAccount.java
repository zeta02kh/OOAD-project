package bank.system.bankingsystem;

public class ChequeAccount extends Account {
    private String employer;
    private String employerAddress;

    public ChequeAccount(String accountNumber, double balance, String branch,
                         Customer customer, String employer, String employerAddress) {
        super(accountNumber, balance, branch, customer, "CHEQUE");
        this.employer = employer;
        this.employerAddress = employerAddress;
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
        // no interest for cheque accounts
    }

    @Override
    public String getAccountInfo() {
        return String.format("Cheque %s | BWP %.2f | %s %s | Employer: %s",
                accountNumber, balance, customer.getFirstName(), customer.getSurname(), employer);
    }

    public String getEmployer() { return employer; }
    public String getEmployerAddress() { return employerAddress; }
}
