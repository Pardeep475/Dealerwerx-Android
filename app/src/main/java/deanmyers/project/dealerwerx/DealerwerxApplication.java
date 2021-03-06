package deanmyers.project.dealerwerx;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.lang.reflect.Field;
import java.util.HashMap;

import deanmyers.project.dealerwerx.Services.BeaconService;
import deanmyers.project.dealerwerx.Services.BootReceiver;

/**
 * Created by mac3 on 2016-11-16.
 */

public class DealerwerxApplication extends Application {
    private static boolean background;
    private static LifecycleHandler handler;

    public static boolean isBackground(){
        for (String s : handler.activities.keySet()) {
            if (handler.activities.get(s) == 1) {
                return false;
            }
        }
        return true;
    }

    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        overrideFont(getApplicationContext(), "SERIF", "fonts/font.ttf");
        appContext = getApplicationContext();
        FacebookSdk.sdkInitialize(appContext);
        AppEventsLogger.activateApp(this);

        BootReceiver receiver = new BootReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, filter);

        registerCallbacks();
        if (!isBackground()) {
            Intent serviceIntent = new Intent(this, BeaconService.class);
            startService(serviceIntent);
        }
    }

    public static Context getContext(){
        return appContext;
    }

    private static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
        }
    }

    private void registerCallbacks(){
        if(handler == null){
            handler = new LifecycleHandler();
            registerActivityLifecycleCallbacks(handler);
        }
    }

    private class LifecycleHandler implements ActivityLifecycleCallbacks{

        private HashMap<String, Integer> activities;

        public LifecycleHandler(){
            activities = new HashMap<>();
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            activities.put(activity.getLocalClassName(), 1);
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activities.put(activity.getLocalClassName(), 0);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
