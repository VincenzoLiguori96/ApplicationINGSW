package com.example.applicationingsw.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.applicationingsw.R;
import com.example.applicationingsw.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter implements Filterable {
    List<Item> mItems;
    List<Item> filteredItems;
    Context mContext;
    public static final int LOADING_ITEM = 0;
    public static final int PRODUCT_ITEM = 1;
    int LoadingItemPos;
    public boolean loading = false;
    private ItemsAdapterListener listener;


    public ItemsAdapter(Context mContext,ItemsAdapterListener listener,List<Item> itemsList) {
        mItems = itemsList;
        filteredItems = mItems;
        this.mContext = mContext;
        this.listener = listener;

    }

    public void changeAdapterData(List<Item> newItems){
        this.mItems.clear();
        this.filteredItems.clear();
        this.mItems.addAll(newItems);
        this.filteredItems.addAll(newItems);
        this.notifyDataSetChanged();
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //get current Item
        final Item currentItem = filteredItems.get(position);
        if (holder instanceof ItemHolder) {
            ItemHolder ItemHolder = (ItemHolder) holder;
            //bind Items information with view
            Picasso.with(mContext).load(currentItem.getUrl()).into(ItemHolder.imageViewItemThumb);
            ItemHolder.textViewItemName.setText(currentItem.getName());
            ItemHolder.textViewItemPrice.setText(currentItem.getPrice());
            if (currentItem.isNew() && !currentItem.isLoading())
                ItemHolder.textViewNew.setVisibility(View.VISIBLE);
            else
                ItemHolder.textViewNew.setVisibility(View.GONE);

            ItemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onItemSelected(filteredItems.get(position));
                }
            });
        }

    }

    public interface ItemsAdapterListener {
        void onItemSelected(Item item);
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredItems = mItems;
                } else {
                    List<Item> filteredList = new ArrayList<>();
                    for (Item item : mItems) {

                        if (item.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item);
                        }
                        else{
                            List<String> tagsOfItem = item.getTags();
                            for(String tag : tagsOfItem){
                                if(tag.toLowerCase().contains(charString.toLowerCase())){
                                    filteredList.add(item);
                                }
                            }
                        }
                    }

                    filteredItems = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItems;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredItems = (ArrayList<Item>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
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
        Item item = new Item();
        item.setLoading(true);
        item.setNew(false);
        mItems.add(item);
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