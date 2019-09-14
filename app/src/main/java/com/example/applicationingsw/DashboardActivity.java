package com.example.applicationingsw;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.applicationingsw.adapters.ItemsAdapter;
import com.example.applicationingsw.helpers.AdaptiveScrollListener;
import com.example.applicationingsw.helpers.Space;
import com.example.applicationingsw.model.Item;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ItemsAdapter productsAdapter;
    private ImageView menuImageView;
    private ImageView cartImageView;
    private DrawerLayout leftSideMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        /*
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        leftSideMenu =findViewById( R.id.drawer_layout);
        setNavigationViewListener();
        menuImageView = findViewById(R.id.leftMenuImage);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLeftMenu();
            }
        });
        cartImageView = findViewById(R.id.cartImage);
        cartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: vai al carrello
                Toast.makeText(DashboardActivity.this, "Go to cart clicked", Toast.LENGTH_LONG).show();
            }
        });

        //Bind RecyclerView from layout to recyclerViewProducts object
        RecyclerView recyclerViewProducts = findViewById(R.id.recyclerViewProducts);

        //Create new ProductsAdapter
        productsAdapter = new ItemsAdapter(this);
        //Create new GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                2,//span count no of items in single row
                GridLayoutManager.VERTICAL,//Orientation
                false);//reverse scrolling of recyclerview
        //set layout manager as gridLayoutManager
        recyclerViewProducts.setLayoutManager(gridLayoutManager);

        //Crete new EndlessScrollListener fo endless recyclerview loading
        AdaptiveScrollListener endlessScrollListener = new AdaptiveScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!productsAdapter.loading)
                    feedData();
            }
        };
        //to give loading item full single row
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (productsAdapter.getItemViewType(position)) {
                    case ItemsAdapter.PRODUCT_ITEM:
                        return 1;
                    case ItemsAdapter.LOADING_ITEM:
                        return 2; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });
        //add on on Scroll listener
        recyclerViewProducts.addOnScrollListener(endlessScrollListener);
        //add space between cards
        recyclerViewProducts.addItemDecoration(new Space(2, 20, true, 0));
        //Finally set the adapter
        recyclerViewProducts.setAdapter(productsAdapter);
        //load first page of recyclerview
        endlessScrollListener.onLoadMore(0, 0);
    }

    //Load Data from your server here
    // loading data from server will make it very large
    // that's why i created data locally
    private void feedData() {
        //show loading in recyclerview
        productsAdapter.showLoading();
        final List<Item> products = new ArrayList<>();
        int[] imageUrls = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4};
        String[] ProductName = {"Kingsmon Top", "Adidas Top", "Butterfly Top", "White Top"};
        Float[] ProductPrice = {Float.valueOf(594), Float.valueOf(5000), Float.valueOf(20), Float.valueOf(1999)};
        boolean[] isNew = {true, false, false, true};
        for (int i = 0; i < imageUrls.length; i++) {
            Item product = new Item(1, ProductName[i],"Nessuno", ProductPrice[i].floatValue(), "Articolo",3,"",null);
            products.add(product);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //hide loading
                productsAdapter.hideLoading();
                //add products to recyclerview
                productsAdapter.addItems(products);
            }
        }, 2000);

    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.leftSideMenu);
        navigationView.setNavigationItemSelectedListener(this);
    }



    public void openLeftMenu(){
        leftSideMenu.openDrawer(Gravity.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                leftSideMenu.closeDrawer(Gravity.START);
                return true;
            case R.id.nav_cart:
                //TODO vai al carrello
                return true;
            case R.id.nav_categories:
                //TODO vai alla ricerca per categorie
                return true;
            case R.id.nav_profile:
                //TODO vai al profilo
                return true;
            case R.id.nav_logout:
                //TODO esci e cancella i dati di aws
                return true;

        }
        return false;
    }
}
