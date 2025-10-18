package bank.system.banksystem2;

public class StudentCustomer extends Customer {
    private String schoolName;
    private String studentId;
    private String major;

    public StudentCustomer(String firstName, String lastName, String address,
                           String schoolName, String studentId, String major) {
        super(firstName, lastName, address);
        this.schoolName = schoolName;
        this.studentId = studentId;
        this.major = major;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getMajor() {
        return major;
    }

    // Student-specific method
    public void applyStudentBenefits() {
        System.out.println("Applying student benefits for " + getFirstName() +
                " (" + studentId + "): Fee waivers and lower interest rates.");
    }

    @Override
    public String toString() {
        return super.toString() +
                " | School: " + schoolName +
                " | Major: " + major;
    }
}
