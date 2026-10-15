package com.agrosmart.activities.farmer;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.agrosmart.R;
import com.agrosmart.databinding.ActivityFarmerDashboardBinding;
import com.agrosmart.fragments.BookingsFragment;
import com.agrosmart.fragments.HomeFragment;
import com.agrosmart.fragments.MarketplaceFragment;
import com.agrosmart.fragments.ProfileFragment;
import com.agrosmart.fragments.ServicesFragment;
import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;

public class FarmerDashboardActivity extends AppCompatActivity {

    private ActivityFarmerDashboardBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager session = new SessionManager(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, session.getLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFarmerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default to Home Fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_services) {
                selectedFragment = new ServicesFragment();
            } else if (itemId == R.id.nav_marketplace) {
                selectedFragment = new MarketplaceFragment();
            } else if (itemId == R.id.nav_bookings) {
                selectedFragment = new BookingsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}
