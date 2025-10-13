import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String firstName;
    private String lastName;
    private String address;
    private List<Account> accounts;

    public Customer(String firstName, String lastName, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void showAccounts() {
        for (Account a : accounts) {
            System.out.println(a);
        }
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " | Address: " + address;
    }
}

