package com.example.smahadik.kloudkafe;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import io.grpc.Context;

public class Home extends AppCompatActivity {

    public static FirebaseFirestore firestore;
    public static CollectionReference db;
    public static CollectionReference dbTransaction;
    public static StorageReference storageRef;
    CollectionReference vendorItemRef;
    CollectionReference vendorTaxRef;
    CollectionReference categoryItemRef;


    Intent login;
    public static HashMap fcDetails;
    public static HashMap tableDetails;
    public static HashMap adminDetails;
    public static HashMap employeeDetails;
    public static HashMap basicDetails;
    public static HashMap orderDetails;
    public static DecimalFormat formatter = new DecimalFormat("##,##,##,##0.00");
    public static DecimalFormat decimalFormatter = new DecimalFormat("####0.00");
    public static DecimalFormat removedecimalFormatter = new DecimalFormat("#####");
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static String todaysDate = simpleDateFormat.format(Calendar.getInstance().getTime());


    public static BottomNavigationView navigation;
    public static Boolean flagAdvFirst = true;
    public static Boolean flagMenuFirst = true;
    public static Boolean flagCartFirst = true;
    public static Boolean flagOrderFirst = true;
    public static long counter = 0;
    public static CountDownTimer inActiveCounternew;
    Boolean getVendorIdVar;
    Boolean getCatIdVar;
    public static Boolean checkStartOrderID = true;
    public static FragmentManager fragmentManager;
    public static FragmentTransaction ft;
    public static ProgressDialog progressDialog;



    //Home Initials
    CartFragment cartFragment = new CartFragment();
    public static Home home = new Home();
    public static ArrayList<HashMap> advertisementArr;
    public static ArrayList<HashMap> whatsNewArr;
    public static ArrayList<HashMap> ymalArr;
    public static ArrayList<HashMap> freebieArr;
    public static ArrayList<HashMap> vendorArr;
    public static ArrayList<ArrayList <HashMap>> categoryArr;
    public static ArrayList<ArrayList <HashMap>> vendorTaxArr;
    public static ArrayList<HashMap> fcTaxArr;
    public static ArrayList<ArrayList <ArrayList <HashMap>>> foodItemArr;
    public static ArrayList<HashMap> cartFCTaxArr;
    public static ArrayList<ArrayList<HashMap>> cartArr;
    public static ArrayList<ArrayList <HashMap>> cartVendorTaxArr;
    public static ArrayList<HashMap> cartVendorArr;
    public static ArrayList<String> orderIdArr = new ArrayList<>();
    public static ArrayList<ArrayList <HashMap>> orderVendorArr = new ArrayList<>();
    public static ArrayList<ArrayList<ArrayList <HashMap>>> orderFoodItemArr = new ArrayList<>();
    public static int vendorPosition;
    public static int catPosition;
    public static int lastVendorPosition;
    public static String currencyFc;
    public static String orderId;
    public static Boolean orderPlaced = false;
    public static String [] zeros = {"00000000" , "0000000" , "000000" , "00000" , "0000" , "000" , "00" , "0"};


    // Asysny Initials
    int catindex = -1;
    int venindex = -1;
    int vendorTaxindex = -1;
    Boolean firstTimeCat = true;


    public static int width;
    DisplayMetrics metrics = new DisplayMetrics();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.i("Clicked on" , "HOME");
                    if(navigation.getSelectedItemId() != R.id.navigation_home || flagAdvFirst) {
                        if(flagAdvFirst) {
                            Log.i("Flag ADV FIRST " ,  "Flag ADV FIRST = TRUE");
                            Log.i("Navigation " ,  "Changed");
                            stopImageSwitcher(false , true);
                            pushFragment(new AdvertisementFragment(), flagAdvFirst);
                            flagAdvFirst = false;
                        } else {
                            stopImageSwitcher(false , true);
                            Log.i("Flag ADV SECOND " ,  "Flag ADV SECOND = FALSE");
                            pushFragment(new AdvertisementFragment(), flagAdvFirst);
                        }
                        if(inActiveCounternew != null) { inActiveCounternew.cancel(); }
                    }
                    return true;
                case R.id.navigation_menu:
                    Log.i("Clicked on" , "MENU");
                    pushMenuFrag();
                    return true;

                case R.id.navigation_cart:
                    if(navigation.getSelectedItemId() != R.id.navigation_cart) {
                        stopImageSwitcher(true, true);
                        if(flagCartFirst) {
                            Log.i("Flag CART FIRST " ,  "Flag CART FIRST = TRUE");
                            Log.i("Navigation " ,  "Changed");
                            pushFragment(new CartFragment(), flagCartFirst);
                            flagCartFirst = false;
                        } else {
                            Log.i("Flag CART SECOND " ,  "Flag CART SECOND = FALSE");
                            pushFragment(new CartFragment(), flagCartFirst);
                        }
                        setCounter();
                    }
                    return true;
                case R.id.navigation_order:
                    pushOrdersFrag();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // HIDE ACTION BAR;
//        getSupportActionBar().hide();
//        advFrag = new AdvertisementFragment();


        fragmentManager = getFragmentManager();
        ft = fragmentManager.beginTransaction();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;


