package com.example.applicationingsw;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.example.applicationingsw.model.CognitoUserPoolShared;

public class LaunchActivity extends AppCompatActivity {
    private final String TAG = "Launch Activity";
    private ImageView logoView;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_activity);
        logoView = findViewById(R.id.logoImageView);
        logoView.setVisibility(View.VISIBLE);
        CognitoUserPool userPool = CognitoUserPoolShared.getInstance().getUserPool();
        final CognitoUser savedUser = userPool.getCurrentUser();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                savedUser.getDetailsInBackground(new GetDetailsHandler() {
                    @Override
                    public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                        Log.e(TAG, " utente salvato, vado al login");
                        Intent myIntent = new Intent(LaunchActivity.this, DashboardActivity.class);
                        LaunchActivity.this.startActivity(myIntent);
                        LaunchActivity.this.finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e(TAG, "Nessun utente salvato, vado al login" + exception.getLocalizedMessage());
                        Intent myIntent = new Intent(LaunchActivity.this, LoginActivity.class);
                        LaunchActivity.this.startActivity(myIntent);
                        LaunchActivity.this.finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }
                });
            }
        }, 2000);

    }

    public void loadAnimation(){

    }
}
