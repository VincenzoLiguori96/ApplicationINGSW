package com.example.applicationingsw.model;

import com.android.volley.Response;

import java.util.List;

public interface ItemDAO {

    void readAllItems(NetworkOperationsListener listener);
    void readItemsWithFilter(NetworkOperationsListener listener,String filterParameters);

}
