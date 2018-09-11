package com.example.smahadik.kloudkafe;

import android.app.Dialog;
import android.app.Fragment;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class CartFragment extends Fragment {

    View view;
    RecyclerView cartRecyclerView;
    RecyclerView recyclerviewIHFTaxPopUp;
    RecyclerView ymalRecyclerview;
    TextView textViewBaseAmountIHFTaxPopup;
    TextView textViewTotalTaxIHFTaxPopup;
    LinearLayout ihfFCTax;
    LinearLayout continueOrderingButton;

    public static Button paynowButton;
    public static TextView textViewIHFPer;
    public static TextView textViewGrandTotal;
    public static RecyclerViewAdapterCartVendorList recyclerViewAdapterCartVendorList;
    public static RecyclerViewAdapterYmalList recyclerViewAdapterYmalList;
    Dialog iHFPopUp;

//    Home home = new Home();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iHFPopUp = new Dialog(getContext());
        iHFPopUp.setCanceledOnTouchOutside(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.cart_fragment, container, false);

        cartRecyclerView = view.findViewById(R.id.cartRecyclerview);
        recyclerViewAdapterCartVendorList = new RecyclerViewAdapterCartVendorList(getContext());
        cartRecyclerView.setLayoutManager(new GridLayoutManager(getContext() , 1));
        cartRecyclerView.setAdapter(recyclerViewAdapterCartVendorList);

        ymalRecyclerview = view.findViewById(R.id.ymalRecyclerview);
        recyclerViewAdapterYmalList = new RecyclerViewAdapterYmalList(getContext());
        ymalRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
        ymalRecyclerview.setAdapter(recyclerViewAdapterYmalList);



        textViewIHFPer = view.findViewById(R.id.textViewIHFPer);
        textViewGrandTotal = view.findViewById(R.id.textViewGrandTotal);
        ihfFCTax = view.findViewById(R.id.ihfFC);
        paynowButton = view.findViewById(R.id.paynowButton);
        continueOrderingButton = view.findViewById(R.id.continueOrderingButton);

        ihfFCTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Home.cartArr.size() > 0) { showPopUp(); }
            }
        });
        continueOrderingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // continue ordering
                Log.i("Continue Ordering" , "vendor POS: " + Home.lastVendorPosition );
                if(Home.lastVendorPosition == -1) {
                    Home.navigation.setSelectedItemId(R.id.navigation_menu);
                }else {
                    int lastvenpos = Home.lastVendorPosition;
                    Home.navigation.setSelectedItemId(R.id.navigation_menu);
                    Home.vendorPosition = lastvenpos;
                    Home.lastVendorPosition = lastvenpos;

                    Home.ft = Home.fragmentManager.beginTransaction();
                    Fragment frag =  Home.fragmentManager.findFragmentByTag(VendorItemListFragment.class.getName());
                    Home.ft.replace(R.id.rootLayout, frag);
                    Home.ft.commit();
                }
            }
        });

        textViewGrandTotal.setText(Home.currencyFc + Home.formatter.format(Home.orderDetails.get("baseAmount")));
        textViewIHFPer.setText(Home.removedecimalFormatter.format(Home.fcTaxArr.get(0).get("taxPer")) + " %");
        setPayNow();

        return view;
    }

    public void showPopUp() {
        iHFPopUp.setContentView(R.layout.ihf_tax_breakup_popup);

        recyclerviewIHFTaxPopUp = iHFPopUp.findViewById(R.id.recyclerviewIHFTaxPopUp);
        RecyclerViewAdapterIHFTaxBreakup recyclerViewAdapterIHFTaxBreakup = new RecyclerViewAdapterIHFTaxBreakup(getContext());
        recyclerviewIHFTaxPopUp.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerviewIHFTaxPopUp.setAdapter(recyclerViewAdapterIHFTaxBreakup);

        textViewBaseAmountIHFTaxPopup = iHFPopUp.findViewById(R.id.textViewBaseAmountIHFTaxPopup);
        textViewTotalTaxIHFTaxPopup = iHFPopUp.findViewById(R.id.textViewTotalTaxIHFTaxPopup);
        textViewBaseAmountIHFTaxPopup.setText(Home.currencyFc + Home.formatter.format(Home.orderDetails.get("iHFBaseAmount")));
        textViewTotalTaxIHFTaxPopup.setText(Home.currencyFc + Home.formatter.format(Home.orderDetails.get("iHFTotalAmount")));

        iHFPopUp.show();
    }

    public void setPayNow() {
        String paynowText = "PAY NOW" + "\n" +  Home.currencyFc + Home.formatter.format(Home.orderDetails.get("grandTotal"));
        paynowButton.setText(paynowText);
    }


    public void notifyForUpdates() {
        recyclerViewAdapterCartVendorList.notifyDataSetChanged();
        recyclerViewAdapterYmalList.notifyDataSetChanged();
        textViewGrandTotal.setText(Home.currencyFc + Home.formatter.format(Home.orderDetails.get("baseAmount")));
        // change also grandTotal Amount that is in BUtton
        setPayNow();
    }

}
