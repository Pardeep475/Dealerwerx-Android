package deanmyers.com.dealerwerx;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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

public class GarageActivity extends NavigationActivity {

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
        setCurrentActivity(R.id.nav_garage);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        setViewTitlePrimary("Garage");
        setViewTitle("My Listings");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GarageActivity.this, AddListingActivity.class);
                startActivity(intent);
            }
        });

        srl = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        srlListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                APIConsumer.GetMyListingsAsyncTask task = APIConsumer.GetMyListings(PreferencesManager.getUserInformation().getAccessToken(), new APIResponder<Listing[]>() {
                    @Override
                    public void success(Listing[] result) {
                        ListView listView = (ListView)findViewById(R.id.listings_list);

                        try {
                            listView.setAdapter((ListingsAdapter)classConstructor.newInstance(GarageActivity.this, 0, Arrays.asList(result)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        srl.setRefreshing(false);
                    }

                    @Override
                    public void error(String errorMessage) {
                        srl.setRefreshing(false);
                        Toast.makeText(GarageActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(GarageActivity.this, ListingDetailActivity.class);
                intent.putExtra("listing", listing);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Listing listing = (Listing)parent.getItemAtPosition(position);
                AlertDialog alert = new AlertDialog.Builder(GarageActivity.this)
                        .setTitle("Actions")
                        .setMessage("What action would you like to perform?")
                        .setNeutralButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(GarageActivity.this, UpdateListingActivity.class);
                                intent.putExtra("listing", listing);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("More", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialogint, int whichButton) {
                                AlertDialog.Builder alert2Builder = new AlertDialog.Builder(GarageActivity.this)
                                        .setTitle("More Actions")
                                        .setMessage("What action would you like to perform?");
                                        if(listing.isExpired()){
                                            alert2Builder.setNeutralButton("Relist", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogint, int which) {
                                                    AlertDialog dialog = new AlertDialog.Builder(GarageActivity.this)
                                                            .setTitle("Relist Listing")
                                                            .setMessage("Are you sure you want to relist this listing?")

                                                            .setPositiveButton("Relist", new DialogInterface.OnClickListener() {

                                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                    final DialogInterface dialog1 = dialog;
                                                                    APIConsumer.RelistListingAsyncTask task = APIConsumer.RelistListing(
                                                                            PreferencesManager.getUserInformation().getAccessToken(),
                                                                            listing,
                                                                            new APIResponder<Void>() {
                                                                                @Override
                                                                                public void success(Void result) {
                                                                                    Toast.makeText(GarageActivity.this, "Listing successfully relisted!", Toast.LENGTH_LONG).show();
                                                                                    updateListview();
                                                                                    dialog1.dismiss();
                                                                                }

                                                                                @Override
                                                                                public void error(String errorMessage) {
                                                                                    Toast.makeText(GarageActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
                                            });
                                        }
                                        alert2Builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialogint, int whichButton) {
                                                AlertDialog dialog = new AlertDialog.Builder(GarageActivity.this)
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
                                                                                Toast.makeText(GarageActivity.this, "Listing successfully deleted!", Toast.LENGTH_LONG).show();
                                                                                updateListview();
                                                                                dialog1.dismiss();
                                                                            }

                                                                            @Override
                                                                            public void error(String errorMessage) {
                                                                                Toast.makeText(GarageActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
                                        });
                                AlertDialog alert2 = alert2Builder.create();
                                alert2.show();
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
                setViewTitle("My Listings");
                setClassConstructor(ListingsAdapter.class);
                updateListview();
                setTint(allListings);
            }
        });
        carListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Car Listings");
                setClassConstructor(CarListingsAdapter.class);
                updateListview();
                setTint(carListings);
            }
        });
        motorcycleListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Motorcycle Listings");
                setClassConstructor(MotorcycleListingsAdapter.class);
                updateListview();
                setTint(motorcycleListings);
            }
        });
        boatListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Boat Listings");
                setClassConstructor(BoatListingsAdapter.class);
                updateListview();
                setTint(boatListings);
            }
        });
        equipmentListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Equipment Listings");
                setClassConstructor(EquipmentListingsAdapter.class);
                updateListview();
                setTint(equipmentListings);
            }
        });
        otherListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewTitle("My Other Listings");
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
