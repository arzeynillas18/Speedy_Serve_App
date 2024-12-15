package com.example.speedyserve.customerFoodPanel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.speedyserve.Customer;
import com.example.speedyserve.MainMenu;
import com.example.speedyserve.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;

import java.util.ArrayList;

public class CustomerProfileFragment extends Fragment {

    // Philippine States and Cities
    String[] Philippines = {"Cebu", "Davao", "Manila"};

    String[] Cebu = {
            "Cebu City", "Mandaue City", "Lapu-Lapu City", "Talisay City", "Minglanilla", "San Fernando", "Consolacion", "Cordova"
    };

    String[] Manila = {
            "Quezon City", "Makati", "Pasig", "Taguig", "Pasay"
    };

    String[] Davao = {
            "Davao City", "Davao del Sur", "Davao Oriental", "Compostela Valley"
    };

    // Cebu City Specific Areas
    String[] CebuCity = {
            "Apas", "Banilad", "Basak San Nicolas", "Camputhaw", "Capitol Site", "Guadalupe", "Lahug", "Mabolo", "Pardo", "Pasil", "Poblacion", "Sambag", "Tejeros"
    };

    EditText firstname, lastname, address;
    Spinner State, City, Suburban;
    TextView mobileno, Email;
    Button Update;
    LinearLayout password, LogOut;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String statee, cityy, suburban, email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Profile");
        View v = inflater.inflate(R.layout.fragment_customerprofile, null);

        firstname = v.findViewById(R.id.fnamee);
        lastname = v.findViewById(R.id.lnamee);
        address = v.findViewById(R.id.address);
        Email = v.findViewById(R.id.emailID);
        State = v.findViewById(R.id.statee);
        City = v.findViewById(R.id.cityy);
        Suburban = v.findViewById(R.id.sub);
        mobileno = v.findViewById(R.id.mobilenumber);
        Update = v.findViewById(R.id.update);
        password = v.findViewById(R.id.passwordlayout);
        LogOut = v.findViewById(R.id.logout_layout);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Customer").child(userId);

        // Fetch and populate user data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Customer customer = dataSnapshot.getValue(Customer.class);

                // Set the fields with customer data
                if (customer != null) {
                    firstname.setText(customer.getFirstName());
                    lastname.setText(customer.getLastName());
                    address.setText(customer.getLocalAddress());
                    Email.setText(customer.getEmailId());

                    // Call the method to set the state spinner selection
                    State.setSelection(getIndexByString(State, customer.getState()));
                    State.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            statee = parent.getItemAtPosition(position).toString().trim();
                            updateCitySpinner();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    City.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            cityy = parent.getItemAtPosition(position).toString().trim();
                            updateSuburbanSpinner();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    Suburban.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            suburban = parent.getItemAtPosition(position).toString().trim();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CustomerProfile", "Error loading data: " + databaseError.getMessage());
            }
        });

        updateInformation();
        return v;
    }

    private void updateInformation() {
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference("Customer").child(userId);

                // Update logic to save the changes to Firebase (first name, last name, address, state, city, suburban)
                // Your Firebase update code goes here.
                Log.d("CustomerProfile", "Updating customer data.");
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomerPassword.class);
                startActivity(intent);
            }
        });

        mobileno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CustomerPhonenumber.class);
                startActivity(i);
            }
        });

        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), MainMenu.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void updateCitySpinner() {
        ArrayList<String> list = new ArrayList<>();
        switch (statee) {
            case "Cebu":
                for (String text : Cebu) list.add(text);
                break;
            case "Manila":
                for (String text : Manila) list.add(text);
                break;
            case "Davao":
                for (String text : Davao) list.add(text);
                break;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        City.setAdapter(arrayAdapter);
    }

    private void updateSuburbanSpinner() {
        ArrayList<String> listt = new ArrayList<>();
        if (cityy.equals("Cebu City")) {
            for (String text : CebuCity) {
                listt.add(text);
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listt);
            Suburban.setAdapter(arrayAdapter);
        }
    }

    // Method to find the index of a string in a Spinner
    private int getIndexByString(Spinner spinner, String stringValue) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValue)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
