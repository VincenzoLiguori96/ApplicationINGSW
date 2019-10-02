package com.example.applicationingsw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.example.applicationingsw.model.CognitoUserPoolShared;

public class ChangePasswordActivity extends Activity {
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText newPasswordConfirmEditText;
    private Button buttonChangePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.change_password_activity);
        oldPasswordEditText = findViewById(R.id.oldPassword);
        newPasswordEditText = findViewById(R.id.newPasswordChange);
        newPasswordConfirmEditText = findViewById(R.id.confirmPasswordChange);
        buttonChangePassword = findViewById(R.id.passwordChangeDone);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordClicked();
            }
        });

    }

    public void changePasswordClicked(){
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String newPasswordConfirm = newPasswordConfirmEditText.getText().toString();
        if(newPassword.equals(newPasswordConfirm)){
            changePasswordWithCognito(oldPassword,newPassword);
        }
        else{
            new AlertDialog.Builder(this)
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

    public void changePasswordWithCognito(String oldPassword,String newPassword){
        GenericHandler handler = new GenericHandler() {

            @Override
            public void onSuccess() {
                new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setTitle("Password changed!")
                        .setMessage("Your password have been correctly updated.")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                ChangePasswordActivity.this.finish();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Exception exception) {
            Log.e("CHANGEPASSWORDACTIVITY","porco dio",exception);
                new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setTitle("Can not change password!")
                        .setMessage("Your password can not be updated. Error: " + exception.getMessage())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent turnToLoginPage = new Intent(ChangePasswordActivity.this,LoginActivity.class);
                                turnToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ChangePasswordActivity.this.startActivity(turnToLoginPage);
                            }
                        })
                        .show();
            }
        };
        CognitoUserPoolShared.getInstance().getUserPool().getCurrentUser().changePasswordInBackground(oldPassword, newPassword, handler);
    }
}
