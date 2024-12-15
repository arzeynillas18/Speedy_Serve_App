package com.example.speedyserve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.speedyserve.deliveryFoodPanel.DeliveryPendingOrderFragment;
import com.example.speedyserve.deliveryFoodPanel.DeliveryShipOrderFragment;

public class DeliveryFoodPanel_BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_food_panel_bottom_navigation);

        BottomNavigationView navigationView = findViewById(R.id.delivery_bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        // Load the default fragment if savedInstanceState is null
        if (savedInstanceState == null) {
            // You can set any default fragment here (e.g., DeliveryPendingOrderFragment)
            loadFragment(new DeliveryPendingOrderFragment());
            navigationView.setSelectedItemId(R.id.pendingorders); // Make sure the correct item is selected
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        // Check which menu item is selected and load the corresponding fragment
        if (itemId == R.id.shiporders) {
            fragment = new DeliveryShipOrderFragment();
        } else if (itemId == R.id.pendingorders) {
            fragment = new DeliveryPendingOrderFragment();
        }

        // Replace the current fragment with the selected one
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            // Begin fragment transaction to load the selected fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_containerbott, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