        // FIRESTORE
        firestore = FirebaseFirestore.getInstance();
        db = firestore.collection("foodcourts");
        storageRef = FirebaseStorage.getInstance().getReference();


        // INITIALIZATION
        login = getIntent();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting Up Your App");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        fcDetails = (HashMap) login.getSerializableExtra("fcDetails");
        adminDetails = (HashMap) login.getSerializableExtra("adminDetails");
        tableDetails = (HashMap) login.getSerializableExtra("tableDetails");
        employeeDetails = (HashMap) login.getSerializableExtra("employeeDetails");
        basicDetails = (HashMap) login.getSerializableExtra("basicDetails");
        basicDetails.put("baseAmount" , 0);
        basicDetails.put("iHFBaseAmount" , 0);
        basicDetails.put("iHFtax" , 0);
        basicDetails.put("iHFTotalAmount" , 0);
        basicDetails.put("grandTotal" , 0);
        menuInit();
        cartInit();
        dbTransaction = firestore.collection("transactions/" + fcDetails.get("fcid") + "/orders");

        currencyFc = fcDetails.get("curky").toString();
        if(currencyFc.equals("INR")) {
            currencyFc = "\u20B9" + " ";
        }else { currencyFc = "$"; }


        // Asysnc Task Start
        new AsysncTask().execute();
        new CountDownTimer(7500, 1000) {
            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                Log.i("Food Item Started from asysnc task in 7 sec" , "TRUE TRUE TRUE");
                Toast.makeText(Home.this, "Food Item Started from asysnc task in 7 sec" +
                        "", Toast.LENGTH_SHORT).show();
                getFoodItem();
                getVendorTax();
            }
        }.start();


        navigation = findViewById(R.id.navigation);

        View view1 = LayoutInflater.from(this).inflate(R.layout.bottom_navigation_home_card, null);
        View view2 = LayoutInflater.from(this).inflate(R.layout.bottom_navigation_home_card, null);
        View view3 = LayoutInflater.from(this).inflate(R.layout.bottom_navigation_home_card, null);
        View view4 = LayoutInflater.from(this).inflate(R.layout.bottom_navigation_home_card, null);
        navigation.addView(view1, 0);
        navigation.addView(view2, 1);
        navigation.addView(view3, 2);
        navigation.addView(view4, 3);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        BottomNavigationViewHelper.removeShiftMode(navigation);



//        navigation.setSelectedItemId(R.id.navigation_home);
//        pushFragment(new AdvertisementFragment(), flagAdvFirst);
//        flagAdvFirst = false;

//        inActiveCounternew = new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                Log.i("Seconds" , String.valueOf(millisUntilFinished/1000));
//            }
//
//            public void onFinish() {
//                Log.i("COUNTER" , "COMPLETED" );
//            }
//        }.start();



    }




    // BASIC =======================================================================================
    protected void pushFragment(Fragment fragment, Boolean flag) {
        if (fragment == null) { return ; }

        fragmentManager = getFragmentManager();
        ft = fragmentManager.beginTransaction();

        if (fragmentManager != null) {

            if(flag) {
                Log.i("Push Fragment FIRST " ,  "Push Fragment  FIRST = TRUE");

                if (ft != null) {
                    ft.replace(R.id.rootLayout, fragment, fragment.getClass().getName());
                    Log.i("TAGGGGGGG" , fragment.getClass().getName());
                    ft.addToBackStack(null);
                    Log.i("BACKSTACK ENTRY LIST", String.valueOf(getFragmentManager().getBackStackEntryCount()));
//                    ft.replace(R.id.rootLayout, fragment);
//                    if(flagAdvFirst) {
                    Log.i("Calling Commit", "TRUEEEEEE");
                    ft.commit();
//                    }

                }
            } else {
                Log.i("Push Fragment Second " ,  "Push Fragment  Second = TRUE");
                if (ft != null) {
                    Fragment frag =  fragmentManager.findFragmentByTag(fragment.getClass().getName());
                    ft.replace(R.id.rootLayout, frag);
                    ft.commit();
                }
            }

        }
    }


    public void pushMenuFrag() {
        if(navigation.getSelectedItemId() != R.id.navigation_menu) {
            vendorPosition = -1;
            lastVendorPosition = -1;
            if(flagMenuFirst) {
                Log.i("Flag MENU FIRST " ,  "Flag MENU FIRST = TRUE");
                Log.i("Navigation " ,  "Changed");
                stopImageSwitcher(true , false);
                pushFragment(new MenuFragment(), flagMenuFirst);
                flagMenuFirst = false;
            } else {
                Log.i("Flag MENU SECOND " ,  "Flag MENU SECOND = FALSE");
                stopImageSwitcher(true , false);
                pushFragment(new MenuFragment(), flagMenuFirst);
            }
            setCounter();
        }
    }

    public void pushOrdersFrag() {
        if(navigation.getSelectedItemId() != R.id.navigation_order) {
            stopImageSwitcher(true, true);
            if(flagOrderFirst) {
                Log.i("Flag ORDER FIRST " ,  "Flag ORDER FIRST = TRUE");
                Log.i("Navigation " ,  "Changed");
                pushFragment(new OrdersPageFragment(), flagOrderFirst);
                flagOrderFirst = false;
            } else {
                Log.i("Flag ORDER SECOND " ,  "Flag ORDER SECOND = FALSE");
                pushFragment(new OrdersPageFragment(), flagOrderFirst);
            }
            setCounter();
        }
    }



    public void stopImageSwitcher(boolean advCancelled, boolean menuCancelled) {
        AdvertisementFragment.cancelled = advCancelled;
        MenuFragment.cancelled = menuCancelled;
//        if(getSupportActionBar().isShowing()) {
//            Log.i("HIDE ACTION BAR" , "DONE");
//            getSupportActionBar().hide();
//        }
    }



    // Menu ========================================================================================
    public void menuInit() {
        whatsNewArr  = new ArrayList<>();
        ymalArr  = new ArrayList<>();
        vendorArr  = new ArrayList<>();
        categoryArr  = new ArrayList<>();
        foodItemArr  = new ArrayList<>();
        vendorTaxArr = new ArrayList<>();
        advertisementArr = new ArrayList<>();
    }



    // Cart ========================================================================================
