package bgu.spl.net.impl.BGRSServer;

import java.util.Vector;


/**
 * Class that represents all the information about a student or an admin.
 */

public class User {
    private String password;
    private boolean admin;
    private Boolean loggedIn;
    private Vector<Integer> registeredCourses;

    public User(String password, boolean admin) {
        this.password = password;
        this.admin = admin;
        this.loggedIn = false;
        this.registeredCourses=new Vector<>();
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public Boolean isLoggedIn() {
        return loggedIn;
    }

    public void logIn() {
        this.loggedIn = true;
    }

    public void logOut() {
        this.loggedIn = false;
    }

    public Vector<Integer> getRegisteredCourses() {
        return registeredCourses;
    }

}
