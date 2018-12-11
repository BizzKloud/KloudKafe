package com.example.smahadik.kloudkafe;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;

public class CartFragment extends Fragment {

    View view;
    RecyclerView cartRecyclerView;
    RecyclerView recyclerviewIHFTaxPopUp;
    RecyclerView ymalRecyclerview;
    TextView textViewBaseAmountIHFTaxPopup;
    TextView textViewTotalTaxIHFTaxPopup;
    TextView payModeTitleAmountTextView;
    TextView textViewTotalPayMode;
    Button closePayModeButton;
    Button cashButton;
    LinearLayout ihfFCTax;
    LinearLayout continueOrderingButton;

    public static Button paynowButton;
    public static TextView textViewIHFPer;
    public static TextView textViewGrandTotal;
    public static RecyclerViewAdapterCartVendorList recyclerViewAdapterCartVendorList;
    public static RecyclerViewAdapterYmalList recyclerViewAdapterYmalList;
    Dialog iHFPopUp;
    Dialog payModePopUp;

    DocumentReference placeOrderDbRef;
    int lenOrderId;
    String newOrderId;
    String venOrderId;
    String [] venIdArr;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iHFPopUp = new Dialog(getContext());
        iHFPopUp.setCanceledOnTouchOutside(true);
        payModePopUp = new Dialog(getContext());
        payModePopUp.setCanceledOnTouchOutside(false);
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
                if(Home.cartArr.size() > 0) { showPopUpIHF(); }
            }
        });


        paynowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Home.cartArr.size() > 0) { showPopUpPayMode(); }
            }
        });

        continueOrderingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // continue ordering
