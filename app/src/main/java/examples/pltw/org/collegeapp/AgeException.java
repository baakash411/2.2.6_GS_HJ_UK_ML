package examples.pltw.org.collegeapp;

/**
 * Created by wdumas on 4/12/16.
 */

public class AgeException extends RuntimeException {
    public AgeException(String message){
        super(message);
    }

    public String joinMessageAndYear(String message, int year) {
        return message + " " + year;
    }
}
