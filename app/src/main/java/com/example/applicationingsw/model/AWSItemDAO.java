package com.example.applicationingsw.model;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.applicationingsw.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AWSItemDAO implements ItemDAO {
    private String apiItemEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/items";

    /**
     * This method has to get all the Items from the AWS API. It's done by a Volley Request that is managed by the Listener passed as parameter.
     * @param networkOperationsListener This parameter is an object that implements the NetworkOperaitonsListener interface. This interface has two methods, get error and get result that are called on Volley network response. The method of this parameters could be implemented as needed.
     * */
    @Override
    public void readAllItems(final NetworkOperationsListener networkOperationsListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
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
                        currentItem.setNew(new Random().nextBoolean());
                        networkOperationsListener.getResult(currentItem);
                    }
                    networkOperationsListener.onFinish();

                } catch (JSONException e) {
                    Log.e("Networking json xcept",e.getLocalizedMessage());
                    networkOperationsListener.getError(e);
                }
                catch (Exception e){
                    Log.e("generic netw exc",e.toString(),e);
                    networkOperationsListener.getError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkOperationsListener.getError(error);
            }
        });
        requestQueue.add(request);
    }

    /**
     * This method has to get the Items from the AWS API based on the query string parameter. It's done by a Volley Request that is managed by the Listener passed as parameter.
     * @param networkOperationsListener This parameter is an object that implements the NetworkOperaitonsListener interface. This interface has two methods, get error and get result that are called on Volley network response. The method of this parameters could be implemented as needed.
     * @param filterParameters This parameter is a String that has to be a correct query string parameter for the Aws endpoint /items. See the API documentation for the use of the query string parameter.
     * */
    @Override
    public void readItemsWithFilter(final NetworkOperationsListener networkOperationsListener, String filterParameters) {
        RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
        final JsonObjectRequest request = new JsonObjectRequest(apiItemEndpoint+filterParameters, null, new Response.Listener<JSONObject>() {
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
                        currentItem.setNew(new Random().nextBoolean());
                        networkOperationsListener.getResult(currentItem);
                    }
                    networkOperationsListener.onFinish();
                } catch (JSONException e) {
                    Log.e("json netw xception",e.getLocalizedMessage());
                    networkOperationsListener.getError(e);
                }
                catch (Exception e){
                    Log.e("netw generic exc",e.toString(),e);
                    networkOperationsListener.getError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkOperationsListener.getError(error);
            }
        });
        requestQueue.add(request);
    }

    public String getApiItemEndpoint(){
        return apiItemEndpoint;
    }
}