//    public void setCartFragmentforDrawer() {
//        VendorItemListFragment.drawerLayout.openDrawer(Gravity.END);
//        FragmentManager fragManager = getFragmentManager();
//        FragmentTransaction ft2 = fragManager.beginTransaction();
//        ft2.replace(R.id.cartDrawerFrameLayout, new CartFragment());
//        ft2.commit();
//    }

    public void cartInit() {
        cartArr = new ArrayList<>();
        cartVendorArr = new ArrayList<>();
        cartVendorTaxArr = new ArrayList<>();
        cartFCTaxArr = new ArrayList<>();
        fcTaxArr = new ArrayList<>();
        freebieArr = new ArrayList<>();
        orderDetails = basicDetails;
    }

    public void addtocart(HashMap foodItem) {

        progressDialog.setMessage("Adding Food Item to Cart");
        progressDialog.show();
        ArrayList <HashMap> newFoodItemArrayList = new ArrayList<>();

        foodItem.put("quantity", 1);
        foodItem.put("totalAmount", foodItem.get("amount").toString());
        Boolean venidFoundFlag = false;

        Log.i("FoodItem At addtocart" , foodItem.toString());

        if(cartArr.size() > 0 ){
            for (int i=0; i<cartArr.size(); i++) {
                if(cartArr.get(i).get(0).get("venid").equals(foodItem.get("venid"))) {
                    // code if venid found in cart
                    cartArr.get(i).add(foodItem);
                    calculateTaxAndVendorAmount(i);
                    calculateIHFAndGrandTotal();
                    venidFoundFlag = true;
                    break;
                }
            }

            if(!venidFoundFlag) {
                // code if venid not found
                newFoodItemArrayList.add(foodItem);
                cartArr.add(newFoodItemArrayList);
                addVendorDetailsToCart(vendorPosition, -1);
                addVendortaxDetailsToCart(vendorPosition, -1);
                calculateTaxAndVendorAmount(cartArr.size()-1);
                calculateIHFAndGrandTotal();
            }
        }else {
                // code if cartArr is blank or venid not found
                newFoodItemArrayList.add(foodItem);
                cartArr.add(newFoodItemArrayList);
                addVendorDetailsToCart(vendorPosition, -1);
                addVendortaxDetailsToCart(vendorPosition, -1);
                calculateTaxAndVendorAmount(0);
                calculateIHFAndGrandTotal();
        }

    }

    public void removefromcart(HashMap foodItem) {
        progressDialog.setMessage("Removing Food Item from Cart");
        progressDialog.show();

        for(int i=0; i<cartVendorArr.size(); i++){
            if(cartVendorArr.get(i).get("venid").toString().equals(foodItem.get("venid").toString())) {
                // venid Found in cart

                for(int j=0; j<cartArr.get(i).size(); j++){
                    if(cartArr.get(i).get(j).get("fdid").toString().equals(foodItem.get("fdid").toString())
                            && cartArr.get(i).get(j).get("catid").toString().equals(foodItem.get("catid").toString()) ) {
                        // FoodItem Found
                        if(cartArr.get(i).size() == 1){
                            cartArr.remove(i);
                            cartVendorArr.remove(i);
                            cartVendorTaxArr.remove(i);
                            Log.i("cartArry Size at last item" , String.valueOf(cartArr.size()));
                            Log.i("cartVendorArry Size at last item" , String.valueOf(cartVendorArr.size()));
                            Log.i("cartVendorTaxArry Size at last item" , String.valueOf(cartVendorTaxArr.size()));
                            calculateIHFAndGrandTotal();
                        }else {
                            cartArr.get(i).remove(j);
                            Log.i("cartArry Size " , String.valueOf(cartArr.size()));
                            Log.i("cartVendorArry Size " , String.valueOf(cartVendorArr.size()));
                            Log.i("cartVendorTaxArry Size " , String.valueOf(cartVendorTaxArr.size()));
                            calculateTaxAndVendorAmount(i);
                            calculateIHFAndGrandTotal();
                        }
                        break;
                    }
                }
            }
        }

    }

    public void addVendortaxDetailsToCart(int pos, int cartpos) {
        Log.i("START cart vendor tax array size ", String.valueOf(cartVendorTaxArr.size()));
        Log.i("passed pos ", String.valueOf(pos));
        ArrayList <HashMap> newVendorTaxArrayList = vendorTaxArr.get(pos);
        for(int i=0; i<newVendorTaxArrayList.size() ; i++) {
            newVendorTaxArrayList.get(i).put("amount", 00);
        }
        if(cartpos != -1){
            cartVendorTaxArr.set( cartpos , newVendorTaxArrayList);
            Log.i("cart vendor tax array setted  ", cartVendorTaxArr.toString());
        }else {
            cartVendorTaxArr.add(newVendorTaxArrayList);
            Log.i("cart vendor tax array  ", "added");
        }
        Log.i("END cart vendor tax array size ", String.valueOf(cartVendorTaxArr.size()));
        Log.i("cart vendor tax array ", "DONE");
    }

    public void addVendorDetailsToCart(int pos, int cartpos) {
        HashMap newVendorArrayList = vendorArr.get(pos);

        newVendorArrayList.put("orderStatus", "OPEN" );
        ArrayList<String> oHStatus = new ArrayList<>();
        oHStatus.add("OPEN");
        newVendorArrayList.put("oHStatus", oHStatus );
        newVendorArrayList.put("orderId", 00 );
        newVendorArrayList.put("baseAmount", 00 );
        newVendorArrayList.put("totalTax", 00 );
        newVendorArrayList.put("subTotalAmount", 00 );

        if(cartpos != -1){
            cartVendorArr.set( cartpos , newVendorArrayList);
            Log.i("cart vendor tax array setted  ", cartVendorTaxArr.toString());
        }else {
            cartVendorArr.add(newVendorArrayList);
            Log.i("cart vendor array  ", "added");
        }



    }

    public void calculateTaxAndVendorAmount(int position) {

        Log.i("position passed" , String.valueOf(position));
        Double amount = 0.00;
        Double taxAmount = 0.00;

        for(int i=0; i<cartArr.get(position).size(); i++) {
            amount = amount +  Double.parseDouble(cartArr.get(position).get(i).get("totalAmount").toString());
            amount = Double.parseDouble(decimalFormatter.format(amount));
        }
        cartVendorArr.get(position).put("baseAmount" , amount);

        Log.i("cartvendor tax array size" , String.valueOf(cartVendorTaxArr.size()));
        for(int i=0; i<cartVendorTaxArr.get(position).size(); i++) {
            taxAmount = taxAmount + ( (amount *  Double.parseDouble(Home.cartVendorTaxArr.get(position).get(i).get("taxPer").toString())) /100 );
            taxAmount = Double.parseDouble(Home.decimalFormatter.format(taxAmount));
            cartVendorTaxArr.get(position).get(i).put("amount" , Home.decimalFormatter.format((amount *  Double.parseDouble(Home.cartVendorTaxArr.get(position).get(i).get("taxPer").toString()))/100 )  );
        }
        cartVendorArr.get(position).put("totalTax" , taxAmount);
        cartVendorArr.get(position).put("subTotalAmount" , Double.parseDouble(Home.decimalFormatter.format(taxAmount + amount)) );

    }

    public void calculateIHFAndGrandTotal() {
        Double baseAmount = 0.00;
        for (HashMap vendorTax : Home.cartVendorArr){
            baseAmount = Double.parseDouble(decimalFormatter.format(baseAmount + Double.parseDouble(vendorTax.get("subTotalAmount").toString())) );
        }
        Home.orderDetails.put("baseAmount" , baseAmount);
        updateFCTaxAmountAndGrandTotal();
    }

    public void updateFCTaxAmountAndGrandTotal() {
        Double baseAmount = 0.00;
        Double taxAmount = 0.00;
        Double totalTaxAmount = 0.00;
        for (int i=0; i<cartFCTaxArr.size(); i++) {
            if(i==0) {
                if(cartArr.size() > 0) {
                    baseAmount = (Double.parseDouble(cartFCTaxArr.get(i).get("taxPer").toString()) / 100) * Double.parseDouble(orderDetails.get("baseAmount").toString());
                    baseAmount = Double.parseDouble(decimalFormatter.format(baseAmount));
                }
                cartFCTaxArr.get(i).put("amount" , baseAmount);
            }else{
                if(cartArr.size() > 0) {
                    taxAmount = (Double.parseDouble(cartFCTaxArr.get(i).get("taxPer").toString()) / 100) * baseAmount;
                    taxAmount = Double.parseDouble(decimalFormatter.format(taxAmount));
                    totalTaxAmount = Double.parseDouble(decimalFormatter.format(totalTaxAmount + taxAmount));
                }
                cartFCTaxArr.get(i).put("amount" , taxAmount);
            }
        }
//        if(cartArr.size() > 0) {
            orderDetails.put("iHFBaseAmount" , baseAmount);
            orderDetails.put("iHFtax" , totalTaxAmount );
            orderDetails.put("iHFTotalAmount" , Double.parseDouble(decimalFormatter.format(totalTaxAmount + baseAmount)) );
            orderDetails.put("grandTotal" , Double.parseDouble(decimalFormatter.format(Double.parseDouble(orderDetails.get("baseAmount").toString()) + totalTaxAmount + baseAmount)) );
            orderDetails.put("modeOfPay" , "CASH" );
            orderDetails.put("finalOrderStatus" , "CREATED" );
//        }

        if(CartFragment.textViewIHFPer != null) {
            CartFragment.textViewIHFPer.setText(removedecimalFormatter.format(fcTaxArr.get(0).get("taxPer")) + " %");
        }

        progressDialog.dismiss();
        Log.i("Every Thing Done" ,"TRUE ");
    }

    public void clearcartButton(View view) {
        clearcart();
    }
    public void clearcart(){
        cartArr.clear();
        cartVendorTaxArr.clear();
        cartVendorArr.clear();
        calculateIHFAndGrandTotal();
        if(CartFragment.recyclerViewAdapterCartVendorList != null) {
            cartFragment.notifyForUpdates();
        }
    }

    public HashMap findFoodItem(HashMap fooditem) {

        for(int i=0; i<vendorArr.size(); i++) {
            if (vendorArr.get(i).get("venid").toString().equals(fooditem.get("venid").toString())) {

                //vendor found
                for(int j=0; j<categoryArr.get(i).size(); j++){
                    if (categoryArr.get(i).get(j).get("catid").toString().equals(fooditem.get("catid").toString())) {

                        // cat Found
                        for(int k=0; k<foodItemArr.get(i).get(j).size(); k++) {
                            if (foodItemArr.get(i).get(j).get(k).get("fdid").toString().equals(fooditem.get("fdid").toString())) {

                                // foodItem Found
                                vendorPosition = i;
//                                Log.i("FOOD ITEM FOUND" ,i + ":" + foodItemArr.get(i).get(j).get(k).toString() );
                                return foodItemArr.get(i).get(j).get(k);
                            }
                        }
                    }
                }
            }
        }
//        Log.i("Food Item in menu " , "NOT FOUND");
        return null;
    }

    public boolean checkFoodIteminCart(HashMap fooditem) {
        if(cartArr.size() > 0) {
            for(int i=0; i<cartArr.size(); i++) {
                if(cartArr.get(i).get(0).get("venid").toString().equals(fooditem.get("venid").toString())) {
                    //found Venid- vendor
                    for(int j=0; j<cartArr.get(i).size(); j++) {
                        if(cartArr.get(i).get(j).get("fdid").toString().equals(fooditem.get("fdid").toString())
                                && Home.cartArr.get(i).get(j).get("catid").toString().equals(fooditem.get("catid").toString()) ) {
                            // found fdid- foodItem
//                            Log.i("Food Item At WhatsNew" , "FOUND");
                            return true;
                        }
                    }
                }
            }
        }
//        Log.i("Food Item At WhatsNew" , "NOT FOUND");
        return false;
    }

    public void updateFDItemList(int venpos, int cartpos) {

        //vendor, cartvendorarr
        ArrayList<HashMap> foodListven = cartArr.get(cartpos);
        for (int i=0; i<foodListven.size(); i++ ) {

            for(int j=0; j<categoryArr.get(venpos).size(); j++){
                if (categoryArr.get(venpos).get(j).get("catid").toString().equals(foodListven.get(i).get("catid").toString())) {

                    // cat Found
                    for(int k=0; k<foodItemArr.get(venpos).get(j).size(); k++) {
                        if (foodItemArr.get(venpos).get(j).get(k).get("fdid").toString().equals(foodListven.get(i).get("fdid").toString())) {

                            // foodItem Found
                            HashMap foodItem = foodItemArr.get(venpos).get(j).get(k);
                            foodItem.put("quantity", Integer.parseInt(foodListven.get(i).get("quantity").toString()) );

                            Double amt = Double.parseDouble(foodItem.get("amount").toString())
                                    * Integer.parseInt(foodItem.get("quantity").toString());

                            foodItem.put("totalAmount", decimalFormatter.format(amt) );
                            cartArr.get(cartpos).set(i, foodItem);
                        }
                    }

                }
            }

        }
    }

    public void notifyforupdates() {

        if(MenuFragment.recyclerViewAdapterVendorList != null) {
            Log.i("Notify Done vendorlist" ,"TRUE ");
            MenuFragment.recyclerViewAdapterVendorList.notifyDataSetChanged();
        }
        if(VendorItemListFragment.categorySliderAdapter != null) {
            Log.i("Notify Done category slider" ,"TRUE ");
            VendorItemListFragment.categorySliderAdapter.notifyDataSetChanged();
            VendorItemListFragment.categorySliderAdapter = new CategorySliderAdapter(this);
            VendorItemListFragment.foodItemListViewPager.setAdapter(VendorItemListFragment.categorySliderAdapter);
        }
        if(CategorySliderAdapter.recyclerViewAdapterFoodItemList != null) {
            Log.i("Notify Done fooditemlist" ,"TRUE ");
            CategorySliderAdapter.recyclerViewAdapterFoodItemList.notifyDataSetChanged();
        }
        if(CartFragment.recyclerViewAdapterCartVendorList != null) {
            Log.i("Notify Done cartvendor list" ,"TRUE ");
            Log.i("cart Array" , cartArr.toString());
            Log.i("cart Vendor Array" , cartVendorArr.toString());
            Log.i("cart vendor tax Array" , cartVendorTaxArr.toString());
            Log.i("cart FC tax Array" , cartFCTaxArr.toString());
            Log.i("cart order details Array" , orderDetails.toString());
            cartFragment.notifyForUpdates();
        }

        progressDialog.dismiss();

    }



    // ASYSNC TASK =================================================================================
    private class AsysncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            // Advertisement
            db.document(fcDetails.get("fcid").toString()).collection("AdvertisementM").whereEqualTo("status", true).orderBy("adpos", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    advertisementArr.clear();
                    for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        advertisementArr.add( (HashMap) document.getData());
                        Log.i("Advertisemnt Array" , advertisementArr.toString());
                    }
                    if(AdvertisementFragment.advertisementSliderAdapter != null) {
                        AdvertisementFragment.advertisementSliderAdapter.notifyDataSetChanged();
                    }
                }
            });



            // Cart TaxFCM
            db.document(fcDetails.get("fcid").toString()).collection("TaxFCM").whereEqualTo("status", true).orderBy("taxid", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    fcTaxArr.clear();
                    cartFCTaxArr.clear();
                    for(QueryDocumentSnapshot taxfcmItem : queryDocumentSnapshots) {
                        fcTaxArr.add( (HashMap) taxfcmItem.getData());
                        cartFCTaxArr.add( (HashMap) taxfcmItem.getData());
                    }
                    // updating IHF text and Grand Total
                    if(cartArr.size() > 0){
                        updateFCTaxAmountAndGrandTotal();
                    }
                }
            });



            // WhatsNew Array
            db.document(fcDetails.get("fcid").toString()).collection("WhatsNewM").whereEqualTo("status", true).orderBy("newpos", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        whatsNewArr.clear();
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            whatsNewArr.add((HashMap) document.getData());
                            Log.i("WhatsNew Array" , whatsNewArr.toString());
                        }
                        if(MenuFragment.whatsNewSliderAdapter != null) {
//                            Log.i("WhatsNew Array" , (String.valueOf(whatsNewArr.size())) );
                            MenuFragment.whatsNewSliderAdapter.notifyDataSetChanged();
                            MenuFragment menuFragment = new MenuFragment();
                            menuFragment.setDotsLayout(0, getApplicationContext());
                        }

