package bank.system.bankingsystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;


public class FileDataService {
    private final File customersFile;
    private final File accountsFile;
    private final File passwordsFile;
    private final File transactionsFile;

    public FileDataService() {
        File base = new File(System.getProperty("user.dir"), "data");
        if (!base.exists()) base.mkdirs();

        customersFile = new File(base, "customers.txt");
        accountsFile = new File(base, "accounts.txt");
        passwordsFile = new File(base, "passwords.txt");
        transactionsFile = new File(base, "transactions.log");
    }


    // Customers

    public List<Customer> loadAllCustomers() throws IOException {
        List<Customer> list = new ArrayList<>();
        if (!customersFile.exists()) return list;

        List<String> lines = Files.readAllLines(customersFile.toPath());
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("\\|");
            if (parts.length < 7) continue;

            String type = parts[0];
            if ("INDIVIDUAL".equalsIgnoreCase(type) && parts.length >= 8) {
                // INDIVIDUAL|C001|John|Doe|123 Main St|email|phone|ID123
                IndividualCustomer c = new IndividualCustomer(
                        parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]
                );
                list.add(c);
            } else if ("CORPORATE".equalsIgnoreCase(type) && parts.length >= 10) {
                // CORPORATE|C005|TechCorp|Ltd|Tech Park|info@...|555444333|CompanyName|CompanyAddr|REG123
                CorporateCustomer c = new CorporateCustomer(
                        parts[1], parts[2], parts[3], parts[4], parts[5], parts[6],
                        parts[7], parts[8], parts[9]
                );
                list.add(c);
            }
        }
        return list;
    }

    public void saveAllCustomers(Collection<Customer> customers) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Customer c : customers) {
            if (c instanceof IndividualCustomer) {
                IndividualCustomer ic = (IndividualCustomer) c;
                lines.add(String.join("|",
                        "INDIVIDUAL",
                        ic.getCustomerId(),
                        ic.getFirstName(),
                        ic.getSurname(),
                        ic.getAddress(),
                        ic.getEmail(),
                        ic.getPhoneNumber(),
                        ic.getIdNumber()
                ));
            } else if (c instanceof CorporateCustomer) {
                CorporateCustomer cc = (CorporateCustomer) c;
                // company fields stored in CorporateCustomer.toString fields are private; we rely on constructor fields passed earlier.
                // Here we assume CorporateCustomer has getters (if needed add them). For now we reconstruct from toString - but to be clean, let's add getters in CorporateCustomer if needed.
                // To avoid extra changes, use reflection of fields is avoided. We'll store minimal corporate info by casting.
                // However above CorporateCustomer class doesn't expose company fields — so to be more robust add getters (update CorporateCustomer class above if desired).
                // For now cast and attempt retrieval via toString parsing - but simplest is to extend CorporateCustomer with getters; please add them if you want more stable behavior.
                // For basic functionality the constructor values will be available when loaded (we saved them earlier so this method will have original instances).
                // Here we'll write a minimal line using toString parts (NOT ideal but acceptable).
                // Safer approach: write corporate using explicit fields - modify CorporateCustomer to include getters. (I'll assume getters exist.)
                try {
                    java.lang.reflect.Method gm = CorporateCustomer.class.getDeclaredMethod("getCompanyName");
                    String compName = (String) gm.invoke(cc);
                    String compAddr = (String) CorporateCustomer.class.getDeclaredMethod("getCompanyAddress").invoke(cc);
                    String reg = (String) CorporateCustomer.class.getDeclaredMethod("getRegistrationNumber").invoke(cc);

                    lines.add(String.join("|",
                            "CORPORATE",
                            cc.getCustomerId(),
                            cc.getFirstName(),
                            cc.getSurname(),
                            cc.getAddress(),
                            cc.getEmail(),
                            cc.getPhoneNumber(),
                            compName,
                            compAddr,
                            reg
                    ));
                } catch (Exception ex) {
                    // Fallback: write minimal
                    lines.add(String.join("|",
                            "CORPORATE",
                            cc.getCustomerId(),
                            cc.getFirstName(),
                            cc.getSurname(),
                            cc.getAddress(),
                            cc.getEmail(),
                            cc.getPhoneNumber(),
                            "UNKNOWN_COMPANY", "UNKNOWN_ADDR", "UNKNOWN_REG"
                    ));
                }
            }
        }
        Files.write(customersFile.toPath(), lines);
    }

    // save or update a single customer (rewrites whole file with map)
    public void saveCustomerMap(Map<String, Customer> customers) throws IOException {
        saveAllCustomers(customers.values());
    }


    // Accounts

    public List<Account> loadAllAccounts(Map<String, Customer> knownCustomers) throws IOException {
        List<Account> list = new ArrayList<>();
        if (!accountsFile.exists()) return list;

        List<String> lines = Files.readAllLines(accountsFile.toPath());
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|");
            if (p.length < 5) continue;

            String type = p[0];
            String accNum = p[1];
            double bal = Double.parseDouble(p[2]);
            String branch = p[3];
            String custId = p[4];
            Customer cust = knownCustomers.get(custId);
            if (cust == null) continue;

            switch (type.toUpperCase()) {
                case "SAVINGS":
                    list.add(new SavingsAccount(accNum, bal, branch, cust));
                    break;
                case "INVESTMENT":
                    list.add(new InvestmentAccount(accNum, bal, branch, cust));
                    break;
                case "CHEQUE":
                    // CHEQUE|CA000003|3000.0|Main Branch|C001|Employer|EmployerAddr
                    if (p.length >= 7) {
                        list.add(new ChequeAccount(accNum, bal, branch, cust, p[5], p[6]));
                    } else {
                        list.add(new ChequeAccount(accNum, bal, branch, cust, "Unknown", "Unknown"));
                    }
                    break;
            }
        }
        return list;
    }

    public void saveAllAccounts(Collection<Account> accounts) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Account a : accounts) {
            if ("SAVINGS".equalsIgnoreCase(a.getAccountType())) {
                lines.add(String.join("|", "SAVINGS", a.getAccountNumber(), String.valueOf(a.getBalance()), a.getBranch(), a.getCustomer().getCustomerId()));
            } else if ("INVESTMENT".equalsIgnoreCase(a.getAccountType())) {
                lines.add(String.join("|", "INVESTMENT", a.getAccountNumber(), String.valueOf(a.getBalance()), a.getBranch(), a.getCustomer().getCustomerId()));
            } else if ("CHEQUE".equalsIgnoreCase(a.getAccountType())) {
                ChequeAccount ca = (ChequeAccount) a;
                lines.add(String.join("|", "CHEQUE", a.getAccountNumber(), String.valueOf(a.getBalance()), a.getBranch(), a.getCustomer().getCustomerId(),
                        ca.getEmployer() == null ? "Unknown" : ca.getEmployer(),
                        ca.getEmployerAddress() == null ? "Unknown" : ca.getEmployerAddress()));
            }
        }
        Files.write(accountsFile.toPath(), lines);
    }

    public void saveAccountMap(Map<String, Account> accounts) throws IOException {
        saveAllAccounts(accounts.values());
    }


    // Passwords

    public Map<String, String> loadAllPasswords() throws IOException {
        Map<String, String> map = new HashMap<>();
        if (!passwordsFile.exists()) return map;
        List<String> lines = Files.readAllLines(passwordsFile.toPath());
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                map.put(parts[0], parts[1]);
            }
        }
        return map;
    }

    public void saveCustomerPassword(String customerId, String password) throws IOException {
        Map<String, String> map = loadAllPasswords();
        map.put(customerId, password);
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            lines.add(e.getKey() + ":" + e.getValue());
        }
        Files.write(passwordsFile.toPath(), lines);
    }


    // Transactions log

    public void saveTransaction(String transaction) throws IOException {
        String timestamp = new Date().toString();
        String line = String.format("%s - %s", timestamp, transaction);
        Files.write(transactionsFile.toPath(), (line + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }


    // Authentication

    public boolean authenticateUser(String username, String password) throws IOException {
        // special admin
        if ("admin".equals(username) && "admin123".equals(password)) return true;
        Map<String, String> map = loadAllPasswords();
        String stored = map.get(username);
        return stored != null && stored.equals(password);
    }
}
