package com.example.applicationingsw.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.applicationingsw.CartActivity;
public class CartAccessService {
    public static void goToCart(Context fromContext, Activity fromActivity){
        Intent i = new Intent(fromContext, CartActivity.class);
        fromActivity.startActivity(i);
    }
}
