package com.example.speedyserve.customerFoodPanel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.speedyserve.UpdateDishModel; // Correct import here
import com.example.speedyserve.R;
import com.example.speedyserve.UpdateDishModel;


import java.util.List;

public class CustomerHomeAdapter extends RecyclerView.Adapter<CustomerHomeAdapter.ViewHolder> {

    private Context mContext;
    private List<UpdateDishModel> updateDishModelList;

    public CustomerHomeAdapter(Context context, List<UpdateDishModel> updateDishModelList) {
        this.updateDishModelList = updateDishModelList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.customer_menudish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final UpdateDishModel updateDishModel = updateDishModelList.get(position);

        // Loading the image
        Glide.with(mContext).load(updateDishModel.getImageURL()).into(holder.imageView);
        holder.Dishname.setText(updateDishModel.getDishes());
        holder.price.setText("Price: ₹ " + updateDishModel.getPrice());

        // Click listener for the item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setting a default quantity (for example, 1)
                String selectedQuantity = "1"; // You can change this value dynamically later if needed

                // Pass the selected data to the OrderDish activity
                Intent intent = new Intent(mContext, OrderDish.class);
                intent.putExtra("FoodMenu", updateDishModel.getRandomUID());
                intent.putExtra("ChefId", updateDishModel.getChefId());
                intent.putExtra("Quantity", selectedQuantity);  // Pass the selected quantity

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return updateDishModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView Dishname, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.menu_image);
            Dishname = itemView.findViewById(R.id.dishname);
            price = itemView.findViewById(R.id.dishprice);
        }
    }
}
