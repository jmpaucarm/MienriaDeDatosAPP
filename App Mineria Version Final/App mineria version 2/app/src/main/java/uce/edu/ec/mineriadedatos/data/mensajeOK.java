package uce.edu.ec.mineriadedatos.data;

import java.util.ArrayList;

public class mensajeOK {
    private String message;
    User UserObject;


    public mensajeOK(User userObject) {
        UserObject = userObject;
    }

    public User getUserObject() {
        return UserObject;
    }





    public void setUserObject(User userObject) {
        UserObject = userObject;
    }

    // Getter Methods

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return UserObject;
    }

    // Setter Methods

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(User userObject) {
        this.UserObject = userObject;
    }
}



