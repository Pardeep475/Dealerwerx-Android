package deanmyers.com.dealerwerx.Adapters;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

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

        if(convertView == null){
            view = inflater.inflate(R.layout.rows_listings, parent, false);
        }else{
            view = convertView;
        }

        if(view == null)
            return convertView;

        final ImageView vehicleImage = (ImageView)view.findViewById(R.id.image_vehicle);

        String actualTitle = vehicle.getTitle().toUpperCase();

        switch(vehicle.getType()){
            case Car:
                vehicleImage.setImageResource(R.drawable.car_icon);
                CarExtra cExtra = (CarExtra)vehicle.getExtra();
                actualTitle = String.format(Locale.CANADA, "%d %s %s", cExtra.getYear(), cExtra.getMake(), cExtra.getModel()).toUpperCase();
                break;
            case Motorcycle:
                vehicleImage.setImageResource(R.drawable.motorcycle_icon);
                MotorcycleExtra mExtra = (MotorcycleExtra)vehicle.getExtra();
                actualTitle = String.format(Locale.CANADA, "%d %s %s", mExtra.getYear(), mExtra.getMake(), mExtra.getModel()).toUpperCase();
                break;
            case Boat:
                vehicleImage.setImageResource(R.drawable.boat_icon);
                BoatExtra bExtra = (BoatExtra)vehicle.getExtra();
                actualTitle = String.format(Locale.CANADA, "%d %s %s", bExtra.getYear(), bExtra.getMake(), bExtra.getModel()).toUpperCase();
                break;
            case Equipment:
                vehicleImage.setImageResource(R.drawable.equipment_icon);
                break;
            case Other:
                vehicleImage.setImageResource(R.drawable.misc_icon);
                break;
            default:
                vehicleImage.setImageResource(R.drawable.misc_icon);
        }

        TextView title = (TextView)view.findViewById(R.id.text_title);
        TextView askingPrice = (TextView)view.findViewById(R.id.text_asking_price);
        TextView location = (TextView)view.findViewById(R.id.text_location);
        ImageView safeZone = (ImageView)view.findViewById(R.id.image_safezone_approval);
        TextView description = (TextView)view.findViewById(R.id.text_description);

        description.setText(vehicle.getDescription());
        title.setText(actualTitle);
        askingPrice.setText(listing.isExpired() ? "EXPIRED" : (listing.getAskingPrice() < 0 ? "CONTACT" : String.format(Locale.CANADA, "$%.2f", listing.getAskingPrice())));
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
            Picasso.with(getContext()).load(media[0].getThumbnailUrl()).into(vehicleImage);
//            APIConsumer.DownloadImageAsyncTask task = APIConsumer.DownloadImage(media[0].getThumbnailUrl(), new APIResponder<Bitmap>() {
//                @Override
//                public void success(Bitmap result) {
//                    vehicleImage.setImageBitmap(result);
//                }
//
//                @Override
//                public void error(String errorMessage) {
//                    //do nothing
//                }
//            });

            //task.execute();
        }

        return view;
    }
}
