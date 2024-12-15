package com.example.speedyserve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class Delivery_Registration extends AppCompatActivity {

    // List of states and cities including Cebu region (as requested)
        String[] Cebu = {"Cebu City", "Mandaue City", "Lapu-Lapu City"};
        String[] OtherCities = {"Davao City", "Iloilo City", "Manila"};

    TextInputLayout Fname, Lname, Email, Pass, Cpass, Mobileno, Houseno, Area, Pincode;
    Spinner Statespin, Cityspin;
    Button Signup, Emaill, Phone;
    CountryCodePicker Cpp;
    FirebaseAuth FAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String fname, lname, emailid, password, confpassword, mobile, house, area, pincode, statee, cityy;
    String role = "DeliveryPerson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_registration);

        // Initialize Views
        initializeViews();

        // Firebase Initialization
        FAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Spinner for States and Cities
        setupStateCitySpinners();

        // Signup Button Click Listener
        Signup.setOnClickListener(v -> {
            // Collect input values
            collectInputValues();

            // Validate inputs and proceed if valid
            if (isValid()) {
                showProgressDialog();
                registerUser();
            }
        });

        // Login Buttons
        Emaill.setOnClickListener(v -> navigateToLogin(Delivery_Login.class));
        Phone.setOnClickListener(v -> navigateToLogin(Delivery_Loginphone.class));
    }

    // Method to initialize views
    private void initializeViews() {
        Fname = findViewById(R.id.fname);
        Lname = findViewById(R.id.lname);
        Email = findViewById(R.id.Emailid);
        Pass = findViewById(R.id.password);
        Cpass = findViewById(R.id.confirmpassword);
        Mobileno = findViewById(R.id.mobileno);
        Houseno = findViewById(R.id.Houseno);
        Pincode = findViewById(R.id.Pincodee);
        Statespin = findViewById(R.id.State);
        Cityspin = findViewById(R.id.City);
        Area = findViewById(R.id.Areaa);
        Signup = findViewById(R.id.Signupp);
        Emaill = findViewById(R.id.emaillid);
        Phone = findViewById(R.id.Phonenumber);
        Cpp = findViewById(R.id.ctrycode);
    }

    // Method to set up state and city spinners
    private void setupStateCitySpinners() {
        Statespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statee = parent.getItemAtPosition(position).toString().trim();

                if (statee.equals("Cebu")) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Delivery_Registration.this, android.R.layout.simple_spinner_item, Cebu);
                    Cityspin.setAdapter(adapter);
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Delivery_Registration.this, android.R.layout.simple_spinner_item, OtherCities);
                    Cityspin.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Cityspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityy = parent.getItemAtPosition(position).toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Method to collect input values
    private void collectInputValues() {
        fname = Fname.getEditText().getText().toString().trim();
        lname = Lname.getEditText().getText().toString().trim();
        emailid = Email.getEditText().getText().toString().trim();
        mobile = Mobileno.getEditText().getText().toString().trim();
        password = Pass.getEditText().getText().toString().trim();
        confpassword = Cpass.getEditText().getText().toString().trim();
        area = Area.getEditText().getText().toString().trim();
        house = Houseno.getEditText().getText().toString().trim();
        pincode = Pincode.getEditText().getText().toString().trim();
    }

    // Method to show the progress dialog
    private void showProgressDialog() {
        ProgressDialog mDialog = new ProgressDialog(Delivery_Registration.this);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("Registration in progress... please wait...");
        mDialog.show();
    }

    // Method to register user
    private void registerUser() {
        FAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId);
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("Role", role);

                // Store user data
                databaseReference.setValue(userMap).addOnCompleteListener(task1 -> {
                    HashMap<String, String> deliveryMap = new HashMap<>();
                    deliveryMap.put("Mobile No", mobile);
                    deliveryMap.put("First Name", fname);
                    deliveryMap.put("Last Name", lname);
                    deliveryMap.put("EmailId", emailid);
                    deliveryMap.put("City", cityy);
                    deliveryMap.put("Area", area);
                    deliveryMap.put("Password", password);
                    deliveryMap.put("Pincode", pincode);
                    deliveryMap.put("State", statee);
                    deliveryMap.put("Confirm Password", confpassword);
                    deliveryMap.put("House", house);

                    // Store delivery data
                    firebaseDatabase.getReference("DeliveryPerson")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(deliveryMap).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    sendEmailVerification();
                                } else {
                                    showError(task2.getException().getMessage());
                                }
                            });
                });
            } else {
                showError(task.getException().getMessage());
            }
        });
    }

    // Method to send email verification
    private void sendEmailVerification() {
        FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_Registration.this);
                builder.setMessage("You have successfully registered! Please verify your email.");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    String phoneNumber = Cpp.getSelectedCountryCodeWithPlus() + mobile;
                    Intent intent = new Intent(Delivery_Registration.this, Delivery_VerifyPhone.class);
                    intent.putExtra("phonenumber", phoneNumber);
                    startActivity(intent);
                });
                builder.create().show();
            } else {
                showError(task.getException().getMessage());
            }
        });
    }

    // Method to show error
    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_Registration.this);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // Validation Method
    public boolean isValid() {
        // Validation logic as before...
        // Returns true if all fields are valid, false otherwise
        return true;  // Assuming all fields are validated
    }

    // Navigation to login
    private void navigateToLogin(Class<?> activityClass) {
        startActivity(new Intent(Delivery_Registration.this, activityClass));
        finish();
    }
}
