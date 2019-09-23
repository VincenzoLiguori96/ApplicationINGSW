package com.example.applicationingsw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.applicationingsw.Services.PaypalConfigurationService;
import com.example.applicationingsw.adapters.CartAdapter;
import com.example.applicationingsw.model.Cart;
import com.example.applicationingsw.model.Item;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;

public class SummaryTabFragment extends Fragment implements PaymentMethod {


    private View summaryView;
    private TextView buyerName;
    private TextView buyerAddress;
    private ListView listview;
    private TextView editShippingDetailsButton;
    private TextView amount;
    private TextView payButton;
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
        payButton = summaryView.findViewById(R.id.payButton);
        editShippingDetailsButton = summaryView.findViewById(R.id.editButton);
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PaypalConfigurationService.getConfig());
        getActivity().startService(intent);
        for (int i= 0; i< TITLE.length; i++){
            Item currentItem = new Item(2,TITLE[i],DESCRIPTION[i],99.99f,DESCRIPTION[i],32,"https://cdn-media.italiani.it/site-matera/2019/02/San-Gerardo-Maiella.jpg","Sacred objects",new ArrayList<String>());
            Cart.getInstance().addItemInCart(currentItem,23);
        }


        baseAdapter = new CartAdapter(getActivity(), Cart.getInstance()) {
        };

        listview.setAdapter(baseAdapter);


        return  summaryView;

    }

    /**
     * Concrete PaymentMethod strategy implementation, takes to the Paypal sdk activities for payment.
     * @param amount the amount of the payment.
     */
    @Override
    public void pay(float amount) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal("1.75"), "USD", "hipster jeans",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(App.getAppContext(), PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PaypalConfigurationService.getConfig());

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.
                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
    }
}