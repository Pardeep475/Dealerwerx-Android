package deanmyers.project.dealerwerx.Adapters;

import android.content.Context;

import java.util.List;

import deanmyers.project.dealerwerx.API.Listing;
import deanmyers.project.dealerwerx.API.VehicleType;

/**
 * Created by mac3 on 2016-11-21.
 */

public class MotorcycleListingsAdapter extends ListingsAdapter{
    public MotorcycleListingsAdapter(Context context, int resource, List<Listing> items) {
        super(context, resource);

        int i = 0;
        for (Listing item : items){
            if(item.getVehicle().getType() == VehicleType.Motorcycle)
                insert(item, i++);
        }
    }
}
