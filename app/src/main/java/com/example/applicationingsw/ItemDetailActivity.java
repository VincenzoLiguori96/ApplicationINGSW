package com.example.applicationingsw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationingsw.Services.CartAccessService;
import com.example.applicationingsw.model.Cart;
import com.example.applicationingsw.model.Item;
import com.squareup.picasso.Picasso;

public class ItemDetailActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener{

    private ImageView menuImageView;
    private ImageView addToCartImageView;
    private ImageView minusImageView;
    private ImageView plusImageView;
    private ImageView cartImageView;
    private ImageView productImage;
    private DrawerLayout leftSideMenu;
    private TextView nameTextView;
    private TextView priceTextView;
    private TextView descriptionTextView;
    private TextView manufacturerTextView;
    private TextView quantityTextView;
    private TextView availabilityTextView;
    private LinearLayout addToCart ;
    private Item currentItem;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        leftSideMenu =findViewById( R.id.drawer_layout);
        setNavigationViewListener();
        Intent intent = getIntent();
        currentItem = intent.getParcelableExtra("CurrentItem");
        //TODO creare una classe statica in cui mettere questi metodi di inizializzazione del menu a toolbar?
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
        addToCartImageView = findViewById(R.id.addToCartRapidButton);
        addToCartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(currentItem);
            }
        });
        availabilityTextView = findViewById(R.id.availabilityLabel);
        if(currentItem.getQuantity()>=1){
            availabilityTextView.setText("Available");
        }
        else{
            availabilityTextView.setText("Unavailable");
            availabilityTextView.setTextColor(getResources().getColor(R.color.red));
        }
        productImage = findViewById(R.id.productimage);
        Picasso.with(this).load(currentItem.getUrl()).into(productImage);
        priceTextView = findViewById(R.id.priceTextView);
        priceTextView.setText(currentItem.getPriceWithConcurrency());
        nameTextView = findViewById(R.id.productName);
        nameTextView.setText(currentItem.getName());
        descriptionTextView = findViewById(R.id.productDescription);
        descriptionTextView.setText(currentItem.getDescription());
        manufacturerTextView = findViewById(R.id.manufacturerLabel);
        manufacturerTextView.setText(currentItem.getManufacturer());
        quantityTextView = findViewById(R.id.quantityNumber);
        quantityTextView.setText("1");
        minusImageView = findViewById(R.id.minus);
        plusImageView = findViewById(R.id.plus);
        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuantity();
            }
        });
        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity();
            }
        });
        addToCart = findViewById(R.id.addToCart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(currentItem);
            }
        });

    }


    public void increaseQuantity(){
        int quantity = Integer.parseInt(quantityTextView.getText().toString());
        quantityTextView.setText(String.valueOf(quantity+1));

    }


    public void decreaseQuantity(){
        int quantity = Integer.parseInt(quantityTextView.getText().toString());
        if(quantity >0){
            quantityTextView.setText(String.valueOf(quantity-1));
        }
    }


    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.leftSideMenu);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                finish();
                return true;
            case R.id.nav_cart:
                CartAccessService.goToCart(getApplicationContext(),this);
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

    public void addToCart(Item itemToAdd){
        Toast.makeText(getApplicationContext(),itemToAdd.getName() + " added", Toast.LENGTH_SHORT).show();
        int quantity = Integer.valueOf(quantityTextView.getText().toString());
        Cart.getInstance().addItemInCart(itemToAdd,quantity);
    }

    public void goToCart(){
        CartAccessService.goToCart(getApplicationContext(),this);
    }
    public void openLeftMenu(){
        leftSideMenu.openDrawer(Gravity.START);
    }
}
