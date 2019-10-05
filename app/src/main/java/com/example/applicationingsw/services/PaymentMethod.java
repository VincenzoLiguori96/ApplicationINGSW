package com.example.applicationingsw.services;

import android.content.Intent;

/**
 * Interface used as a strategy for the payment, that could be done through different methods.
 */
public interface PaymentMethod {
    public void pay(float amount);
}
