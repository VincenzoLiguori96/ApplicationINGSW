package com.example.applicationingsw.model;

import android.util.Log;
import android.support.v4.util.Pair;

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
import com.example.applicationingsw.services.PaymentMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static Cart instance;
    private int cartID = -1;
    private static String cartIDEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/getcart";


    /**
     * Items in cart is a pair of values of type (Item,Quantity) where Item is an object of item type and Quantity is the quantity of the related item in the cart.
     */
    private List<Pair<Item,Integer>> itemsInCart = new ArrayList<>();

    /**
     * The private constructor for the CartSingleton class
     */
    private Cart() {
    }

    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
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

    public float calculateTotalPrice(){
        float amount = 0;
        for(Pair<Item,Integer> itemInCart : itemsInCart){
            amount = amount + (itemInCart.first.getPriceWithoutConcurrency() * itemInCart.second);
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
                    getCartIDFromAPI(cartIDEndpoint);
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
            getCartIDFromAPI(cartIDEndpoint);
        }
    }

    public void clearCart(){
        itemsInCart.clear();
    }

    public JSONObject getCartAsJson(){
        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray arrayOfItems = new JSONArray();
            JSONObject singleItem = new JSONObject();
            for(Pair<Item,Integer>itemInCart : itemsInCart){
                singleItem.put("id", itemInCart.first.getId());
                singleItem.put("quantity", itemInCart.second);
                arrayOfItems.put(singleItem);
                singleItem = new JSONObject();
            }
            jsonBody.put("items", arrayOfItems);
            if(getCartID() == -1){
                getCartIDFromAPI(cartIDEndpoint);
            }
            jsonBody.put("cart", getCartID());
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