//                        Log.i("WhatsNew Array" , whatsNewArr.toString());
                    }
            });

            // Vendor Array
            db.document(fcDetails.get("fcid").toString()).collection("VendorM").whereEqualTo("status", true).orderBy("venpos", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    vendorArr.clear();
//                    categoryArr.clear();

                    for (QueryDocumentSnapshot vendor : queryDocumentSnapshots) {
                        vendorArr.add((HashMap) vendor.getData());
                        Log.i("Vendor" , vendor.getData().toString());
                        if(vendorArr.size() == queryDocumentSnapshots.size()) {
                            Log.i("CAlling for Category starting..........." , ".......... TRUE");
                            getCategoryItem();
                            getVendorTax();
                        }
                    }
//                    Log.i("Vendor Array" , vendorArr.toString());
//                    Log.i("Vendor Array Size" , String.valueOf(vendorArr.size()));
                }
            });

            //YMAL ARRAY
            db.document(fcDetails.get("fcid").toString()).collection("YouMayAlsoLikeM").whereEqualTo("status", true).orderBy("likepos" ,Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    ymalArr.clear();

                    for (QueryDocumentSnapshot ymalItem : queryDocumentSnapshots) {
                        ymalArr.add( (HashMap) ymalItem.getData());
                        Log.i("YMAL Array" , ymalArr.toString());
                    }
                    if(MenuFragment.whatsNewSliderAdapter != null) {
                        MenuFragment.whatsNewSliderAdapter.notifyDataSetChanged();
                    }
                }
            });


            //Freebie Array
            db.document(fcDetails.get("fcid").toString()).collection("FreebieM").whereEqualTo("status", true).orderBy("name", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    freebieArr.clear();
                    for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int count = Integer.parseInt(document.get("count").toString());
                        if(count > 0) {
                            freebieArr.add( (HashMap) document.getData());
                        }
                        Log.i("Freebie Array" , freebieArr.toString());
                    }
                    notifyforupdates();
                }
            });


            return null;
        }


        @Override
        protected void onPostExecute(Void unused) {
            Toast.makeText(Home.this, " PROGRESS COMPLETED", Toast.LENGTH_SHORT).show();
        }


    }

    public void getCategoryItem() {

        // Category Item Started
        Log.i("Vendor Array Size IN CATEGORY ITEM " , String.valueOf(vendorArr.size()));
        Log.i("Category Array Size IN CATEGORY ITEM" , String.valueOf(categoryArr.size()));

        firstTimeCat = true;
        categoryArr.clear();

        for (int ven=0; ven<vendorArr.size(); ven++ ) {

            Log.i(" searching for venid " , vendorArr.get(ven).get("venid").toString());
            vendorItemRef = db.document(fcDetails.get("fcid").toString()).collection("VendorM/" + vendorArr.get(ven).get("venid").toString() + "/CategoryM");
            vendorItemRef.whereEqualTo("status" , true).orderBy("catpos", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                    getVendorIdVar = false;
                    final ArrayList<HashMap> sample = new ArrayList<HashMap>();
                    for(QueryDocumentSnapshot catItem : queryDocumentSnapshots) {
                        if (!getVendorIdVar) {
                            for(HashMap vendor : vendorArr) {
                                if (vendor.get("venid").toString().equals(catItem.getString("venid")) & !getVendorIdVar ) {
                                    catindex = vendorArr.indexOf(vendor);
                                    getVendorIdVar = true;
                                    Log.i(" category VENDOR venid " , vendor.get("venid").toString());
                                    Log.i(" category venid " , catItem.getString("venid"));
                                    break;
                                }
                            }
                        }
                        sample.add((HashMap) catItem.getData());

                    }
                    Log.i("category size" , String.valueOf(categoryArr.size()));
                    Log.i("category index" , String.valueOf(catindex));
                    if(categoryArr.size() > catindex && !sample.isEmpty() && !firstTimeCat) {
                        Log.i("Category Second index" , String.valueOf(catindex));
                        categoryArr.set(catindex, sample);
                        firstTimeCat = false;
                        notifyforupdates();
                    }else if(!sample.isEmpty() && firstTimeCat) {
                        Log.i("Category First" , "True");
                        categoryArr.add(sample);
                    }

                    Log.i("Category Array Size" , String.valueOf(categoryArr.size()));
                    Log.i("Category Array Lst" , String.valueOf(categoryArr.get(categoryArr.size()-1).size()));

                    if(categoryArr.size() == vendorArr.size()) {
                        // Sorting Category
                        Log.i("Category Array Lst Sorting" , "TRUEEEEEEE");
                        for (int i=0; i<vendorArr.size(); i++) {
                            for (int j=i; j<categoryArr.size(); j++) {
                                if(vendorArr.get(i).get("venid").toString().equals(categoryArr.get(j).get(0).get("venid").toString())) {
                                    if(i == j) {
                                        break;
                                    } else if (i < j) {
                                        ArrayList<HashMap> temp = categoryArr.get(i);
                                        categoryArr.set(i, categoryArr.get(j));
                                        categoryArr.set(j, temp);
                                    }
                                }
                            }
                        }
                        new CountDownTimer(2500, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                        }

                                        @Override
                                        public void onFinish() {
                                            getFoodItem();
                                            firstTimeCat = false;
                                        }
                                    }.start();
                    }
                }
            });

        }





    }

    public void getFoodItem() {

        // Food Item Started
        Log.i("Vendor Array Size IN FOOD ITEM " , String.valueOf(vendorArr.size()));
        Log.i("Category Array Size IN FOOD ITEM" , String.valueOf(categoryArr.size()));

        foodItemArr.clear();

        for (int ven=0; ven<vendorArr.size(); ven++ ) {
            for (int cat=0; cat<categoryArr.get(ven).size(); cat++) {

                categoryItemRef = db.document(fcDetails.get("fcid").toString()).collection("VendorM/" + vendorArr.get(ven).get("venid").toString() + "/CategoryM/" + categoryArr.get(ven).get(cat).get("catid").toString() + "/MenuM");

                categoryItemRef.whereEqualTo("status" , true).orderBy("fspos", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        getCatIdVar = false;
                        final ArrayList<HashMap> samplefdItem = new ArrayList<HashMap>();
                        for(QueryDocumentSnapshot fdItem : queryDocumentSnapshots) {

                            if (!getCatIdVar) {
                                for(int i=0; i<vendorArr.size(); i++) {
                                    if(vendorArr.get(i).get("venid").toString().equals(fdItem.getString("venid"))  && !getCatIdVar ) {
                                        for(int j=0; j<categoryArr.get(i).size(); j++) {
                                            if (categoryArr.get(i).get(j).get("catid").toString().equals(fdItem.getString("catid")) && !getCatIdVar ) {
                                                catindex = j;
                                                venindex = i;
                                                getCatIdVar = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            samplefdItem.add((HashMap) fdItem.getData());
                        }

                        Log.i("Venindex" , String.valueOf(venindex));
                        Log.i("catindex" , String.valueOf(catindex));

                        if(foodItemArr.size() > venindex && !samplefdItem.isEmpty()) {

                            if(foodItemArr.get(venindex).size() > catindex) {
                                foodItemArr.get(venindex).set(catindex, samplefdItem);
                            }else {
                                foodItemArr.get(venindex).add(samplefdItem);
                            }
                            updateForVendorTaxFoodItem();
                        }else if(!samplefdItem.isEmpty()) {
                            ArrayList< ArrayList<HashMap>> newfditem = new ArrayList<>();
                            newfditem.add(samplefdItem);
                            foodItemArr.add(newfditem);
                        }
                    }
                });
            }
        }
        updateForVendorTaxFoodItem();
    }

    public void updateForVendorTaxFoodItem() {
        progressDialog.setMessage("PLease Wait while the data is being updated..");
        progressDialog.show();

        new CountDownTimer(2500, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                Log.i("Counter started for updation in foodItem" , "TRUE TRUE");

                //update FoodItem Details
                for (int i=0; i<cartVendorArr.size(); i++) {
                    for (int j=0; j<vendorArr.size(); j++) {
                        if(cartVendorArr.get(i).get("venid").toString().equals(vendorArr.get(j).get("venid").toString())) {
                            updateFDItemList(j, i);
                            break;
                        }
                    }
                }

                //update vendordetais and vendorTaxDetails
                for (int i=0; i<cartVendorArr.size(); i++) {
                    for (int j=0; j<vendorArr.size(); j++) {
                        if(cartVendorArr.get(i).get("venid").toString().equals(vendorArr.get(j).get("venid").toString())) {
                            addVendorDetailsToCart(j, i);
                            addVendortaxDetailsToCart(j, i);
                            calculateTaxAndVendorAmount(i);
                            break;
                        }
                    }
                    if(i == cartVendorArr.size()-1){
                        calculateIHFAndGrandTotal();
                        progressDialog.setMessage("PLease Wait while the data is being updated..");
                        progressDialog.show();
                    }
                }


                //Update Adapters
                new CountDownTimer(2000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) { }

                    @Override
                    public void onFinish() {
                        Log.i("Calling Notify" ,"TRUE");
                        notifyforupdates();
                        progressDialog.dismiss();
                    }
                }.start();
            }
        }.start();
    }

    public void getVendorTax() {

        vendorTaxArr.clear();

        for (int ven=0; ven<vendorArr.size(); ven++ ) {

            vendorTaxRef = db.document(fcDetails.get("fcid").toString()).collection("VendorM/" + vendorArr.get(ven).get("venid").toString() + "/TaxM");

            vendorTaxRef.whereEqualTo("status",true).orderBy("taxName", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    getVendorIdVar = false;
                    ArrayList<HashMap> sampleVendorTaxItem = new ArrayList<HashMap>();
                    for(QueryDocumentSnapshot venTax : queryDocumentSnapshots) {

                        if (!getVendorIdVar) {
                            for(HashMap vendor : vendorArr) {
                                if (vendor.get("venid").toString().equals((String)venTax.get("venid")) && !getVendorIdVar ) {
                                    vendorTaxindex = vendorArr.indexOf(vendor);
                                    getVendorIdVar = true;
                                    break;
                                }
                            }
                        }
//                        Log.i("Upadtes single " ,  venTax.getData().toString());
                        sampleVendorTaxItem.add((HashMap) venTax.getData());

                    }


                    if(vendorTaxArr.size() > vendorTaxindex && !sampleVendorTaxItem.isEmpty()) {
                        vendorTaxArr.set(vendorTaxindex, sampleVendorTaxItem);
//                        Log.i("Upadtes total " ,  sampleVendorTaxItem.toString());
//                        Log.i("Upadted ventaxArray" ,  vendorTaxArr.get(vendorTaxindex).toString());
                        if (!cartVendorTaxArr.isEmpty()) {
                            for (int ven=0; ven<cartVendorTaxArr.size(); ven++ ) {
//                                Log.i("VENDOR TAX ARRAY" , vendorTaxArr.get(vendorTaxindex).get(0).get("venid").toString());
//                                Log.i("CART VENDOR TAX ARRAY" , cartVendorTaxArr.get(ven).get(0).get("venid").toString());
//                                Log.i("CARY VENDOR TAX ARRAY SIZE" , String.valueOf(cartVendorTaxArr.size()));

                                if(vendorTaxArr.get(vendorTaxindex).get(0).get("venid").toString().equals(cartVendorTaxArr.get(ven).get(0).get("venid").toString())) {
                                    final int p = ven;
//                                    Log.i("CountDownTimer" , "started");
                                    new CountDownTimer(2500, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) { }

                                        @Override
                                        public void onFinish() {
//                                            Log.i("CountDownTimer" , "finish");
                                            addVendortaxDetailsToCart(vendorTaxindex, p);
                                            calculateTaxAndVendorAmount(p);
                                            calculateIHFAndGrandTotal();
                                            if(CartFragment.recyclerViewAdapterCartVendorList != null) {
                                                CartFragment.recyclerViewAdapterCartVendorList.notifyDataSetChanged();
//                                                Log.i("Notify done", "CART RECYCLER");
//                                                Log.i("CARY VENDOR TAX ARRAY SIZE" , String.valueOf(cartVendorTaxArr.size()));
                                            }
                                        }
                                    }.start();
                                    break;
                                }
                            }
                        }
//                        Log.i("UPDATED AT SECOND" , sampleVendorTaxItem.toString());

                    }else if(!sampleVendorTaxItem.isEmpty()) {
                        vendorTaxArr.add(sampleVendorTaxItem);
//                        Log.i("UPDATED AT FIRST" , sampleVendorTaxItem.toString());
                    }
                }
            });
        }
    }



    // ON BACK PRESSED =============================================================================
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }



    // COUNTER =====================================================================================
    public void setCounter() {

        if(inActiveCounternew != null) {
            inActiveCounternew.cancel();
        }

        inActiveCounternew = new CountDownTimer(480000, 1000) {

            public void onTick(long millisUntilFinished) {
                counter = millisUntilFinished/1000;
//                Log.i("Counter" , String.valueOf(counter) );
            }

            public void onFinish() {
                Toast.makeText(Home.this, "No Activity since 8 Minutes", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }




}
