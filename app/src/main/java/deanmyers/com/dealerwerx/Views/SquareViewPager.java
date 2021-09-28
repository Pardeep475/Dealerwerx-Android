package deanmyers.com.dealerwerx.Views;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

/**
 * Created by mac3 on 2016-11-25.
 */

public class SquareViewPager extends ViewPager {

    public SquareViewPager(Context context){
        super(context);
    }

    public SquareViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
