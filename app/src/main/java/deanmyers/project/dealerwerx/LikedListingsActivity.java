package deanmyers.project.dealerwerx;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import deanmyers.project.dealerwerx.API.APIConsumer;
import deanmyers.project.dealerwerx.API.APIResponder;
import deanmyers.project.dealerwerx.API.Listing;
import deanmyers.project.dealerwerx.Adapters.BoatListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.CarListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.EquipmentListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.ListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.MotorcycleListingsAdapter;
import deanmyers.project.dealerwerx.Adapters.OtherListingsAdapter;

public class LikedListingsActivity extends NavigationActivity {

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
        setClassConstructor(ListingsAdapter.class);
        setCurrentActivity(R.id.nav_liked);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        setViewTitlePrimary("Liked");
        setViewTitle("Liked Listings");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        srl = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        srlListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (PreferencesManager.getUserInformation() == null) {
                    PreferencesManager.setUserInformation(null);
                    Intent loginIntent = new Intent(LikedListingsActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    finish();
                    return;
                }
                if (PreferencesManager.getUserInformation().getAccessToken() == null) {
                    PreferencesManager.setUserInformation(null);
                    Intent loginIntent = new Intent(LikedListingsActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginIntent);
                    finish();
                    return;
                }
                APIConsumer.GetListingsAsyncTask task = APIConsumer.GetListings(PreferencesManager.getUserInformation().getAccessToken(), new APIResponder<Listing[]>() {
                    @Override
                    public void success(Listing[] result) {
                        ListView listView = (ListView)findViewById(R.id.listings_list);
                        ArrayList<Listing> newList = new ArrayList<>();

                        for(Listing i : result)
                            if(PreferencesManager.hasLiked(i.getId()))
                                newList.add(i);

                        try {
                            listView.setAdapter((ListingsAdapter)classConstructor.newInstance(LikedListingsActivity.this, 0, newList));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        srl.setRefreshing(false);
                    }

                    @Override
                    public void error(String errorMessage) {
                        srl.setRefreshing(false);
                        Toast.makeText(LikedListingsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(LikedListingsActivity.this, ListingDetailActivity.class);
                intent.putExtra("listing", listing);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Listing listing = (Listing)parent.getItemAtPosition(position);
                AlertDialog alert = new AlertDialog.Builder(LikedListingsActivity.this)
                        .setTitle("Actions")
                        .setMessage("What action would you like to perform?")
                        .setNeutralButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(LikedListingsActivity.this, UpdateListingActivity.class);
                                intent.putExtra("listing", listing);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialogint, int whichButton) {
                                AlertDialog dialog = new AlertDialog.Builder(LikedListingsActivity.this)
                                        .setTitle("Delete Listing")
                                        .setMessage("Are you sure you want to delete this listing?")

                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                final DialogInterface dialog1 = dialog;
                                                APIConsumer.DeleteListingAsyncTask task = APIConsumer.DeleteListing(
                                                        PreferencesManager.getUserInformation().getAccessToken(),
                                                        listing,
                                                        new APIResponder<Void>() {
                                                            @Override
                                                            public void success(Void result) {
                                                                Toast.makeText(LikedListingsActivity.this, "Listing successfully deleted!", Toast.LENGTH_LONG).show();
                                                                updateListview();
                                                                dialog1.dismiss();
                                                            }

                                                            @Override
                                                            public void error(String errorMessage) {
                                                                Toast.makeText(LikedListingsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                                                dialog1.dismiss();
                                                            }
                                                        }
                                                );
                                                task.execute();
                                            }

                                        })
                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create();
                                dialog.show();
                                dialogint.dismiss();
                            }

                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alert.show();
                return true;
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
                setViewTitle("Liked Listings");
                setClassConstructor(ListingsAdapter.class);
                updateListview();
                setTint(allListings);
            }
        });
        carListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Liked Car Listings");
                setClassConstructor(CarListingsAdapter.class);
                updateListview();
                setTint(carListings);
            }
        });
        motorcycleListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Liked Motorcycle Listings");
                setClassConstructor(MotorcycleListingsAdapter.class);
                updateListview();
                setTint(motorcycleListings);
            }
        });
        boatListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Liked Boat Listings");
                setClassConstructor(BoatListingsAdapter.class);
                updateListview();
                setTint(boatListings);
            }
        });
        equipmentListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Liked Equipment Listings");
                setClassConstructor(EquipmentListingsAdapter.class);
                updateListview();
                setTint(equipmentListings);
            }
        });
        otherListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("Liked Other Listings");
                setClassConstructor(OtherListingsAdapter.class);
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
