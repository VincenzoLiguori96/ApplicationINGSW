package com.example.applicationingsw.model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static Cart instance;

    /**
     * Items in cart is a pair of values of type (Item,Quantity) where Item is an object of item type and Quantity is the quantity of the related item in the cart.
     */
    private List<Pair<Item,Integer>> itemsInCart = new ArrayList<>();

    /**
     * The private constructor for the CartSingleton class
     */
    private Cart() {}

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void addItemInCart(Item anItem, Integer aQuantity){
        itemsInCart.add(Pair.create(anItem, aQuantity));
    }

    public void deleteItemFromCart(Item anItem){
        Integer relatedQuantity = 0;
        for (Pair itemsInCart : itemsInCart){
            if(itemsInCart.first.equals(anItem)){
                relatedQuantity = (Integer) itemsInCart.second;
            }
        }
        itemsInCart.remove(Pair.create(anItem, relatedQuantity));
    }

    public List<Pair<Item,Integer>> getPair(){
        return itemsInCart;
    }

}
