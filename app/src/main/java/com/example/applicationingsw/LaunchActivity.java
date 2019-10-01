package com.example.applicationingsw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.example.applicationingsw.model.CognitoUserPoolShared;

public class LaunchActivity extends Activity {
    private final String TAG = "Launch Activity";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CognitoUserPool userPool = CognitoUserPoolShared.getInstance().getUserPool();
        CognitoUser savedUser = userPool.getCurrentUser();
        //TODO trova un modo per risolvere se non c'è internet
        savedUser.getDetailsInBackground(new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                Log.e(TAG, " utente salvato, vado al login");
                Intent myIntent = new Intent(LaunchActivity.this, DashboardActivity.class);
                LaunchActivity.this.startActivity(myIntent);
                LaunchActivity.this.finish();
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "Nessun utente salvato, vado al login" + exception.getLocalizedMessage());
                Intent myIntent = new Intent(LaunchActivity.this, LoginActivity.class);
                LaunchActivity.this.startActivity(myIntent);
                LaunchActivity.this.finish();
            }
        });
    }
}
