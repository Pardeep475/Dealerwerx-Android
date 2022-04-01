package deanmyers.project.dealerwerx.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mac3 on 2016-12-05.
 */

public class Beacon implements Serializable{
    private String name;
    private String uuid;
    private int major;
    private int minor;
    private Listing listing;

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public Listing getListing(){
        return listing;
    }

    private Beacon(String name, String uuid, int major, int minor, Listing listing) {
        this.name = name;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.listing = listing;
    }

    public static Beacon fromJsonObject(JSONObject obj) throws JSONException {
        return new Beacon(
                obj.getString("name"),
                obj.getString("uuid"),
                obj.getInt("major"),
                obj.getInt("minor"),
                obj.isNull("listing") ? null : Listing.fromJsonObject(obj.getJSONObject("listing"))
        );
    }

    public static Beacon[] fromJsonArray(JSONArray obj) throws JSONException{
        Beacon[] arrListing = new Beacon[obj.length()];

        for(int i = 0; i < obj.length(); i++)
            arrListing[i] = Beacon.fromJsonObject(obj.getJSONObject(i));

        return arrListing;
    }
}
