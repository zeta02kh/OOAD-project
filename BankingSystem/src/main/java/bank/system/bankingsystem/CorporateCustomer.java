package bank.system.bankingsystem;

public class CorporateCustomer extends Customer {
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

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    @Override
    public String getCustomerType() {
        return "Corporate";
    }
}