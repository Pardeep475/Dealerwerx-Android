package deanmyers.com.dealerwerx.Views;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;

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
