public class BankSystemTest {
    public static void main(String[] args) {
        Customer c1 = new Customer("John", "Doe", "Gaborone");

        Account savings = new SavingsAccount("S001", "Main Branch", 1000);
        Account invest = new InvestmentAccount("I001", "Main Branch", 2000);
        Account cheque = new ChequeAccount("C001", "Main Branch", 5000, "BAC", "Gaborone");

        c1.addAccount(savings);
        c1.addAccount(invest);
        c1.addAccount(cheque);

        savings.deposit(500);
        invest.withdraw(300);
        cheque.withdraw(1000);

        savings.payInterest();
        invest.payInterest();

        c1.showAccounts();
    }
}

