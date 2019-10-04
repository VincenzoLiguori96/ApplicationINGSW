package com.example.applicationingsw;


import com.example.applicationingsw.model.Cart;
import com.example.applicationingsw.model.Item;

import org.junit.Test;
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
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void calculateTotalPriceOfItemsInCartTest(){
        Cart c = Cart.getInstance();
        Item first = new Item(1,"First","Manufacturer1",34.5f,"Desccription1",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category1",new ArrayList<java.lang.String>());
        Item second = new Item(2,"2nd","Manufacturer2",314.5f,"Desccription2",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category2",new ArrayList<java.lang.String>());
        Item third = new Item(3,"3rd","Manufacturer3",4.5f,"Desccription3",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category3",new ArrayList<java.lang.String>());
        Item fourth = new Item(4,"4th","Manufacturer4",0.09f,"Desccription4",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category4",new ArrayList<java.lang.String>());
        Item fifth = new Item(5,"5th","Manufacturer5",27.322f,"Desccription5",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category5",new ArrayList<java.lang.String>());
        c.addItemInCart(first,1);
        assertEquals(34.5f,c.calculateTotalPrice(),0.009);
    }

    @Test
    public void testAddItem(){
        Cart c = Cart.getInstance();
        Item first = new Item(1,"First","Manufacturer1",34.5f,"Desccription1",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category1",new ArrayList<java.lang.String>());
        c.addItemInCart(first,1);
        assertEquals(1,c.getItemsInCart().size());
    }

}