package deanmyers.com.dealerwerx.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mac3 on 2016-11-16.
 */

public class Listing implements Serializable{
    private int id;
    private String postedBy;
    private boolean lookingFor;
    private boolean isMyPost;
    private double askingPrice;
    private double lowPrice;
    private String datePosted;
    private boolean safeZone;
    private String location;
    private String approvalStatus;
    private Vehicle vehicle;

    public int getId() {
        return id;
    }

    public String getPostedBy(){
        return postedBy;
    }

    public boolean isMyPost() {
        return isMyPost;
    }

    public double getAskingPrice() {
        return askingPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public boolean isSafeZone() {
        return safeZone;
    }

    public boolean isLookingFor() { return lookingFor; }

    public String getLocation() {
        return location;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    private Listing(int id, String postedBy, boolean isMyPost, double askingPrice, String datePosted, boolean lookingFor, boolean safeZone, String location, String approvalStatus, Vehicle vehicle) {
        this.id = id;
        this.postedBy = postedBy;
        this.isMyPost = isMyPost;
        this.askingPrice = askingPrice;
        this.lowPrice = askingPrice;
        this.datePosted = datePosted;
        this.safeZone = safeZone;
        this.lookingFor = lookingFor;
        this.location = location;
        this.approvalStatus = approvalStatus;
        this.vehicle = vehicle;
    }

    public Listing(double askingPrice, double lowPrice, boolean safeZone, boolean lookingFor, String location, Vehicle vehicle) {
        this.askingPrice = askingPrice;
        this.lowPrice = lowPrice;
        this.safeZone = safeZone;
        this.lookingFor = lookingFor;
        this.location = location;
        this.vehicle = vehicle;
    }

    public static Listing fromJsonObject(JSONObject obj) throws JSONException{
        return new Listing(
                obj.getInt("id"),
                obj.getString("postedBy"),
                obj.getBoolean("isMyPost"),
                obj.getDouble("askingPrice"),
                obj.getString("datePosted"),
                obj.has("isLooking") ? obj.getBoolean("isLooking") : false,
                obj.getBoolean("safe_zone"),
                obj.getString("location"),
                obj.getString("approval-status"),
                Vehicle.fromJsonObject(obj.getJSONObject("vehicle"))
        );
    }

    public static Listing[] fromJsonArray(JSONArray obj) throws JSONException{
        Listing[] arrListing = new Listing[obj.length()];

        for(int i = 0; i < obj.length(); i++)
            arrListing[i] = Listing.fromJsonObject(obj.getJSONObject(i));

        return arrListing;
    }

    public JSONObject toJsonObject() throws JSONException{
        JSONObject returnObject = new JSONObject();

        returnObject.put("askingPrice", this.askingPrice);
        returnObject.put("lowPrice", this.lowPrice);
        returnObject.put("isLooking", this.lookingFor);
        returnObject.put("safe_zone", this.safeZone);
        returnObject.put("location", this.location);

        JSONObject vehicleObject = vehicle.toJsonObject();

        returnObject.put("title", vehicleObject.get("title"));
        returnObject.put("description", vehicleObject.get("description"));
        returnObject.put("type", vehicleObject.get("type"));
        returnObject.put("extra", vehicleObject.get("extra"));

        return returnObject;
    }
}
