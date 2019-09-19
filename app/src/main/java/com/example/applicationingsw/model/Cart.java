package com.example.applicationingsw.model;

import android.content.res.Resources;
import android.util.Pair;

import com.example.applicationingsw.R;

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

    public List<Pair<Item,Integer>> getItemsInCart(){
        return itemsInCart;
    }

    public void deleteItemFromCart(Item anItem){
        Integer relatedQuantity = null;
        for (Pair itemsInCart : itemsInCart){
            if(itemsInCart.first.equals(anItem)){
                relatedQuantity = (Integer) itemsInCart.second;
            }
        }
        if(relatedQuantity!=null){
            itemsInCart.remove(Pair.create(anItem, relatedQuantity));
        }
    }

    public float calculateTotalPrice(){
        float amount = 0;
        for(Pair<Item,Integer> itemInCart : itemsInCart){
            amount += itemInCart.first.getPriceWithoutConcurrency() * itemInCart.second;
        }
        return amount;
    }

    public void updateItemInCart(Item anItem,Integer newQuantity){
        for(int i = 0; i<itemsInCart.size(); i++){
            if(itemsInCart.get(i).first.equals(anItem)){
                itemsInCart.set(i,Pair.create(anItem,newQuantity));
                break;
            }
        }
    }
}



/*
utilizzo
public void testCart(){
        Cart cart = Cart.getInstance();
        for(Item i : itemsList){
            Log.e("AGGIUNGO","" + i.getName());
            cart.addItemInCart(i, 1);
        }
        for(Pair<Item,Integer> coppia : cart.getPair()){
            Log.e("VEDO COME è ORA: ", ""+coppia.first.toString() + coppia.second.toString());
        }
        Log.e("CANCELLO", itemsList.get(12).getName());
        cart.deleteItemFromCart(itemsList.get(12));
        for(Pair<Item,Integer> coppia : cart.getPair()){
            Log.e("VEDO COME è dopo: ", ""+ coppia.first.toString() + coppia.second.toString());
        }
    }
 */