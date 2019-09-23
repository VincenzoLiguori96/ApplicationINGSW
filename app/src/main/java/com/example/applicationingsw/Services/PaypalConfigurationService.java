package com.example.applicationingsw.Services;

import android.content.Intent;

import com.example.applicationingsw.App;
import com.example.applicationingsw.PaymentMethod;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;

public class PaypalConfigurationService {
    private static String clientId = "AXbUP4TavFas1HZlM3_rybNI6mhAwBWTwpuDcCiE8dQGEo-N_ikbOORK6GrgmSVT3AXfFl9j9rGtFNvA";
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(clientId);

    public static PayPalConfiguration getConfig() {
        return config;
    }

}
