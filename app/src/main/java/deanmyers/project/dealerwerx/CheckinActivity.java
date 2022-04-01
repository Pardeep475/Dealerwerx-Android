package deanmyers.project.dealerwerx;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class CheckinActivity extends NavigationActivity {


    protected void onCreate(Bundle savedInstanceState) {
        setCurrentActivity(R.id.nav_checkin);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        setViewTitlePrimary("Check In");
        setViewTitle("");

        TextView checkinMessageView = (TextView)findViewById(R.id.checkin_message);
        ImageView qrImageView = (ImageView)findViewById(R.id.qr_image);

        if(PreferencesManager.getUserInformation() == null){
            checkinMessageView.setText("\nYou are not a SafeZone member!\nOpen the menu and tap Videos to see\nthe various perks of enrolling in your\nsystem!");
        }else if(PreferencesManager.getUserInformation().getQrImageUrl() == null){
            checkinMessageView.setText("\nYou are not a SafeZone member!\nOpen the menu and tap Videos to see\nthe various perks of enrolling in your\nsystem!");
        }else{
            String qrImageUrl = PreferencesManager.getUserInformation().getQrImageUrl();

            if(qrImageUrl != null){
                checkinMessageView.setText("You are SafeZone Verified\n\nPlease scan the following code at the desk of your SafeZone location to check in.");
                Picasso.with(this).load(qrImageUrl).into(qrImageView);
            }
        }


    }
}
