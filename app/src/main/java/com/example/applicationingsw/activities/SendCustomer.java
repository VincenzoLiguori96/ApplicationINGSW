package com.example.applicationingsw.activities;

import com.example.applicationingsw.model.Customer;

public interface SendCustomer{
    void send(Customer aCustomer, int toPage);
}