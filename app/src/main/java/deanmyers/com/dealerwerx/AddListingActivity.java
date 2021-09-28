package deanmyers.com.dealerwerx;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import deanmyers.com.dealerwerx.API.VinDecodeResult;

public class AddListingActivity extends TitleCompatActivity {

    private VinDecodeResult decodeResult = null;

    public VinDecodeResult getDecodeResult() {
        return decodeResult;
    }

    public void setDecodeResult(VinDecodeResult decodeResult) {
        this.decodeResult = decodeResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlisting);
        //doLoad(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        if(extras != null && extras.getBoolean("scavenger", false))
            setViewTitlePrimary("Add Scavenger Listing");
        else
        setViewTitlePrimary("Add Listing");


        Fragment fragment;

        if(PreferencesManager.getUserInformation().getPhoneNumber() == null){
            RequireMoreInformationFragment requireFragment = new RequireMoreInformationFragment();
            requireFragment.targetFragment = new AddListingFragment();
            fragment = requireFragment;
        }
        else
            fragment = new AddListingFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();


        if(extras != null)
            fragment.setArguments(extras);

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}
