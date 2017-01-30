package deanmyers.com.dealerwerx.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    private boolean expired;
    private Double lat;
    private Double lon;
    private Long customerNumber;

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

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public boolean isSafeZone() {
        return safeZone;
    }

    public boolean isLookingFor() { return lookingFor; }

    public boolean isExpired() { return expired; }

    public String getLocation() {
        return location;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Long getCustomerNumber() { return customerNumber; }

    private Listing(int id, String postedBy, boolean isMyPost, double askingPrice, String datePosted, boolean lookingFor, boolean safeZone, boolean expired, String location, Double lat, Double lon, String approvalStatus, Long customerNumber, Vehicle vehicle) {
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
        this.expired = expired;
        this.lat = lat;
        this.lon = lon;
        this.customerNumber = customerNumber;
    }

    public Listing(double askingPrice, double lowPrice, boolean safeZone, boolean lookingFor, String location, Long customerNumber, Vehicle vehicle) {
        this.askingPrice = askingPrice;
        this.lowPrice = lowPrice;
        this.safeZone = safeZone;
        this.lookingFor = lookingFor;
        this.location = location;
        this.vehicle = vehicle;
        this.customerNumber = customerNumber;
    }

    public static Listing fromJsonObject(JSONObject obj) throws JSONException{
        String newDatePosted;

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date postDate = sdf.parse(obj.getString("datePosted"));
            sdf = new SimpleDateFormat("EEEE, MMM d, yyyy h:mm a", Locale.CANADA);
            sdf.setTimeZone(TimeZone.getDefault());
            newDatePosted = sdf.format(postDate);
        }catch(ParseException ex){
            newDatePosted = obj.getString("datePosted");
        }


        return new Listing(
                obj.getInt("id"),
                obj.getString("postedBy"),
                obj.getBoolean("isMyPost"),
                obj.getDouble("askingPrice"),
                newDatePosted,
                obj.has("isLooking") ? obj.getBoolean("isLooking") : false,
                obj.getBoolean("safe_zone"),
                obj.getBoolean("expired"),
                obj.getString("location"),
                obj.has("lat") && !obj.isNull("lat") ? obj.getDouble("lat") : null,
                obj.has("lon") && !obj.isNull("lon")  ? obj.getDouble("lon") : null,
                obj.getString("approval-status"),
                obj.isNull("customer_no") ? null : obj.getLong("customer_no"),
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
        returnObject.put("customer_no", customerNumber ==  null ? -1 : customerNumber);
        returnObject.put("stock_no", vehicleObject.get("stock_no"));
        returnObject.put("type", vehicleObject.get("type"));
        returnObject.put("extra", vehicleObject.get("extra"));

        return returnObject;
    }
}
