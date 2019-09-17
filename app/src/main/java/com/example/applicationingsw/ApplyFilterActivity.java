package com.example.applicationingsw;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.jaygoo.widget.RangeSeekBar;

public class ApplyFilterActivity extends Activity {
    RangeSeekBar priceRange ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_filters);
        setLayout();
        priceRange = findViewById(R.id.priceRange);
        priceRange.getRightSeekBar().setIndicatorTextDecimalFormat("0.00");
        priceRange.getLeftSeekBar().setIndicatorTextDecimalFormat("0.00");
        priceRange.setProgress(999.99f);
        priceRange.setProgress(0,999.99f);
    }



    private void setLayout(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        getWindow().setLayout((int)Math.round(width *.8),(int)Math.round(height *.65));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 40;
        getWindow().setAttributes(params);
    }
}
