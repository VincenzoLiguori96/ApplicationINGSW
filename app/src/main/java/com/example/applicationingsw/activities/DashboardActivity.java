package com.example.applicationingsw.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.applicationingsw.R;
import com.example.applicationingsw.services.CartAccessService;
import com.example.applicationingsw.adapters.ItemsAdapter;
import com.example.applicationingsw.helpers.Space;
import com.example.applicationingsw.model.AWSItemDAO;
import com.example.applicationingsw.model.CognitoUserPoolShared;
import com.example.applicationingsw.model.Item;
import com.example.applicationingsw.model.ItemDAO;
import com.example.applicationingsw.model.NetworkOperationsListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ItemsAdapter.ItemsAdapterListener {
    private ItemsAdapter itemsAdapter;
    private ImageView menuImageView;
    private ImageView cartImageView;
    private ImageView filteredSearchImageView;
    private static final int REQUEST_FILTER = 0;
    private SwipeRefreshLayout refreshLayout;
    private DrawerLayout leftSideMenu;
    private List<Item> itemsList = new ArrayList<>();
    private SearchView searchView;
    private RecyclerView recyclerViewProducts;
    private ItemDAO itemDAO = new AWSItemDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        leftSideMenu =findViewById( R.id.drawer_layout);
        filteredSearchImageView = findViewById(R.id.filteredSearch);
        filteredSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openApplyFilterView();
            }
        });
        setNavigationViewListener();
        searchView = findViewById(R.id.action_search_dashboard);
        searchView.setBackgroundResource(R.drawable.rect_rounded_white);
        filteredSearchImageView = findViewById(R.id.filteredSearch);
        searchView.setSubmitButtonEnabled(false);
        refreshLayout = findViewById(R.id.dashboardRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        setSearchManager();
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
                goToCart();
            }
        });

        //Bind RecyclerView from layout to recyclerViewProducts object
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);

        //Create new GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                2,//span count no of items in single row
                GridLayoutManager.VERTICAL,//Orientation
                false);//reverse scrolling of recyclerview
        //set layout manager as gridLayoutManager
        recyclerViewProducts.setLayoutManager(gridLayoutManager);

        //to give loading item full single row
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (itemsAdapter.getItemViewType(position)) {
                    case ItemsAdapter.PRODUCT_ITEM:
                        return 1;
                    case ItemsAdapter.LOADING_ITEM:
                        return 2; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });
        itemsAdapter = new ItemsAdapter(this,this,itemsList);
        loadItemsData();
        //add on on Scroll listener
        //add space between cards
        //Create new itemsAdapter
        recyclerViewProducts.addItemDecoration(new Space(2, 20, true, 0));
        //Finally set the adapter
        recyclerViewProducts.setAdapter(itemsAdapter);


    }

    private void openApplyFilterView(){
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), ApplyFilterActivity.class);
        startActivityForResult(intent, REQUEST_FILTER);
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataBack) {
        if (requestCode == REQUEST_FILTER) {
            if (resultCode == RESULT_OK) {
                Bundle extras = dataBack.getExtras();
                Item prova = null;
                String query_string = extras.getString("EXTRA_QUERY_STRING");
                Log.e("DASHACT_API endpoint: ",query_string);
                itemsList.clear();
                itemsAdapter.notifyDataSetChanged();
                getItemsFiltered(query_string);
            }
        }
    }


    private void loadItemsData() {
        getItemsFromAPI();
    }


    public void refreshData(){
        itemsList.clear();
        getItemsFromAPI();
        itemsAdapter.addItems(itemsList);
        itemsAdapter.notifyDataSetChanged();
    }
    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.leftSideMenu);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void getItemsFiltered(String queryStringParameters){
        itemsAdapter.showLoading();
        itemDAO.readItemsWithFilter(new NetworkOperationsListener(){
            @Override
            public void getResult(Object object) {
                if(itemsAdapter.isLoading()){
                    itemsAdapter.hideLoading();
                }
                Item castedResult = (Item) object;
                itemsList.add(castedResult);
                itemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void getError(Object object) {
                if ( object instanceof JSONException){
                    JSONException exc = (JSONException) object;
                    Log.e("DASHBOARDACTIVITY", exc.getLocalizedMessage());
                }
                if ( object instanceof VolleyError){
                    VolleyError exc = (VolleyError) object;
                    manageNetworkingError(exc);
                }
                else{
                    if(refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                        itemsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFinish() {
                requestCompleted();
            }
        },queryStringParameters);
    }

    public void getItemsFromAPI(){
        itemsAdapter.showLoading();
        itemDAO.readAllItems(new NetworkOperationsListener() {
            @Override
            public void getResult(Object object) {
                if(itemsAdapter.isLoading()){
                    itemsAdapter.hideLoading();
                }
                Item castedResult = (Item) object;
                itemsList.add(castedResult);
                itemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void getError(Object object) {
                if ( object instanceof JSONException){
                    JSONException exc = (JSONException) object;
                    Log.e("DASHBOARDACTIVITY", exc.getLocalizedMessage());
                }
                if ( object instanceof VolleyError){
                    VolleyError exc = (VolleyError) object;
                    manageNetworkingError(exc);
                }
                else{
                    if(refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                        itemsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFinish() {
                requestCompleted();
            }
        });
        requestCompleted();
    }

    public void requestCompleted(){
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
            itemsAdapter.notifyDataSetChanged();
            itemsAdapter.hideLoading();
            recyclerViewProducts.setAdapter(itemsAdapter);
        }
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
                goToCart();
                return true;
            case R.id.nav_profile:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.nav_logout:
                CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().signOut();
                Intent turnToLoginPage = new Intent(this,LoginActivity.class);
                turnToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                this.startActivity(turnToLoginPage);
                return true;

        }
        return false;
    }

    public void manageNetworkingError(VolleyError error){
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
            itemsAdapter.notifyDataSetChanged();
        }
        itemsAdapter.showLoading();
        String message = null;
        if (error instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!";
        } else if (error instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ParseError) {
            message = "Parsing error! Please try again after some time!";
        } else if (error instanceof NoConnectionError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }
        else{
            message = "Unknown connection error. Please retry.";
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setTitle("An error occured");
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onItemSelected(Item item) {
        Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
        intent.putExtra("CurrentItem", item);
        startActivity(intent);
    }
    
    public void setSearchManager(){
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search an item...");

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("ERRORE", "QUERY: " + query);
                // filter recycler view when query submitted
                itemsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                itemsAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    public void goToCart(){
        CartAccessService.goToCart(getApplicationContext(),this);
    }
}
