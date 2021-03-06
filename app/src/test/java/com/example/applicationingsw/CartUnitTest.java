package com.example.applicationingsw;


import com.example.applicationingsw.model.Cart;
import com.example.applicationingsw.model.Item;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CartUnitTest {
    //Fake items for testing purposes.
    Item first = new Item(1,"First","Manufacturer1",34.5f,"Description1",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category1",new ArrayList<java.lang.String>());
    Item second = new Item(2,"2nd","Manufacturer2",314.5f,"Description2",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category2",new ArrayList<java.lang.String>());
    Item third = new Item(3,"3rd","Manufacturer3",4.5f,"Description3",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category3",new ArrayList<java.lang.String>());
    Item fourth = new Item(4,"4th","Manufacturer4",0.09f,"Description4",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category4",new ArrayList<java.lang.String>());
    Item fifth = new Item(5,"5th","Manufacturer5",27.322f,"Description5",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category5",new ArrayList<java.lang.String>());
    Item sixth = new Item(6,"6th","Manufacturer6",0.0001f,"Description6",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category6",new ArrayList<java.lang.String>());
    Item seventh = new Item(7,"7th","Manufacturer7",1.01f,"Description7",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category7",new ArrayList<java.lang.String>());
    Item eighth = new Item(8,"8th","Manufacturer8",0.009f,"Description8",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category8",new ArrayList<java.lang.String>());
    Item ninth = new Item(9,"9th","Manufacturer9",0f,"Description9",10,"https://s3-eu-west-1.amazonaws.com/image.repository.progettoingegneria/items/1920x1080-light-blue-solid-color-background.jpg","Category9",new ArrayList<java.lang.String>());


    /**
     * This test is done before all the others because it's a prerequisite for the others. Check if the method clearCart effectively removes all the items in the cart.
     * It does a double check both when there are no items in the cart and after adding some fakes.
     */
    @Before
    @Test
    public void testClearCartReturnZeroItems(){
        Cart c = Cart.getInstance();
        c.clearCart();
        assertEquals(0,c.getItemsInCart().size());
        c.addItemInCart(first,1,true);
        c.addItemInCart(second, 2,true);
        c.addItemInCart(third, 1,true);
        c.addItemInCart(fourth, 3,true);
        c.addItemInCart(fifth, 2,true);
        c.addItemInCart(sixth, 15,true);
        c.addItemInCart(seventh, 9,true);
        c.addItemInCart(eighth, 2,true);
        c.addItemInCart(ninth, 102,true);
        c.clearCart();
        assertTrue(c.getItemsInCart().size() == 0);
    }

    /**
     * This test check if the addItemInCart method works properly.
     * Note that the the final result has to be the number of different items in the cart, without considering the quantity of each one.
     */
    @Test
    public void addItemTest(){
        Cart c = Cart.getInstance();
        c.clearCart();
        c.addItemInCart(first,1,true);
        c.addItemInCart(second, 1,true);
        c.addItemInCart(third, 1,true);
        c.addItemInCart(fourth, 1,true);
        c.addItemInCart(fifth, 1,true);
        c.addItemInCart(sixth, 1,true);
        c.addItemInCart(seventh, 1,true);
        c.addItemInCart(eighth, 1,true);
        c.addItemInCart(ninth, 1,true);
        assertEquals(9, c.getItemsInCart().size());
    }

    /**
     * This test create some fake items and add them in the cart in various quantities with various price even 0s or less than 0.01 to check the stability of the final price considering a delta of 0.009.
     * It has as prerequisite the validity of clearCart and addItemInCart methods that are tested before.
     */
    @Test
    public void calculateTotalPriceOfItemsInCartTest(){
        Cart c = Cart.getInstance();
        c.clearCart();
        c.addItemInCart(first,1,true);
        c.addItemInCart(second, 2,true);
        c.addItemInCart(third, 1,true);
        c.addItemInCart(fourth, 3,true);
        c.addItemInCart(fifth, 2,true);
        c.addItemInCart(sixth, 15,true);
        c.addItemInCart(seventh, 9,true);
        c.addItemInCart(eighth, 2,true);
        c.addItemInCart(ninth, 102,true);
        assertEquals(732.02f,c.calculateTotalPrice(),0.009);
    }


    /**
     * This test create some fake items and add them in the cart and then check if the new quantity of that item is equal to the sum of the previous one and the newly added one.
     */
    @Test
    public void testUpdateItemInCart(){
        Cart c = Cart.getInstance();
        c.clearCart();
        c.addItemInCart(first,1,true);
        c.updateItemInCart(first, 25,true);
        int newQuantity = c.getItemsInCart().get(0).second;
        assertEquals(26,newQuantity);
        c.updateItemInCart(first, 0,true);
        newQuantity = c.getItemsInCart().get(0).second;
        assertEquals(26,newQuantity);
    }





}