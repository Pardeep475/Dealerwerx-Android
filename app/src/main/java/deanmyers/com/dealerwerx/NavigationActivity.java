package deanmyers.com.dealerwerx;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import deanmyers.com.dealerwerx.Services.BeaconService;

public class NavigationActivity extends TitleCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int currentActivity;

    void setCurrentActivity(int currentActivity){
        this.currentActivity = currentActivity;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listings, menu);


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.scanning).setTitle(PreferencesManager.getAllowBackgroundScanning() ? "Disable Scanning" : "Enable Scanning");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            PreferencesManager.setUserInformation(null);
            Intent loginIntent = new Intent(NavigationActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        } else if (id == R.id.scanning){
            final boolean canScan = PreferencesManager.getAllowBackgroundScanning();
            final BeaconService service = BeaconService.getInstance();

            if(canScan){
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setPositiveButton("Disable", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PreferencesManager.setAllowBackgroundScanning(false);
                                try {
                                    if(service != null){
                                        service.setEnabled(false);
                                    }
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setMessage("Are you sure you wish to disable the scanning of beacons? Scanning is necessary for the Near Me feature to operate correctly. Disabling this can lead to unpredictable behavior.")
                        .create();

                dialog.show();
            }else{
                PreferencesManager.setAllowBackgroundScanning(true);
                if (service != null){
                    try {
                        service.setEnabled(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else{
                    Intent serviceIntent = new Intent(this, BeaconService.class);
                    startService(serviceIntent);
                }
            }
            invalidateOptionsMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id != currentActivity){
            if(id == R.id.nav_browse){
                Intent intent = new Intent(this, ListingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_maps){
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
            }
            else if(id == R.id.nav_nearme){
                Intent intent = new Intent(this, NearMeListingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_scavenger){
                Intent intent = new Intent(this, ScavengerListingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_search){
                Intent intent = new Intent(this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_garage){
                Intent intent = new Intent(this, GarageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_lookingfor){
                Intent intent = new Intent(this, LookingForActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_mybeacons){
                Intent intent = new Intent(this, MyBeaconsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_liked){
                Intent intent = new Intent(this, LikedListingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_safezone){
                Intent intent = new Intent(this, SafeZoneActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_videos){
                Intent intent = new Intent(this, VideosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_checkin){
                Intent intent = new Intent(this, CheckinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else if(id == R.id.nav_settings){
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
