package com.example.speedyserve.chefFoodPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.speedyserve.Chef;
import com.example.speedyserve.chefFoodPanel.FoodSupplyDetails;
import com.example.speedyserve.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class chef_postDish extends AppCompatActivity {

    private Button post_dish;
    private Spinner Dishes;
    private TextInputLayout desc, qty, pri;
    private String description, quantity, price, dishes;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference dataaa;
    private FirebaseAuth FAuth;
    private String ChefId;
    private String RandomUId;
    private String State, City, Sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_post_dish);

        // Initialize Firebase components
        FAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        initializeViews();

        // Retrieve and validate chef data
        retrieveChefData();
    }

    private void initializeViews() {
        Dishes = findViewById(R.id.dishes);
        desc = findViewById(R.id.description);
        qty = findViewById(R.id.quantity);
        pri = findViewById(R.id.price);
        post_dish = findViewById(R.id.post);
    }

    private void retrieveChefData() {
        // Check for authenticated user
        FirebaseUser currentUser = FAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userid = currentUser.getUid();
        dataaa = FirebaseDatabase.getInstance().getReference("Chef").child(userid);
        dataaa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    // Safely retrieve and validate Chef data
                    Chef chefc = dataSnapshot.getValue(Chef.class);
                    if (chefc != null) {
                        // Validate retrieved data
                        State = validateString(chefc.getState(), "State");
                        City = validateString(chefc.getCity(), "City");
                        Sub = validateString(chefc.getArea(), "Area");

                        // Setup UI listeners only if data is valid
                        setupListeners();
                    } else {
                        Log.e("chef_postDish", "Chef data is null");
                        showErrorAndFinish("Unable to load chef profile");
                    }
                } catch (Exception e) {
                    Log.e("chef_postDish", "Error processing chef data", e);
                    showErrorAndFinish("Error processing chef information");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("chef_postDish", "Database error: " + databaseError.getMessage());
                showErrorAndFinish("Unable to retrieve chef information");
            }
        });
    }

    private String validateString(String value, String fieldName) {
        if (TextUtils.isEmpty(value)) {
            Log.w("chef_postDish", fieldName + " is empty or null");
            return "";
        }
        return value.trim();
    }

    private void setupListeners() {
        // Post dish listener
        post_dish.setOnClickListener(v -> {
            dishes = Dishes.getSelectedItem().toString().trim();
            description = desc.getEditText().getText().toString().trim();
            quantity = qty.getEditText().getText().toString().trim();
            price = pri.getEditText().getText().toString().trim();

            if (isValid()) {
                uploadDishData();
            }
        });
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private boolean isValid() {
        // Reset previous errors
        resetErrors();

        boolean isValidDescription = validateField(desc, description, "Description is Required");
        boolean isValidQuantity = validateField(qty, quantity, "Quantity is Required");
        boolean isValidPrice = validateField(pri, price, "Price is Required");

        return isValidDescription && isValidQuantity && isValidPrice;
    }

    private boolean validateField(TextInputLayout layout, String value, String errorMessage) {
        if (TextUtils.isEmpty(value)) {
            layout.setErrorEnabled(true);
            layout.setError(errorMessage);
            return false;
        }
        layout.setErrorEnabled(false);
        layout.setError(null);
        return true;
    }

    private void resetErrors() {
        desc.setErrorEnabled(false);
        desc.setError("");
        qty.setErrorEnabled(false);
        qty.setError("");
        pri.setErrorEnabled(false);
        pri.setError("");
    }

    private void uploadDishData() {
        RandomUId = UUID.randomUUID().toString();
        ChefId = FAuth.getCurrentUser().getUid();

        // Ensure location data is not empty before uploading
        if (TextUtils.isEmpty(State) || TextUtils.isEmpty(City) || TextUtils.isEmpty(Sub)) {
            Toast.makeText(this, "Location information is incomplete", Toast.LENGTH_SHORT).show();
            return;
        }

        FoodSupplyDetails info = new FoodSupplyDetails(
                dishes, quantity, price, description,
                "", RandomUId, ChefId
        );

        FirebaseDatabase.getInstance().getReference("FoodSupplyDetails")
                .child(State)
                .child(City)
                .child(Sub)
                .child(ChefId)
                .child(RandomUId)
                .setValue(info)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Dish posted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity after successful upload
                    } else {
                        Toast.makeText(this, "Failed to post dish", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
