package deanmyers.com.dealerwerx.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mac3 on 2016-11-23.
 */

public class VideoEntry {
    private String title;
    private String videoUrl;

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    private VideoEntry(String title, String videoUrl) {
        this.title = title;
        this.videoUrl = videoUrl;
    }

    public static VideoEntry[] fromJsonArray(JSONArray obj) throws JSONException {
        VideoEntry[] arrVideo = new VideoEntry[obj.length()];

        for(int i = 0; i < obj.length(); i++)
            arrVideo[i] = fromJsonObject(obj.getJSONObject(i));

        return arrVideo;
    }

    private static VideoEntry fromJsonObject(JSONObject obj) throws JSONException{
        return new VideoEntry(obj.getString("title"), obj.getString("url"));
    }
}
