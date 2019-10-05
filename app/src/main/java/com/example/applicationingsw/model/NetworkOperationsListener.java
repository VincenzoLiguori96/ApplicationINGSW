package com.example.applicationingsw.model;

public interface NetworkOperationsListener<T> {
    void getResult(T object);
    void getError(T object);
    void onFinish();
}
