package deanmyers.com.dealerwerx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.Listing;
import deanmyers.com.dealerwerx.Adapters.BoatListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.BoatScavengerListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.CarListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.CarScavengerListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.EquipmentListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.EquipmentScavengerListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.ListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.MotorcycleListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.MotorcycleScavengerListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.OtherListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.OtherScavengerListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.ScavengerListingsAdapter;

public class ScavengerListingsActivity extends NavigationActivity {

    private Constructor<?> classConstructor;
    private SwipeRefreshLayout srl;
    private SwipeRefreshLayout.OnRefreshListener srlListener;
    private ListView listView;

    private LinearLayout allListings;
    private LinearLayout carListings;
    private LinearLayout motorcycleListings;
    private LinearLayout boatListings;
    private LinearLayout equipmentListings;
    private LinearLayout otherListings;

    protected void onCreate(Bundle savedInstanceState) {
        setClassConstructor(ScavengerListingsAdapter.class);
        setCurrentActivity(R.id.nav_scavenger);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);


        setViewTitlePrimary("Scavenger");
        setViewTitle("My Scavenger Listings");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScavengerListingsActivity.this, AddListingActivity.class);
                intent.putExtra("scavenger", true);
                startActivity(intent);
            }
        });

        srl = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        srlListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                APIConsumer.GetScavengerListingsAsyncTask task = APIConsumer.GetScavengerListings(PreferencesManager.getUserInformation().getAccessToken(), new APIResponder<Listing[]>() {
                    @Override
                    public void success(Listing[] result) {
                        ListView listView = (ListView)findViewById(R.id.listings_list);

                        try {
                            listView.setAdapter((ScavengerListingsAdapter)classConstructor.newInstance(ScavengerListingsActivity.this, 0, Arrays.asList(result)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        srl.setRefreshing(false);
                    }

                    @Override
                    public void error(String errorMessage) {
                        srl.setRefreshing(false);
                        Toast.makeText(ScavengerListingsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
                Listing listing = (Listing)listView.getItemAtPosition(position);
                Intent intent = new Intent(ScavengerListingsActivity.this, ListingDetailActivity.class);
                intent.putExtra("listing", listing);
                startActivity(intent);
            }
        });

        View empty = findViewById(R.id.noitems);
        listView.setEmptyView(empty);

        allListings = (LinearLayout)findViewById(R.id.action_listings_all);
        carListings = (LinearLayout)findViewById(R.id.action_listings_cars);
        motorcycleListings = (LinearLayout)findViewById(R.id.action_listings_motorcycles);
        boatListings = (LinearLayout)findViewById(R.id.action_listings_boats);
        equipmentListings = (LinearLayout)findViewById(R.id.action_listings_equipment);
        otherListings = (LinearLayout)findViewById(R.id.action_listings_other);

        setTint(allListings);

        allListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Scavenger Listings");
                setClassConstructor(ScavengerListingsAdapter.class);
                updateListview();
                setTint(allListings);
            }
        });
        carListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Car Scavenger Listings");
                setClassConstructor(CarScavengerListingsAdapter.class);
                updateListview();
                setTint(carListings);
            }
        });
        motorcycleListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Motorcycle Scavenger Listings");
                setClassConstructor(MotorcycleScavengerListingsAdapter.class);
                updateListview();
                setTint(motorcycleListings);
            }
        });
        boatListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Boat Scavenger Listings");
                setClassConstructor(BoatScavengerListingsAdapter.class);
                updateListview();
                setTint(boatListings);
            }
        });
        equipmentListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Equipment Scavenger Listings");
                setClassConstructor(EquipmentScavengerListingsAdapter.class);
                updateListview();
                setTint(equipmentListings);
            }
        });
        otherListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Other Scavenger Listings");
                setClassConstructor(OtherScavengerListingsAdapter.class);
                updateListview();
                setTint(otherListings);
            }
        });

//        APIConsumer.GetListingsAsyncTask task = APIConsumer.GetListings(PreferencesManager.getUserInformation().getAccessToken(), new APIResponder<Listing[]>() {
//            @Override
//            public void success(Listing[] result) {
//                listView.setAdapter(new ListingsAdapter(ListingsActivity.this, 0, Arrays.asList(result)));
//            }
//
//            @Override
//            public void error(String errorMessage) {
//                Toast.makeText(ListingsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        task.execute();

        updateListview();
    }

    private boolean setClassConstructor(Class<?> className) {
        try{
            classConstructor = className.getConstructor(Context.class, int.class, List.class);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    private void setTint(LinearLayout target){
        LinearLayout[] layouts = new LinearLayout[]{allListings, carListings, motorcycleListings, boatListings, equipmentListings, otherListings};

        for(int i = 0; i < layouts.length; i++){
            AppCompatImageView imageView = (AppCompatImageView)layouts[i].getChildAt(0);
            TextView textView = (TextView)layouts[i].getChildAt(1);

            int targetColor = (layouts[i] == target) ? R.color.buttonsSelectedAccent : R.color.buttonsAccent;

            int resolvedColor = ContextCompat.getColor(this, targetColor);
            imageView.setColorFilter(resolvedColor);
            textView.setTextColor(resolvedColor);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        updateListview();
    }
}
