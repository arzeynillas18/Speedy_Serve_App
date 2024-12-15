package com.example.speedyserve.customerFoodPanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.speedyserve.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.example.speedyserve.R;
import com.example.speedyserve.UpdateDishModel;

public class CustomerHomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    private List<UpdateDishModel> updateDishModelList;
    private CustomerHomeAdapter adapter;
    String State, City, Area;
    DatabaseReference dataa, databaseReference;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customerhome, null);
        getActivity().setTitle("Home");
        recyclerView = v.findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.move);
        recyclerView.startAnimation(animation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateDishModelList = new ArrayList<>();
        swipeRefreshLayout = v.findViewById(R.id.swipelayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.Red);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                dataa = FirebaseDatabase.getInstance().getReference("Customer").child(userid);
                dataa.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Customer custo = snapshot.getValue(Customer.class);

                        if (custo != null) {
                            State = custo.getState();
                            City = custo.getCity();
                            Area = custo.getArea();
                            Log.d("CustomerHomeFragment", "State: " + State + ", City: " + City + ", Area: " + Area);
                        } else {
                            Log.e("CustomerHomeFragment", "Customer data is null");
                        }

                        customermenu();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CustomerHomeFragment", "Firebase database error: " + error.getMessage());
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onRefresh() {
        customermenu();
    }

    private void customermenu() {
        swipeRefreshLayout.setRefreshing(true);

        // Add null checks for State, City, and Area
        if (State == null || City == null || Area == null) {
            Log.e("CustomerHomeFragment", "Location data is null: State=" + State + ", City=" + City + ", Area=" + Area);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("FoodDetails")
                .child(State)
                .child(City)
                .child(Area);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateDishModelList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                        UpdateDishModel updateDishModel = snapshot2.getValue(UpdateDishModel.class);
                        if (updateDishModel != null) {
                            updateDishModelList.add(updateDishModel);
                        }
                    }
                }
                adapter = new CustomerHomeAdapter(getContext(), updateDishModelList);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e("CustomerHomeFragment", "Database error: " + error.getMessage());
            }
        });
    }
}
