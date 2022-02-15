package com.example.project;

import android.app.Application;
import android.content.Context;

public class AppBase extends Application {
    private static AppBase instance;
    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        FireBaseManager.getInstance();
        DataSourceManager.getInstance();
    }
}
