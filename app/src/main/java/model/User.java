package model;

import java.io.Serializable;

/**
 * Created by haidangdam on 2/17/17.
 */

public class User implements Serializable {
  private static final long serialVersionUID = 1L;
  String email;
  String credential;
  String name;

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

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }





}
