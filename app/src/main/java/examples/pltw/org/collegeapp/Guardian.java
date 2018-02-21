package examples.pltw.org.collegeapp;

import android.util.Log;

/**
 * Created by wdumas on 4/8/16.
 */
public class Guardian extends FamilyMember {
    private String occupation;

    public Guardian() {
        super();
        occupation = "unknown";
    }

    public Guardian(String firstName, String lastName) {
        super(firstName, lastName);
        occupation = "unknown";
    }


    public Guardian(String firstName, String lastName, String occupation) {
        super(firstName, lastName);
        this.occupation = occupation;

    }
    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String toString() {
        return "Guardian:" + getFirstName() + " " + getLastName() + "\n" +
                "Occupation:" + occupation;

    }

}
