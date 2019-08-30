package com.example.applicationingsw;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;

public class SignupActivity extends Activity {
    private static final String TAG = "SignupActivity";

    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText surnameText;
    private EditText cityText;
    private EditText addressText;
    private Button signupButton;
    private Spinner genderSpinner;
    private TextView loginLink;
    //Istanza di un user pool cognito
    private CognitoUserPool userPool ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        nameText = findViewById(R.id.input_name);
        signupButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);
        genderSpinner = findViewById(R.id.input_gender);
        surnameText = findViewById(R.id.input_surname);
        cityText = findViewById(R.id.input_city);
        addressText = findViewById(R.id.input_address);
        userPool = new CognitoUserPool(getApplicationContext(), "eu-west-1_KQhWEFGrY", "3kjf4fl4bmn540hfg7v105mvmb", null, Regions.EU_WEST_1);
        final String[] genders = getResources().getStringArray(R.array.gender_array);
        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item,genders){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);

                }
                else {
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerArrayAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.LTGRAY);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                genderSpinner.setSelection(0);
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String surname = surnameText.getText().toString();
        String city = cityText.getText().toString();
        String address = addressText.getText().toString();
        String gender = "";
        if ( !genderSpinner.getSelectedItem().toString().equals("Gender")){
            gender = genderSpinner.getSelectedItem().toString();
        }
        signUpWithCognito(name,email,password,surname,city,address,gender);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void signUpWithCognito(String name,String email,String password,String surname,String city,String address,String gender){
        // Create a CognitoUserAttributes object and add user attributes
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
// Add the user attributes. Attributes are added as key-value pairs
        userAttributes.addAttribute("name", name);
        userAttributes.addAttribute("email", email);
        userAttributes.addAttribute("family_name", surname);
        userAttributes.addAttribute("locale", city);
        userAttributes.addAttribute("address", address);
        userAttributes.addAttribute("gender", gender);
        SignUpHandler signupCallback = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                // Sign-up was successful

                // Check if this user (cognitoUser) needs to be confirmed
                if(!userConfirmed) {
                    // TODO: This user must be confirmed and a confirmation code was sent to the user,cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent, Get the confirmation code from user
                    // This user must be confirmed and a confirmation code was sent to the user
                    // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                    // Get the confirmation code from user
                    Log.i(TAG, cognitoUserCodeDeliveryDetails.getDeliveryMedium() + " poi " + cognitoUserCodeDeliveryDetails.getAttributeName() + " poi "+ cognitoUserCodeDeliveryDetails.getDestination());
                }
                else {
                    // The user has already been confirmed
                }
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign-up failed, check exception for the cause
                Log.i(TAG, exception.getLocalizedMessage());
            }
        };
        userPool.signUpInBackground(email,password, userAttributes, null,signupCallback);
    }


    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String surname = surnameText.getText().toString();
        String city = cityText.getText().toString();
        String address = addressText.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();;

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }
        if (surname.isEmpty() || surname.length() < 2 || surname.length() > 50) {
            surnameText.setError("between 2 and 50 alphanumeric characters");
            valid = false;
        } else {
            surnameText.setError(null);
        }
        if (city.isEmpty() || city.length() < 2 || city.length() > 50) {
            cityText.setError("between 2 and 50 alphanumeric characters");
            valid = false;
        } else {
            cityText.setError(null);
        }
        if (address.isEmpty() || address.length() < 2 || address.length() > 70) {
            addressText.setError("between 2 and 70 alphanumeric characters");
            valid = false;
        } else {
            addressText.setError(null);
        }
        if ( gender.equals("Gender")){
            ((TextView)genderSpinner.getSelectedView()).setError("Select a gender");
            valid = false;
        }
        else{
            ((TextView)genderSpinner.getSelectedView()).setError(null);
        }

        return valid;
    }
}
