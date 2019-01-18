package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RecyclerViewAdapterFoodItemListCart extends RecyclerView.Adapter<RecyclerViewAdapterFoodItemListCart.MyViewHolderFoodItemCart> {

    Context context;
    int cartvenpos;
    StorageReference storageRefCartFoodItem;
    View view;
    LayoutInflater layoutInflater;
    Home home = new Home();
    private CartFragment cartFragment = new CartFragment();


    public RecyclerViewAdapterFoodItemListCart (Context context, int pos) {
        this.context = context;
        this.cartvenpos = pos;
    }

    @NonNull
    @Override
    public MyViewHolderFoodItemCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.cart_fooditem_cardview, parent, false);
        return new MyViewHolderFoodItemCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderFoodItemCart holder, final int position) {

        final HashMap foodItemCart = Home.cartArr.get(cartvenpos).get(position);
        storageRefCartFoodItem = Home.storageRef.child(foodItemCart.get("pic").toString());

        Glide.with(holder.imageViewFoodItemCart.getContext())
                .using(new FirebaseImageLoader())
                .load(storageRefCartFoodItem)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBarFoodItemCart.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageViewFoodItemCart);
        holder.textViewFoodItemNameCart.setText(foodItemCart.get("name").toString());
        Double amt = Double.parseDouble(foodItemCart.get("amount").toString()) * Integer.parseInt(foodItemCart.get("quantity").toString());
        foodItemCart.put("totalAmount", Home.decimalFormatter.format(amt));
        Home.cartArr.get(cartvenpos).get(position).put("totalAmount", Home.decimalFormatter.format(amt));
        holder.textViewFoodItemAmountCart.setText(Home.currencyFc + Home.formatter.format(amt));
        holder.textViewFoodItemQuantity.setText(foodItemCart.get("quantity").toString());

        holder.decreaseButton.setImageResource(R.drawable.delete_white_icon);

        updates(holder, position);
        if(Integer.parseInt(foodItemCart.get("quantity").toString()) > 1) {
            holder.decreaseButton.setImageResource(R.drawable.decrease_icon);
        }else {
            holder.decreaseButton.setImageResource(R.drawable.delete_white_icon);
        }

        holder.increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(Home.cartArr.get(cartvenpos).get(position).get("quantity").toString()) + 1;
                if(quantity <= 15 ) {
                    Home.progressDialog.setMessage("Please Wait..");
                    Home.progressDialog.show();
                    Home.cartArr.get(cartvenpos).get(position).put("quantity", quantity );
                    holder.textViewFoodItemQuantity.setText(String.valueOf(quantity));
                    updates(holder, position);
                    cartFragment.notifyForUpdates();
                }else {
                    Toast.makeText(context, "Item Quantity MAX limit Reached", Toast.LENGTH_SHORT).show();
                }

                Home.cartCounter++;
                Home.cartCounterTextView.setText(String.valueOf(Home.cartCounter));
            }
        });

        holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(Home.cartArr.get(cartvenpos).get(position).get("quantity").toString()) - 1;

                if(quantity > 0) {
                    Home.progressDialog.setMessage("Please Wait..");
                    Home.progressDialog.show();

                    Home.cartArr.get(cartvenpos).get(position).put("quantity", quantity );
                    holder.textViewFoodItemQuantity.setText(String.valueOf(quantity));
                    updates(holder, position);
                    cartFragment.notifyForUpdates();

                    Home.cartCounter--;
                    Home.cartCounterTextView.setText(String.valueOf(Home.cartCounter));

                }else if(quantity == 0){
                    Home.progressDialog.setMessage("Please Wait..");
                    Home.progressDialog.show();
                    home.removefromcart(foodItemCart);
                    cartFragment.notifyForUpdates();
                    Toast.makeText(context, "Food Item Removed from Cart", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    // Upadtes
    public void updates (MyViewHolderFoodItemCart holder, int pos ) {
        Double amt = Double.parseDouble(Home.cartArr.get(cartvenpos).get(pos).get("amount").toString())
                * Integer.parseInt(Home.cartArr.get(cartvenpos).get(pos).get("quantity").toString());
        Home.cartArr.get(cartvenpos).get(pos).put("totalAmount", amt);

        String totalAmount = Home.currencyFc + Home.formatter.format(amt);
        holder.textViewFoodItemAmountCart.setText(totalAmount);
        home.calculateTaxAndVendorAmount(cartvenpos);
        home.calculateIHFAndGrandTotal();
    }


    @Override
    public int getItemCount() {
        return Home.cartArr.get(cartvenpos).size();
    }

    public static class MyViewHolderFoodItemCart extends RecyclerView.ViewHolder {

        ProgressBar progressBarFoodItemCart;
        ImageView imageViewFoodItemCart;
        TextView textViewFoodItemNameCart;
        TextView textViewFoodItemAmountCart;
        ImageButton increaseButton;
        ImageButton decreaseButton;
        TextView textViewFoodItemQuantity;


        public MyViewHolderFoodItemCart(View itemView) {
            super(itemView);

            progressBarFoodItemCart = itemView.findViewById(R.id.progressBarFoodItemCart);
            imageViewFoodItemCart = itemView.findViewById(R.id.imageViewFoodItemCart);
            textViewFoodItemNameCart = itemView.findViewById(R.id.textViewFoodItemNameCart);
            textViewFoodItemAmountCart = itemView.findViewById(R.id.textViewFoodItemAmountCart);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            textViewFoodItemQuantity = itemView.findViewById(R.id.textViewFoodItemQuantity);
        }
    }
}
