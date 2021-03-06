package deanmyers.project.dealerwerx;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;


public class SettingsActivity extends NavigationActivity {


    protected void onCreate(Bundle savedInstanceState) {
        setCurrentActivity(R.id.nav_settings);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setViewTitlePrimary("SETTINGS");
        setViewTitle("");

        Switch beaconsEnabledSwitch = (Switch)findViewById(R.id.beacons_enabled);
        Switch lookingModeSwitch = (Switch)findViewById(R.id.beacon_looking_mode);

        beaconsEnabledSwitch.setChecked(PreferencesManager.getAllowBackgroundScanning());
        lookingModeSwitch.setChecked(PreferencesManager.getBeaconLookingMode());

        beaconsEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.setAllowBackgroundScanning(isChecked);
            }
        });

        lookingModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferencesManager.setBeaconLookingMode(isChecked);
            }
        });
    }
}
