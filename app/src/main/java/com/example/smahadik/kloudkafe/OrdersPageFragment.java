package com.example.smahadik.kloudkafe;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class OrdersPageFragment extends Fragment {

    View view;

    ExpandableListView expandanbleordersOrdersList;
    ExpandableListAdapterOrdersList expandableListAdapterOrdersList;

    int previousGroup = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.orderspage_fragment, container, false);

        Log.i("ORDER PLACED BOOLEAN" , Home.orderPlaced.toString());
        if(Home.orderPlaced) {
            Log.i("was inside IF" , "TRUE");
            getOrderDetails(Home.orderId);
            new CountDownTimer(4000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    Home.progressDialog.dismiss();
                    setOrderListAdapter();
                    Home.orderPlaced = false;
                }
            }.start();
        } else {
            Log.i("was inside IF" , "FALSE");
            setOrderListAdapter();
        }


        return view;
    }




    public void setOrderListAdapter() {

        ArrayList<LinkedHashMap> venAr = new ArrayList<>();
        ArrayList <ArrayList <LinkedHashMap>> fddAr = new ArrayList<>();
//        ArrayList <LinkedHashMap> samplefdarr = new ArrayList<>();

        for (int i=0; i<Home.orderIdArr.size(); i++) {
            for (int j=0; j<Home.orderVendorArr.get(i).size(); j++) {
                LinkedHashMap<String , String> linkedHashMap = new LinkedHashMap<String, String>(Home.orderVendorArr.get(i).get(j));
                venAr.add(linkedHashMap);
                ArrayList <LinkedHashMap> samplefdarr = new ArrayList<>();
//                Log.i("NEW NEW VENDOR ARRAY" , venAr.toString());

                for (int k=0; k<Home.orderFoodItemArr.get(i).get(j).size(); k++) {
                    LinkedHashMap<String , String> samplelinkedHashMap = new LinkedHashMap<String, String>(Home.orderFoodItemArr.get(i).get(j).get(k));
                    samplefdarr.add(samplelinkedHashMap);
//                    Log.i("NEW FOOD ITEM" , samplelinkedHashMap.toString());
                }
                fddAr.add(samplefdarr);
//                Log.i("NEW NEW FOOD ARRAY" , fddAr.toString());
            }
//            Log.i("NEW NEW FOOD ITEM LIST" , fddAr.toString());
        }

        expandanbleordersOrdersList = view.findViewById(R.id.expandanbleordersOrdersList);
        expandanbleordersOrdersList.setIndicatorBounds(Home.width - GetPixelFromDips(45), Home.width - GetPixelFromDips(5));
        expandableListAdapterOrdersList = new ExpandableListAdapterOrdersList(getContext(), venAr , fddAr );
        expandanbleordersOrdersList.setAdapter(expandableListAdapterOrdersList);
        expandableListAdapterOrdersList.notifyDataSetChanged();

        expandanbleordersOrdersList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup) { expandanbleordersOrdersList.collapseGroup(previousGroup); }
                previousGroup = groupPosition;
            }
        });

    }


    public void getOrderDetails(final String orderId) {
        Home.orderIdArr.add(orderId);

        //getting Order-vendor details
        Home.dbTransaction.document(Home.todaysDate).collection(orderId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                ArrayList<HashMap> Odr1 = new ArrayList<>();
                for(QueryDocumentSnapshot vendorDoc : queryDocumentSnapshots) {
                    if(vendorDoc.getData().containsKey("oHStatus")) {
                        Odr1.add((HashMap) vendorDoc.getData());
                    }
                }

                int id = findOrder(Odr1);
                if(id != -1) {
                    // ADD to to particular field
                    Home.orderVendorArr.set(id, Odr1);
                    Log.i("UPDATED ORDER ID DETAILS" , Home.orderIdArr.toString());
                    Log.i("UPDATED VENDOR ORDER DETAILS" , Home.orderVendorArr.toString());
                    // NOTIFICATION ==> call
                    setOrderListAdapter();


                } else {
                    Home.orderVendorArr.add(Odr1);
                    Log.i(" ORDER ID DETAILS" , Home.orderIdArr.toString());
                    Log.i("VENDOR ORDER DETAILS" , Home.orderVendorArr.toString());
//                    Log.i(" FOODITEM DETAILS" , Home.orderFoodItemArr.toString());
                    getOrderFoodItemDetails(orderId);
                }

            }
        });
    }


    private void getOrderFoodItemDetails (String orderId) {

        final ArrayList<ArrayList<HashMap>> venfoodItemList = new ArrayList<>();
        final ArrayList<HashMap> OdrVenList = Home.orderVendorArr.get(Home.orderVendorArr.size()-1);
//        Log.i(" CURRENT OdrVenList :" , OdrVenList.toString());

        for (int i=0; i<OdrVenList.size(); i++) {
            final HashMap odrven = OdrVenList.get(i);
//            venfoodItemList.add(getFoodItemList(OdrVenList.get(i)));

            Home.dbTransaction.document(Home.todaysDate).collection(orderId + "/" + odrven.get("orderId") + "/FoodItems").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    ArrayList<HashMap> foodItemList = new ArrayList<>();

                    Log.i("CURRENT VEN", odrven.toString() );
                    for(QueryDocumentSnapshot foodItemDoc : queryDocumentSnapshots) {
                        foodItemList.add((HashMap) foodItemDoc.getData());
//                            Log.i("INSIDE FOODITEM-1 DETAILS" , foodItemList.toString());
//                            Log.i("INSIDE VEN-FOODITEM-1 DETAILS" , venfoodItemList.toString());
//                            Log.i("INSIDE FOODITEM DETAILS" , Home.orderFoodItemArr.toString());
                    }
                    venfoodItemList.add(foodItemList);
                    Log.i("OUTSIDE VEN-FOODITEM-1 DETAILS" , venfoodItemList.toString());
//
                    if (venfoodItemList.size() == OdrVenList.size()) {
                        Home.orderFoodItemArr.add(venfoodItemList);
                        Log.i(" FOODITEM DETAILS at getOderFoodItems" , Home.orderFoodItemArr.toString());
                    }
                }
            });

        }

    }


    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    public int findOrder(ArrayList<HashMap> vendor) {

        for(int i=0; i<Home.orderVendorArr.size(); i++) {

            for(int j=0; j<Home.orderVendorArr.get(i).size(); j++){
                if (Home.orderVendorArr.get(i).get(j).get("orderId").toString().equals(vendor.get(0).get("orderId").toString())) {
//                    vendor Found in orderVendor
                    Log.i("FOUND AT ID : i , j=0" , String.valueOf(i) +" , " + j);
                    return i;
                }
            }
        }
//        Log.i("Food Item in menu " , "NOT FOUND");
        return -1;
    }






}
