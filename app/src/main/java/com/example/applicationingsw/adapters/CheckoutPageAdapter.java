package com.example.applicationingsw.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.applicationingsw.ShippingTabFragment;
import com.example.applicationingsw.SummaryTabFragment;

public class CheckoutPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public CheckoutPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ShippingTabFragment tab1 = new ShippingTabFragment();
                return tab1;
            case 1:
                SummaryTabFragment tab2 = new SummaryTabFragment();
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