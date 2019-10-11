package com.example.applicationingsw.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.support.v4.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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
import java.util.Observable;
import java.util.Observer;

public class Cart extends Observable {
    private static Cart instance;
    private int cartID = -1;
    private boolean inSyncWithServer = false;
    private static String getLastCartEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/getlastcart";
    private static String addToCartCartEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/addtocart";
    private static String deleteFromCartEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/deletefromcart";




    /**
     * Items in cart is a pair of values of type (Item,Quantity) where Item is an object of item type and Quantity is the quantity of the related item in the cart.
     */
    private List<Pair<Item,Integer>> itemsInCart = new ArrayList<>();

    /**
     * The private constructor for the CartSingleton class
     */
    private Cart() {
        getCartIDFromAPI(getLastCartEndpoint);
    }

    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    /**
     * This method add a number of copy of an item to cart.
     * @param anItem The item to add
     * @param aQuantity The quantity of item to add
     * @param fromServer This parameters specifies if the item and the quantity are taken from the Server or added locally. If it's false, the cart is updated also on the server.
     */
    public void addItemInCart(Item anItem, Integer aQuantity,boolean fromServer){
        if(!fromServer){
            postItemOnServer(new Pair<Item, Integer>(anItem,aQuantity));
        }
        boolean alreadyInCart = false;
        for(Pair<Item,Integer> itemInCart : itemsInCart){
            if(itemInCart.first.getId()== anItem.getId()){
                alreadyInCart = true;
            }
        }
        if(alreadyInCart){
            updateItemInCart(anItem,aQuantity,fromServer);
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


    public void updateItemInCart(Item anItem,Integer newQuantity,boolean fromServer){
        if(!fromServer){
            postItemOnServer(new Pair<Item, Integer>(anItem,newQuantity));
        }
        for(int i = 0; i<itemsInCart.size(); i++){
            if(itemsInCart.get(i).first.equals(anItem)){
                itemsInCart.set(i,Pair.create(anItem,newQuantity+itemsInCart.get(i).second));
                break;
            }
        }
    }

    public void deleteItemFromCart(Item anItem){
        for(int i = 0; i<itemsInCart.size(); i++){
            if(itemsInCart.get(i).first.equals(anItem)){
                deleteFromServer(itemsInCart.get(i));
                itemsInCart.remove(i);
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
                        Log.e("Response",response);
                        JSONObject responseBody = new JSONObject(response);
                        boolean success = responseBody.getBoolean("success");
                        if(success){
                            int idCart = responseBody.getInt("cart");
                            setCartID(idCart);
                            Log.e("CART",String.valueOf(idCart));
                            JSONArray jsonArrayOfItems = responseBody.getJSONArray("items");
                            for(int i = 0; i< jsonArrayOfItems.length();i++){
                                JSONObject item = jsonArrayOfItems.getJSONObject(i);
                                int id = item.getInt("id");
                                String name = item.getString("name");
                                String manufacturer = item.getString("manufacturer");
                                float price = new Float(item.getDouble("price"));
                                String description = item.getString("description");
                                String imageUrl = item.getString("url");
                                Integer quantity = item.getInt("quantity");
                                ArrayList<String> tags = new ArrayList<String>();
                                Item currentItem = new Item(id,name,manufacturer,price,description,quantity,imageUrl,"",tags);
                                addItemInCart(currentItem,currentItem.getQuantity(),true);
                                Log.e("CART",currentItem.toString());
                            }
                            inSyncWithServer = true;
                            setChanged();
                            notifyObservers();
                        }
                        else{
                            inSyncWithServer = true;
                            setChanged();
                            notifyObservers();
                            setCartID(-1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        getCartIDFromAPI(getLastCartEndpoint);
                        Log.e("CARTcazzo",e.getLocalizedMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setCartID(-1);
                    inSyncWithServer = true;
                    setChanged();
                    notifyObservers();
                    Log.e("CART","Errore" + error.toString()+ error.getLocalizedMessage());
                    //getCartIDFromAPI(getLastCartEndpoint);
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
            stringRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 25000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 10;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                    Log.e("CART","RETRY" + error.getMessage());
                }
            });
            requestQueue.add(stringRequest);
            requestQueue.start();

        } catch (JSONException e) {
            getCartIDFromAPI(getLastCartEndpoint);
        }
    }

    public void clearCart(){
        itemsInCart.clear();
    }

   /* public JSONObject getCartAsJson(){
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
    }*/

    public void postItemOnServer(final Pair<Item,Integer> toAdd){
        final RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("itemId", toAdd.first.getId());
            jsonBody.put("cartId", getCartID());
            jsonBody.put("quantity", toAdd.second);
            final String requestBody = jsonBody.toString();
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, addToCartCartEndpoint, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responseBody = new JSONObject(response);
                        boolean success = responseBody.getBoolean("success");
                        if(!success){
                            new AlertDialog.Builder(App.getAppContext())
                                    .setTitle("We're running some issues.")
                                    .setMessage("Please retry.")
                                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            Log.e("CART",responseBody.getString("errorInfo"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("CART",e.getLocalizedMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setCartID(-1);
                    Log.e("CART","Errore" + error.toString()+ error.getLocalizedMessage());
                    postItemOnServer(toAdd);
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
            postItemOnServer(toAdd);
        }
    }

    public void deleteFromServer(final Pair<Item,Integer> toRemove){
        final RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("item", toRemove.first.getId());
            jsonBody.put("cart", getCartID());
            final String requestBody = jsonBody.toString();
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteFromCartEndpoint, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responseBody = new JSONObject(response);
                        boolean success = responseBody.getBoolean("success");
                        if(!success){

                            new AlertDialog.Builder(App.getAppContext())
                                    .setTitle("We're running some issues.")
                                    .setMessage("Please retry.")
                                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            addItemInCart(toRemove.first,toRemove.second,true);
                            Log.e("CART",responseBody.getString("errorInfo"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        addItemInCart(toRemove.first,toRemove.second,true);
                        Log.e("CART",e.getLocalizedMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setCartID(-1);
                    Log.e("CART","Errore" + error.toString()+ error.getLocalizedMessage());
                    deleteFromServer(toRemove);
                    addItemInCart(toRemove.first,toRemove.second,true);
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
            deleteFromServer(toRemove);
        }
    }

    public int getCartID() {
        return cartID;
    }

    public static String getGetLastCartEndpoint() {
        return getLastCartEndpoint;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }

    public boolean isInSyncWithServer() {
        return inSyncWithServer;
    }
}
