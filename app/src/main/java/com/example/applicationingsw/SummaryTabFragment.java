package com.example.applicationingsw;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.applicationingsw.adapters.CartAdapter;
import com.example.applicationingsw.model.Cart;
import com.example.applicationingsw.model.Item;

import java.util.ArrayList;

public class SummaryTabFragment extends Fragment {


    private View summaryView;
    private TextView buyerName;
    private TextView buyerAddress;
    private ListView listview;
    private TextView amount;
    private int[] IMAGE = {R.drawable.cio_card_io_logo, R.drawable.ic_list, R.drawable.ic_close_tag,
            R.drawable.ic_add_to_cart, R.drawable.ic_cart};
    private String[] TITLE = {"Teak & Steel Petanque Set", "Lemon Peel Baseball", "Seil Marschall Hiking Pack", "Teak & Steel Petanque Set", "Lemon Peel Baseball"};
    private String[] DESCRIPTION = {"One Size", "One Size", "Size L", "One Size", "One Size"};
    private String[] DATE = {"$ 220.00","$ 49.00","$ 320.00","$ 220.00","$ 49.00"};
    private CartAdapter baseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        summaryView = inflater.inflate(R.layout.cart_summary_fragment, container, false);
        listview = (ListView)summaryView.findViewById(R.id.listview);
        buyerAddress = summaryView.findViewById((R.id.buyerAddress));
        buyerName = summaryView.findViewById(R.id.buyerName);
        amount = summaryView.findViewById(R.id.amount);
        for (int i= 0; i< TITLE.length; i++){
            Item currentItem = new Item(2,TITLE[i],DESCRIPTION[i],99.99f,DESCRIPTION[i],32,"https://cdn-media.italiani.it/site-matera/2019/02/San-Gerardo-Maiella.jpg","Sacred objects",new ArrayList<String>());
            Cart.getInstance().addItemInCart(currentItem,23);
        }


        baseAdapter = new CartAdapter(getActivity(), Cart.getInstance()) {
        };

        listview.setAdapter(baseAdapter);


        return  summaryView;

    }
}