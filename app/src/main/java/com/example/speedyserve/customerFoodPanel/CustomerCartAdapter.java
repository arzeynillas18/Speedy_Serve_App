package com.example.speedyserve.customerFoodPanel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.speedyserve.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class CustomerCartAdapter extends RecyclerView.Adapter<CustomerCartAdapter.ViewHolder> {

    private Context mcontext;
    private List<Cart> cartModellist;
    static int total = 0;

    public CustomerCartAdapter(Context context, List<Cart> cartModellist) {
        this.cartModellist = cartModellist;
        this.mcontext = context;
        total = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.cart_placeorder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Cart cart = cartModellist.get(position);
        holder.dishname.setText(cart.getDishName());
        holder.PriceRs.setText("Price: ₹ " + cart.getPrice());
        holder.Qty.setText("× " + cart.getDishQuantity());
        holder.Totalrs.setText("Total: ₹ " + cart.getTotalprice());

        int currentQuantity = Integer.parseInt(cart.getDishQuantity());
        holder.quantity.setText(String.valueOf(currentQuantity));
        final int dishprice = Integer.parseInt(cart.getPrice());

        // Increment button
        holder.btnIncrement.setOnClickListener(v -> {
            int newQuantity = currentQuantity + 1;
            updateQuantity(holder, cart, newQuantity, dishprice);
        });

        // Decrement button
        holder.btnDecrement.setOnClickListener(v -> {
            int newQuantity = currentQuantity - 1;
            if (newQuantity > 0) {
                updateQuantity(holder, cart, newQuantity, dishprice);
            } else {
                removeItem(cart);
            }
        });

        CustomerCartFragment.grandt.setText("Grand Total: ₹ " + total);
        FirebaseDatabase.getInstance().getReference("Cart").child("GrandTotal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("GrandTotal").setValue(String.valueOf(total));
    }

    private void updateQuantity(ViewHolder holder, Cart cart, int newQuantity, int dishprice) {
        int totalprice = newQuantity * dishprice;
        holder.quantity.setText(String.valueOf(newQuantity));
        holder.Totalrs.setText("Total: ₹ " + totalprice);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("DishID", cart.getDishID());
        hashMap.put("DishName", cart.getDishName());
        hashMap.put("DishQuantity", String.valueOf(newQuantity));
        hashMap.put("Price", String.valueOf(dishprice));
        hashMap.put("Totalprice", String.valueOf(totalprice));
        hashMap.put("ChefId", cart.getChefId());

        FirebaseDatabase.getInstance().getReference("Cart").child("CartItems")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(cart.getDishID()).setValue(hashMap);
    }

    private void removeItem(Cart cart) {
        FirebaseDatabase.getInstance().getReference("Cart").child("CartItems")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(cart.getDishID()).removeValue();
    }

    @Override
    public int getItemCount() {
        return cartModellist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dishname, PriceRs, Qty, Totalrs, quantity;
        ImageView btnIncrement, btnDecrement;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dishname = itemView.findViewById(R.id.Dishname);
            PriceRs = itemView.findViewById(R.id.pricers);
            Qty = itemView.findViewById(R.id.qty);
            Totalrs = itemView.findViewById(R.id.totalrs);
            quantity = itemView.findViewById(R.id.quantity);
            btnIncrement = itemView.findViewById(R.id.btn_increment);
            btnDecrement = itemView.findViewById(R.id.btn_decrement);
        }
    }
}
