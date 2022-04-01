package deanmyers.project.dealerwerx.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mac3 on 2016-11-16.
 */

public class Vehicle implements Serializable {
    private String title;
    private String description;
    private VehicleType type;
    private String typeName;
    private VehicleExtra extra;
    private ImageMedia[] media;
    private String stockNumber;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public VehicleType getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public VehicleExtra getExtra() {
        return extra;
    }

    public ImageMedia[] getMedia() {
        return media;
    }

    public String getStockNumber() { return stockNumber; }


    public Vehicle(String title, String description, String stockNumber, VehicleType type, String typeName, VehicleExtra extra, ImageMedia[] media) {

        this.title = title.trim();
        this.description = description.trim();
        this.type = type;
        this.typeName = typeName;
        this.extra = extra;
        this.media = media;
        this.stockNumber = stockNumber;
    }

    public JSONObject toJsonObject() throws JSONException{
        JSONObject returnObject = new JSONObject();

        returnObject.put("title", title);
        returnObject.put("description", description);
        returnObject.put("type", ""+type.asChar());
        returnObject.put("extra", extra == null ? JSONObject.NULL : extra.toJsonObject());
        returnObject.put("stock_no", stockNumber == null ? "" : stockNumber);

        return returnObject;
    }

    public static Vehicle fromJsonObject(JSONObject obj) throws JSONException{
        return new Vehicle(
                obj.getString("title"),
                obj.getString("description"),
                obj.isNull("stock_no") ? null : obj.getString("stock_no"),
                VehicleType.get(obj.getString("type").charAt(0)),
                obj.getString("typeName"),
                obj.isNull("extra") ? null : VehicleExtra.fromJsonObject(VehicleType.get(obj.getString("type").charAt(0)), obj.getJSONObject("extra")),
                obj.isNull("media") ? null : ImageMedia.fromJsonArray(obj.getJSONArray("media"))
        );
    }
}
