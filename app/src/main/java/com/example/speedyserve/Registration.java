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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;

public class Registration extends AppCompatActivity {


    String[] CebuCities = {"Cebu City", "Mandaue City", "Lapu-Lapu City", "Toledo", "Carcar", "Danao City"};
    TextInputLayout Fname, Lname, Email, Pass, cpass, mobileno, localaddress, area, pincode;
    Spinner Statespin, Cityspin;
    Button signup, Emaill, Phone;
    CountryCodePicker Cpp;
    FirebaseAuth FAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String fname, lname, emailid, password, confpassword, mobile, Localaddress, Area, Pincode, statee, cityy;
    String role = "Customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize fields
        Fname = findViewById(R.id.Fname);
        Lname = findViewById(R.id.Lname);
        Email = findViewById(R.id.Emailid);
        Pass = findViewById(R.id.Password);
        cpass = findViewById(R.id.confirmpass);
        mobileno = findViewById(R.id.Mobilenumber);
        localaddress = findViewById(R.id.Localaddress);
        pincode = findViewById(R.id.Pincode);
        Statespin = findViewById(R.id.Statee);
        Cityspin = findViewById(R.id.Citys);
        area = findViewById(R.id.Area);

        signup = findViewById(R.id.button);
        Emaill = findViewById(R.id.email);
        Phone = findViewById(R.id.phone);

        Cpp = findViewById(R.id.CountryCode);

