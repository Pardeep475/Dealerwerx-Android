package deanmyers.com.dealerwerx;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import deanmyers.com.dealerwerx.API.VinDecodeResult;

public class RequireMoreInformationActivity extends TitleCompatActivity {

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
        setResult(0);
        RequireMoreInformationFragment fragment = new RequireMoreInformationFragment();
        fragment.targetFragment = null;

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}
