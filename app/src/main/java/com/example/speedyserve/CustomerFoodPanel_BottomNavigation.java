package com.example.speedyserve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.speedyserve.customerFoodPanel.CustomerCartFragment;
import com.example.speedyserve.customerFoodPanel.CustomerHomeFragment;
import com.example.speedyserve.customerFoodPanel.CustomerOrdersFragment;
import com.example.speedyserve.customerFoodPanel.CustomerProfileFragment;

public class CustomerFoodPanel_BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_food_panel_bottom_navigation);

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        if (navigationView == null) {
            throw new NullPointerException("BottomNavigationView not found in layout");
        }
        navigationView.setOnNavigationItemSelectedListener(this);

        // Load default fragment
        loadFragment(new CustomerHomeFragment());
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int itemId = item.getItemId();
        if (itemId == R.id.cust_Home) {
            fragment = new CustomerHomeFragment();
        } else if (itemId == R.id.cart) {
            fragment = new CustomerCartFragment();
        } else if (itemId == R.id.cust_profile) {
            fragment = new CustomerProfileFragment();
        } else if (itemId == R.id.Cust_order) {
            fragment = new CustomerOrdersFragment();
        } else {
            return false;
        }

        return loadFragment(fragment);
    }



    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
