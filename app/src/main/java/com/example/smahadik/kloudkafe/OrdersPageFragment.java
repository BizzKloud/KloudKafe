package com.example.smahadik.kloudkafe;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
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
//        Log.i("ORDER PLACED BOOLEAN" , Home.orderPlaced.toString());

        if(Home.checkPreviousOrders) {
            Home.progressDialog.setMessage("Checking for previously placed OPEN orders");
            Home.progressDialog.show();
//            Log.i("was inside IF - 1" , "TRUE");
            checkPreviouslyPlacedOrders();
            new CountDownTimer(8000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(Math.round(millisUntilFinished/1000) == 2) {
//                        Log.i("MILLLI SECONDS " , String.valueOf(millisUntilFinished) + "...." + Math.round(millisUntilFinished/1000));
                        sortOrders();
                    }
                }
                @Override
                public void onFinish() {
                    Log.i("ON FINISH CALLED" , "TRUE");
                    Home.progressDialog.dismiss();
                    setOrderListAdapter();
                    Home.checkPreviousOrders = false;
                    if(Home.orderPlaced) { Home.orderPlaced = false; }
                }
            }.start();

        } else if(Home.orderPlaced)  {
            Home.progressDialog.setMessage("Getting Your Order Details");
            Home.progressDialog.show();
//            Log.i("was inside IF - 2" , "TRUE");
            getOrderDetails(Home.orderId);
            new CountDownTimer(4000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) { }
                @Override
                public void onFinish() {
                    Log.i("ON FINISH CALLED" , "TRUE");
                    Toast.makeText(getContext(), "Order Placed Successfully", Toast.LENGTH_LONG).show();
                    Home.progressDialog.dismiss();
                    setOrderListAdapter();
                    Home.orderPlaced = false;
                }
            }.start();

        } else {
//            Log.i("was inside IF" , "FALSE");
            if(Home.orderIdArr.size() > 0) {
                setOrderListAdapter();
            }
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
        expandanbleordersOrdersList.setIndicatorBounds(Home.width - GetPixelFromDips(30), Home.width - GetPixelFromDips(5));
        expandableListAdapterOrdersList = new ExpandableListAdapterOrdersList(getContext(), venAr , fddAr );
        expandanbleordersOrdersList.setAdapter(expandableListAdapterOrdersList);
        expandableListAdapterOrdersList.notifyDataSetChanged();

        expandanbleordersOrdersList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Home.home.setAdvCounter();
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

                    HashMap notificationOrder = getUpdatedOrderDetails(Home.orderVendorArr.get(id) , Odr1);
                    if(notificationOrder != null) {
                        Log.i("NOTIFICATION ADDED to array" , notificationOrder.toString());
                        Home.orderNotificationArr.add(notificationOrder);

                        // START ORDER DELETE TIMER
                        if(notificationOrder.get("orderStatus").toString().equals("CLOSED") || notificationOrder.get("orderStatus").toString().equals("CANCELLED")) {
                            Log.i("CLOSED Order Found" , "TRUE");
                            Home.autoOrderCLearArr.add(notificationOrder);
                            if(!Home.autoOrderClearActive) {
                                Home.autoOrderClearActive = true;
                                new CountDownTimer(5000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) { }
                                    @Override
                                    public void onFinish() {
                                        startOrderClearingTimer();
                                    }
                                }.start();
                            }
                        }
                    } else {
                        Log.i("NOTIFICATION ADDED to array" , "FALSE");
                    }

                    // ADD to to particular field
                    Home.orderVendorArr.set(id, Odr1);
                    Log.i("UPDATED ORDER ID DETAILS" , Home.orderIdArr.toString());
                    Log.i("UPDATED VENDOR ORDER DETAILS" , Home.orderVendorArr.toString());
                    Log.i("UPDATED SINGLE VENDOR ORDER DETAILS" , Home.orderVendorArr.get(id).toString());

                    // NOTIFICATION ==> call
                    setOrderListAdapter();
                    if(!Home.notificationActive) {
                        Home.notificationActive = true;
                        showNotification();
//                        Log.i("NOTIFICATION Array" , Home.orderNotificationArr.toString());
                    }

                    if(Home.navigation.getSelectedItemId() != R.id.navigation_order) {
                        Home.notificationBadge.setVisibility(View.VISIBLE);
                    }



                } else {
                    Home.orderVendorArr.add(Odr1);
//                    Log.i(" ORDER ID DETAILS" , Home.orderIdArr.toString());
//                    Log.i("VENDOR ORDER DETAILS" , Home.orderVendorArr.toString());
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

//                    Log.i("CURRENT VEN", odrven.toString() );
                    for(QueryDocumentSnapshot foodItemDoc : queryDocumentSnapshots) {
                        foodItemList.add((HashMap) foodItemDoc.getData());
//                            Log.i("INSIDE FOODITEM-1 DETAILS" , foodItemList.toString());
//                            Log.i("INSIDE VEN-FOODITEM-1 DETAILS" , venfoodItemList.toString());
//                            Log.i("INSIDE FOODITEM DETAILS" , Home.orderFoodItemArr.toString());
                    }
                    venfoodItemList.add(foodItemList);
//                    Log.i("OUTSIDE VEN-FOODITEM-1 DETAILS" , venfoodItemList.toString());
//
                    if (venfoodItemList.size() == OdrVenList.size()) {
                        Home.orderFoodItemArr.add(venfoodItemList);
//                        Log.i(" FOODITEM DETAILS at getOderFoodItems" , Home.orderFoodItemArr.toString());
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
//                    Log.i("FOUND AT ID : i , j=0" , String.valueOf(i) +" , " + j);
                    return i;
                }
            }
        }
