package deanmyers.com.dealerwerx;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mac3 on 2016-11-21.
 */

public abstract class TitleCompatActivity extends AppCompatActivity {
    void setViewTitlePrimary(String title){
        ((TextView)findViewById(R.id.page_title_primary)).setText(title);
    }
    void setViewTitle(String title){
        ((TextView)findViewById(R.id.page_title)).setText(title);
    }


    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.login_logo).setVisibility(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        findViewById(R.id.login_logo).setVisibility(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? View.GONE : View.VISIBLE);
    }
}
