package deanmyers.project.dealerwerx;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import deanmyers.project.dealerwerx.API.VinDecodeResult;

public class UpdateListingActivity extends TitleCompatActivity {

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
            setViewTitlePrimary("Update Scavenger Listing");
        else
        setViewTitlePrimary("Update Listing");


        Fragment fragment = new UpdateListingFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();


        if(extras != null)
            fragment.setArguments(extras);

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}
