package deanmyers.com.dealerwerx;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.Beacon;
import deanmyers.com.dealerwerx.API.Listing;
import deanmyers.com.dealerwerx.Adapters.BeaconsAdapter;

public class MyBeaconsActivity extends NavigationActivity {

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

    private final int REQUEST_SELECTLISTING = 1;

    protected void onCreate(Bundle savedInstanceState) {
        setCurrentActivity(R.id.nav_mybeacons);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);

        setViewTitlePrimary("My Beacons");
        setViewTitle("");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyBeaconsActivity.this, AddBeaconActivity.class);
                startActivity(intent);
            }
        });

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Bluetooth Required")
                    .setMessage("For beacons and vehicle notification features, you must enable Bluetooth.")
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBluetoothAdapter.enable();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }

        srl = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        srlListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                APIConsumer.GetMyBeaconsAsyncTask task = APIConsumer.GetMyBeacons(PreferencesManager.getUserInformation().getAccessToken(), new APIResponder<Beacon[]>() {
                    @Override
                    public void success(Beacon[] result) {
                        ListView listView = (ListView)findViewById(R.id.beacons_list);

                        try {
                            listView.setAdapter(new BeaconsAdapter(MyBeaconsActivity.this, 0, Arrays.asList(result)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        srl.setRefreshing(false);
                    }

                    @Override
                    public void error(String errorMessage) {
                        srl.setRefreshing(false);
                        Toast.makeText(MyBeaconsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

                task.execute();
            }
        };

        srl.setOnRefreshListener(srlListener);

        listView = (ListView)findViewById(R.id.beacons_list);

        View empty = findViewById(R.id.noitems);
        listView.setEmptyView(empty);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Beacon beacon = (Beacon)listView.getItemAtPosition(position);
                Listing listing = beacon.getListing();
                if(listing == null){
                    AlertDialog dialog = new AlertDialog.Builder(MyBeaconsActivity.this)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    associateListing(beacon);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setTitle("Not Associated")
                            .setMessage("This beacon is not yet associated with a listing, would you like to associate one now?")
                            .create();

                    dialog.show();
                }else{
                    Intent intent = new Intent(MyBeaconsActivity.this, ListingDetailActivity.class);
                    intent.putExtra("listing", listing);
                    startActivity(intent);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Beacon beacon = (Beacon)parent.getItemAtPosition(position);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyBeaconsActivity.this)
                        .setTitle("Actions")
                        .setMessage("What action would you like to perform?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
//                        .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                renameBeacon(beacon);
//                            }
//                        });

                if(beacon.getListing() != null){
                    alertBuilder.setNeutralButton("Disassociate", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            disassociateListing(beacon);
                        }
                    });
                }else{
                    alertBuilder.setNeutralButton("Associate", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            associateListing(beacon);
                        }
                    });
                }

                AlertDialog alert = alertBuilder.create();

                alert.show();
                return true;
            }
        });
        updateListview();
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

    private void disassociateListing(final Beacon beacon){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Disassociate Beacon")
                .setMessage("Are you sure you wish to disassociate the listing from this beacon?")
                .setPositiveButton("Disassociate", null)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final AlertDialog alertDialog = (AlertDialog)dialog;
                final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                final Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveButton.setEnabled(false);
                        negativeButton.setEnabled(false);

                        APIConsumer.AssociateBeaconAsyncTask task = APIConsumer.AssociateBeacon(
                                PreferencesManager.getUserInformation().getAccessToken(),
                                beacon,
                                null,
                                new APIResponder<Beacon>() {
                                    @Override
                                    public void success(Beacon result) {
                                        Toast.makeText(MyBeaconsActivity.this, "Beacon successfully " + ((result.getListing() != null) ? "associated" : "disassociated") + "!", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        updateListview();
                                    }

                                    @Override
                                    public void error(String errorMessage) {
                                        Toast.makeText(MyBeaconsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                }
                        );

                        task.execute();
                    }
                });

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private void associateListing(Beacon beacon){
        Intent pickerIntent = new Intent(this, ListingPickerActivity.class);
        pickerIntent.putExtra("title1", "Beacons");
        pickerIntent.putExtra("title2", "Select A Listing...");
        pickerIntent.putExtra("beacon", beacon);
        startActivityForResult(pickerIntent, REQUEST_SELECTLISTING);
    }

    private void renameBeacon(final Beacon beacon){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setHint("New Name");
        input.setText(beacon.getName());
        input.setMaxLines(1);
        input.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(128)
        });

        builder.setTitle("Rename Beacon")
                .setView(input)
                .setPositiveButton("Rename", null)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final AlertDialog alertDialog = (AlertDialog)dialog;
                final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                final Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveButton.setEnabled(false);
                        negativeButton.setEnabled(false);

                        APIConsumer.RenameBeaconAsyncTask task = APIConsumer.RenameBeacon(
                                PreferencesManager.getUserInformation().getAccessToken(),
                                beacon,
                                input.getText().toString(),
                                new APIResponder<Beacon>() {
                                    @Override
                                    public void success(Beacon result) {
                                        Toast.makeText(MyBeaconsActivity.this, "Beacon successfully renamed!", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        updateListview();
                                    }

                                    @Override
                                    public void error(String errorMessage) {
                                        input.setError(errorMessage);
                                        positiveButton.setEnabled(true);
                                        negativeButton.setEnabled(true);
                                    }
                                }
                        );

                        task.execute();
                    }
                });

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SELECTLISTING){
            if(resultCode == Activity.RESULT_OK){
                Listing listing = (Listing)data.getSerializableExtra("listing");
                Beacon beacon = (Beacon)data.getSerializableExtra("beacon");
                listView.setEnabled(false);
                APIConsumer.AssociateBeaconAsyncTask task = APIConsumer.AssociateBeacon(
                        PreferencesManager.getUserInformation().getAccessToken(),
                        beacon,
                        listing,
                        new APIResponder<Beacon>() {
                            @Override
                            public void success(Beacon result) {
                                Toast.makeText(MyBeaconsActivity.this, "Beacon successfully " + ((result.getListing() != null) ? "associated" : "disassociated") + "!", Toast.LENGTH_LONG).show();
                                updateListview();
                                listView.setEnabled(true);
                            }

                            @Override
                            public void error(String errorMessage) {
                                Toast.makeText(MyBeaconsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                listView.setEnabled(true);
                            }
                        }
                );

                task.execute();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListview();
    }
}
