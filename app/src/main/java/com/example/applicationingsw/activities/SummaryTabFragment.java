package com.example.applicationingsw.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Currency;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.applicationingsw.App;
import com.example.applicationingsw.R;
import com.example.applicationingsw.adapters.CartAdapter;
import com.example.applicationingsw.model.Cart;
import com.example.applicationingsw.model.CognitoUserPoolShared;
import com.example.applicationingsw.model.Customer;
import com.example.applicationingsw.model.Item;
import com.example.applicationingsw.services.PaymentMethod;
import com.example.applicationingsw.services.PaypalConfigurationService;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class SummaryTabFragment extends Fragment implements PaymentMethod, Observer {

    private View summaryView;
    private TextView buyerName;
    private TextView buyerAddress;
    private TextView editShippingDetailsButton;
    private TextView amount;
    private TextView payButton;
    private ListView listview;
    private Customer currentCustomer;
    private CartAdapter baseAdapter;
    private ProgressDialog loadingDialog;
    private String postOrderEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/postorder";
    private String invoiceEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/invoice";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        summaryView = inflater.inflate(R.layout.cart_summary_fragment, container, false);
        listview = (ListView)summaryView.findViewById(R.id.listview);
        buyerAddress = summaryView.findViewById((R.id.buyerAddress));
        buyerName = summaryView.findViewById(R.id.buyerName);
        amount = summaryView.findViewById(R.id.amount);
        if(!Cart.getInstance().isInSyncWithServer()){
            loadingDialog = ProgressDialog.show(getActivity(), "",
                    "We're loading your last cart. Please wait...", true);
        }
        getUserData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            amount.setText(String.valueOf(Cart.getInstance().calculateTotalPrice())+ Currency.getInstance(Locale.getDefault()).getSymbol());
        }
        else{
            amount.setText(String.valueOf(Cart.getInstance().calculateTotalPrice())+ "€");
        }
        Cart.getInstance().addObserver(this);
        payButton = summaryView.findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        listview.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    amount.setText(String.valueOf(Cart.getInstance().calculateTotalPrice())+ Currency.getInstance(Locale.getDefault()).getSymbol());
                }
                else{
                    amount.setText(String.valueOf(Cart.getInstance().calculateTotalPrice())+ "€");
                }
            }
        });
        return  summaryView;

    }

    /**
     * Concrete PaymentMethod strategy implementation, takes to the Paypal sdk activities for payment after the confirm of the availability of all the items.
     * @param amount the amount of the payment.
     */
    @Override
    public void pay(float amount) {
        checkAvailabilityForOrder();
    }

    public void proceedWithPayment(float amount){
        if(amount>0){
            String description = "" ;
            for(Pair<Item,Integer> itemInCart : Cart.getInstance().getItemsInCart()){
                description += itemInCart.first.getName() + " n." + itemInCart.second + ",";
            }
            description = description.substring(0,description.lastIndexOf(","));
            String concurrencyCode = getPaypalConcurrencyCode();
            PayPalPayment payment = new PayPalPayment(new BigDecimal(Cart.getInstance().calculateTotalPrice()),concurrencyCode , description,
                    PayPalPayment.PAYMENT_INTENT_SALE);

            Intent intent = new Intent(App.getAppContext(), PaymentActivity.class);

            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PaypalConfigurationService.getConfig());

            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            startActivityForResult(intent, 0);
        }
        else {
            loadingDialog.dismiss();
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

    public String getPaypalConcurrencyCode(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        }
        else{
            return "EUR";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                postOrderOnDB(postOrderEndpoint);
                sendInvoice(invoiceEndpoint);
                Cart.getInstance().clearCart();
                baseAdapter.notifyDataSetChanged();
                new AlertDialog.Builder(getContext())
                        .setTitle("Purchase done")
                        .setMessage("The order is completed! Check your inbox for the invoice and continue shopping!")
                        .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SummaryTabFragment.this.getActivity().finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("ERRORE PAYPAL", "result canceled");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.e("ERRORE PAYPAL", "result extras invalid");
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
            SendCustomer paymentToShippingFragment = (SendCustomer) getActivity();
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
                String savedEmail = list.getAttributes().getAttributes().get("email");
                String savedName = list.getAttributes().getAttributes().get("name");
                String savedSurname = list.getAttributes().getAttributes().get("family_name");
                String savedAddress = list.getAttributes().getAttributes().get("address");
                String savedCity = list.getAttributes().getAttributes().get("locale");
                String birthdate = list.getAttributes().getAttributes().get("birthdate");
                Customer customer = new Customer(savedName,savedSurname,savedAddress,savedEmail,"",savedCity,birthdate);
                SendCustomer customerToPaymentFragment = (SendCustomer) getActivity();
                displayShippingInfo(customer);
                customerToPaymentFragment.send(customer,1);
            }

            @Override
            public void onFailure(final Exception exception) {
                // Failed to retrieve the user details, probe exception for the cause
                Log.e("Exc dettagli utente",exception.toString());
                Customer customer = new Customer("","","","","","","");
                SendCustomer customerToPaymentFragment = (SendCustomer) getActivity();
                customerToPaymentFragment.send(customer,1);
            }
        };
        CognitoUser curr = CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser();
        CognitoUserPoolShared.getInstance().getUserPool().getUser(curr.getUserId()).getDetailsInBackground(handler);
    }

    public void checkAvailabilityForOrder(){
        final RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("cart", Cart.getInstance().getCartID());
            jsonBody.put("checkAvailability", true);
            final String requestBody = jsonBody.toString();
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, postOrderEndpoint, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e("Response",response);
                        JSONObject responseBody = new JSONObject(response);
                        if(!responseBody.getBoolean("success")){
                            if(responseBody.getJSONObject("errorReport").getInt("errorCode") == 23514){
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Items unavailable")
                                        .setMessage("We're sorry but some items are not available. Try to reduce your quantity.")
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
                        else{
                            proceedWithPayment(Cart.getInstance().calculateTotalPrice());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Exception",e.getLocalizedMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Postorder","Errore" + error.toString()+ error.getLocalizedMessage());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        Log.i("BODY", requestBody);
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }


                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    return super.parseNetworkResponse(response);
                }
            };
            stringRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 25000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 10;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                    Log.e("CART","RETRY" + error.getMessage());
                }
            });
            requestQueue.add(stringRequest);
            requestQueue.start();

        } catch (JSONException e) {
            checkAvailabilityForOrder();
        }
    }

   public void postOrderOnDB(final String endpoint){
       final RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
       try {
           JSONObject jsonBody = new JSONObject();
           jsonBody.put("cart", Cart.getInstance().getCartID());
           jsonBody.put("checkAvailability", false);
           final String requestBody = jsonBody.toString();
           final StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint, new Response.Listener<String>() {
               @Override
               public void onResponse(String response) {
                   try {
                       Log.e("Response",response);
                       JSONObject responseBody = new JSONObject(response);
                       if(!responseBody.getBoolean("success")){
                           if(responseBody.getJSONObject("errorReport").getInt("errorCode") == 23514){
                               Log.e("Postorderondb","articoli non sufficienti");
                           }
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                       Log.e("Exception",e.getLocalizedMessage());
                   }

               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   Log.e("Postorder","Errore" + error.toString()+ error.getLocalizedMessage());
               }
           }) {
               @Override
               public String getBodyContentType() {
                   return "application/json; charset=utf-8";
               }

               @Override
               public byte[] getBody() throws AuthFailureError {
                   try {
                       Log.i("BODY", requestBody);
                       return requestBody == null ? null : requestBody.getBytes("utf-8");
                   } catch (UnsupportedEncodingException uee) {
                       VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                       return null;
                   }
               }


               @Override
               protected Response<String> parseNetworkResponse(NetworkResponse response) {
                   return super.parseNetworkResponse(response);
               }
           };
           stringRequest.setRetryPolicy(new RetryPolicy() {
               @Override
               public int getCurrentTimeout() {
                   return 25000;
               }

               @Override
               public int getCurrentRetryCount() {
                   return 10;
               }

               @Override
               public void retry(VolleyError error) throws VolleyError {
                   Log.e("CART","RETRY" + error.getMessage());
               }
           });
           requestQueue.add(stringRequest);
           requestQueue.start();

       } catch (JSONException e) {
           postOrderOnDB(postOrderEndpoint);
       }
    }

    public void sendInvoice(String endpoint){
        final RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
        JSONObject jsonBody = getInvoiceAsJson();
        Log.e("BODY POST INVOICE",jsonBody.toString());
        final String requestBody = jsonBody.toString();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseBody = new JSONObject(response);
                    boolean result = responseBody.getBoolean("success");
                    Log.e("RISULTATO POST INVOICE",responseBody.toString());
                    if(!result){
                        sendInvoice(invoiceEndpoint);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERRORE POST INVOICE",error.getLocalizedMessage());
                sendInvoice(invoiceEndpoint);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    Log.i("BODY", requestBody);
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(stringRequest);
        requestQueue.start();
    }


    public JSONObject getInvoiceAsJson(){
        JSONObject invoiceJson = new JSONObject();
        try {
            JSONObject header = new JSONObject();
            Date cDate = new Date();
            String formattedDate;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                header.put("invoiceDate", formattedDate);
            }
            else {
                header.put("invoiceDate", Calendar.getInstance().getTime());
            }
            header.put("invoiceNumber",Calendar.getInstance().getTimeInMillis());
            JSONObject personalData = new JSONObject();
            personalData.put("first",currentCustomer.getName());
            personalData.put("last",currentCustomer.getSurname());
            personalData.put("address",currentCustomer.getAddress());
            personalData.put("city",currentCustomer.getCity());
            JSONArray arrayOfItems = new JSONArray();
            JSONObject singleItem = new JSONObject();
            for(Pair<Item,Integer>itemInCart : Cart.getInstance().getItemsInCart()){
                Log.e("ARTICOLI ENTRO","VOLTE");
                singleItem.put("productId", itemInCart.first.getId());
                singleItem.put("description", itemInCart.first.getName() + ", " + itemInCart.first.getDescription());
                singleItem.put("quantity", itemInCart.second);
                singleItem.put("unitPrice", itemInCart.first.getPriceWithoutConcurrency());
                arrayOfItems.put(singleItem);
                singleItem = new JSONObject();
            }
            JSONObject invoiceBody = new JSONObject();
            invoiceBody.put("header",header);
            invoiceBody.put("billTo",personalData);
            invoiceBody.put("notes","");
            invoiceBody.put("rows",arrayOfItems);
            invoiceJson.put("invoice", invoiceBody);
            invoiceJson.put("email",CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().getUserId());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                invoiceJson.put("concurrency",Currency.getInstance(Locale.getDefault()).getSymbol());
            }
            else{
                invoiceJson.put("concurrency","€");
            }
            Log.e("Il json invoice è:" , invoiceJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return invoiceJson;
    }


    @Override
    public void update(Observable observable, Object o) {
        if(loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
        Log.e("SUMMARYTAB","NOTIFICATO");
        baseAdapter.notifyDataSetChanged();
    }
}