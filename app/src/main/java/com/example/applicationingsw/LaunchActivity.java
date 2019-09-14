package com.example.applicationingsw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.example.applicationingsw.model.CognitoUserPoolShared;

public class LaunchActivity extends Activity {
    private final String TAG = "Launch Activity";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CognitoUserPool userPool = CognitoUserPoolShared.getInstance().getUserPool();
        CognitoUser savedUser = userPool.getCurrentUser();
        if (savedUser.getUserId() == null ){
            Log.i(TAG, "Nessun utente salvato, vado al login");
            Intent myIntent = new Intent(LaunchActivity.this, LoginActivity.class);
            LaunchActivity.this.startActivity(myIntent);
            this.finish();
        }
        else{
            Log.i(TAG, "Utente trovato,vado all'app");
            Log.i(TAG,"Utente: " + savedUser.getUserId());
            Intent myIntent = new Intent(LaunchActivity.this, DashboardActivity.class);
            LaunchActivity.this.startActivity(myIntent);
            this.finish();
        }
    }
}
