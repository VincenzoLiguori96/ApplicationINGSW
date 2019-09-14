package com.example.applicationingsw.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.applicationingsw.model.Item;
import com.example.applicationingsw.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter {
    List<Item> mItems;
    Context mContext;
    public static final int LOADING_ITEM = 0;
    public static final int PRODUCT_ITEM = 1;
    int LoadingItemPos;
    public boolean loading = false;

    public ItemsAdapter(Context mContext) {
        mItems = new ArrayList<>();
        this.mContext = mContext;
    }

    //method to add Items as soon as they fetched 
    public void addItems(List<Item> Items) {
        int lastPos = mItems.size();
        this.mItems.addAll(Items);
        notifyItemRangeInserted(lastPos, mItems.size());
    }


    @Override
    public int getItemViewType(int position) {
        Item currentItem = mItems.get(position);
        if (currentItem.isLoading()) {
            return LOADING_ITEM;
        } else {
            return PRODUCT_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //Check which view has to be populated 
        if (viewType == LOADING_ITEM) {
            View row = inflater.inflate(R.layout.custom_item_row, parent, false);
            return new LoadingHolder(row);
        } else if (viewType == PRODUCT_ITEM) {
            View row = inflater.inflate(R.layout.custom_item_row, parent, false);
            return new ItemHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //get current Item
        final Item currentItem = mItems.get(position);
        if (holder instanceof ItemHolder) {
            ItemHolder ItemHolder = (ItemHolder) holder;
            //bind Items information with view
            //TODO: Fetch dell'immagine
            //Picasso.with(mContext).load(currentItem.getImageResourceId()).into(ItemHolder.imageViewItemThumb);
            //TODO: Sostituisci con il fetch
            Picasso.with(mContext).load(R.drawable.img1).into(ItemHolder.imageViewItemThumb);
            ItemHolder.textViewItemName.setText(currentItem.getName());
            ItemHolder.textViewItemPrice.setText(currentItem.getPrice());
            if (currentItem.isNew())
                ItemHolder.textViewNew.setVisibility(View.VISIBLE);
            else
                ItemHolder.textViewNew.setVisibility(View.GONE);

            ItemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // user selected Item now you can show details of that Item 
                    Toast.makeText(mContext, "Selected " + currentItem.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    //Holds view of Item with information
    private class ItemHolder extends RecyclerView.ViewHolder {
        ImageView imageViewItemThumb;
        TextView textViewItemName, textViewItemPrice, textViewNew;


        public ItemHolder(View itemView) {
            super(itemView);
            imageViewItemThumb = itemView.findViewById(R.id.imageViewItemThumb);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemPrice = itemView.findViewById(R.id.textViewItemPrice);
            textViewNew = itemView.findViewById(R.id.textViewNew);

        }
    }

    //holds view of loading item 
    private class LoadingHolder extends RecyclerView.ViewHolder {
        public LoadingHolder(View itemView) {
            super(itemView);
        }
    }

    //method to show loading 
    public void showLoading() {
        Item Item = new Item();
        Item.setLoading(true);
        mItems.add(Item);
        LoadingItemPos = mItems.size();
        notifyItemInserted(mItems.size());
        loading = true;
    }

    //method to hide loading 
    public void hideLoading() {
        if (LoadingItemPos <= mItems.size()) {
            mItems.remove(LoadingItemPos - 1);
            notifyItemRemoved(LoadingItemPos);
            loading = false;
        }
    }
}