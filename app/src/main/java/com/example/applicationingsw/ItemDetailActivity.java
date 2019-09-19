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
                //TODO: vai al carrello
                Toast.makeText(ItemDetailActivity.this, "Go to cart clicked", Toast.LENGTH_LONG).show();
            }
        });
        addToCartImageView = findViewById(R.id.addToCartRapidButton);
        addToCartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Aggiungi al Carrello
            }
        });
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
                //TODO Aggiungi al Carrello
                Toast.makeText(ItemDetailActivity.this, "Add to cart clicked", Toast.LENGTH_LONG).show();
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

    public void openLeftMenu(){
        leftSideMenu.openDrawer(Gravity.START);
    }
}
