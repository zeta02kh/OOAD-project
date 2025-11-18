package bank.system.bankingsystem;

public class IndividualCustomer extends Customer {
    private String idNumber;

    public IndividualCustomer(String customerId, String firstName, String surname,
                              String address, String email, String phoneNumber, String idNumber) {
        super(customerId, firstName, surname, address, email, phoneNumber);
        this.idNumber = idNumber;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("\nID Number: %s", idNumber);
    }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    @Override
    public String getCustomerType() {
        return "Individual";
    }
}