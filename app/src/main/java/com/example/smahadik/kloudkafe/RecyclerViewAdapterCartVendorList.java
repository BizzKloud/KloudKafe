package com.example.smahadik.kloudkafe;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RecyclerViewAdapterCartVendorList extends RecyclerView.Adapter<RecyclerViewAdapterCartVendorList.MyViewHolderCartVendorList>{

    Context context;
    LayoutInflater layoutInflater;
    View view;

    public RecyclerViewAdapterCartVendorList (Context context) { this.context = context; }

    @NonNull
    @Override
    public MyViewHolderCartVendorList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.cart_vendor_andits_fooditem_cardview, parent , false);
        return new MyViewHolderCartVendorList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderCartVendorList holder, final int position) {

        for(int i=0; i<Home.vendorArr.size(); i++) {
            if(Home.cartArr.get(position).get(0).get("venid").toString().equals(Home.vendorArr.get(i).get("venid").toString())) {
                holder.textViewCartVendorNameTitle.setText(Home.vendorArr.get(i).get("name").toString());
                break;
            }
        }

        RecyclerViewAdapterFoodItemListCart recyclerViewAdapterFoodItemListCart = new RecyclerViewAdapterFoodItemListCart(context , position);
        holder.recyclerviewFoodItemListCart.setLayoutManager(new GridLayoutManager(context, 1));
        holder.recyclerviewFoodItemListCart.setAdapter(recyclerViewAdapterFoodItemListCart);

        updateTaxAndSubTotal(holder, position);

        holder.vendorTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(holder, position);
            }
        });

    }

    public void showPopUp(MyViewHolderCartVendorList holder, int position) {
        holder.vendorTaxPopUp = new Dialog(context);
        holder.vendorTaxPopUp.setContentView(R.layout.tax_breakup_popup);

        holder.recyclerviewVendorTaxPopUp = holder.vendorTaxPopUp.findViewById(R.id.recyclerviewVendorTaxPopUp);
        RecyclerViewAdapterVendorTaxBreakup recyclerViewAdapterVendorTaxBreakup = new RecyclerViewAdapterVendorTaxBreakup(context, position);
        holder.recyclerviewVendorTaxPopUp.setLayoutManager(new GridLayoutManager(context, 1));
        holder.recyclerviewVendorTaxPopUp.setAdapter(recyclerViewAdapterVendorTaxBreakup);

        holder.textViewBaseAmountVendorTaxPopup = holder.vendorTaxPopUp.findViewById(R.id.textViewBaseAmountVendorTaxPopup);
        holder.textViewTotalTaxVendorTaxPopup = holder.vendorTaxPopUp.findViewById(R.id.textViewTotalTaxVendorTaxPopup);
        holder.textViewBaseAmountVendorTaxPopup.setText(Home.currencyFc + Home.formatter.format(Home.cartVendorArr.get(position).get("baseAmount")));
        holder.textViewTotalTaxVendorTaxPopup.setText(Home.currencyFc + Home.formatter.format(Home.cartVendorArr.get(position).get("totalTax")));

        holder.vendorTaxPopUp.show();
    }

    public void updateTaxAndSubTotal(MyViewHolderCartVendorList holder, int position) {
        holder.textViewVendorTax.setText(Home.currencyFc + Home.formatter.format(Home.cartVendorArr.get(position).get("totalTax")));
        holder.textViewVendorSubTotal.setText(Home.currencyFc + Home.formatter.format(Home.cartVendorArr.get(position).get("subTotalAmount")));
    }

    @Override
    public int getItemCount() {
        return Home.cartArr.size();
    }

    public static class MyViewHolderCartVendorList extends RecyclerView.ViewHolder {

        TextView textViewCartVendorNameTitle;
        RecyclerView recyclerviewFoodItemListCart;
        LinearLayout vendorTax;
        TextView textViewVendorTax;
        TextView textViewVendorSubTotal;
        Dialog vendorTaxPopUp;
        RecyclerView recyclerviewVendorTaxPopUp;
        TextView textViewBaseAmountVendorTaxPopup;
        TextView textViewTotalTaxVendorTaxPopup;



        public MyViewHolderCartVendorList(View itemView) {
            super(itemView);

            textViewCartVendorNameTitle = itemView.findViewById(R.id.textViewCartVendorNameTitle);
            recyclerviewFoodItemListCart = itemView.findViewById(R.id.recyclerviewFoodItemListCart);
            textViewVendorTax = itemView.findViewById(R.id.textViewVendorTax);
            vendorTax = itemView.findViewById(R.id.vendorTax);
            textViewVendorSubTotal = itemView.findViewById(R.id.textViewVendorSubTotal);
        }
    }
}
