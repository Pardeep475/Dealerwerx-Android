package deanmyers.com.dealerwerx;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by mac3 on 2017-01-17.
 */

public class SearchActivity extends NavigationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setCurrentActivity(R.id.nav_search);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //doLoad(savedInstanceState);

        setViewTitlePrimary("Search");
        setViewTitle("");

        Fragment fragment = new SearchFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();


        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}

