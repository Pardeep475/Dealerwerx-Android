package deanmyers.com.dealerwerx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.VideoEntry;
import deanmyers.com.dealerwerx.Adapters.VideosAdapter;

public class VideosActivity extends NavigationActivity {
    private SwipeRefreshLayout srl;
    private SwipeRefreshLayout.OnRefreshListener srlListener;
    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        setCurrentActivity(R.id.nav_videos);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        setViewTitlePrimary("Videos");
        setViewTitle("");

        srl = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        srlListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                APIConsumer.GetVideosAsyncTask task = APIConsumer.GetVideos(new APIResponder<VideoEntry[]>() {
                    @Override
                    public void success(VideoEntry[] result) {
                        ListView listView = (ListView)findViewById(R.id.listings_list);

                        try {
                            listView.setAdapter(new VideosAdapter(VideosActivity.this, 0, Arrays.asList(result)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        srl.setRefreshing(false);
                    }

                    @Override
                    public void error(String errorMessage) {
                        srl.setRefreshing(false);
                        Toast.makeText(VideosActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

                task.execute();
            }
        };

        srl.setOnRefreshListener(srlListener);

        listView = (ListView)findViewById(R.id.listings_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoEntry item = (VideoEntry)parent.getItemAtPosition(position);

                String url = item.getVideoUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        View empty = findViewById(R.id.noitems);
        listView.setEmptyView(empty);

        updateListview();
    }

    private void updateListview(){
        srl.post(new Runnable() {
            @Override public void run() {
                srl.setRefreshing(true);
                // directly call onRefresh() method
                srlListener.onRefresh();
            }
        });
    }
}
