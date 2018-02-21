package examples.pltw.org.collegeapp;

/**
 * Created by wdumas on 4/8/16.
 */
public class Sibling extends FamilyMember {

    public Sibling() {
        super();
    }

    public Sibling(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public String toString() {
        return "Sibling:" + getFirstName() + " " + getLastName();
    }

}
