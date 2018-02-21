package examples.pltw.org.collegeapp;

/**
 * Created by wdumas on 4/8/16.
 */
public abstract class FamilyMember extends ApplicantData {
    public final static String EXTRA_RELATION = "org.pltw.examples.collegeapp.relation";
    public final static String EXTRA_INDEX = "org.pltw.examples.collegeapp.index";

    private String firstName;
    private String lastName;
    private String applicantEmail;

    public FamilyMember() {
        firstName = "Ada";
        lastName = "Lovelace";
    }

    public FamilyMember(String firstName, String lastName) {
       this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public boolean equals(Object o) {
        if ((o instanceof Guardian) && (this instanceof Guardian)) {
            Guardian g = (Guardian)o;
            if (g.getFirstName().equals(this.getFirstName()) && (g.getLastName().equals(this.getLastName()))){
                return true;
            }
        }
        else if ((o instanceof Sibling) && (this instanceof Sibling)) {
            Sibling s = (Sibling)o;
            if (s.getFirstName().equals(this.getFirstName())&& (s.getLastName().equals(this.getLastName()))) {
                return true;
            }
        }
        return false;
    }

}
