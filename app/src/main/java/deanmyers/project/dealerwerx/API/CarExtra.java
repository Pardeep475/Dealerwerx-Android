package deanmyers.project.dealerwerx.API;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mac3 on 2016-11-16.
 */

public class CarExtra extends VehicleExtra {
    private int year;
    private String make;
    private String model;
    private String interiorColor;
    private String exteriorColor;
    private String trim;
    private String bodyStyle;
    private String engine;
    private String fuelType;
    private long kilometers;
    private int doors;
    private int seats;
    private String transmission;
    private String driveTrain;
    private String vin;

    public int getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getInteriorColor() {
        return interiorColor;
    }

    public String getExteriorColor() {
        return exteriorColor;
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

    public int getDoors() {
        return doors;
    }

    public int getSeats() {
        return seats;
    }

    public String getTransmission() {
        return transmission;
    }

    public String getDriveTrain() {
        return driveTrain;
    }

    public String getVin() {
        return vin;
    }

    public CarExtra(int year, String make, String model, String interiorColor, String exteriorColor,
                    String trim, String bodyStyle, String engine, String fuelType, long kilometers,
                    int doors, int seats, String transmission, String driveTrain, String vin) {
        this.year = year;
        this.make = make.trim();
        this.model = model.trim();
        this.interiorColor = interiorColor.trim();
        this.exteriorColor = exteriorColor.trim();
        this.trim = trim.trim();
        this.bodyStyle = bodyStyle.trim();
        this.engine = engine.trim();
        this.fuelType = fuelType.trim();
        this.kilometers = kilometers;
        this.doors = doors;
        this.seats = seats;
        this.transmission = transmission.trim();
        this.driveTrain = driveTrain.trim();
        this.vin = vin.trim();
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject returnObject = new JSONObject();

        returnObject.put("year", year);
        returnObject.put("make", make);
        returnObject.put("model", model);
        returnObject.put("interior_color", interiorColor);
        returnObject.put("exterior_color", exteriorColor);
        returnObject.put("car_trim", trim);
        returnObject.put("body_style", bodyStyle);
        returnObject.put("engine", engine);
        returnObject.put("fuel_type", fuelType);
        returnObject.put("kilometers", kilometers);
        returnObject.put("doors", doors);
        returnObject.put("num_seats", seats);
        returnObject.put("transmission", transmission);
        returnObject.put("drive_train", driveTrain);
        returnObject.put("vin", vin);

        return returnObject;
    }

    public static VehicleExtra fromJsonObject(JSONObject obj) throws JSONException {
        return new CarExtra(
                obj.getInt("year"),
                obj.getString("make"),
                obj.getString("model"),
                obj.getString("interior_color"),
                obj.getString("exterior_color"),
                obj.getString("car_trim"),
                obj.getString("body_style"),
                obj.getString("engine"),
                obj.getString("fuel_type"),
                obj.getLong("kilometers"),
                obj.getInt("doors"),
                obj.getInt("num_seats"),
                obj.getString("transmission"),
                obj.getString("drive_train"),
                obj.getString("vin")
        );
    }
}
