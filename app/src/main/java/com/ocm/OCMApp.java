package com.ocm;

import com.firebase.client.Firebase;

/**
 * Created by Aswathy_G on 8/29/2017.
 */

public class OCMApp extends android.app.Application {
    private static OCMApp application;
    @Override
    public void onCreate() {
        super.onCreate();

        //Previous versions of Firebase
        Firebase.setAndroidContext(this);
        application = this;

    }
    public static OCMApp getApp() {
        return application;
    }

}
