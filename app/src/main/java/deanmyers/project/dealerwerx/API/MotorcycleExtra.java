package deanmyers.project.dealerwerx.API;

/**
 * Created by mac3 on 2016-11-16.
 */

import org.json.JSONException;
import org.json.JSONObject;


public class MotorcycleExtra extends VehicleExtra {
    private int year;
    private String make;
    private String model;
    private String color;
    private String trim;
    private String bodyStyle;
    private String engine;
    private String fuelType;
    private long kilometers;

    public int getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public String getTrim() {
        return trim;
    }

    public String getBodyStyle() {
        return bodyStyle;
    }

    public String getEngine() {
        return engine;
    }

    public String getFuelType() {
        return fuelType;
    }

    public long getKilometers() {
        return kilometers;
    }

    public MotorcycleExtra(int year, String make, String model, String color, String trim, String bodyStyle, String engine, String fuelType, long kilometers) {
        this.year = year;
        this.make = make.trim();
        this.model = model.trim();
        this.color = color.trim();
        this.trim = trim.trim();
        this.bodyStyle = bodyStyle.trim();
        this.engine = engine.trim();
        this.fuelType = fuelType.trim();
        this.kilometers = kilometers;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject returnObject = new JSONObject();

        returnObject.put("year", year);
        returnObject.put("make", make);
        returnObject.put("model", model);
        returnObject.put("color", color);
        returnObject.put("car_trim", trim);
        returnObject.put("body_style", bodyStyle);
        returnObject.put("engine", engine);
        returnObject.put("fuel_type", fuelType);
        returnObject.put("kilometers", kilometers);

        return returnObject;
    }

    public static MotorcycleExtra fromJsonObject(JSONObject obj) throws JSONException {
        return new MotorcycleExtra(
                obj.getInt("year"),
                obj.getString("make"),
                obj.getString("model"),
                obj.getString("color"),
                obj.getString("car_trim"),
                obj.getString("body_style"),
                obj.getString("engine"),
                obj.getString("fuel_type"),
                obj.getLong("kilometers")
        );
    }
}
