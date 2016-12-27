package deanmyers.com.dealerwerx;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mac3 on 2016-11-21.
 */
public abstract class TitleCompatFragment extends Fragment {
    void setViewTitle(View container, String title){
        ((TextView)container.getRootView().findViewById(R.id.page_title)).setText(title);
    }
    public void setViewTitle(String title){
        ((TextView)getActivity().findViewById(R.id.page_title)).setText(title);
    }

    public void setViewTitlePrimary(View container, String title){
        ((TextView)container.getRootView().findViewById(R.id.page_title_primary)).setText(title);
    }
    public void setViewTitlePrimary(String title){
        ((TextView)getActivity().findViewById(R.id.page_title_primary)).setText(title);
    }
}
