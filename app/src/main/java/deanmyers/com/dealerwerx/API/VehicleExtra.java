package deanmyers.com.dealerwerx.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mac3 on 2016-11-16.
 */

public abstract class VehicleExtra implements Serializable
{
    public final static VehicleExtra fromJsonObject(VehicleType type, JSONObject obj) throws JSONException{
        switch (type){
            case Car:
                return CarExtra.fromJsonObject(obj);
            case Motorcycle:
                return MotorcycleExtra.fromJsonObject(obj);
            case Boat:
                return BoatExtra.fromJsonObject(obj);
            case Equipment:
            case Other:
            default:
                return null;
        }
    }

    public abstract JSONObject toJsonObject() throws JSONException;
}
