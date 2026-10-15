package com.agrosmart.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;

/**
 * AgroSmart BaseActivity
 * Ensures all child activities automatically apply the user's selected language
 * (English, Hindi, Marathi) consistently across the entire application backstack.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager session = new SessionManager(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, session.getLanguage()));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
