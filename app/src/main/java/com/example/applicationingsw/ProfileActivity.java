package com.example.applicationingsw;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.regions.Regions;
import com.example.applicationingsw.model.CognitoUserPoolShared;
import com.example.applicationingsw.model.Customer;

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

    public void change_pass(View view) {
        Toast.makeText(this, "Change Password Clicked", Toast.LENGTH_SHORT).show();
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
                    //TODO cancella la cache dell'utente
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
