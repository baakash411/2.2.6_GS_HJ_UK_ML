package examples.pltw.org.collegeapp;

import java.util.ArrayList;

/**
 * Created by wdumas on 4/8/16.
 */
public class Family {
    private static final String TAG = Family.class.getName();

    private static Family sFamily;

    private ArrayList<FamilyMember> family = new ArrayList<>();

    private Family() {
        family = new ArrayList<>();

        /* create a family of your own choosing... */
        family.add(new Sibling("Pippin","Took"));
        family.add(new Sibling("Merry","Brandywine"));

        family.add(new Guardian("Frodo", "Baggins", "Ring Bearer"));
        family.add(new Guardian("Samwise", "Gamgee", "Gardener"));

    }

    public static Family get() {
        if (sFamily == null) {
            sFamily = new Family();
        }
        return sFamily;

    }

    public void addFamilyMember(FamilyMember f) {
        family.add(f);
    }

    public void deleteFamilyMember(FamilyMember f) {
        family.remove(f);
    }

    public ArrayList<FamilyMember> getFamily() {
        return family;
    }

    public void setFamily(ArrayList<FamilyMember> family) {
        this.family = family;
    }
}
