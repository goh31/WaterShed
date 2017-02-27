package model;


import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.core.GeoHash;

/**
 * Created by haidangam on 2/22/17.
 */

public class WaterData {
    String name;
    int drinkingLevel;
    GeoLocation geoLocation;
    GeoHash geoHash;

    public WaterData() {
    }
    public WaterData(String name, GeoLocation geoLocation,  int drinkingLevel, GeoHash geoHash) {
        this.name = name;
        this.geoLocation = geoLocation;
        this.drinkingLevel = drinkingLevel;
        this.geoHash = geoHash;
    }

    public String getName() {
        return this.name;
    }

    public int DrinkingLevel() {
        return this.drinkingLevel;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public GeoHash getGeoHashString() {
        return geoHash;
    }

}
