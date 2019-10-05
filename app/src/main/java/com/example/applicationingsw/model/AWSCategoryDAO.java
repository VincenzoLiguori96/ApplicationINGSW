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
import java.util.Random;

public class AWSCategoryDAO implements CategoryDAO {
    String apiCategoriesEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/categories/allcategories";

    /**
     * This method has to get all the Items from the AWS API. It's done by a Volley Request that is managed by the Listener passed as parameter.
     * @param networkOperationsListener This parameter is an object that implements the NetworkOperaitonsListener interface. This interface has two methods, get error and get result that are called on Volley network response. The method of this parameters could be implemented as needed.
     * */
    @Override
    public void readAllCategories(final NetworkOperationsListener networkOperationsListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
        final JsonObjectRequest request = new JsonObjectRequest(apiCategoriesEndpoint, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArrayOfCategories = response.getJSONArray("elem");
                    for(int i = 0; i< jsonArrayOfCategories.length();i++){
                        JSONObject item = jsonArrayOfCategories.getJSONObject(i);
                        String name = item.getString("name");
                        networkOperationsListener.getResult(name);
                    }
                    networkOperationsListener.onFinish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkOperationsListener.getError("");
            }
        });
        requestQueue.add(request);
    }

}
