package com.example.applicationingsw;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.example.applicationingsw.model.CognitoUserPoolShared;

import java.util.Map;

/**
 * Created by apple on 18/03/16.
 */
public class ShippingTabFragment extends Fragment {
    private TextView name;
    private TextView surname;
    private TextView email;
    private TextView phoneNumber;
    private TextView address;
    private TextView continueButton;
    private View shippingTab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         shippingTab = inflater.inflate(R.layout.shipping_fragment, container, false);
         name = shippingTab.findViewById(R.id.firstNameTextView);
         email = shippingTab.findViewById(R.id.emailTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            email.setTooltipText("The email will be used for communications about the order.");
        }
        else {
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(),"The email will be used for communications about the order.",Toast.LENGTH_LONG);
                }
            });
        }
        surname = shippingTab.findViewById(R.id.secondNameTextView);
        phoneNumber = shippingTab.findViewById(R.id.phoneNumberTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phoneNumber.setTooltipText("The email will be used for communications about the order.");
        }
        else {
            phoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(),"The email will be used for communications about the order.",Toast.LENGTH_LONG);
                }
            });
        }
        address = shippingTab.findViewById(R.id.addressTextView);
        continueButton = shippingTab.findViewById(R.id.continueToOrderButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 continueButtonPressed();
             }
         });
        GetDetailsHandler handler = new GetDetailsHandler() {
            @Override
            public void onSuccess(final CognitoUserDetails list) {
                // Successfully retrieved user details
                String savedEmail = list.getAttributes().getAttributes().get("email");
                email.setText(savedEmail);
                String savedName = list.getAttributes().getAttributes().get("name");
                name.setText(savedName);
                String savedSurname = list.getAttributes().getAttributes().get("family_name");
                surname.setText(savedSurname);
                String savedAddress = list.getAttributes().getAttributes().get("locale") + " " + list.getAttributes().getAttributes().get("address");
                address.setText(savedAddress);
            }

            @Override
            public void onFailure(final Exception exception) {
                // Failed to retrieve the user details, probe exception for the cause
                Log.e("Exc dettagli utente",exception.toString());
            }
        };
        CognitoUser curr = CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser();
        CognitoUserPoolShared.getInstance().getUserPool().getUser(curr.getUserId()).getDetailsInBackground(handler);
        return shippingTab;
    }



    public void continueButtonPressed(){
        if(validate()){
            //TODO passa al tab del riepilogo passandogli i dati di spedizione
        }
    }


    public boolean validate() {
        boolean valid = true;

        String nameString = name.getText().toString();
        String emailString = email.getText().toString();
        String surnameString = surname.getText().toString();
        String addressString = address.getText().toString();

        if (nameString.isEmpty()) {
            name.setError("Insert a name!");
            valid = false;
        } else {
            name.setError(null);
        }

        if (emailString.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("Enter a valid email address!");
            valid = false;
        } else {
            email.setError(null);
        }

        if (surnameString.isEmpty()) {
            surname.setError("Enter a surname");
            valid = false;
        } else {
            surname.setError(null);
        }
        if (addressString.isEmpty()) {
            address.setError("Enter an address.");
            valid = false;
        } else {
            address.setError(null);
        }
        return valid;
    }
}