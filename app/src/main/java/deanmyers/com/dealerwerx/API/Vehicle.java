package deanmyers.com.dealerwerx.API;

import org.json.JSONArray;
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

    public Vehicle(String title, String description, VehicleType type, String typeName, VehicleExtra extra, ImageMedia[] media) {

        this.title = title;
        this.description = description;
        this.type = type;
        this.typeName = typeName;
        this.extra = extra;
        this.media = media;
    }

    public JSONObject toJsonObject() throws JSONException{
        JSONObject returnObject = new JSONObject();

        returnObject.put("title", title);
        returnObject.put("description", description);
        returnObject.put("type", ""+type.asChar());
        returnObject.put("extra", extra == null ? JSONObject.NULL : extra.toJsonObject());

        return returnObject;
    }

    public static Vehicle fromJsonObject(JSONObject obj) throws JSONException{
        return new Vehicle(
                obj.getString("title"),
                obj.getString("description"),
                VehicleType.get(obj.getString("type").charAt(0)),
                obj.getString("typeName"),
                obj.isNull("extra") ? null : VehicleExtra.fromJsonObject(VehicleType.get(obj.getString("type").charAt(0)), obj.getJSONObject("extra")),
                obj.isNull("media") ? null : ImageMedia.fromJsonArray(obj.getJSONArray("media"))
        );
    }
}
