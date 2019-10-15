package com.example.applicationingsw.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
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
import com.example.applicationingsw.model.CognitoUserPoolShared;
import com.example.applicationingsw.model.Customer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ProfileActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView profileCompleteName;
    private TextView profileEmail;
    private TextView profileName;
    private TextView profileSurname;
    private TextView profileBirthdate;
    private TextView profileGender;
    private TextView profileAddress;
    private TextView profileCity;
    private Customer currentCustomer;
    private String deleteCustomerEndpoint = "https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/deletecustomer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileCity = findViewById(R.id.profile_city);
        profileCompleteName = findViewById(R.id.profile_complete_name);
        profileEmail = findViewById(R.id.profile_email);
        profileSurname = findViewById(R.id.profile_surname);
        profileName = findViewById(R.id.profile_name);
        profileBirthdate = findViewById(R.id.profile_birthdate);
        profileGender = findViewById(R.id.profile_gender);
        profileAddress = findViewById(R.id.profile_address);
        swipeRefreshLayout = findViewById(R.id.profile_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        fillUserDataFields();
    }

    public void logout(View view) {
        CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().signOut();
        Intent turnToLoginPage = new Intent(this,LoginActivity.class);
        turnToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ProfileActivity.this.startActivity(turnToLoginPage);
    }

    public void deleteCustomer(View view) {
        final String email = CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().getUserId();
        Log.e("utente", CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().getUserId());
        GenericHandler handler = new GenericHandler() {
            @Override
            public void onSuccess() {
                deleteCustomerOnDB(email);
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Goodbye!")
                        .setMessage("We're sorry you deleted your account. You can come back whenever you want!")
                        .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent turnToLoginPage = new Intent(ProfileActivity.this,LoginActivity.class);
                                turnToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ProfileActivity.this.startActivity(turnToLoginPage);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            @Override
            public void onFailure(Exception exception) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Error deleting profile!")
                        .setMessage("We're sorry but we can't delete your account now. Please, try again. Error details: " + exception.getLocalizedMessage())
                        .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent turnToLoginPage = new Intent(ProfileActivity.this,LoginActivity.class);
                                turnToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ProfileActivity.this.startActivity(turnToLoginPage);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };
        CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().deleteUserInBackground(handler);
    }


    public void deleteCustomerOnDB(final String email){
        final RequestQueue requestQueue = Volley.newRequestQueue(App.getAppContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            final String requestBody = jsonBody.toString();
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteCustomerEndpoint, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e("Response",response);
                        JSONObject responseBody = new JSONObject(response);
                        boolean success = responseBody.getBoolean("success");

                    } catch (JSONException e) {
                        Log.e("Profile delete customer", e.getLocalizedMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    deleteCustomerOnDB(email);
                    Log.e("PROFACT_DELETE","Errore" + error.toString()+ error.getLocalizedMessage());
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
                    return 50;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                }
            });
            requestQueue.add(stringRequest);
            requestQueue.start();

        } catch (JSONException e) {
            deleteCustomerOnDB(email);
        }
    }


    public void changePassword(View view) {
        Intent changePassword = new Intent(this,ChangePasswordActivity.class);
        this.startActivity(changePassword);
    }


    public void fillUserDataFields(){
        GetDetailsHandler handler = new GetDetailsHandler() {
            @Override
            public void onSuccess(final CognitoUserDetails list) {
                // Successfully retrieved user details
                String savedEmail = list.getAttributes().getAttributes().get("email");
                String savedName = list.getAttributes().getAttributes().get("name");
                String savedSurname = list.getAttributes().getAttributes().get("family_name");
                String savedAddress = list.getAttributes().getAttributes().get("address");
                String savedCity = list.getAttributes().getAttributes().get("locale");
                String savedGender = list.getAttributes().getAttributes().get("gender");
                String birthdate = list.getAttributes().getAttributes().get("birthdate");
                currentCustomer = new Customer(savedName,savedSurname,savedAddress,savedEmail,savedGender,savedCity,birthdate);
                profileGender.setText(currentCustomer.getGender());
                profileBirthdate.setText(currentCustomer.getBirthdate());
                profileName.setText(currentCustomer.getName());
                profileSurname.setText(currentCustomer.getSurname());
                profileEmail.setText(currentCustomer.getEmail());
                profileCity.setText(currentCustomer.getCity());
                profileCompleteName.setText(currentCustomer.getName() + " " + currentCustomer.getSurname());
                profileAddress.setText(currentCustomer.getAddress());
            }
                @Override
                public void onFailure(final Exception exception) {
                    // Failed to retrieve the user details, probe exception for the cause
                    Log.e("Exc dettagli utente",exception.toString());
                    currentCustomer = new Customer("","","","","","","");
                    profileGender.setText(currentCustomer.getGender());
                    profileBirthdate.setText(currentCustomer.getBirthdate());
                    profileName.setText(currentCustomer.getName());
                    profileSurname.setText(currentCustomer.getSurname());
                    profileEmail.setText(currentCustomer.getEmail());
                    profileCity.setText(currentCustomer.getCity());
                    profileCompleteName.setText(currentCustomer.getName() + " " + currentCustomer.getSurname());
                    new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("Profile error!")
                            .setMessage("We're sorry but we are experiencing problems with your account. Try to exit and log in again. Error details: " + exception.getLocalizedMessage())
                            .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent turnToLoginPage = new Intent(ProfileActivity.this,LoginActivity.class);
                                    turnToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    ProfileActivity.this.startActivity(turnToLoginPage);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            };
        CognitoUser curr = CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser();
        CognitoUserPoolShared.getInstance().getUserPool().getUser(curr.getUserId()).getDetailsInBackground(handler);
    }


}
