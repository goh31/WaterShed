package model;


import java.util.ArrayList;

/**
 * Created by haidangam on 2/22/17.
 */

public class WaterData {

  String name;
  ArrayList<Double> drinkingLevel;
  ArrayList<Double> l;
  String g;

  public WaterData() {
  }

  public WaterData(String name, ArrayList<Double> l, ArrayList<Double> drinkingLevel, String g) {
    this.name = name;
    this.g = g;
    this.drinkingLevel = drinkingLevel;
    this.l = l;
  }

  public String getlocationName() {
    return this.name;
  }

  public void setlocationName(String name) {
    this.name = name;
  }

  public ArrayList<Double> getcriticalLevel() {
    return this.drinkingLevel;
  }

  public void setcriticalLevel(ArrayList<Double> a) {
    drinkingLevel = a;
  }

  public ArrayList<Double> getL() {
    return l;
  }

  public void setL(ArrayList<Double> l) {
    this.l = l;
  }

  public String getg() {
    return g;
  }


  public void setg(String g) {
    this.g = g;
  }
}
