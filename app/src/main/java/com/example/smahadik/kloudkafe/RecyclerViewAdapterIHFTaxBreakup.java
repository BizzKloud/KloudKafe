package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecyclerViewAdapterIHFTaxBreakup extends RecyclerView.Adapter<RecyclerViewAdapterIHFTaxBreakup.MyViewHolderIHFTaxBreakup>{

    Context context;
    View view;
    LayoutInflater layoutInflater;

    public RecyclerViewAdapterIHFTaxBreakup (Context context) { this.context = context; }

    @NonNull
    @Override
    public MyViewHolderIHFTaxBreakup onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.tax_breakup_card, parent, false);
        return new MyViewHolderIHFTaxBreakup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderIHFTaxBreakup holder, int position) {

        String label = Home.cartFCTaxArr.get(position+1).get("desp").toString()
                + " ("
                + Home.cartFCTaxArr.get(position+1).get("taxName").toString()
                + ") @"
                + Home.cartFCTaxArr.get(position+1).get("taxPer").toString()
                + "%";
        holder.textViewTaxLabel.setText(label);
        holder.textViewTaxAmount.setText(Home.currencyFc + Home.formatter.format( Double.parseDouble(Home.cartFCTaxArr.get(position+1).get("amount").toString())) );
    }



    @Override
    public int getItemCount() {
        return Home.cartFCTaxArr.size()-1;
    }

    public class MyViewHolderIHFTaxBreakup extends RecyclerView.ViewHolder {

        TextView textViewTaxLabel;
        TextView textViewTaxAmount;

        public MyViewHolderIHFTaxBreakup(View itemView) {
            super(itemView);
            textViewTaxLabel = itemView.findViewById(R.id.textViewTaxLabel);
            textViewTaxAmount = itemView.findViewById(R.id.textViewTaxAmount);
        }
    }
}
