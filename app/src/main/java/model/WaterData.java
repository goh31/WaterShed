package model;

import java.util.Date;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by haidangam on 2/22/17.
 */

public class WaterData implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private ArrayList<Double> l;
  private ArrayList<Date> datelist;
  private String g;
  private String waterType;
  private String waterCondition;
  private ArrayList<String> reporterId;
  private ArrayList<Double> virusPPM;
  private ArrayList<Double> contaminantPPM;


  public WaterData() {
  }

  public WaterData(String name, ArrayList<Double> l,  String g, ArrayList<Date> datelist) {
    this.name = name;
    this.g = g;
    this.l = l;
    this.datelist = datelist;
  }

  public String getlocationName() {
    return this.name;
  }

  public void setlocationName(String name) {
    this.name = name;
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

  public String getwaterType() { return waterType; }

  public String getwaterCondition() {return waterCondition; }

  public ArrayList<String> getreporterId() {return reporterId; }

  public void setwaterType(String s) {waterType = s;}

  public void setwaterCondition(String s) {waterCondition = s;}

  public void setreporterId(ArrayList<String> s) {reporterId = s;}

  public void setvirusPPM(ArrayList<Double> a) {virusPPM = a;}

  public void setcontaminantPPM(ArrayList<Double> a) {contaminantPPM = a;}

  public ArrayList<Double> getvirusPPM() {return virusPPM;}

  public ArrayList<Double> getcontaminantPPM() {return contaminantPPM;}

}
