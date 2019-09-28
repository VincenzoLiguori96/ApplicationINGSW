package com.example.applicationingsw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.example.applicationingsw.Services.PaypalConfigurationService;
import com.example.applicationingsw.adapters.CartAdapter;
import com.example.applicationingsw.model.Cart;
import com.example.applicationingsw.model.CognitoUserPoolShared;
import com.example.applicationingsw.model.Customer;
import com.example.applicationingsw.model.Item;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class SummaryTabFragment extends Fragment implements PaymentMethod {


    private View summaryView;
    private TextView buyerName;
    private TextView buyerAddress;
    private ListView listview;
    private TextView editShippingDetailsButton;
    private TextView amount;
    private TextView payButton;
    private Customer currentCustomer;
    private int[] IMAGE = {R.drawable.cio_card_io_logo, R.drawable.ic_list, R.drawable.ic_close_tag,
            R.drawable.ic_add_to_cart, R.drawable.ic_cart};
    private CartAdapter baseAdapter;
    private String postOrderEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/postorder";
    public SummaryTabFragment(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        summaryView = inflater.inflate(R.layout.cart_summary_fragment, container, false);
        listview = (ListView)summaryView.findViewById(R.id.listview);
        buyerAddress = summaryView.findViewById((R.id.buyerAddress));
        buyerName = summaryView.findViewById(R.id.buyerName);
        amount = summaryView.findViewById(R.id.amount);
        getUserData();
        amount.setText(String.valueOf(Cart.getInstance().calculateTotalPrice())+ App.getAppContext().getString(R.string.concurrency));
        payButton = summaryView.findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountWithoutConcurrency = amount.getText().toString().replaceAll(App.getAppContext().getString(R.string.concurrency),"");
                Cart.getInstance().pay(SummaryTabFragment.this);
            }
        });
        editShippingDetailsButton = summaryView.findViewById(R.id.editButton);
        editShippingDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShippingDetails();
            }
        });
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PaypalConfigurationService.getConfig());
        getActivity().startService(intent);
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
        if(amount>0){
            String description = "" ;
            for(Pair<Item,Integer> itemInCart : Cart.getInstance().getItemsInCart()){
                description += itemInCart.first.getName() + "n." + itemInCart.second +",";
            }
            description = description.substring(0,description.lastIndexOf(","));
            PayPalPayment payment = new PayPalPayment(new BigDecimal(Cart.getInstance().calculateTotalPrice()), App.getAppContext().getString(R.string.concurrency), description,
                    PayPalPayment.PAYMENT_INTENT_SALE);

            Intent intent = new Intent(App.getAppContext(), PaymentActivity.class);

            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PaypalConfigurationService.getConfig());

            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            startActivityForResult(intent, 0);
        }
        else {
            new AlertDialog.Builder(getContext())
                    .setTitle("No items in cart.")
                    .setMessage("Are you sure you added something?")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO aggiornare il db con l'ordine + mandare email riepilogo.
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

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


    public void  displayShippingInfo(Customer ofCustomer){
        currentCustomer = ofCustomer;
        buyerName.setText(ofCustomer.getName() + " " + ofCustomer.getSurname());
        buyerAddress.setText(ofCustomer.getCity() + ", " + ofCustomer.getAddress());
    }

    public void goToShippingDetails(){
        try {
            ShippingTabFragment.SendCustomer paymentToShippingFragment = (ShippingTabFragment.SendCustomer) getActivity();
            paymentToShippingFragment.send(currentCustomer,1);
            CartActivity myActivity = (CartActivity) getActivity();
            myActivity.changeTab(1);
        } catch (ClassCastException e) {
            CartActivity myActivity = (CartActivity) getActivity();
            myActivity.changeTab(1);
        }
    }

    public void getUserData(){
        GetDetailsHandler handler = new GetDetailsHandler() {
            @Override
            public void onSuccess(final CognitoUserDetails list) {
                // Successfully retrieved user details
                String savedEmail = list.getAttributes().getAttributes().get("email");
                String savedName = list.getAttributes().getAttributes().get("name");
                String savedSurname = list.getAttributes().getAttributes().get("family_name");
                String savedAddress = list.getAttributes().getAttributes().get("address");
                String savedCity = list.getAttributes().getAttributes().get("locale");
                Customer customer = new Customer(savedName,savedSurname,savedAddress,savedEmail,"",savedCity);
                ShippingTabFragment.SendCustomer customerToPaymentFragment = (ShippingTabFragment.SendCustomer) getActivity();
                displayShippingInfo(customer);
                customerToPaymentFragment.send(customer,1);
            }

            @Override
            public void onFailure(final Exception exception) {
                // Failed to retrieve the user details, probe exception for the cause
                Log.e("Exc dettagli utente",exception.toString());
                Customer customer = new Customer("","","","","","");
                ShippingTabFragment.SendCustomer customerToPaymentFragment = (ShippingTabFragment.SendCustomer) getActivity();
                customerToPaymentFragment.send(customer,1);
            }
        };
        CognitoUser curr = CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser();
        CognitoUserPoolShared.getInstance().getUserPool().getUser(curr.getUserId()).getDetailsInBackground(handler);
    }


    public void postOrderOnDB(String endpoint){

    }


}