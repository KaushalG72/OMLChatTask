package com.sunilkumar.omlchattask.caching;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class OfflineCaching extends Application {   // implement in next version

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}