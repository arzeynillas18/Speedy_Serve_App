package com.example.speedyserve.deliveryFoodPanel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedyserve.DeliveryFoodPanel_BottomNavigation;
import com.example.speedyserve.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Delivery_ShippingOrder extends AppCompatActivity {

    TextView Address, ChefName, grandtotal, MobileNumber, Custname;
    Button Call, Shipped;
    LinearLayout l1, l2;
    String randomuid;
    String userid, Chefid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_shipping_order);
        Address = findViewById(R.id.ad3);
        ChefName = findViewById(R.id.chefname2);
        grandtotal = findViewById(R.id.Shiptotal1);
        MobileNumber = findViewById(R.id.ShipNumber1);
        Custname = findViewById(R.id.ShipName1);
        l1 = findViewById(R.id.linear3);
        l2 = findViewById(R.id.linearl1);
        Call = findViewById(R.id.call2);
        Shipped = findViewById(R.id.shipped2);
        initializeShipping();
    }

    private void initializeShipping() {
        randomuid = getIntent().getStringExtra("RandomUID");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DeliveryShipFinalOrders")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(randomuid)
                .child("OtherInformation");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DeliveryShipFinalOrders1 deliveryShipFinalOrders1 = dataSnapshot.getValue(DeliveryShipFinalOrders1.class);
                grandtotal.setText("₹ " + deliveryShipFinalOrders1.getGrandTotalPrice());
                Address.setText(deliveryShipFinalOrders1.getAddress());
                Custname.setText(deliveryShipFinalOrders1.getName());
                MobileNumber.setText("+91" + deliveryShipFinalOrders1.getMobileNumber());
                ChefName.setText("Chef " + deliveryShipFinalOrders1.getChefName());
                userid = deliveryShipFinalOrders1.getUserId();
                Chefid = deliveryShipFinalOrders1.getChefId();

                Shipped.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateOrderStatus();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if needed
            }
        });
    }

    private void updateOrderStatus() {
        FirebaseDatabase.getInstance().getReference("CustomerFinalOrders")
                .child(userid)
                .child(randomuid)
                .child("OtherInformation")
                .child("Status")
                .setValue("Your Order is delivered")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showAlert();
                    }
                });
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_ShippingOrder.this);
        builder.setMessage("Order is delivered, Now you can check for new Orders")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Delivery_ShippingOrder.this, DeliveryFoodPanel_BottomNavigation.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
