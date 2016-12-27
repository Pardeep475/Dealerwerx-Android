package deanmyers.com.dealerwerx.API;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by mac3 on 2016-11-16.
 */

class ImageCacheManager {
    private static LruCache<String, Bitmap> imageCache;

    static{
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;

        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void storeImage(String url, Bitmap image){
        imageCache.put(url, image);
    }

    public static Bitmap getImage(String url){
        return imageCache.get(url);
    }

    public static void clearCache(){
        imageCache.evictAll();
    }
}
