package deanmyers.project.dealerwerx;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import deanmyers.project.dealerwerx.API.APIConsumer;
import deanmyers.project.dealerwerx.API.APIResponder;
import deanmyers.project.dealerwerx.API.BoatExtra;
import deanmyers.project.dealerwerx.API.CarExtra;
import deanmyers.project.dealerwerx.API.ImageMedia;
import deanmyers.project.dealerwerx.API.Listing;
import deanmyers.project.dealerwerx.API.MotorcycleExtra;
import deanmyers.project.dealerwerx.API.Vehicle;

/**
 * Created by mac3 on 2016-11-18.
 */

public class ListingDetailActivity extends TitleCompatActivity {
    private ImageMedia[] media;
    private Listing listing;

    private LinearLayout holdListing;
    private LinearLayout buyListing;
    private LinearLayout makeOfferOnListing;
    private LinearLayout likeListing;
    private LinearLayout shareListing;
    private ViewPager pager;
    SingleDateAndTimePickerDialog.Builder singleBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaillisting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewGroup container = (ViewGroup) findViewById(R.id.detail_container);

        listing = (Listing) getIntent().getSerializableExtra("listing");
        Vehicle vehicle = listing.getVehicle();


        LayoutInflater inflater = getLayoutInflater();

        View v;
        switch (vehicle.getType()) {
            case Car:
                v = inflater.inflate(R.layout.content_viewcar, container, false);
                break;
            case Motorcycle:
                v = inflater.inflate(R.layout.content_viewmotorcycle, container, false);
                break;
            case Boat:
                v = inflater.inflate(R.layout.content_viewboat, container, false);
                break;
            case Equipment:
                v = inflater.inflate(R.layout.content_viewequipment, container, false);
                break;
            case Other:
                v = inflater.inflate(R.layout.content_viewother, container, false);
                break;
            default:
                v = inflater.inflate(R.layout.content_viewother, container, false);
        }

        container.addView(v);

        View bottomButtons = findViewById(R.id.bottom_buttons);
        if (listing.isMyPost())
            bottomButtons.setVisibility(View.GONE);

        holdListing = (LinearLayout) findViewById(R.id.action_listing_hold);
        buyListing = (LinearLayout) findViewById(R.id.action_listing_buy);
        makeOfferOnListing = (LinearLayout) findViewById(R.id.action_listing_makeoffer);
        likeListing = (LinearLayout) findViewById(R.id.action_listing_like);
        shareListing = (LinearLayout) findViewById(R.id.action_listing_share);

        updateLikeButton(PreferencesManager.hasLiked(listing.getId()));

        holdListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holdListing();
            }
        });
        buyListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyListing();
            }
        });
        makeOfferOnListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeOffer();
            }
        });
        likeListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeListing();
            }
        });
        shareListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareListing();
            }
        });


        media = vehicle.getMedia();
        pager = (ViewPager) v.findViewById(R.id.image_container);

        TextView mTitle = (TextView) v.findViewById(R.id.title);
        TextView mDescription = (TextView) v.findViewById(R.id.description);
        TextView mPostedBy = (TextView) v.findViewById(R.id.seller);
        TextView mAskingPrice = (TextView) v.findViewById(R.id.asking_price);
        TextView mLocation = (TextView) v.findViewById(R.id.location);
        TextView mDatePosted = (TextView) v.findViewById(R.id.date_posted);
        TextView mYear = (TextView) v.findViewById(R.id.year);
        TextView mMake = (TextView) v.findViewById(R.id.make);
        TextView mModel = (TextView) v.findViewById(R.id.model);
        TextView mTrim = (TextView) v.findViewById(R.id.trim);
        TextView mColor = (TextView) v.findViewById(R.id.color);
        TextView mInteriorColor = (TextView) v.findViewById(R.id.interior_color);
        TextView mExteriorColor = (TextView) v.findViewById(R.id.exterior_color);
        TextView mBodyStyle = (TextView) v.findViewById(R.id.bodystyle);
        TextView mEngine = (TextView) v.findViewById(R.id.engine);
        TextView mFuelType = (TextView) v.findViewById(R.id.fueltype);
        TextView mKilometers = (TextView) v.findViewById(R.id.kilometers);
        TextView mDoors = (TextView) v.findViewById(R.id.doors);
        TextView mSeats = (TextView) v.findViewById(R.id.seats);
        TextView mTransmission = (TextView) v.findViewById(R.id.transmission);
        TextView mDriveTrain = (TextView) v.findViewById(R.id.drivetrain);
        TextView mVin = (TextView) v.findViewById(R.id.vin);
        TextView mAppointmentRequired = (TextView) v.findViewById(R.id.appointment_required);
        TextView mCustomerNumber = (TextView) v.findViewById(R.id.customer_number);
        TextView mStockNumber = (TextView) v.findViewById(R.id.stock_number);

        ImageView mSafeZone = (ImageView) v.findViewById(R.id.image_safezone_approval);


        switch (vehicle.getType()) {
            case Car:
                CarExtra cExtra = (CarExtra) vehicle.getExtra();
                mYear.setText(String.format(Locale.CANADA, "%d", cExtra.getYear()));
                mMake.setText(cExtra.getMake());
                mModel.setText(cExtra.getModel());
                mTrim.setText(cExtra.getTrim());
                mInteriorColor.setText(cExtra.getInteriorColor());
                mExteriorColor.setText(cExtra.getExteriorColor());
                mBodyStyle.setText(cExtra.getBodyStyle());
                mEngine.setText(cExtra.getEngine());
                mFuelType.setText(cExtra.getFuelType());
                mKilometers.setText(String.format(Locale.CANADA, "%d", cExtra.getKilometers()));
                mDoors.setText(String.format(Locale.CANADA, "%d", cExtra.getDoors()));
                mSeats.setText(String.format(Locale.CANADA, "%d", cExtra.getSeats()));
                mTransmission.setText(cExtra.getTransmission());
                mDriveTrain.setText(cExtra.getDriveTrain());
                mVin.setText(cExtra.getVin());

                if (isUnset(cExtra.getTrim()))
                    ((View) mTrim.getParent()).setVisibility(View.GONE);

                if (isUnset(cExtra.getInteriorColor()))
                    ((View) mInteriorColor.getParent()).setVisibility(View.GONE);

                if (isUnset(cExtra.getBodyStyle()))
                    ((View) mBodyStyle.getParent()).setVisibility(View.GONE);

                if (isUnset(cExtra.getFuelType()))
                    ((View) mFuelType.getParent()).setVisibility(View.GONE);

                if (isUnset(mDoors.getText().toString()))
                    ((View) mDoors.getParent()).setVisibility(View.GONE);

                if (isUnset(mSeats.getText().toString()))
                    ((View) mSeats.getParent()).setVisibility(View.GONE);

                if (isUnset(cExtra.getTransmission()))
                    ((View) mTransmission.getParent()).setVisibility(View.GONE);

                if (isUnset(cExtra.getDriveTrain()))
                    ((View) mDriveTrain.getParent()).setVisibility(View.GONE);

                if (isUnset(cExtra.getVin()))
                    ((View) mVin.getParent()).setVisibility(View.GONE);
                break;
            case Motorcycle:
                MotorcycleExtra mExtra = (MotorcycleExtra) vehicle.getExtra();
                mYear.setText(String.format(Locale.CANADA, "%d", mExtra.getYear()));
                mMake.setText(mExtra.getMake());
                mModel.setText(mExtra.getModel());
                mTrim.setText(mExtra.getTrim());
                mColor.setText(mExtra.getColor());
                mBodyStyle.setText(mExtra.getBodyStyle());
                mEngine.setText(mExtra.getEngine());
                mFuelType.setText(mExtra.getFuelType());
                mKilometers.setText(String.format(Locale.CANADA, "%d", mExtra.getKilometers()));

                if (isUnset(mExtra.getTrim()))
                    ((View) mTrim.getParent()).setVisibility(View.GONE);

                if (isUnset(mExtra.getBodyStyle()))
                    ((View) mBodyStyle.getParent()).setVisibility(View.GONE);

                if (isUnset(mExtra.getFuelType()))
                    ((View) mFuelType.getParent()).setVisibility(View.GONE);
                break;
            case Boat:
                BoatExtra bExtra = (BoatExtra) vehicle.getExtra();
                mYear.setText(String.format(Locale.CANADA, "%d", bExtra.getYear()));
                mMake.setText(bExtra.getMake());
                mModel.setText(bExtra.getModel());
                mTrim.setText(bExtra.getTrim());
                mColor.setText(bExtra.getColor());
                mBodyStyle.setText(bExtra.getBodyStyle());
                mEngine.setText(bExtra.getEngine());
                mFuelType.setText(bExtra.getFuelType());
                mKilometers.setText(String.format(Locale.CANADA, "%d", bExtra.getKilometers()));

                if (isUnset(bExtra.getTrim()))
                    ((View) mTrim.getParent()).setVisibility(View.GONE);

                if (isUnset(bExtra.getBodyStyle()))
                    ((View) mBodyStyle.getParent()).setVisibility(View.GONE);

                if (isUnset(bExtra.getFuelType()))
                    ((View) mFuelType.getParent()).setVisibility(View.GONE);
                break;
            case Equipment:
            case Other:
        }

        mDescription.setText(vehicle.getDescription());
        mPostedBy.setText(listing.getPostedBy());
        mAskingPrice.setText(listing.getAskingPrice() < 0 ? "Contact for Pricing" : String.format(Locale.CANADA, "$%.2f", listing.getAskingPrice()));
        mLocation.setText(listing.getLocation());
        mDatePosted.setText(listing.getDatePosted());

        if (isUnset(vehicle.getDescription())) {
            ((View) mDescription.getParent()).setVisibility(View.GONE);
        }

        if (isUnset(listing.getCustomerNumber())) {
            ((View) mCustomerNumber.getParent()).setVisibility(View.GONE);
        } else {
            mCustomerNumber.setText(String.format(Locale.CANADA, "%d", listing.getCustomerNumber()));
        }

        if (isUnset(vehicle.getStockNumber())) {
            ((View) mStockNumber.getParent()).setVisibility(View.GONE);
        } else {
            mStockNumber.setText(vehicle.getStockNumber());
        }

        switch (vehicle.getType()) {
            case Car:
            case Motorcycle:
            case Boat:
                setViewTitlePrimary(mMake.getText() + " " + mModel.getText());
                setViewTitle(mYear.getText().toString());
                break;
            case Equipment:
            case Other:
            default:
                setViewTitlePrimary(listing.getVehicle().getTitle());
                setViewTitle("");
        }
        if (listing.isSafeZone())
            mSafeZone.setImageResource(R.drawable.dealerwerx_safezone);
        else {
            mAppointmentRequired.setVisibility(View.GONE);
        }

        if (media.length == 0) {
            if (!listing.isSafeZone())
                ((View) pager.getParent()).setVisibility(View.GONE);
            else
                pager.setVisibility(View.GONE);
        } else
            pager.setAdapter(new ImageViewPageAdapter());
    }

    private final static int REQUEST_BUY = 3;

    private void buyListing() {
        if (requestPhoneNumber(REQUEST_BUY)) {
         /*   Dialog dialog = new Dialog(this, R.style.DialogTheme);
            dialog.setContentView(R.layout.content_purchase);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            Button dateInput1 = dialog.findViewById(R.id.date1);
            dateInput1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDate(1, dateInput1);
                }
            });*/
            AlertDialog.Builder builder = new AlertDialog.Builder(ListingDetailActivity.this);

            builder.setTitle("Request Purchase")
                    .setView(R.layout.content_purchase)
                    .setPositiveButton("Request", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            final AlertDialog mainDialog = builder.create();
            mainDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    final AlertDialog alertDialog = (AlertDialog) dialog;
                    final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    final Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                    ((TextView) alertDialog.findViewById(R.id.safezone_enabled)).setText(listing.isSafeZone() ? R.string.safezone_enabled : R.string.safezone_disabled);

                    final View appointmentButtons = alertDialog.findViewById(R.id.safezone_only);

                    date1 = null;
                    date2 = null;
                    date3 = null;

                    final Button dateInput1 = (Button) alertDialog.findViewById(R.id.date1);
                    final Button dateInput2 = (Button) alertDialog.findViewById(R.id.date2);
                    final Button dateInput3 = (Button) alertDialog.findViewById(R.id.date3);

                    dateInput1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDate(1, dateInput1);
                        }
                    });

                    dateInput2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDate(2, dateInput2);
                        }
                    });

                    dateInput3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDate(3, dateInput3);
                        }
                    });

                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String dateString1 = "";
                            String dateString2 = "";
                            String dateString3 = "";

                            if ((date1 != null || date2 != null || date3 != null)) {
                                SimpleDateFormat dt1 = new SimpleDateFormat("EEEE, MMMM dd hh:mm a", Locale.CANADA);
                                if (date1 != null)
                                    dateString1 = dt1.format(date1);
                                if (date2 != null)
                                    dateString2 = dt1.format(date2);
                                if (date3 != null)
                                    dateString3 = dt1.format(date3);
                            } else {
                                Toast.makeText(ListingDetailActivity.this, "You must select at least one appointment preference date.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            positiveButton.setEnabled(false);
                            negativeButton.setEnabled(false);


                            APIConsumer.PurchaseListingAsyncTask task = APIConsumer.PurchaseListing(
                                    PreferencesManager.getUserInformation().getAccessToken(),
                                    listing.getId(),
                                    dateString1,
                                    dateString2,
                                    dateString3,
                                    requestSafezone == null ? false : requestSafezone,
                                    new APIResponder<Void>() {
                                        @Override
                                        public void success(Void result) {
                                            Toast.makeText(ListingDetailActivity.this, "Purchase request successfully placed!", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void error(String errorMessage) {
                                            Toast.makeText(ListingDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
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

            if (!listing.isSafeZone() && requestSafezone == null) {
                new AlertDialog.Builder(this).setTitle("Not SafeZone Verified")
                        .setMessage("This listing is not SafeZone verified. Would you like to request SafeZone Approval to the seller?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestSafezone = true;
                                dialog.dismiss();
                                mainDialog.show();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestSafezone = false;
                        dialog.dismiss();
                        mainDialog.show();
                    }
                }).create().show();
            } else {
                mainDialog.show();
            }
        }
        }

    private Boolean requestSafezone = null;

    private final static int REQUEST_MAKEOFFER = 2;

    private void makeOffer() {
        if (requestPhoneNumber(REQUEST_MAKEOFFER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Make Offer")
                    .setView(R.layout.content_makeoffer)
                    .setPositiveButton("Place Offer", null)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            final AlertDialog mainDialog = builder.create();

            mainDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    final AlertDialog alertDialog = (AlertDialog) dialog;
                    final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    final Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                    ((TextView) alertDialog.findViewById(R.id.asking_price)).setText(String.format(Locale.CANADA, "$%.2f", listing.getAskingPrice()));

                    if (listing.getAskingPrice() < 0) {
                        ((View) (alertDialog.findViewById(R.id.asking_price).getParent())).setVisibility(View.GONE);
                    }

                    ((TextView) alertDialog.findViewById(R.id.safezone_enabled)).setText(listing.isSafeZone() ? R.string.safezone_enabled : R.string.safezone_disabled);

                    final View appointmentButtons = alertDialog.findViewById(R.id.safezone_only);


                    date1 = null;
                    date2 = null;
                    date3 = null;

                    final Button dateInput1 = (Button) alertDialog.findViewById(R.id.date1);
                    final Button dateInput2 = (Button) alertDialog.findViewById(R.id.date2);
                    final Button dateInput3 = (Button) alertDialog.findViewById(R.id.date3);

                    dateInput1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDate(1, dateInput1);
                        }
                    });

                    dateInput2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDate(2, dateInput2);
                        }
                    });

                    dateInput3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDate(3, dateInput3);
                        }
                    });


                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            EditText offerPrice = (EditText) alertDialog.findViewById(R.id.offerPrice);

                            boolean cancel = false;
                            double offer;

                            String dateString1 = "";
                            String dateString2 = "";
                            String dateString3 = "";

                            if ((date1 != null || date2 != null || date3 != null)) {
                                SimpleDateFormat dt1 = new SimpleDateFormat("EEEE, MMMM dd hh:mm a", Locale.CANADA);
                                if (date1 != null)
                                    dateString1 = dt1.format(date1);
                                if (date2 != null)
                                    dateString2 = dt1.format(date2);
                                if (date3 != null)
                                    dateString3 = dt1.format(date3);
                            } else {
                                Toast.makeText(ListingDetailActivity.this, "You must select at least one appointment preference date.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            positiveButton.setEnabled(false);
                            negativeButton.setEnabled(false);

                            if (TextUtils.isEmpty(offerPrice.getText())) {
                                cancel = true;
                                offerPrice.setError("You must provide an offer value!");
                            } else {
                                try {
                                    offer = Double.parseDouble(offerPrice.getText().toString().trim());
                                    APIConsumer.OfferOnListingAsyncTask task = APIConsumer.OfferOnListing(
                                            PreferencesManager.getUserInformation().getAccessToken(),
                                            listing.getId(),
                                            offer,
                                            dateString1,
                                            dateString2,
                                            dateString3,
                                            requestSafezone == null ? false : requestSafezone,
                                            new APIResponder<Void>() {
                                                @Override
                                                public void success(Void result) {
                                                    Toast.makeText(ListingDetailActivity.this, "Offer successfully placed!", Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                }

                                                @Override
                                                public void error(String errorMessage) {
                                                    Toast.makeText(ListingDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                }
                                            }
                                    );

                                    task.execute();
                                } catch (Exception e) {
                                    cancel = true;
                                    offerPrice.setError("Invalid offer value!");
                                }
                            }

                            if (cancel) {
                                offerPrice.requestFocus();
                                positiveButton.setEnabled(true);
                                negativeButton.setEnabled(true);
                            } else {
                                dialog.dismiss();
                            }
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

            if (!listing.isSafeZone() && requestSafezone == null) {
                new AlertDialog.Builder(this).setTitle("Not SafeZone Verified")
                        .setMessage("This listing is not SafeZone verified. Would you like to request SafeZone Approval to the seller?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestSafezone = true;
                                dialog.dismiss();
                                mainDialog.show();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestSafezone = false;
                        dialog.dismiss();
                        mainDialog.show();
                    }
                }).create().show();
            } else {
                mainDialog.show();
            }

        }
    }

    private static boolean isUnset(String value) {
        if (value == null)
            return true;

        String[] forbiddenValues = new String[]{
                "none",
                "N/A",
                "0",
                "not available",
                "no",
                "na",
                "-"
        };

        for (String i : forbiddenValues) {
            if (i.trim().toLowerCase().contains(value.trim().toLowerCase()))
                return true;
        }

        return false;
    }

    private static boolean isUnset(Long value) {
        if (value == null)
            return true;

        return false;
    }

    private Date date1, date2, date3;
    private final static int REQUEST_HOLD = 1;

    private void getDate(final int index, final Button sender) {
        Date dateDisplay=null;
        Dialog dialog =new Dialog(this, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_custom_time_picker);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final SingleDateAndTimePicker singleDateAndTimePicker = dialog.findViewById(R.id.picker);
        Calendar cal = Calendar.getInstance(Locale.CANADA);
        cal.setTime(new Date());
        singleDateAndTimePicker.setDefaultDate(cal.getTime());

        //cal.add(Calendar.DATE, 30);
        //
        singleDateAndTimePicker.setCustomLocale(Locale.CANADA);
        TextView mTextHeader=dialog.findViewById(R.id.sheetTitle);
        mTextHeader.setText("Appointment " + index);
        TextView mBtnOk=dialog.findViewById(R.id.buttonOk);
         mBtnOk.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (index == 1) {
                     date1 = singleDateAndTimePicker.getDate();
                 } else if (index == 2) {
                     date2 = singleDateAndTimePicker.getDate();
                 } else {
                     date3 = singleDateAndTimePicker.getDate();
                 }
                 SimpleDateFormat dt1 = new SimpleDateFormat("EEEE, MMMM dd hh:mm a", Locale.CANADA);
                 sender.setText(dt1.format(singleDateAndTimePicker.getDate()));
                 dialog.dismiss();
             }
         });


        SingleDateAndTimePicker.OnDateChangedListener changeListener = (displayed, date) -> {
            if (index == 1) {
                date1 = date;
            } else if (index == 2) {
                date2 = date;
            } else {
                date3 = date;
            }
            SimpleDateFormat dt1 = new SimpleDateFormat("EEEE, MMMM dd hh:mm a", Locale.CANADA);
            sender.setText(dt1.format(date));
        };

        singleDateAndTimePicker.addOnDateChangedListener(changeListener);
        dialog.show();
        /*DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if (index == 1) {
                date1 = cal.getTime();
            } else if (index == 2) {
                date2 = cal.getTime();
            } else {
                date3 = cal.getTime();
            }
            SimpleDateFormat dt1 = new SimpleDateFormat("EEEE, MMMM dd hh:mm a", Locale.CANADA);
            sender.setText(dt1.format(cal.getTime()));


        };
        new DatePickerDialog(ListingDetailActivity.this, date, cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();*/
    }

    private void display(String toDisplay) {
        Toast.makeText(this, toDisplay, Toast.LENGTH_SHORT).show();
    }

    private void holdListing() {
        if (requestPhoneNumber(REQUEST_HOLD)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Request Hold")
                    .setView(R.layout.content_hold)
                    .setPositiveButton("Request", null)
                    .setNegativeButton("Cancel", null);

            final AlertDialog mainDialog = builder.create();

            mainDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    final AlertDialog alertDialog = (AlertDialog) dialog;
                    final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    final Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                    ((TextView) alertDialog.findViewById(R.id.safezone_enabled)).setText(listing.isSafeZone() ? R.string.safezone_enabled : R.string.safezone_disabled);

                    final View appointmentButtons = alertDialog.findViewById(R.id.safezone_only);

                    date1 = null;
                    date2 = null;
                    date3 = null;

                    final Button dateInput1 = (Button) alertDialog.findViewById(R.id.date1);
                    final Button dateInput2 = (Button) alertDialog.findViewById(R.id.date2);
                    final Button dateInput3 = (Button) alertDialog.findViewById(R.id.date3);

                    dateInput1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDate(1, dateInput1);
                        }
                    });

                    dateInput2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDate(2, dateInput2);
                        }
                    });

                    dateInput3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDate(3, dateInput3);
                        }
                    });


                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String dateString1 = "";
                            String dateString2 = "";
                            String dateString3 = "";

                            if ((date1 != null || date2 != null || date3 != null)) {
                                SimpleDateFormat dt1 = new SimpleDateFormat("EEEE, MMMM dd hh:mm a", Locale.CANADA);
                                if (date1 != null)
                                    dateString1 = dt1.format(date1);
                                if (date2 != null)
                                    dateString2 = dt1.format(date2);
                                if (date3 != null)
                                    dateString3 = dt1.format(date3);
                            } else {
                                Toast.makeText(ListingDetailActivity.this, "You must select at least one appointment preference date.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            positiveButton.setEnabled(false);
                            negativeButton.setEnabled(false);

                            APIConsumer.HoldListingAsyncTask task = APIConsumer.HoldListing(
                                    PreferencesManager.getUserInformation().getAccessToken(),
                                    listing.getId(),
                                    dateString1,
                                    dateString2,
                                    dateString3,
                                    requestSafezone == null ? false : requestSafezone,
                                    new APIResponder<Void>() {
                                        @Override
                                        public void success(Void result) {
                                            Toast.makeText(ListingDetailActivity.this, "Hold request successfully placed!", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void error(String errorMessage) {
                                            Toast.makeText(ListingDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
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

            if (!listing.isSafeZone() && requestSafezone == null) {
                new AlertDialog.Builder(this).setTitle("Not SafeZone Verified")
                        .setMessage("This listing is not SafeZone verified. Would you like to request SafeZone Approval to the seller?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestSafezone = true;
                                dialog.dismiss();
                                mainDialog.show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestSafezone = false;
                                dialog.dismiss();
                                mainDialog.show();
                            }
                        })
                        .create().show();
            } else {
                mainDialog.show();
            }
        }
    }

    private void likeListing() {
        boolean liked = PreferencesManager.hasLiked(listing.getId());

        if (liked) {
            PreferencesManager.removeLike(listing.getId());
        } else {
            PreferencesManager.addLike(listing.getId());
        }

        updateLikeButton(!liked);
    }

    private void updateLikeButton(boolean liked) {
        ImageView likeImage = (ImageView) likeListing.getChildAt(0);
        TextView likeText = (TextView) likeListing.getChildAt(1);
        likeImage.setImageResource(liked ? R.drawable.like_active : R.drawable.like_icon);
        likeText.setText(liked ? "Unlike" : "Like");
    }

    private void shareListing() {
        ShareLinkContent.Builder builder = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://dealerwerx.com"))
                .setContentTitle(listing.getVehicle().getTitle())
                .setContentDescription(listing.getVehicle().getDescription());
        if (media != null && media.length > 0) {
            builder.setImageUrl(Uri.parse(media[0].getImageUrl()));
        }
        ShareLinkContent content = builder.build();
        ShareDialog.show(this, content);
    }

    private boolean requestPhoneNumber(int requestCode) {
        if (PreferencesManager.getUserInformation().getPhoneNumber() == null) {
            Intent intent = new Intent(this, RequireMoreInformationActivity.class);
            startActivityForResult(intent, requestCode);
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != 1) return;
        switch (requestCode) {
            case REQUEST_BUY:
                buyListing();
                break;
            case REQUEST_HOLD:
                holdListing();
                break;
            case REQUEST_MAKEOFFER:
                makeOffer();
                break;
        }
    }

    private class ImageViewPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return media.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LinearLayout llayout = new LinearLayout(ListingDetailActivity.this);
            llayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            llayout.setBackgroundColor(2139402);
            final ImageView imageView = new ImageView(ListingDetailActivity.this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setBackgroundColor(2139402);

            Picasso.with(ListingDetailActivity.this).load(media[position].getThumbnailUrl()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog nagDialog = new Dialog(ListingDetailActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            nagDialog.setContentView(R.layout.preview_image);
                            Button btnClose = (Button) nagDialog.findViewById(R.id.action_close);
                            Button btnDelete = (Button) nagDialog.findViewById(R.id.action_delete);
                            final ImageView ivPreview = (ImageView) nagDialog.findViewById(R.id.preview);

                            FrameLayout.LayoutParams ivParams = (FrameLayout.LayoutParams) ivPreview.getLayoutParams();
                            ivParams.setMargins(0, 0, 0, 0);
                            ivPreview.setLayoutParams(ivParams);

                            btnDelete.setVisibility(View.INVISIBLE);

                            Picasso.with(ListingDetailActivity.this).load(media[position].getImageUrl()).into(ivPreview);
//                                    APIConsumer.DownloadImageAsyncTask task1 = APIConsumer.DownloadImage(media[position].getImageUrl(), new APIResponder<Bitmap>() {
//                                                @Override
//                                                public void success(Bitmap result2) {
//                                                    ivPreview.setImageBitmap(result2);
//                                                }
//
//                                                @Override
//                                                public void error(String errorMessage) {
//
//                                                }
//                                            });
//                                    task1.execute();

                            btnClose.setOnClickListener(new Button.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    nagDialog.dismiss();
                                }
                            });

                            nagDialog.show();
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });
//            APIConsumer.DownloadImageAsyncTask task = APIConsumer.DownloadImage(
//                    media[position].getThumbnailUrl(),
//                    new APIResponder<Bitmap>() {
//                        @Override
//                        public void success(Bitmap result) {
//
//                        }
//
//                        @Override
//                        public void error(String errorMessage) {
//
//                        }
//                    }
//            );
//            task.execute();
            llayout.addView(imageView);
            container.addView(llayout);
            return llayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
