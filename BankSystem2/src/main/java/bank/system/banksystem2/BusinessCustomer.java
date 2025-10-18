package bank.system.banksystem2;

public class BusinessCustomer extends Customer {
    private String companyName;
    private String taxId;
    private String businessType;

    public BusinessCustomer(String firstName, String lastName, String address,
                            String companyName, String taxId, String businessType) {
        super(firstName, lastName, address);
        this.companyName = companyName;
        this.taxId = taxId;
        this.businessType = businessType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getBusinessType() {
        return businessType;
    }

    @Override
    public String toString() {
        return super.toString() +
                " | Company: " + companyName +
                " | Tax ID: " + taxId +
                " | Business Type: " + businessType;
    }
}
