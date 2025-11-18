package bank.system.bankingsystem;

import java.io.*;
import java.util.*;

public class FileDataService {
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String PASSWORDS_FILE = "passwords.txt";

    // Customer operations
    public void saveCustomer(Customer customer) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(CUSTOMERS_FILE, true))) {
            out.println(serializeCustomer(customer));
        }
    }

    public List<Customer> loadAllCustomers() throws IOException {
        List<Customer> customers = new ArrayList<>();
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) return customers;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Customer customer = deserializeCustomer(line);
                if (customer != null) {
                    customers.add(customer);
                }
            }
        }
        return customers;
    }

    // Account operations
    public void saveAccount(Account account) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(ACCOUNTS_FILE, true))) {
            out.println(serializeAccount(account));
        }
    }

    public List<Account> loadAllAccounts(List<Customer> customers) throws IOException {
        List<Account> accounts = new ArrayList<>();
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) return accounts;

        Map<String, Customer> customerMap = new HashMap<>();
        for (Customer customer : customers) {
            customerMap.put(customer.getCustomerId(), customer);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Account account = deserializeAccount(line, customerMap);
                if (account != null) {
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }

    // Transaction operations
    public void saveTransaction(String transaction) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            out.println(transaction);
        }
    }

    public List<String> loadAllTransactions() throws IOException {
        List<String> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) return transactions;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                transactions.add(line);
            }
        }
        return transactions;
    }

    // Password operations
    public void saveCustomerPassword(String customerId, String password) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(PASSWORDS_FILE, true))) {
            out.println(customerId + "|" + password);
        }
    }

    public Map<String, String> loadAllPasswords() throws IOException {
        Map<String, String> passwords = new HashMap<>();
        File file = new File(PASSWORDS_FILE);
        if (!file.exists()) return passwords;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    passwords.put(parts[0], parts[1]);
                }
            }
        }
        return passwords;
    }

    public boolean verifyCustomerPassword(String customerId, String password) throws IOException {
        Map<String, String> passwords = loadAllPasswords();
        return passwords.containsKey(customerId) && passwords.get(customerId).equals(password);
    }

    // Serialization methods
    private String serializeCustomer(Customer customer) {
        if (customer instanceof IndividualCustomer) {
            IndividualCustomer ic = (IndividualCustomer) customer;
            return String.format("INDIVIDUAL|%s|%s|%s|%s|%s|%s|%s",
                    ic.getCustomerId(), ic.getFirstName(), ic.getSurname(),
                    ic.getAddress(), ic.getEmail(), ic.getPhoneNumber(), ic.getIdNumber());
        } else if (customer instanceof CorporateCustomer) {
            CorporateCustomer cc = (CorporateCustomer) customer;
            return String.format("CORPORATE|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                    cc.getCustomerId(), cc.getFirstName(), cc.getSurname(),
                    cc.getAddress(), cc.getEmail(), cc.getPhoneNumber(),
                    cc.getCompanyName(), cc.getCompanyAddress(), cc.getRegistrationNumber());
        }
        return "";
    }

    private Customer deserializeCustomer(String data) {
        String[] parts = data.split("\\|");
        if (parts.length < 7) return null;

        String type = parts[0];
        String customerId = parts[1];
        String firstName = parts[2];
        String surname = parts[3];
        String address = parts[4];
        String email = parts[5];
        String phone = parts[6];

        if ("INDIVIDUAL".equals(type) && parts.length >= 8) {
            return new IndividualCustomer(customerId, firstName, surname, address, email, phone, parts[7]);
        } else if ("CORPORATE".equals(type) && parts.length >= 10) {
            return new CorporateCustomer(customerId, firstName, surname, address, email, phone,
                    parts[7], parts[8], parts[9]);
        }
        return null;
    }

    private String serializeAccount(Account account) {
        if (account instanceof SavingsAccount) {
            return String.format("SAVINGS|%s|%.2f|%s|%s",
                    account.getAccountNumber(), account.getBalance(),
                    account.getBranch(), account.getCustomer().getCustomerId());
        } else if (account instanceof InvestmentAccount) {
            return String.format("INVESTMENT|%s|%.2f|%s|%s",
                    account.getAccountNumber(), account.getBalance(),
                    account.getBranch(), account.getCustomer().getCustomerId());
        } else if (account instanceof ChequeAccount) {
            ChequeAccount ca = (ChequeAccount) account;
            return String.format("CHEQUE|%s|%.2f|%s|%s|%s|%s",
                    account.getAccountNumber(), account.getBalance(),
                    account.getBranch(), account.getCustomer().getCustomerId(),
                    ca.getEmployer(), ca.getEmployerAddress());
        }
        return "";
    }

    private Account deserializeAccount(String data, Map<String, Customer> customerMap) {
        String[] parts = data.split("\\|");
        if (parts.length < 5) return null;

        String type = parts[0];
        String accountNumber = parts[1];
        double balance = Double.parseDouble(parts[2]);
        String branch = parts[3];
        String customerId = parts[4];

        Customer customer = customerMap.get(customerId);
        if (customer == null) return null;

        switch (type) {
            case "SAVINGS":
                return new SavingsAccount(accountNumber, balance, branch, customer);
            case "INVESTMENT":
                return new InvestmentAccount(accountNumber, balance, branch, customer);
            case "CHEQUE":
                if (parts.length >= 7) {
                    return new ChequeAccount(accountNumber, balance, branch, customer, parts[5], parts[6]);
                }
                break;
        }
        return null;
    }
}