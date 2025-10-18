package bank.system.banksystem2;

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

    // ---- Getters ----
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    // ---- Business methods ----
    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void showAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found for " + getFullName());
        } else {
            System.out.println("Accounts for " + getFullName() + ":");
            for (Account a : accounts) {
                System.out.println(" - " + a);
            }
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName() + " | Address: " + address;
    }
}
