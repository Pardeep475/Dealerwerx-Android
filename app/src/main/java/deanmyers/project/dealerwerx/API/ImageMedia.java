package deanmyers.project.dealerwerx.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mac3 on 2016-11-16.
 */

public class ImageMedia  implements Serializable {
    private String thumbnailUrl;
    private String imageUrl;
    private int id;

    public int getId() { return id; }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    private ImageMedia(int id, String thumbnailUrl, String imageUrl) {
        this.id = id;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
    }

    public static ImageMedia[] fromJsonArray(JSONArray obj) throws JSONException{
        ImageMedia[] arrMedia = new ImageMedia[obj.length()];

        for(int i = 0; i < obj.length(); i++)
            arrMedia[i] = fromJsonObject(obj.getJSONObject(i));

        return arrMedia;
    }

    private static ImageMedia fromJsonObject(JSONObject obj) throws JSONException{
        return new ImageMedia(obj.getInt("id"), obj.getString("thumbnail"), obj.getString("imageUrl"));
    }
}
