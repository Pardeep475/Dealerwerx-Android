package deanmyers.com.dealerwerx.Adapters;

import android.content.Context;

import java.util.List;

import deanmyers.com.dealerwerx.API.Listing;
import deanmyers.com.dealerwerx.API.VehicleType;

/**
 * Created by mac3 on 2016-11-21.
 */

public class BoatListingsAdapter extends ListingsAdapter{
    public BoatListingsAdapter(Context context, int resource, List<Listing> items) {
        super(context, resource);

        int i = 0;
        for (Listing item : items){
            if(item.getVehicle().getType() == VehicleType.Boat)
                insert(item, i++);
        }
    }
}