package deanmyers.com.dealerwerx.API;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mac3 on 2016-11-17.
 */

public class VinDecodeResult {
    private String title;
    private VehicleType type;

    private int year;
    private String make;
    private String model;
    private String trim;
    private String bodyStyle;
    private String engine;
    private String fuelType;
    private int doors;
    private String transmission;
    private String vin;
    private String driveTrain;

    public String getTitle() {
        return title;
    }

    public VehicleType getType() {
        return type;
    }

    public int getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
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

    public int getDoors() {
        return doors;
    }

    public String getTransmission() {
        return transmission;
    }

    public String getVin() {
        return vin;
    }

    public String getDriveTrain() {
        return driveTrain;
    }

    private VinDecodeResult(String title, VehicleType type, int year, String make, String model, String car_trim, String body_style, String engine, String fuel_type, int doors, String transmission, String vin, String driveTrain) {
        this.title = title;
        this.type = type;
        this.year = year;
        this.make = make;
        this.model = model;
        this.trim = car_trim;
        this.bodyStyle = body_style;
        this.engine = engine;
        this.fuelType = fuel_type;
        this.doors = doors;
        this.transmission = transmission;
        this.vin = vin;
        this.driveTrain = driveTrain;
    }

    public static VinDecodeResult fromJsonObject(JSONObject obj) throws JSONException {
        JSONObject extra = obj.getJSONObject("extra");

        return new VinDecodeResult(
                obj.getString("title"),
                VehicleType.get(obj.getString("type").charAt(0)),
                extra.getInt("year"),
                extra.getString("make"),
                extra.getString("model"),
                extra.getString("car_trim"),
                extra.getString("body_style"),
                extra.getString("engine"),
                extra.getString("fuel_type"),
                extra.getInt("doors"),
                extra.getString("transmission"),
                extra.getString("vin"),
                extra.getString("drive_train")
        );
    }
}
