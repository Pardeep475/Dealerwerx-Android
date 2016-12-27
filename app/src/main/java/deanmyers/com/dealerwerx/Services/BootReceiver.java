package deanmyers.com.dealerwerx.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mac3 on 2016-11-29.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, BeaconService.class);
        context.startService(service);
    }
}
