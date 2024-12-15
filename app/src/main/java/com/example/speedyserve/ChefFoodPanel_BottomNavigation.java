package com.example.speedyserve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.speedyserve.chefFoodPanel.ChefHomeFragment;
import com.example.speedyserve.chefFoodPanel.ChefPendingOrdersFragment;
import com.example.speedyserve.chefFoodPanel.ChefProfileFragment;
import com.example.speedyserve.chefFoodPanel.ChefOrderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.speedyserve.R;


public class ChefFoodPanel_BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_food_panel_bottom_navigation);

        BottomNavigationView navigationView = findViewById(R.id.chef_bottom_navigation);
        navigationView.setOnItemSelectedListener(this); // Updated listener method

        String name = getIntent().getStringExtra("PAGE");
        if (name != null) {
            switch (name.toLowerCase()) {
                case "orderpage":
                    loadFragment(new ChefPendingOrdersFragment());
                    break;
                case "confirmpage":
                    loadFragment(new ChefOrderFragment());
                    break;
                case "acceptorderpage":
                case "deliveredpage":
                    loadFragment(new ChefHomeFragment());
                    break;
                default:
                    loadFragment(new ChefHomeFragment());
            }
        } else {
            loadFragment(new ChefHomeFragment()); // Default to ChefHomeFragment if no intent
        }
    }

    // Utility method to load fragments
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment) // Correct ID
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        int itemId = menuItem.getItemId();

        if (itemId == R.id.chefHome) {
            fragment = new ChefHomeFragment();
        } else if (itemId == R.id.PendingOrders) {
            fragment = new ChefPendingOrdersFragment();
        } else if (itemId == R.id.Orders) {
            fragment = new ChefOrderFragment();
        } else if (itemId == R.id.chefProfile) {
            fragment = new ChefProfileFragment();
        }

        return loadFragment(fragment);
    }
}