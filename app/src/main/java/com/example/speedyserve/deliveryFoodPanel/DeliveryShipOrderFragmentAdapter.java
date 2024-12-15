package com.example.speedyserve.deliveryFoodPanel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.speedyserve.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DeliveryShipOrderFragmentAdapter extends RecyclerView.Adapter<DeliveryShipOrderFragmentAdapter.ViewHolder> {

    private Context context;
    private List<DeliveryShipFinalOrders1> deliveryShipFinalOrders1list;

    public DeliveryShipOrderFragmentAdapter(Context context, List<DeliveryShipFinalOrders1> deliveryShipFinalOrders1list) {
        this.deliveryShipFinalOrders1list = deliveryShipFinalOrders1list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.delivery_shiporders, parent, false);
        return new DeliveryShipOrderFragmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final DeliveryShipFinalOrders1 deliveryShipFinalOrders1 = deliveryShipFinalOrders1list.get(position);
        holder.Address.setText(deliveryShipFinalOrders1.getAddress());
        holder.grandtotalprice.setText("Grand Total: ₹ " + deliveryShipFinalOrders1.getGrandTotalPrice());
        holder.mobilenumber.setText("+91" + deliveryShipFinalOrders1.getMobileNumber());
        final String random = deliveryShipFinalOrders1.getRandomUID();
        final String userid = deliveryShipFinalOrders1.getUserId();
        holder.Vieworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DeliveryShipOrderView.class);
                intent.putExtra("RandomUID", random);
                context.startActivity(intent);
            }
        });

        holder.ShipOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference("CustomerFinalOrders").child(userid).child(random).child("OtherInformation").child("Status").setValue("Your Order is on the way...").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(context, Delivery_ShippingOrder.class);
                        intent.putExtra("RandomUID", random);
                        context.startActivity(intent);
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return deliveryShipFinalOrders1list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Address, grandtotalprice, mobilenumber;
        Button Vieworder, ShipOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Address = itemView.findViewById(R.id.ad2);
            mobilenumber = itemView.findViewById(R.id.MB2);
            grandtotalprice = itemView.findViewById(R.id.TP2);
            Vieworder = itemView.findViewById(R.id.view2);
            ShipOrder = itemView.findViewById(R.id.ship2);
        }
    }
}
