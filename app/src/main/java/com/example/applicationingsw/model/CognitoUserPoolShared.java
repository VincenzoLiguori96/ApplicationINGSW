package com.example.applicationingsw.model;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;
import com.example.applicationingsw.App;

public class CognitoUserPoolShared {
    private static CognitoUserPoolShared INSTANCE = new CognitoUserPoolShared();
    private CognitoUserPool userPool ;

    private CognitoUserPoolShared() {
        userPool = new CognitoUserPool(App.getAppContext(), "eu-west-1_KQhWEFGrY", "3kjf4fl4bmn540hfg7v105mvmb", null, Regions.EU_WEST_1);
    }

    public static CognitoUserPoolShared getInstance() {
        return(INSTANCE);
    }

    public CognitoUserPool getUserPool() {
        return userPool;
    }

}
