package com.agrosmart;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;

/**
 * AgroSmart Application Class.
 * Ensures proper application-wide locale initialization and persistence across app lifecycles.
 */
public class AgroSmartApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        SessionManager session = new SessionManager(base);
        super.attachBaseContext(LocaleHelper.setLocale(base, session.getLanguage()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SessionManager session = new SessionManager(this);
        LocaleHelper.setLocale(this, session.getLanguage());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SessionManager session = new SessionManager(this);
        LocaleHelper.setLocale(this, session.getLanguage());
    }
}
