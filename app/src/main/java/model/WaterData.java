package model;

import java.util.Date;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by haidangam on 2/22/17.
 */

public class WaterData implements Serializable {
  private static final long serialVersionUID = 1L;
  String name;
  ArrayList<Double> drinkingLevel;
  ArrayList<Double> l;
  ArrayList<Date> datelist;
  String g;

  public WaterData() {
  }

  public WaterData(String name, ArrayList<Double> l, ArrayList<Double> drinkingLevel, String g, ArrayList<Date> datelist) {
    this.name = name;
    this.g = g;
    this.drinkingLevel = drinkingLevel;
    this.l = l;
    this.datelist = datelist;
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

  public ArrayList<Date> getdatelist() {return datelist;}

  public void setdatelist(ArrayList<Date> datelist) {
    this.datelist = datelist;
  }
}
