package com.example.applicationingsw.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.example.applicationingsw.R;
import com.example.applicationingsw.model.CognitoUserPoolShared;


public class RecoverPasswordActivity extends Activity {
    private EditText emailEditText;
    private EditText newPasswordEditText;
    private EditText newPasswordConfirmEditText;
    private Button requestCodeButton;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.forgot_password_activity);
        emailEditText = findViewById(R.id.forgotPasswordEmail);
        newPasswordEditText = findViewById(R.id.newPasswordForgot);
        newPasswordConfirmEditText = findViewById(R.id.confirmPasswordForgot);
        requestCodeButton = findViewById(R.id.requestCodeButton);
        requestCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newPasswordConfirmEditText.getText().toString().equals(newPasswordEditText.getText().toString())){
                    codeRequested();
                }
                else{
                    new AlertDialog.Builder(RecoverPasswordActivity.this)
                            .setTitle("Password not matching!")
                            .setMessage("Please reinsert your password. The one in the new password field is different from the one in the confirm new password field.")
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }


    public void codeRequested(){
        ForgotPasswordHandler handler = new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                if(progressDialog != null){
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
                new AlertDialog.Builder(RecoverPasswordActivity.this)
                        .setTitle("Password updated!")
                        .setMessage("Your password has been successfully changed.")
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().signOut();
                                Intent turnToLoginPage = new Intent(RecoverPasswordActivity.this,LoginActivity.class);
                                turnToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                RecoverPasswordActivity.this.startActivity(turnToLoginPage);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }

            @Override
            public void getResetCode(final ForgotPasswordContinuation continuation) {
                // A code will be sent, use the "continuation" object to continue with the forgot password process

                Log.e("RECOVER PASSWORD","Codice spedito a : " + continuation.getParameters().getDestination());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showEnterCodeDialog(continuation);
                    }
                });
            }

            /**
             * This is called for all fatal errors encountered during the password reset process
             * Probe {@exception} for cause of this failure.
             * @param exception
             */
            public void onFailure(Exception exception) {
                if(progressDialog != null){
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
                // Forgot password processing failed, probe the exception for cause
                Log.e("RECOVER PASSWORD","Errore spedito a : " + exception.getLocalizedMessage());
                new AlertDialog.Builder(RecoverPasswordActivity.this)
                        .setTitle("Error changing password!")
                        .setMessage("We encountered an error updating your password. Please try again. Error: " + exception.getLocalizedMessage())
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().signOut();
                                Intent turnToLoginPage = new Intent(RecoverPasswordActivity.this,LoginActivity.class);
                                turnToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                RecoverPasswordActivity.this.startActivity(turnToLoginPage);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        };

        CognitoUserPoolShared.getInstance().getUserPool().getUser(emailEditText.getText().toString()).forgotPasswordInBackground(handler);
    }

    public void showEnterCodeDialog(final ForgotPasswordContinuation continuation){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter verification code:");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.code_input_dialog, (ViewGroup) requestCodeButton.getParent(), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input_code);
        builder.setView(viewInflated);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressDialog = ProgressDialog.show(RecoverPasswordActivity.this, "Processing request...",
                        "Please wait", true);
                String text = input.getText().toString();
                confirmReset(continuation,text);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void confirmReset(ForgotPasswordContinuation continuation,String code){
        // When the code is available

        // Set the new password
        continuation.setPassword(newPasswordConfirmEditText.getText().toString());

        // Set the code to verify
        continuation.setVerificationCode(code);

        // Let the forgot password process proceed
        continuation.continueTask();
    }
}
