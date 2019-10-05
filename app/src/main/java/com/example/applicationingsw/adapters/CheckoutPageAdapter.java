package com.example.applicationingsw.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.applicationingsw.activities.ShippingTabFragment;
import com.example.applicationingsw.activities.SummaryTabFragment;

public class CheckoutPageAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private SummaryTabFragment tab1;
    private ShippingTabFragment  tab2;

    public CheckoutPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        tab1 = new SummaryTabFragment();
        tab2 = new ShippingTabFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return tab1;
            case 1:
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}