//                Log.i("Continue Ordering" , "vendor POS: " + Home.lastVendorPosition );
                if (Home.navigation.getSelectedItemId() == R.id.navigation_menu) {
                    VendorItemListFragment.drawerLayout.closeDrawer(Gravity.END);
                }
                else if(Home.lastVendorPosition == -1) {
                    Home.navigation.setSelectedItemId(R.id.navigation_menu);
                }
                else {
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

    public void showPopUpIHF() {
        iHFPopUp.setContentView(R.layout.ihf_tax_breakup_popup);

        recyclerviewIHFTaxPopUp = iHFPopUp.findViewById(R.id.recyclerviewIHFTaxPopUp);
        RecyclerViewAdapterIHFTaxBreakup recyclerViewAdapterIHFTaxBreakup = new RecyclerViewAdapterIHFTaxBreakup(getContext());
        recyclerviewIHFTaxPopUp.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerviewIHFTaxPopUp.setAdapter(recyclerViewAdapterIHFTaxBreakup);

        textViewBaseAmountIHFTaxPopup = iHFPopUp.findViewById(R.id.textViewBaseAmountIHFTaxPopup);
        textViewTotalTaxIHFTaxPopup = iHFPopUp.findViewById(R.id.textViewTotalTaxIHFTaxPopup);
        textViewBaseAmountIHFTaxPopup.setText(Home.currencyFc + Home.formatter.format(Home.orderDetails.get("iHFBaseAmount")));
        textViewTotalTaxIHFTaxPopup.setText(Home.currencyFc + Home.formatter.format(Home.orderDetails.get("iHFTotalAmount")));

        iHFPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        iHFPopUp.show();
    }


    public void showPopUpPayMode() {
        payModePopUp.setContentView(R.layout.pay_mode_card);

        payModeTitleAmountTextView = payModePopUp.findViewById(R.id.payModeTitleAmountTextView);
        textViewTotalPayMode = payModePopUp.findViewById(R.id.textViewTotalPayMode);
        closePayModeButton = payModePopUp.findViewById(R.id.closePayModeButton);
        cashButton = payModePopUp.findViewById(R.id.cashButton);
        payModeTitleAmountTextView.setText(Home.formatter.format(Home.orderDetails.get("grandTotal")));
        textViewTotalPayMode.setText(Home.currencyFc + Home.formatter.format(Home.orderDetails.get("grandTotal")));

        closePayModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payModePopUp.dismiss();
            }
        });
        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrderId();
            }
        });

        payModePopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        payModePopUp.show();
    }


    public void setPayNow() {
        String paynowText = "PAY NOW" + "\n" +  Home.currencyFc + Home.formatter.format(Home.orderDetails.get("grandTotal"));
        paynowButton.setText(paynowText);
    }

    public void placeOrder() {

        placeOrderDbRef = Home.dbTransaction.document(Home.todaysDate).collection(Home.orderId).document("Total");
        Log.i("cart Array" , Home.cartArr.toString());
        Log.i("cart Vendor Array" , Home.cartVendorArr.toString());
        Log.i("cart vendor tax Array" , Home.cartVendorTaxArr.toString());
        Log.i("cart FC tax Array" , Home.cartFCTaxArr.toString());
        Log.i("cart order details Array" , Home.orderDetails.toString());

        placeOrderDbRef.set(Home.orderDetails);

        for (int i=0; i<Home.cartVendorArr.size(); i++) {
            venIdArr = Home.cartVendorArr.get(i).get("venid").toString().split("_");
            venOrderId = Home.orderId + "_" +  venIdArr[4];
            Home.cartVendorArr.get(i).put("orderId" , venOrderId);
            placeOrderDbRef = Home.dbTransaction.document(Home.todaysDate).collection(Home.orderId).document(venOrderId);
            placeOrderDbRef.set(Home.cartVendorArr.get(i));
        }

        for (int i=0; i<Home.cartVendorArr.size(); i++) {
            for (int j=0; j<Home.cartArr.get(i).size(); j++) {
                placeOrderDbRef = Home.dbTransaction.document(Home.todaysDate).collection(Home.orderId)
                        .document(Home.cartVendorArr.get(i).get("orderId") + "/FoodItems/" + j);
                placeOrderDbRef.set(Home.cartArr.get(i).get(j));
            }

            for (int j=0; j<Home.cartVendorTaxArr.get(i).size(); j++) {
                placeOrderDbRef = Home.dbTransaction.document(Home.todaysDate).collection(Home.orderId)
                        .document(Home.cartVendorArr.get(i).get("orderId") + "/VendorTax/" + j);
                placeOrderDbRef.set(Home.cartVendorTaxArr.get(i).get(j));
            }
        }
        if(Home.freebieArr.size() > 0) {
            placeOrderDbRef = Home.dbTransaction.document(Home.todaysDate).collection(Home.orderId).document("Freebie");
            placeOrderDbRef.set(Home.freebieArr.get(0));
            placeOrderDbRef = Home.db.document(Home.fcDetails.get("fcid").toString() + "/FreebieM/" + Home.freebieArr.get(0).get("frb_id"));
            placeOrderDbRef.update("frb_id" , Integer.parseInt(Home.freebieArr.get(0).get("count").toString())-1);
        }

        for (int i=0; i<Home.cartFCTaxArr.size(); i++) {
            placeOrderDbRef = Home.dbTransaction.document(Home.todaysDate).collection(Home.orderId).document("Tax_Fcm/FoodCourtTax/" + i);
            placeOrderDbRef.set(Home.cartFCTaxArr.get(i));
        }
//        Home.home.clearcart();
        Home.orderPlaced = true;
        payModePopUp.dismiss();

        // GOTO ORDERS PAGE
        Home.navigation.setSelectedItemId(R.id.navigation_order);
//        Home.ft = Home.fragmentManager.beginTransaction();
//        Home.ft.replace(R.id.rootLayout, new OrdersPageFragment());
//        Home.ft.commit();
    }


    public String getOrderId() {

        //OrderID
        Home.firestore.collection("transactions").document(Home.fcDetails.get("fcid").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    DocumentReference dbref = Home.firestore.document("transactions/" + Home.fcDetails.get("fcid").toString());
                    if (document.exists()) {
                        Log.i("Doc exists" , "DocumentSnapshot data: " + document.getData());
                        Home.orderId = document.get("orderId").toString();
                    } else {
                        Log.i("Doc not exists " ,"Doc Created");
                        dbref.set(Home.fcDetails);
                        Home.orderId = "000000001";
                    }
                    Home.orderDetails.put("orderId" , Home.orderId);
                    Home.orderDetails.put("time" , Calendar.getInstance().getTime());
                    newOrderId = String.valueOf(Integer.parseInt(Home.orderId)+1);
                    lenOrderId = newOrderId.length();
                    for (int i=0; i< 9-lenOrderId; i++) {
                        newOrderId = "0" + newOrderId;
                    }
                    dbref.update("orderId" , newOrderId );
                    Home.progressDialog.setMessage("Placing Your Order");
                    Home.progressDialog.show();
                    placeOrder();
                } else {
                    Log.i("Doc not exists --  ERROR " , "No such document");
                    Toast.makeText(getContext(), "ERROR getting ORDER-ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return Home.orderId;
    }

    public void notifyForUpdates() {
        recyclerViewAdapterCartVendorList.notifyDataSetChanged();
        recyclerViewAdapterYmalList.notifyDataSetChanged();
        textViewGrandTotal.setText(Home.currencyFc + Home.formatter.format(Home.orderDetails.get("baseAmount")));
        // change also grandTotal Amount that is in BUtton
        setPayNow();
    }

}
