package bank.system.bankingsystem;

import java.io.*;
import java.util.*;

public class FileDataService {
    private final File customersFile;
    private final File accountsFile;
    private final File passwordsFile;
    private final File transactionsFile;

    public FileDataService() {
        File base = new File(System.getProperty("user.dir"), "data");
        if (!base.exists()) base.mkdirs();
        customersFile = new File(base, "customers.dat");
        accountsFile = new File(base, "accounts.dat");
        passwordsFile = new File(base, "passwords.dat");
        transactionsFile = new File(base, "transactions.log");
    }

    // Customers
    public List<Customer> loadAllCustomers() throws IOException {
        if (!customersFile.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(customersFile))) {
            Object obj = in.readObject();
            if (obj instanceof List) {
                return (List<Customer>) obj;
            }
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    public void saveCustomer(Customer customer) throws IOException {
        List<Customer> customers = loadAllCustomers();
        customers.removeIf(c -> c.getCustomerId().equals(customer.getCustomerId()));
        customers.add(customer);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(customersFile))) {
            out.writeObject(new ArrayList<>(customers));
        }
    }

    // Accounts
    public List<Account> loadAllAccounts() throws IOException {
        if (!accountsFile.exists()) return new ArrayList<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(accountsFile))) {
            Object obj = in.readObject();
            if (obj instanceof List) {
                return (List<Account>) obj;
            }
            return new ArrayList<>();
        } catch (ClassNotFoundException | InvalidClassException | StreamCorruptedException e) {
            System.err.println("Warning: Account data file corrupted, starting fresh");
            return new ArrayList<>();
        }
    }

    public void saveAccount(Account account) throws IOException {
        List<Account> accounts = loadAllAccounts();
        accounts.removeIf(a -> a.getAccountNumber().equals(account.getAccountNumber()));
        accounts.add(account);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(accountsFile))) {
            out.writeObject(new ArrayList<>(accounts));
        }
    }

    // Passwords
    public Map<String, String> loadAllPasswords() throws IOException {
        if (!passwordsFile.exists()) return new HashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(passwordsFile))) {
            Object obj = in.readObject();
            if (obj instanceof Map) {
                return (Map<String, String>) obj;
            }
            return new HashMap<>();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    public void saveCustomerPassword(String customerId, String password) throws IOException {
        Map<String, String> map = loadAllPasswords();
        map.put(customerId, password);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(passwordsFile))) {
            out.writeObject(map);
        }
    }

    // Transactions (append to log)
    public void saveTransaction(String transaction) throws IOException {
        try (FileWriter fw = new FileWriter(transactionsFile, true)) {
            fw.write(transaction + System.lineSeparator());
        }
    }

    // Authentication helper
    public boolean authenticateUser(String username, String password) throws IOException {
        Map<String, String> map = loadAllPasswords();
        // special admin
        if ("admin".equals(username) && "admin123".equals(password)) return true;
        String stored = map.get(username);
        return stored != null && stored.equals(password);
    }
}
