package com.example.applicationingsw;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.applicationingsw.adapters.ItemsAdapter;
import com.example.applicationingsw.helpers.Space;
import com.example.applicationingsw.model.Item;
import com.jaygoo.widget.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ItemsAdapter.ItemsAdapterListener {
    private ItemsAdapter itemsAdapter;
    private ImageView menuImageView;
    private ImageView cartImageView;
    private ImageView filteredSearchImageView;
    private SwipeRefreshLayout refreshLayout;
    private DrawerLayout leftSideMenu;
    private List<Item> itemsList = new ArrayList<>();
    private SearchView searchView;
    private RecyclerView recyclerViewProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        /*
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
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
                //TODO: vai al carrello
                Toast.makeText(DashboardActivity.this, "Go to cart clicked", Toast.LENGTH_LONG).show();
            }
        });

        //Bind RecyclerView from layout to recyclerViewProducts object
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);

        //Create new itemsAdapter
        itemsAdapter = new ItemsAdapter(this,this);
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
        loadItemsData();
        //add on on Scroll listener
        //add space between cards
        recyclerViewProducts.addItemDecoration(new Space(2, 20, true, 0));
        //Finally set the adapter
        recyclerViewProducts.setAdapter(itemsAdapter);

    }

    private void openApplyFilterView(){
        Intent myIntent = new Intent(DashboardActivity.this, ApplyFilterActivity.class);
        startActivity(myIntent);
    }
    //Load Data from your server here
    // loading data from server will make it very large
    // that's why i created data locally
    private void loadItemsData() {
        //show loading in recyclerview
        itemsAdapter.showLoading();
        getItemsFromAPI();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //hide loading
                itemsAdapter.hideLoading();
                //add products to recyclerview
                itemsAdapter.addItems(itemsList);
            }
        }, 3000);
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

    public void getItemsFromAPI(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String apiItemEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/items";
        final JsonObjectRequest request = new JsonObjectRequest(apiItemEndpoint, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArrayOfItems = response.getJSONArray("items");
                    for(int i = 0; i< jsonArrayOfItems.length();i++){
                        JSONObject item = jsonArrayOfItems.getJSONObject(i);
                        int id = item.getInt("id");
                        String name = item.getString("name");
                        String manufacturer = item.getString("manufacturer");
                        float price = new Float(item.getDouble("price"));
                        String description = item.getString("description");
                        String imageUrl = item.getString("url");
                        String category = item.getString("category");
                        Integer quantity = item.getInt("quantity");
                        ArrayList<String> tags = new ArrayList<String>();
                        JSONArray jsonArray = item.getJSONArray("tags");
                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            for (int j=0;j<len;j++){
                                tags.add(jsonArray.getString(j));
                            }
                        }
                        Item currentItem = new Item(id,name,manufacturer,price,description,quantity,imageUrl,category,tags);
                        currentItem.setNew(true);
                        itemsList.add(currentItem);
                        for (Item it : itemsList){
                            Log.e("ITEM", it.toString());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    requestCompleted();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }

    public void requestCompleted(){
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
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

    @Override
    public void onItemSelected(Item item) {
        //TODO vai alla schermata dettaglio
        Toast.makeText(getApplicationContext(), "Selected: " + item.getName() + ", " + item.getCategory(), Toast.LENGTH_LONG).show();
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
}
