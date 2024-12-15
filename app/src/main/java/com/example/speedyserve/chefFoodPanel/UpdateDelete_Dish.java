package com.example.speedyserve.chefFoodPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.speedyserve.Chef;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import com.example.speedyserve.ChefFoodPanel_BottomNavigation;
import com.example.speedyserve.R;
import com.example.speedyserve.UpdateDishModel;

public class UpdateDelete_Dish extends AppCompatActivity {

    TextInputLayout desc, qty, pri;
    TextView Dishname;
    ImageButton imageButton;
    Uri imageuri;
    String dburi;
    Button Update_dish, Delete_dish;
    String description, quantity, price, dishes, ChefId;
    String RandomUID;
    StorageReference ref;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth FAuth;
    String ID;
    private ProgressDialog progressDialog;
    DatabaseReference dataa;
    String State, City, Area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_dish);

        desc = findViewById(R.id.description);
        qty = findViewById(R.id.Quantity);
        pri = findViewById(R.id.price);
        Dishname = findViewById(R.id.dish_name);
        imageButton = findViewById(R.id.image_upload);
        Update_dish = findViewById(R.id.Updatedish);
        Delete_dish = findViewById(R.id.Deletedish);
        ID = getIntent().getStringExtra("updatedeletedish");

        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dataa = FirebaseDatabase.getInstance().getReference("Chef").child(userid);
        dataa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chef cheff = snapshot.getValue(Chef.class);
                State = cheff.getState();
                City = cheff.getCity();
                Area = cheff.getArea();

                Update_dish.setOnClickListener(v -> {
                    description = desc.getEditText().getText().toString().trim();
                    quantity = qty.getEditText().getText().toString().trim();
                    price = pri.getEditText().getText().toString().trim();

                    if (isValid()) {
                        if (imageuri != null) {
                            uploadImage();
                        } else {
                            updatedesc(dburi);
                        }
                    }
                });

                Delete_dish.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateDelete_Dish.this);
                    builder.setMessage("Are you sure you want to Delete Dish");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseDatabase.getInstance().getReference("FoodDetails").child(State).child(City).child(Area)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(ID).removeValue();
                        AlertDialog.Builder food = new AlertDialog.Builder(UpdateDelete_Dish.this);
                        food.setMessage("Your Dish Has Been Deleted!");
                        food.setPositiveButton("OK", (dialog1, which1) -> startActivity(new Intent(UpdateDelete_Dish.this, ChefFoodPanel_BottomNavigation.class)));
                        AlertDialog alert = food.create();
                        alert.show();
                    });
                    builder.setNegativeButton("NO", (dialog, which) -> dialog.cancel());
                    AlertDialog alert = builder.create();
                    alert.show();
                });

                String useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();
                progressDialog = new ProgressDialog(UpdateDelete_Dish.this);
                databaseReference = FirebaseDatabase.getInstance().getReference("FoodDetails").child(State).child(City).child(Area)
                        .child(useridd).child(ID);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UpdateDishModel updateDishModel = snapshot.getValue(UpdateDishModel.class);
                        desc.getEditText().setText(updateDishModel.getDescription());
                        qty.getEditText().setText(updateDishModel.getQuantity());
                        Dishname.setText("Dish name:" + updateDishModel.getDishes());
                        dishes = updateDishModel.getDishes();
                        pri.getEditText().setText(updateDishModel.getPrice());
                        dburi = updateDishModel.getImageURL();

                        // Use Glide with transformations
                        Glide.with(UpdateDelete_Dish.this)
                                .load(dburi)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(16))) // Rounded corners transformation
                                .into(imageButton);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                FAuth = FirebaseAuth.getInstance();
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();

                imageButton.setOnClickListener(v -> pickImage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updatedesc(String buri) {
        ChefId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FoodDetails info = new FoodDetails(dishes, quantity, price, description, buri, ID, ChefId);
        FirebaseDatabase.getInstance().getReference("FoodDetails").child(State).child(City).child(Area)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(ID)
                .setValue(info).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateDelete_Dish.this, "Dish Updated Successfully!", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImage() {
        if (imageuri != null) {
            progressDialog.setTitle("Uploading....");
            progressDialog.show();
            RandomUID = UUID.randomUUID().toString();
            ref = storageReference.child(RandomUID);
            ref.putFile(imageuri).addOnSuccessListener(taskSnapshot ->
                            ref.getDownloadUrl().addOnSuccessListener(uri -> updatedesc(String.valueOf(uri))))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateDelete_Dish.this, "Failed:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Upload " + (int) progress + "%");
                        progressDialog.setCanceledOnTouchOutside(false);
                    });
        }
    }

    private boolean isValid() {
        desc.setErrorEnabled(false);
        desc.setError("");
        qty.setErrorEnabled(false);
        qty.setError("");
        pri.setErrorEnabled(false);
        pri.setError("");

        boolean isValidDescription = false, isValidPrice = false, isValidQuantity = false, isValid = false;
        if (TextUtils.isEmpty(description)) {
            desc.setErrorEnabled(true);
            desc.setError("Description is Required");
        } else {
            desc.setError(null);
            isValidDescription = true;
        }
        if (TextUtils.isEmpty(quantity)) {
            qty.setErrorEnabled(true);
            qty.setError("Enter number of Plates or Items");
        } else {
            isValidQuantity = true;
        }
        if (TextUtils.isEmpty(price)) {
            pri.setErrorEnabled(true);
            pri.setError("Please Mention Price");
        } else {
            isValidPrice = true;
        }
        isValid = isValidDescription && isValidQuantity && isValidPrice;
        return isValid;
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100 && data != null) {
            imageuri = data.getData();

            // Set the picked image using Glide
            Glide.with(this)
                    .load(imageuri)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                    .into(imageButton);
        }
    }
}
