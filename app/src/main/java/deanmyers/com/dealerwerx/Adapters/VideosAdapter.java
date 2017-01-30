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
import deanmyers.com.dealerwerx.API.VideoEntry;
import deanmyers.com.dealerwerx.DealerwerxApplication;
import deanmyers.com.dealerwerx.R;

/**
 * Created by mac3 on 2016-11-16.
 */

public class VideosAdapter extends ArrayAdapter<VideoEntry> {
    public VideosAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public VideosAdapter(Context context, int resource, List<VideoEntry> items) {
        super(context, resource, items);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view;

        LayoutInflater inflater;
        inflater = LayoutInflater.from(getContext());

        VideoEntry video = getItem(position);

        if(video == null)
            return convertView;

        view = inflater.inflate(R.layout.rows_video, parent, false);

        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText(video.getTitle());
        return view;
    }
}