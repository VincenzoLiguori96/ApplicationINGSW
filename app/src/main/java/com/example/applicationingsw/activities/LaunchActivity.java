package com.example.applicationingsw.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.example.applicationingsw.R;
import com.example.applicationingsw.model.Cart;
import com.example.applicationingsw.model.CognitoUserPoolShared;

public class LaunchActivity extends AppCompatActivity {
    private ImageView logoView;
    private TextView slowConnectionHint;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeCart();
        setContentView(R.layout.launch_activity);
        slowConnectionHint = findViewById(R.id.loadingTextView);
        logoView = findViewById(R.id.logoImageView);
        logoView.setVisibility(View.VISIBLE);
        decideStartingActivity();
    }

    public void initializeCart(){
        Cart.getInstance().getCartIDFromAPI("https://6vqj00iw10.execute-api.eu-west-1.amazonaws.com/E-Commerce-Production/getcart");
    }

    public void decideStartingActivity(){
        CognitoUserPool userPool = CognitoUserPoolShared.getInstance().getUserPool();
        final CognitoUser savedUser = userPool.getCurrentUser();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                slowConnectionHint.setVisibility(View.VISIBLE);
                savedUser.getDetailsInBackground(new GetDetailsHandler() {
                    @Override
                    public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                        Intent myIntent = new Intent(LaunchActivity.this, DashboardActivity.class);
                        LaunchActivity.this.startActivity(myIntent);
                        LaunchActivity.this.finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Intent myIntent = new Intent(LaunchActivity.this, LoginActivity.class);
                        LaunchActivity.this.startActivity(myIntent);
                        LaunchActivity.this.finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }
                });
            }
        }, 2000);
    }

}
