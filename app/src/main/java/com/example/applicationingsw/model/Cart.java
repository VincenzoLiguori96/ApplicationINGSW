package com.example.applicationingsw.model;

import android.util.Log;
import android.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.applicationingsw.App;
import com.example.applicationingsw.PaymentMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static Cart instance;
    private int cartID = -1;
    private boolean isValid = false;

    /**
     * Items in cart is a pair of values of type (Item,Quantity) where Item is an object of item type and Quantity is the quantity of the related item in the cart.
     */
    private List<Pair<Item,Integer>> itemsInCart = new ArrayList<>();

    /**
     * The private constructor for the CartSingleton class
     */
    private Cart() {
        String endpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/getcart";
        getCartIDFromAPI(endpoint);
    }

    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        else{
            if(instance.getCartID() == -1){
                instance.getCartIDFromAPI("https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/getcart");
            }
        }
        return instance;
    }

    public void addItemInCart(Item anItem, Integer aQuantity){
        boolean alreadyInCart = false;
        for(Pair<Item,Integer> itemInCart : itemsInCart){
            if(itemInCart.first.getId()== anItem.getId()){
                alreadyInCart = true;
            }
        }
        if(alreadyInCart){
            updateItemInCart(anItem,aQuantity);
        }
        else{
            itemsInCart.add(Pair.create(anItem, aQuantity));
        }
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
                itemsInCart.set(i,Pair.create(anItem,newQuantity+itemsInCart.get(i).second));
                break;
            }
        }
    }
    public void pay(PaymentMethod method) {
        float totalCost = calculateTotalPrice();
        method.pay(totalCost);
    }

    /**
     * Do a POST request to the getCart endpoint and set the id cart. It's called by the constructor and on each getInstance if the previous request was failed.
     * @param endpoint : The endpoint of getCart resource.
     */
    public void getCartIDFromAPI(String endpoint){
        final RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().getUserId());
            final String requestBody = jsonBody.toString();
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responseBody = new JSONObject(response);
                        setCartID(responseBody.getInt("cartID"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setCartID(-1);
                    getCartIDFromAPI("https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/getcart");
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        Log.i("BODY", requestBody);
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }


                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    return super.parseNetworkResponse(response);
                }
            };
            requestQueue.add(stringRequest);
            requestQueue.start();

        } catch (JSONException e) {
            getCartIDFromAPI("https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/getcart");
        }
    }

    public JSONObject getCartAsJson(){
        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray arrayOfItems = new JSONArray();
            JSONObject singleItem = new JSONObject();
            for(Pair<Item,Integer>itemInCart : itemsInCart){
                singleItem.put("id", itemInCart.first.getId());
                singleItem.put("quantity", itemInCart.second);
                singleItem = new JSONObject();
                arrayOfItems.put(singleItem);
            }
            jsonBody.put("items", arrayOfItems);
            if(getCartID() != -1){
                jsonBody.put("cart", getCartID());
            }
            else{

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }
}

/*

        final JsonObjectRequest request = new JsonObjectRequest(endpoint, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject cartID = response.getJSONObject("cartID");
                    if(cartID.getInt("cartID") != -1){
                        setCartID(cartID.getInt("cartID"));
                    }
                } catch (JSONException e) {
                    Log.e("PORCA xception",e.getLocalizedMessage());
                }
                catch (Exception e){
                    Log.e("PORCA MADO",e.toString(),e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setCartID(-1);
            }
        });
 */

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