        // Spinner for State selection (Philippines)
        Statespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object value = parent.getItemAtPosition(position);
                statee = value.toString().trim();
                if (statee.equals("Cebu")) {
                    ArrayList<String> list = new ArrayList<>();
                    for (String cities : CebuCities) {
                        list.add(cities);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Registration.this, android.R.layout.simple_spinner_item, list);
                    Cityspin.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Cityspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object value = parent.getItemAtPosition(position);
                cityy = value.toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Firebase setup
        databaseReference = FirebaseDatabase.getInstance().getReference("Customer");
        FAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(v -> {
            fname = Fname.getEditText().getText().toString().trim();
            lname = Lname.getEditText().getText().toString().trim();
            emailid = Email.getEditText().getText().toString().trim();
            mobile = mobileno.getEditText().getText().toString().trim();
            password = Pass.getEditText().getText().toString().trim();
            confpassword = cpass.getEditText().getText().toString().trim();
            Area = area.getEditText().getText().toString().trim();
            Localaddress = localaddress.getEditText().getText().toString().trim();
            Pincode = pincode.getEditText().getText().toString().trim();

            if (isValid()) {
                final ProgressDialog mDialog = new ProgressDialog(Registration.this);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setMessage("Registration in progress please wait......");
                mDialog.show();

                FAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(useridd);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("Role", role);
                        databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("Mobile No", mobile);
                            hashMap1.put("First Name", fname);
                            hashMap1.put("Last Name", lname);
                            hashMap1.put("EmailId", emailid);
                            hashMap1.put("City", cityy);
                            hashMap1.put("Area", Area);
                            hashMap1.put("Password", password);
                            hashMap1.put("Pincode", Pincode);
                            hashMap1.put("State", statee);
                            hashMap1.put("Confirm Password", confpassword);
                            hashMap1.put("Local Address", Localaddress);

                            FirebaseDatabase.getInstance().getReference("Customer")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(hashMap1).addOnCompleteListener(task2 -> {
                                        mDialog.dismiss();

                                        FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task3 -> {
                                            if (task3.isSuccessful()) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
                                                builder.setMessage("You Have Registered! Make Sure To Verify Your Email");
                                                builder.setCancelable(false);
                                                builder.setPositiveButton("Ok", (dialog, which) -> {
                                                    dialog.dismiss();

                                                    String phonenumber = Cpp.getSelectedCountryCodeWithPlus() + mobile;
                                                    Intent b = new Intent(Registration.this, VerifyPhone.class);
                                                    b.putExtra("phonenumber", phonenumber);
                                                    startActivity(b);
                                                });
                                                AlertDialog Alert = builder.create();
                                                Alert.show();
                                            } else {
                                                mDialog.dismiss();
                                                ReusableCodeForAll.ShowAlert(Registration.this, "Error", task3.getException().getMessage());
                                            }
                                        });

                                    });
                        });
                    } else {
                        mDialog.dismiss();
                        ReusableCodeForAll.ShowAlert(Registration.this, "Error", task.getException().getMessage());
                    }
                });
            }
        });

        Emaill.setOnClickListener(v -> {
            startActivity(new Intent(Registration.this, Login.class));
            finish();
        });

        Phone.setOnClickListener(v -> {
            startActivity(new Intent(Registration.this, Loginphone.class));
            finish();
        });
    }

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid() {
        Email.setErrorEnabled(false);
        Email.setError("");
        Fname.setErrorEnabled(false);
        Fname.setError("");
        Lname.setErrorEnabled(false);
        Lname.setError("");
        Pass.setErrorEnabled(false);
        Pass.setError("");
        mobileno.setErrorEnabled(false);
        mobileno.setError("");
        cpass.setErrorEnabled(false);
        cpass.setError("");
        area.setErrorEnabled(false);
        area.setError("");
        localaddress.setErrorEnabled(false);
        localaddress.setError("");
        pincode.setErrorEnabled(false);
        pincode.setError("");

        boolean isValid = false, isValidlocaladd = false, isValidlname = false, isValidname = false, isValidemail = false, isValidpassword = false, isValidconfpassword = false, isValidmobilenum = false, isValidarea = false, isValidpincode = false;
        if (TextUtils.isEmpty(fname)) {
            Fname.setErrorEnabled(true);
            Fname.setError("Enter First Name");
        } else {
            isValidname = true;
        }
        if (TextUtils.isEmpty(lname)) {
            Lname.setErrorEnabled(true);
            Lname.setError("Enter Last Name");
        } else {
            isValidlname = true;
        }
        if (TextUtils.isEmpty(emailid)) {
            Email.setErrorEnabled(true);
            Email.setError("Enter Email");
        } else if (!emailid.matches(emailpattern)) {
            Email.setErrorEnabled(true);
            Email.setError("Invalid Email Pattern");
        } else {
            isValidemail = true;
        }

        if (TextUtils.isEmpty(password)) {
            Pass.setErrorEnabled(true);
            Pass.setError("Enter Password");
        } else {
            isValidpassword = true;
        }

        if (TextUtils.isEmpty(confpassword)) {
            cpass.setErrorEnabled(true);
            cpass.setError("Enter Confirm Password");
        } else if (!password.equals(confpassword)) {
            cpass.setErrorEnabled(true);
            cpass.setError("Passwords don't match");
        } else {
            isValidconfpassword = true;
        }

        if (TextUtils.isEmpty(mobile)) {
            mobileno.setErrorEnabled(true);
            mobileno.setError("Enter Mobile Number");
        } else {
            isValidmobilenum = true;
        }
        if (TextUtils.isEmpty(Localaddress)) {
            localaddress.setErrorEnabled(true);
            localaddress.setError("Enter Local Address");
        } else {
            isValidlocaladd = true;
        }

        if (TextUtils.isEmpty(Area)) {
            area.setErrorEnabled(true);
            area.setError("Enter Area");
        } else {
            isValidarea = true;
        }

        if (TextUtils.isEmpty(Pincode)) {
            pincode.setErrorEnabled(true);
            pincode.setError("Enter Pincode");
        } else {
            isValidpincode = true;
        }

        isValid = isValidname && isValidlname && isValidemail && isValidpassword && isValidconfpassword && isValidmobilenum && isValidarea && isValidlocaladd && isValidpincode;

        return isValid;
    }
}
