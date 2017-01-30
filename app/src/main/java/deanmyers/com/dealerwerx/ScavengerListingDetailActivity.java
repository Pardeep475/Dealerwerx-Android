package deanmyers.com.dealerwerx;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import deanmyers.com.dealerwerx.API.APIConsumer;
import deanmyers.com.dealerwerx.API.APIResponder;
import deanmyers.com.dealerwerx.API.BoatExtra;
import deanmyers.com.dealerwerx.API.CarExtra;
import deanmyers.com.dealerwerx.API.ImageMedia;
import deanmyers.com.dealerwerx.API.Listing;
import deanmyers.com.dealerwerx.API.MotorcycleExtra;
import deanmyers.com.dealerwerx.API.Vehicle;

/**
 * Created by mac3 on 2016-11-18.
 */

public class ScavengerListingDetailActivity extends TitleCompatActivity {
    private ImageMedia[] media;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaillisting);

        setViewTitlePrimary("Scavenger");
        setViewTitle("Details");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewGroup container = (ViewGroup)findViewById(R.id.detail_container);

        Listing listing = (Listing)getIntent().getSerializableExtra("listing");
        Vehicle vehicle = listing.getVehicle();

        LayoutInflater inflater = getLayoutInflater();

        View v;
        switch (vehicle.getType()){
            case Car:
                v = inflater.inflate(R.layout.content_scavengerviewcar, container, false);
                break;
            case Motorcycle:
                v = inflater.inflate(R.layout.content_scavengerviewmotorcycle, container, false);
                break;
            case Boat:
                v = inflater.inflate(R.layout.content_scavengerviewboat, container, false);
                break;
            case Equipment:
                v = inflater.inflate(R.layout.content_scavengerviewequipment, container, false);
                break;
            case Other:
                v = inflater.inflate(R.layout.content_scavengerviewother, container, false);
                break;
            default:
                v = inflater.inflate(R.layout.content_scavengerviewother, container, false);
        }

        container.addView(v);

        media = vehicle.getMedia();
        final ViewPager pager = (ViewPager)v.findViewById(R.id.image_container);

        TextView mApprovalStatus = (TextView)v.findViewById(R.id.approval_status);
        TextView mTitle = (TextView)v.findViewById(R.id.title);
        TextView mDescription = (TextView)v.findViewById(R.id.description);
        TextView mPostedBy = (TextView)v.findViewById(R.id.seller);
        TextView mAskingPrice = (TextView)v.findViewById(R.id.asking_price);
        TextView mLocation = (TextView)v.findViewById(R.id.location);
        TextView mDatePosted = (TextView)v.findViewById(R.id.date_posted);
        TextView mYear = (TextView)v.findViewById(R.id.year);
        TextView mMake = (TextView)v.findViewById(R.id.make);
        TextView mModel = (TextView)v.findViewById(R.id.model);
        TextView mTrim = (TextView)v.findViewById(R.id.trim);
        TextView mColor = (TextView)v.findViewById(R.id.color);
        TextView mInteriorColor = (TextView)v.findViewById(R.id.interior_color);
        TextView mExteriorColor = (TextView)v.findViewById(R.id.exterior_color);
        TextView mBodyStyle = (TextView)v.findViewById(R.id.bodystyle);
        TextView mEngine = (TextView)v.findViewById(R.id.engine);
        TextView mFuelType = (TextView)v.findViewById(R.id.fueltype);
        TextView mKilometers = (TextView)v.findViewById(R.id.kilometers);
        TextView mDoors = (TextView)v.findViewById(R.id.doors);
        TextView mSeats = (TextView)v.findViewById(R.id.seats);
        TextView mTransmission = (TextView)v.findViewById(R.id.transmission);
        TextView mDriveTrain = (TextView)v.findViewById(R.id.drivetrain);
        TextView mVin = (TextView)v.findViewById(R.id.vin);


        ImageView mSafeZone = (ImageView)v.findViewById(R.id.image_safezone_approval);


        switch(vehicle.getType()){
            case Car:
                CarExtra cExtra = (CarExtra)vehicle.getExtra();
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
                break;
            case Equipment:
            case Other:
        }

        mApprovalStatus.setText(listing.getApprovalStatus());
        mTitle.setText(vehicle.getTitle());
        mDescription.setText(vehicle.getDescription());
        mPostedBy.setText(listing.getPostedBy());
        mAskingPrice.setText(String.format(Locale.CANADA, "$%.2f", listing.getAskingPrice()));
        mLocation.setText(listing.getLocation());
        mDatePosted.setText(listing.getDatePosted());

        if(listing.isSafeZone())
            mSafeZone.setImageResource(R.drawable.dealerwerx_safezone);

        if(media.length == 0)
            pager.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        else
            pager.setAdapter(new ImageViewPageAdapter());
    }

    private class ImageViewPageAdapter extends PagerAdapter{

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
            LinearLayout llayout = new LinearLayout(ScavengerListingDetailActivity.this);
            llayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            llayout.setBackgroundColor(2139402);
            final ImageView imageView = new ImageView(ScavengerListingDetailActivity.this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setBackgroundColor(2139402);

            Picasso.with(ScavengerListingDetailActivity.this).load(media[position].getThumbnailUrl()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog nagDialog = new Dialog(ScavengerListingDetailActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            nagDialog.setContentView(R.layout.preview_image);
                            Button btnClose = (Button)nagDialog.findViewById(R.id.action_close);
                            Button btnDelete = (Button)nagDialog.findViewById(R.id.action_delete);
                            final ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.preview);

                            FrameLayout.LayoutParams ivParams = (FrameLayout.LayoutParams)ivPreview.getLayoutParams();
                            ivParams.setMargins(0,0,0,0);
                            ivPreview.setLayoutParams(ivParams);

                            btnDelete.setVisibility(View.INVISIBLE);

                            Picasso.with(ScavengerListingDetailActivity.this).load(media[position].getImageUrl()).into(ivPreview);
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
            container.removeView((LinearLayout)object);
        }
    }
}
