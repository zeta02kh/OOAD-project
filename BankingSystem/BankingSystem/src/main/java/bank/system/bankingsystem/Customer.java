package bank.system.bankingsystem;

public abstract class Customer {
    protected String customerId;
    protected String firstName;
    protected String surname;
    protected String address;
    protected String email;
    protected String phoneNumber;

    public Customer(String customerId, String firstName, String surname,
                    String address, String email, String phoneNumber) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setSurname(String surname) { this.surname = surname; }

    @Override
    public String toString() {
        return String.format("Customer ID: %s\nName: %s %s\nAddress: %s\nEmail: %s\nPhone: %s",
                customerId, firstName, surname, address, email, phoneNumber);
    }

    public abstract String getCustomerType();
}
