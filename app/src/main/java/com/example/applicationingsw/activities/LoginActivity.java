package com.example.applicationingsw.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.example.applicationingsw.R;
import com.example.applicationingsw.model.CognitoUserPoolShared;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    //Istanza di un user pool cognito
    private CognitoUserPool userPool ;
    private CognitoUser currentUser;
    private EditText emailText ;
    private EditText passwordText ;
    private Button loginButton ;
    private TextView signupLink ;
    private TextView recoverPasswordLink;
    private ProgressDialog progressDialog ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        signupLink = findViewById(R.id.link_signup);
        recoverPasswordLink = findViewById(R.id.forgot_password_link);
        recoverPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPassword();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
        userPool = CognitoUserPoolShared.getInstance().getUserPool();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        loginWithCognito(email,password);

    }

    public void recoverPassword(){
        Intent i = new Intent(this,RecoverPasswordActivity.class);
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataBack) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Bundle extras = dataBack.getExtras();
                String email_string = extras.getString("EXTRA_EMAIL");
                String password_string = extras.getString("EXTRA_PASSWORD");
                emailText.setText(email_string);
                passwordText.setText(password_string);
            }
        }
    }

    public void loginWithCognito(String email, final String password){
        // Callback handler for the sign-in process
        currentUser = userPool.getUser(email);
        CognitoUser saved = userPool.getCurrentUser();
        Log.i(TAG, "Utente attuale:" + currentUser.getUserId());
        Log.i(TAG, "Saved:" + saved.getUserId());
        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {


            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.i(TAG,"Utente connesso");
                progressDialog.dismiss();
                Intent myIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                LoginActivity.this.startActivity(myIntent);
                LoginActivity.this.finish();
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                // The API needs user sign-in credentials to continue
                Log.i(TAG, "Utente con cui mi sono autenticato attuale:" + userId);
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, password, null);

                // Pass the user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                // Allow the sign-in to continue
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
                multiFactorAuthenticationContinuation.continueTask();
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.i(TAG, "Continuation da authentication challenge: "+ continuation.getChallengeName() + continuation.getParameters() + continuation);
                continuation.continueTask();
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign-in failed, check exception for the cause
                Log.e(TAG, "Errore al login");
                progressDialog.dismiss();
                showAlertDialog(LoginActivity.this, "An error occured", "Error: "+exception.getLocalizedMessage()+ " Please retry.");
                loginButton.setEnabled(true);

            }
        };

// Sign in the user
        currentUser.getSessionInBackground(authenticationHandler);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (!checkPassword(password)) {
            passwordText.setError("The password must be at least 8 characters long, and must contain at least one capital letter and one number.");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    public boolean checkPassword(String password){
        String passwordPattern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(passwordPattern);
        java.util.regex.Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
