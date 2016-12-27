package deanmyers.com.dealerwerx.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.BoatExtra;
import deanmyers.com.dealerwerx.API.CarExtra;
import deanmyers.com.dealerwerx.API.ImageMedia;
import deanmyers.com.dealerwerx.API.Listing;
import deanmyers.com.dealerwerx.API.MotorcycleExtra;
import deanmyers.com.dealerwerx.API.Vehicle;
import deanmyers.com.dealerwerx.DealerwerxApplication;
import deanmyers.com.dealerwerx.R;

/**
 * Created by mac3 on 2016-11-16.
 */

public class SafeZoneListingsAdapter extends ArrayAdapter<Listing> {
    public SafeZoneListingsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public SafeZoneListingsAdapter(Context context, int resource, List<Listing> items) {
        super(context, resource);

        for(Listing item: items)
            if(item.isSafeZone())
                add(item);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view;

        LayoutInflater inflater;
        inflater = LayoutInflater.from(getContext());

        Listing listing = getItem(position);

        if(listing == null)
            return convertView;

        Vehicle vehicle = listing.getVehicle();

        switch(vehicle.getType())
        {
            case Car:
                view = inflater.inflate(R.layout.rows_listings_car, parent, false);
                break;
            case Motorcycle:
                view = inflater.inflate(R.layout.rows_listings_motorcycle, parent, false);
                break;
            case Boat:
                view = inflater.inflate(R.layout.rows_listings_boat, parent, false);
                break;
            case Equipment:
                view = inflater.inflate(R.layout.rows_listings_equipment, parent, false);
                break;
            case Other:
                view = inflater.inflate(R.layout.rows_listings_other, parent, false);
                break;
            default:
                view = null;
        }

        if(view == null)
            return convertView;

        TextView year = (TextView)view.findViewById(R.id.text_year);
        TextView make = (TextView)view.findViewById(R.id.text_make);
        TextView model = (TextView)view.findViewById(R.id.text_model);
        TextView engine = (TextView)view.findViewById(R.id.text_engine);
        TextView description = (TextView)view.findViewById(R.id.text_description);

        switch(vehicle.getType()){
            case Car:
                CarExtra cExtra = (CarExtra)vehicle.getExtra();

                year.setText(String.format(Locale.CANADA, "%d", cExtra.getYear()));
                make.setText(cExtra.getMake());
                model.setText(cExtra.getModel());
                engine.setText(cExtra.getEngine());
                break;
            case Motorcycle:
                MotorcycleExtra mExtra = (MotorcycleExtra) vehicle.getExtra();

                year.setText(String.format(Locale.CANADA, "%d", mExtra.getYear()));
                make.setText(mExtra.getMake());
                model.setText(mExtra.getModel());
                engine.setText(mExtra.getEngine());
                break;
            case Boat:
                BoatExtra bExtra = (BoatExtra)vehicle.getExtra();

                year.setText(String.format(Locale.CANADA, "%d", bExtra.getYear()));
                make.setText(bExtra.getMake());
                model.setText(bExtra.getModel());
                engine.setText(bExtra.getEngine());
                break;
            case Equipment:
            case Other:
                description.setText(vehicle.getDescription());
            default:
        }

        TextView title = (TextView)view.findViewById(R.id.text_title);
        TextView askingPrice = (TextView)view.findViewById(R.id.text_asking_price);
        TextView location = (TextView)view.findViewById(R.id.text_location);
        ImageView safeZone = (ImageView)view.findViewById(R.id.image_safezone_approval);

        final ImageView vehicleImage = (ImageView)view.findViewById(R.id.image_vehicle);


        title.setText(vehicle.getTitle());
        askingPrice.setText(String.format(Locale.CANADA, "$%.2f", listing.getAskingPrice()));
        location.setText(listing.getLocation());

        if(listing.isSafeZone()) {
            Context context = DealerwerxApplication.getContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                safeZone.setImageDrawable(context.getResources().getDrawable(R.drawable.dealerwerx_safezone, context.getTheme()));
            } else {
                safeZone.setImageDrawable(context.getResources().getDrawable(R.drawable.dealerwerx_safezone));
            }
        }

        ImageMedia[] media = vehicle.getMedia();

        if(media != null && media.length > 0){
            APIConsumer.DownloadImageAsyncTask task = APIConsumer.DownloadImage(media[0].getThumbnailUrl(), new APIResponder<Bitmap>() {
                @Override
                public void success(Bitmap result) {
                    vehicleImage.setImageBitmap(result);
                }

                @Override
                public void error(String errorMessage) {
                    //do nothing
                }
            });

            task.execute();
        }

        return view;
    }
}
