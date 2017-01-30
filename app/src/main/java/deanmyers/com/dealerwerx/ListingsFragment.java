package deanmyers.com.dealerwerx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import deanmyers.com.dealerwerx.Adapters.CarListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.EquipmentListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.ListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.MotorcycleListingsAdapter;
import deanmyers.com.dealerwerx.Adapters.OtherListingsAdapter;

/**
 * Created by mac3 on 2017-01-17.
 */

public class ListingsFragment extends TitleCompatFragment {

    private Constructor<?> classConstructor;
    private ListView listView;

    private LinearLayout allListings;
    private LinearLayout carListings;
    private LinearLayout motorcycleListings;
    private LinearLayout boatListings;
    private LinearLayout equipmentListings;
    private LinearLayout otherListings;

    private Listing[] searchResults;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_listingfragment, container, false);

        searchResults = (Listing[])getArguments().getSerializable("results");

        setViewTitle(container, "All Listings");

        setClassConstructor(ListingsAdapter.class);
        setViewTitlePrimary("Search Results");
        setViewTitle("All Listings");

        listView = (ListView)v.findViewById(R.id.listings_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listing listing = (Listing)listView.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), ListingDetailActivity.class);
                intent.putExtra("listing", listing);
                startActivity(intent);
            }
        });

        View empty = v.findViewById(R.id.noitems);
        listView.setEmptyView(empty);

        allListings = (LinearLayout)v.findViewById(R.id.action_listings_all);
        carListings = (LinearLayout)v.findViewById(R.id.action_listings_cars);
        motorcycleListings = (LinearLayout)v.findViewById(R.id.action_listings_motorcycles);
        boatListings = (LinearLayout)v.findViewById(R.id.action_listings_boats);
        equipmentListings = (LinearLayout)v.findViewById(R.id.action_listings_equipment);
        otherListings = (LinearLayout)v.findViewById(R.id.action_listings_other);

        setTint(allListings);

        allListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("All Listings");
                setClassConstructor(ListingsAdapter.class);
                updateListview();
                setTint(allListings);
            }
        });
        carListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Car Listings");
                setClassConstructor(CarListingsAdapter.class);
                updateListview();
                setTint(carListings);
            }
        });
        motorcycleListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Motorcycle Listings");
                setClassConstructor(MotorcycleListingsAdapter.class);
                updateListview();
                setTint(motorcycleListings);
            }
        });
        boatListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Boat Listings");
                setClassConstructor(BoatListingsAdapter.class);
                updateListview();
                setTint(boatListings);
            }
        });
        equipmentListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Equipment Listings");
                setClassConstructor(EquipmentListingsAdapter.class);
                updateListview();
                setTint(equipmentListings);
            }
        });
        otherListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Other Listings");
                setClassConstructor(OtherListingsAdapter.class);
                updateListview();
                setTint(otherListings);
            }
        });

        updateListview();

        return v;
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

            int resolvedColor = ContextCompat.getColor(getContext(), targetColor);
            imageView.setColorFilter(resolvedColor);
            textView.setTextColor(resolvedColor);
        }
    }

    private void updateListview(){
        try{
            listView.setAdapter((ListingsAdapter)classConstructor.newInstance(getContext(), 0, Arrays.asList(searchResults)));
        }catch(Exception e){

        }
    }
}
