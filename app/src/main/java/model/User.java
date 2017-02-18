package model;

/**
 * Created by haidangdam on 2/17/17.
 */

public class User {
    String email;
    String credential;

    public User(String email, String credential) {
        this.email = email;
        this.credential = credential;
    }

    public String getCredential() {
        return credential;
    }

    public String getEmail() {
        return email;
    }
}
