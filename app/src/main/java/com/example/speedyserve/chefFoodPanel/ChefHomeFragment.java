package com.example.speedyserve.chefFoodPanel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.speedyserve.Chef;
import com.example.speedyserve.MainMenu;
import com.example.speedyserve.R;
import com.example.speedyserve.UpdateDishModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChefHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<UpdateDishModel> updateDishModelList;
    private ChefHomeAdapter adapter;
    private DatabaseReference dataa;
    private String State, City, Area;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chef_home, null);
        getActivity().setTitle("Home");
        setHasOptionsMenu(true);

        recyclerView = v.findViewById(R.id.Recycle_menu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateDishModelList = new ArrayList<>();

        // Check for authenticated user before proceeding
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showErrorToUser("Please log in again");
            return v;
        }

        String userid = currentUser.getUid();
        dataa = FirebaseDatabase.getInstance().getReference("Chef").child(userid);
        dataa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Additional null checks and logging
                if (snapshot.exists()) {
                    Chef cheff = snapshot.getValue(Chef.class);
                    if (cheff != null) {
                        State = cheff.getState();
                        City = cheff.getCity();
                        Area = cheff.getArea();

                        // Validate retrieved data
                        if (isValidChefData()) {
                            chefDishes();
                        } else {
                            Log.e("ChefHomeFragment", "Retrieved chef data is incomplete");
                            showErrorToUser("Unable to load chef information");
                        }
                    } else {
                        Log.e("ChefHomeFragment", "Chef data could not be parsed");
                        showErrorToUser("Error loading chef profile");
                    }
                } else {
                    Log.e("ChefHomeFragment", "No chef data exists for this user");
                    showErrorToUser("Chef profile not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChefHomeFragment", "Database error: " + error.getMessage());
                showErrorToUser("Unable to retrieve chef information");
            }
        });

        return v;
    }

    // Validate chef data
    private boolean isValidChefData() {
        return State != null && !State.trim().isEmpty() &&
                City != null && !City.trim().isEmpty() &&
                Area != null && !Area.trim().isEmpty();
    }

    private void chefDishes() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("ChefHomeFragment", "No authenticated user");
            showErrorToUser("Please log in again");
            return;
        }

        String useridd = currentUser.getUid();

        // Check for null and empty values before proceeding with the database query
        if (isValidChefData()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FoodDetails")
                    .child(State).child(City).child(Area).child(useridd);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    updateDishModelList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            UpdateDishModel updateDishModel = snapshot1.getValue(UpdateDishModel.class);
                            if (updateDishModel != null) {
                                updateDishModelList.add(updateDishModel);
                            }
                        }

                        if (!updateDishModelList.isEmpty()) {
                            adapter = new ChefHomeAdapter(getContext(), updateDishModelList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.w("ChefHomeFragment", "No dishes found for this chef");
                            showErrorToUser("No dishes available");
                        }
                    } else {
                        Log.w("ChefHomeFragment", "No food details found for this location");
                        showErrorToUser("No dishes found in your area");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ChefHomeFragment", "Database error in chefDishes: " + error.getMessage());
                    showErrorToUser("Error loading dishes");
                }
            });
        } else {
            Log.e("ChefHomeFragment", "Location data is incomplete");
            showErrorToUser("Unable to load chef location");
        }
    }

    // Show user-friendly error messages
    private void showErrorToUser(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idd = item.getItemId();
        if (idd == R.id.LogOut) {
            Logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), MainMenu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}