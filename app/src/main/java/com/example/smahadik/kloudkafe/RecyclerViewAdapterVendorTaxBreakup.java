package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class RecyclerViewAdapterVendorTaxBreakup extends RecyclerView.Adapter<RecyclerViewAdapterVendorTaxBreakup.MyViewHolderVendorTaxBreakup> {

    Context context;
    int position;
    View view;
    LayoutInflater layoutInflater;

    public RecyclerViewAdapterVendorTaxBreakup (Context context, int position) {
        this.context = context;
        this.position = position;
    }

    @NonNull
    @Override
    public MyViewHolderVendorTaxBreakup onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.tax_breakup_card, parent , false);
        return new RecyclerViewAdapterVendorTaxBreakup.MyViewHolderVendorTaxBreakup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderVendorTaxBreakup holder, int position) {
        String label = Home.cartVendorTaxArr.get(this.position).get(position).get("desp").toString()
                + " ("
                + Home.cartVendorTaxArr.get(this.position).get(position).get("taxName").toString()
                + ") @"
                + Home.cartVendorTaxArr.get(this.position).get(position).get("taxPer").toString()
                + "%";
        holder.textViewTaxLabel.setText(label);
        holder.textViewTaxAmount.setText(Home.currencyFc +  Home.formatter.format(Double.parseDouble(Home.cartVendorTaxArr.get(this.position).get(position).get("amount").toString())) )  ;
    }


    @Override
    public int getItemCount() {
        return Home.cartVendorTaxArr.get(this.position).size();
    }

    public class MyViewHolderVendorTaxBreakup extends RecyclerView.ViewHolder {
        TextView textViewTaxLabel;
        TextView textViewTaxAmount;

        public MyViewHolderVendorTaxBreakup(View itemView) {
            super(itemView);
            textViewTaxLabel = itemView.findViewById(R.id.textViewTaxLabel);
            textViewTaxAmount = itemView.findViewById(R.id.textViewTaxAmount);
        }
    }
}