//        Log.i("Food Item in menu " , "NOT FOUND");
        return -1;
    }


    public int [] findOrderForTimer(HashMap vendor) {

        for(int i=0; i<Home.orderVendorArr.size(); i++) {

            for(int j=0; j<Home.orderVendorArr.get(i).size(); j++){
                if (Home.orderVendorArr.get(i).get(j).get("orderId").toString().equals(vendor.get("orderId").toString())) {
//                    vendor Found in orderVendor
//                    Log.i("FOUND AT ID : i , j=0" , String.valueOf(i) +" , " + j);
                    return new int[]{i , j};
                }
            }
        }
//        Log.i("Food Item in menu " , "NOT FOUND");
        return null;
    }


    public HashMap getUpdatedOrderDetails(ArrayList<HashMap> oldOrder , ArrayList<HashMap> updatedOrder) {

        for(int i=0; i<oldOrder.size(); i++) {
            for (int j=0; j<updatedOrder.size(); j++) {
                if(oldOrder.get(i).get("orderId").toString().equals(updatedOrder.get(j).get("orderId").toString())) {
                    if(oldOrder.get(i).get("orderStatus").toString().equals(updatedOrder.get(j).get("orderStatus").toString())) {
                        continue;
                    } else {
                        return updatedOrder.get(j);
                    }
                }
            }
        }

        return null;
    }


    public void showNotification() {
        final Dialog notificationPopUp = new Dialog(getContext());
        notificationPopUp.setCanceledOnTouchOutside(false);
        notificationPopUp.setContentView(R.layout.order_notification);

        final ProgressBar progressBarVenImageNotification = notificationPopUp.findViewById(R.id.progressBarVenImageNotification);
        ImageView imageViewVenImageNotification = notificationPopUp.findViewById(R.id.imageViewVenImageNotification);
        TextView textViewVenNameNotification = notificationPopUp.findViewById(R.id.textViewVenNameNotification);
        TextView textViewVenOrderIdNotification = notificationPopUp.findViewById(R.id.textViewVenOrderIdNotification);
        TextView textViewOrderStatusNotification = notificationPopUp.findViewById(R.id.textViewOrderStatusNotification);

        String pic = Home.orderNotificationArr.get(0).get("pic").toString();
        StorageReference storageRefVenImageNotification = Home.storageRef.child(pic);
        Glide.with(imageViewVenImageNotification.getContext()).using(new FirebaseImageLoader()).load(storageRefVenImageNotification)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBarVenImageNotification.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageViewVenImageNotification);
        textViewVenNameNotification.setText(Home.orderNotificationArr.get(0).get("name").toString());
        textViewVenOrderIdNotification.setText(Home.orderNotificationArr.get(0).get("orderId").toString());
        if(Home.orderNotificationArr.get(0).get("orderStatus").toString().equals("CLOSED")) {
            textViewOrderStatusNotification.setText("READY TO PICK");
        }else {
            textViewOrderStatusNotification.setText(Home.orderNotificationArr.get(0).get("orderStatus").toString());
        }

        notificationPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notificationPopUp.show();

        // New CountDownTimer
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                Log.i("MILLLI SECONDS in notif" , String.valueOf(millisUntilFinished) + "...." + Math.round(millisUntilFinished/1000));
                if(Math.round(millisUntilFinished/1000) == 2) {
//                    Log.i("MILLLI SECONDS is INSIDE NOW" , String.valueOf(millisUntilFinished) + "...." + Math.round(millisUntilFinished/1000));
                    Home.orderNotificationArr.remove(0);
                    notificationPopUp.dismiss();
                }
            }
            @Override
            public void onFinish() {
                if (Home.orderNotificationArr.size() > 0) {
                    showNotification();
                } else {
                    Home.notificationActive = false;
                }
            }
        }.start();
    }


    public void startOrderClearingTimer() {

        final int [] ij = findOrderForTimer(Home.autoOrderCLearArr.get(0));
        HashMap order = Home.orderVendorArr.get(ij[0]).get(ij[1]);

        Timestamp endTime = (Timestamp) order.get("endTime");
        long timeElapsed = Calendar.getInstance().getTime().getTime() - endTime.toDate().getTime();
        Log.i("ELAPSED TIME" , String.valueOf(timeElapsed));

        if((Home.autoOrderClearTimerLimit *60000)-timeElapsed < 60000) {
            Log.i("ELAPSED TIME IS LESS" ,"Than 60 Seconds " );
            Home.autoOrderCLearArr.remove(0);
            removeOrder(ij[0],ij[1]);
            setOrderListAdapter();
            if (Home.autoOrderCLearArr.size() > 0) {
                startOrderClearingTimer();
            } else {
                Home.autoOrderClearActive = false;
            }
        } else {
            new CountDownTimer((Home.autoOrderClearTimerLimit *60000) - timeElapsed, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i("MILLLI SECONDS in Clear Order", String.valueOf(millisUntilFinished) + "...." + Math.round(millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    Home.autoOrderCLearArr.remove(0);
                    removeOrder(ij[0],ij[1]);
                    setOrderListAdapter();
                    if (Home.autoOrderCLearArr.size() > 0) {
                        startOrderClearingTimer();
                    } else {
                        Home.autoOrderClearActive = false;
                    }
                }
            }.start();
        }
    }


    public void removeOrder (int i, int j) {

        if(Home.orderVendorArr.get(i).size() == 1) {
            Home.orderIdArr.remove(i);
            Home.orderVendorArr.remove(i);
            Home.orderFoodItemArr.remove(i);
        } else {
            Home.orderVendorArr.get(i).remove(j);
            Home.orderFoodItemArr.get(i).remove(j);
        }

    }


    // CHECK FOR PREVIOUSLY PLACED AND "OPEN" ORDERS =============================================================================
    public void checkPreviouslyPlacedOrders() {
        Home.firestore.collection("transactions").document(Home.fcDetails.get("fcid").toString() + "/orders/" + Home.todaysDate).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
//                        Log.i("Checking for previously PLaced orders ::" , "Document EXISTS");
//                        Log.i("Todays Date ::" , Home.todaysDate);
//                        Toast.makeText(getContext(), "Today's Date Exists", Toast.LENGTH_SHORT).show();
//                        Log.i("TODAYS DATE-Doc exists" , "DocumentSnapshot data: " + document.getData());
                        HashMap orderIDRange = (HashMap) document.getData();
                        getPlacedOrderDetails(orderIDRange);
                    } else {
//                        Log.i("Checking for previously PLaced orders ::" , "Document does not exists");
                        Toast.makeText(getContext(), "Today's Date Does Not Exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Log.i("ERROR - Checking for previously PLaced orders" , "ERROR");
                    Toast.makeText(getContext(), "ERROR - Checking for previously PLaced orders", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void getPlacedOrderDetails(HashMap orderIDRange) {

        int size = Integer.parseInt(orderIDRange.get("eOrderId").toString()) - Integer.parseInt(orderIDRange.get("sOrderId").toString());
        for(int i=0; i<=size ; i++) {

            String odid = orderIDRange.get("sOrderId").toString();
            String newOrderId;
            newOrderId = String.valueOf(Integer.parseInt(odid) + i);
            newOrderId = Home.zeros[newOrderId.length() - 1] + newOrderId;

//            Log.i("Checking for OPEN ORDERS ID :" , newOrderId);

            Home.dbTransaction.document(Home.todaysDate + "/" + newOrderId + "/Total").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists() ) {
                            HashMap order = (HashMap) document.getData();
                            if(order.get("tabid").toString().equals(Home.basicDetails.get("tabid").toString()) && order.get("finalOrderStatus").toString().equals("OPEN") ) {
                                Log.i("Found Order ID for OPEN ORDERS :" , order.get("orderId").toString());
                                getOrderDetails(order.get("orderId").toString());
                            }
                        }
                    }
                }
            });

        }
    }


    public void sortOrders() {
        for (int i = 0; i < Home.orderIdArr.size(); i++) {

            for (int j = Home.orderIdArr.size() - 1; j > i; j--) {
                if (Integer.parseInt(Home.orderIdArr.get(i)) > Integer.parseInt(Home.orderIdArr.get(j)) ) {

                    String tmpOrderIdArr = Home.orderIdArr.get(i);
                    ArrayList<HashMap> tmpOrderVendorArr = Home.orderVendorArr.get(i);
                    ArrayList< ArrayList<HashMap> >tmpOrderFoodItemArr = Home.orderFoodItemArr.get(i);

                    Home.orderIdArr.set(i, Home.orderIdArr.get(j));
                    Home.orderIdArr.set(j,tmpOrderIdArr);
                    Home.orderVendorArr.set(i, Home.orderVendorArr.get(j));
                    Home.orderVendorArr.set(j,tmpOrderVendorArr);
                    Home.orderFoodItemArr.set(i, Home.orderFoodItemArr.get(j));
                    Home.orderFoodItemArr.set(j,tmpOrderFoodItemArr);
                }

            }
        }

//        Log.i("SORTED ORDER ID ARRAY" , Home.orderIdArr.toString());
    }











}
