package deanmyers.com.dealerwerx.Adapters;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import deanmyers.com.dealerwerx.API.Beacon;
import deanmyers.com.dealerwerx.R;
import deanmyers.com.dealerwerx.Services.BeaconService;

/**
 * Created by mac3 on 2016-11-16.
 */

public class BeaconsAdapter extends ArrayAdapter<Beacon> {
    public BeaconsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BeaconsAdapter(Context context, int resource, List<Beacon> items) {
        super(context, resource, items);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view;

        LayoutInflater inflater;
        inflater = LayoutInflater.from(getContext());

        Beacon beacon = getItem(position);
        if(beacon == null)
            return convertView;

        String key = BeaconService.beaconToKey(beacon.getUuid(), beacon.getMajor(), beacon.getMinor());

        if(convertView != null)
            view = convertView;
        else
            view = inflater.inflate(R.layout.rows_beacon, parent, false);

        TextView name = (TextView)view.findViewById(R.id.text_title);
        TextView associated = (TextView)view.findViewById(R.id.text_associated);
        TextView signalLabel = (TextView)view.findViewById(R.id.signal_label);
        TextView signalStrength = (TextView)view.findViewById(R.id.signal_strength);

        name.setText(beacon.getName());
        associated.setText(beacon.getListing() != null ? beacon.getListing().getVehicle().getTitle() : "(Not Associated)");

        BeaconService instance = BeaconService.getInstance();

        if(instance != null && instance.hasDetectedBeacon(key)){
            BeaconService.Distance distance = instance.getDistance(key);
            signalStrength.setVisibility(View.VISIBLE);
            signalLabel.setText(R.string.signal_strength);
            switch (distance){
                case IMMEDIATE:
                    signalStrength.setText(R.string.excellent);
                    signalStrength.setTextColor(Color.GREEN);
                    break;
                case NEAR:
                    signalStrength.setText(R.string.good);
                    signalStrength.setTextColor(Color.YELLOW);
                    break;
                case FAR:
                    signalStrength.setText(R.string.weak);
                    signalStrength.setTextColor(Color.RED);
                    break;
                default:
                    signalStrength.setTextColor(Color.TRANSPARENT);
                    break;
            }
        }else{
            signalStrength.setVisibility(View.GONE);
            signalLabel.setText(R.string.not_in_range);
        }

        return view;
    }
}
