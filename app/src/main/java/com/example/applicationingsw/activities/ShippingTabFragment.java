package com.example.applicationingsw.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationingsw.R;
import com.example.applicationingsw.model.Customer;

/**
 * Created by apple on 18/03/16.
 */
public class ShippingTabFragment extends Fragment {
    private TextView name;
    private TextView surname;
    private TextView email;
    private TextView phoneNumber;
    private TextView address;
    private TextView city;
    private TextView continueButton;
    private View shippingTab;
    private Customer currentCustomer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         shippingTab = inflater.inflate(R.layout.shipping_fragment, container, false);
         name = shippingTab.findViewById(R.id.firstNameTextView);
         email = shippingTab.findViewById(R.id.emailTextView);
         city = shippingTab.findViewById(R.id.cityTextView);
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
        return shippingTab;
    }



    public void continueButtonPressed(){
        if(validate()){
            Customer customer = new Customer(name.getText().toString(),surname.getText().toString(),address.getText().toString(),email.getText().toString(),"",city.getText().toString(),"");
            try {
                SendCustomer customerToPaymentFragment = (SendCustomer) getActivity();
                customerToPaymentFragment.send(customer,0);
                CartActivity myActivity = (CartActivity) getActivity();
                myActivity.changeTab(0);
            } catch (ClassCastException e) {
                CartActivity myActivity = (CartActivity) getActivity();
                myActivity.changeTab(0);
            }
        }
    }


    public boolean validate() {
        boolean valid = true;

        String nameString = name.getText().toString();
        String emailString = email.getText().toString();
        String surnameString = surname.getText().toString();
        String addressString = address.getText().toString();
        String cityString = city.getText().toString();

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
        if (cityString.isEmpty()) {
            city.setError("Enter a city.");
            valid = false;
        } else {
            city.setError(null);
        }
        return valid;
    }

    public void  displayPassedShippingInfo(Customer ofCustomer){
        if(ofCustomer != null){
            email.setText(ofCustomer.getEmail());
            name.setText(ofCustomer.getName());
            surname.setText(ofCustomer.getSurname());
            city.setText(ofCustomer.getCity());
            address.setText(ofCustomer.getAddress());
        }
    }

    interface SendCustomer{
        public void send(Customer aCustomer,int toPage);
        public void updateData(Customer aCustomer);
    }

}