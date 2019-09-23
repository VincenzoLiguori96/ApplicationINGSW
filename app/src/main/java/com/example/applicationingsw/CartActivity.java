package com.example.applicationingsw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.example.applicationingsw.Services.PaypalConfigurationService;
import com.example.applicationingsw.adapters.CheckoutPageAdapter;
import com.example.applicationingsw.model.Customer;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class CartActivity extends AppCompatActivity implements ShippingTabFragment.SendCustomer {
    private Button buyButton;
    private ViewPager viewPager;
    private CheckoutPageAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("CART SUMMARY"));
        tabLayout.addTab(tabLayout.newTab().setText("SHIPPING DETAILS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new CheckoutPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void send(Customer aCustomer,int toPage) {
        if(toPage == 1){
            ShippingTabFragment f = (ShippingTabFragment) adapter.getItem(toPage);
            f.displayPassedShippingInfo(aCustomer);
        }
        else{
            SummaryTabFragment f = (SummaryTabFragment) adapter.getItem(toPage);
            f.displayShippingInfo(aCustomer);
        }
    }

    public void updateData(Customer aCustomer) {
        SummaryTabFragment f = (SummaryTabFragment) adapter.getItem(1);
        f.displayShippingInfo(aCustomer);
    }

    public void changeTab(int destinationTab){
        viewPager.setCurrentItem(destinationTab);
    }
}

