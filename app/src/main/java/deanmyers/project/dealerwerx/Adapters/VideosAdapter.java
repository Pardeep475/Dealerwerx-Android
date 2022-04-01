package deanmyers.project.dealerwerx.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import deanmyers.project.dealerwerx.API.VideoEntry;
import deanmyers.project.dealerwerx.R;

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
