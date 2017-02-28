package model;

/**
 * Created by haidangdam on 2/17/17.
 */

public class User {
    String email;
    String credential;
    String name;
    String homeAddress;
    String phoneNumber;

    public User() {

    }
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHomeAddress() {
        return homeAddress;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

}
