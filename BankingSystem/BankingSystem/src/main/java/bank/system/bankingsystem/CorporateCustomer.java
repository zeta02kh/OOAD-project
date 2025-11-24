package bank.system.bankingsystem;

public class CorporateCustomer extends Customer {
    private static final long serialVersionUID = 1L;

    private String companyName;
    private String companyAddress;
    private String registrationNumber;

    public CorporateCustomer(String customerId, String firstName, String surname,
                             String address, String email, String phoneNumber,
                             String companyName, String companyAddress, String registrationNumber) {
        super(customerId, firstName, surname, address, email, phoneNumber);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.registrationNumber = registrationNumber;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("\nCompany: %s\nCompany Address: %s\nRegistration: %s",
                companyName, companyAddress, registrationNumber);
    }

    @Override
    public String getCustomerType() {
        return "Corporate";
    }

    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public String getRegistrationNumber() { return registrationNumber; }
}
