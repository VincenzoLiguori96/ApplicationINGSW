package com.example.applicationingsw.adapters;

import android.app.Activity;
import android.content.Context;
import android.icu.util.Currency;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.applicationingsw.App;
import com.example.applicationingsw.R;
import com.example.applicationingsw.model.Cart;
import com.example.applicationingsw.model.Item;
import com.squareup.picasso.Picasso;

import java.util.Locale;


public class CartAdapter extends BaseAdapter {

    Context context;

    Cart cart;

    public CartAdapter(Context context, Cart cart) {


        this.context = context;
        this.cart = cart;
    }











    @Override
    public int getCount() {
        return cart.getItemsInCart().size();
    }

    @Override
    public Object getItem(int position) {
        return cart.getItemsInCart().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_cart_item_row_,null);
            viewHolder = new ViewHolder(convertView);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.itemImageCheckout);
            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            viewHolder.quantity = (TextView)convertView.findViewById(R.id.itemquantity);
            viewHolder.price = (TextView)convertView.findViewById(R.id.itemPrice);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Pair<Item,Integer> itemInCart = (Pair<Item,Integer>)getItem(position);
        Picasso.with(context).load(itemInCart.first.getUrl()).resize(217,217).into(viewHolder.image);
        viewHolder.title.setText(itemInCart.first.getName());
        float totalPrice = itemInCart.first.getPriceWithoutConcurrency() * itemInCart.second;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolder.price.setText(totalPrice+ Currency.getInstance(Locale.getDefault()).getSymbol());
        }
        else{
            viewHolder.price.setText(totalPrice+ "€");
        }
        viewHolder.quantity.setText(itemInCart.second.toString());


        return convertView;
    }









    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView quantity;
        TextView price;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}




