package com.example.smahadik.kloudkafe;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.annotation.Nullable;

import static android.webkit.WebSettings.PluginState.ON;

public class Home extends AppCompatActivity {

    public static FirebaseFirestore firestore;
    public static CollectionReference db;
    public static CollectionReference dbTransaction;
    public static StorageReference storageRef;
    CollectionReference vendorItemRef;
    CollectionReference vendorTaxRef;
    CollectionReference categoryItemRef;

    Intent login;
//    Intent logout;
    ImageView logoutImageView;
    Boolean logoutActive = false;
    int logoutCounter = 0;
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
    BottomNavigationMenuView menuView;
    public static View notificationBadge;
    public static TextView cartCounterTextView;
    public static int cartCounter=0;

    public static Boolean flagAdvFirst = true;
    public static Boolean flagMenuFirst = true;
    public static Boolean flagCartFirst = true;
    public static Boolean flagOrderFirst = true;
    Boolean getVendorIdVar;
    Boolean getCatIdVar;
    public static Boolean checkStartOrderID = true;
    public static FragmentManager fragmentManager;
    public static FragmentTransaction ft;
    public static ProgressDialog progressDialog;
    public static int advertisementStandbyTimerLimit = 8;
    public static int clearCartStandbyTimerLimit = 20;
    public static int  autoOrderClearTimerLimit = 20;
    public static CountDownTimer advertisementStandbyTimer;
    public static CountDownTimer clearCartStandbyTimer;



    //Home Initials
    CartFragment cartFragment = new CartFragment();
    public static Home home = new Home();
    public static ArrayList<HashMap> advertisementArr = new ArrayList<>();
    public static ArrayList<HashMap> whatsNewArr = new ArrayList<>();
    public static ArrayList<HashMap> ymalArr = new ArrayList<>();
    public static ArrayList<HashMap> freebieArr = new ArrayList<>();
    public static ArrayList<HashMap> vendorArr = new ArrayList<>();
    public static ArrayList<ArrayList <HashMap>> categoryArr = new ArrayList<>();
    public static ArrayList<ArrayList <HashMap>> vendorTaxArr = new ArrayList<>();
    public static ArrayList<HashMap> fcTaxArr = new ArrayList<>();
    public static ArrayList<ArrayList <ArrayList <HashMap>>> foodItemArr = new ArrayList<>();
    public static ArrayList<HashMap> cartFCTaxArr = new ArrayList<>();
    public static ArrayList<ArrayList<HashMap>> cartArr = new ArrayList<>();
    public static ArrayList<ArrayList <HashMap>> cartVendorTaxArr = new ArrayList<>();
    public static ArrayList<HashMap> cartVendorArr = new ArrayList<>();
    public static ArrayList<String> orderIdArr = new ArrayList<>();
    public static ArrayList<ArrayList <HashMap>> orderVendorArr = new ArrayList<>();
    public static ArrayList<ArrayList<ArrayList <HashMap>>> orderFoodItemArr = new ArrayList<>();
    public static ArrayList <HashMap> orderNotificationArr = new ArrayList<>();
    public static ArrayList <HashMap> autoOrderCLearArr = new ArrayList<>();
    public static int vendorPosition;
    public static int catPosition;
    public static int lastVendorPosition = -1;
    public static String currencyFc;
    public static String orderId;
    public static Boolean orderPlaced = false;
    public static Boolean checkPreviousOrders = true;
    public static Boolean notificationActive = false;
    public static Boolean autoOrderClearActive = false;
    public static String [] zeros = {"00000000" , "0000000" , "000000" , "00000" , "0000" , "000" , "00" , "0"};
    public static Dialog autoClearCartConfirmationPopup;
    Dialog logoutConfirmationPopup;
    Dialog logoutPasswordAuthenticationPopup;
    Dialog clearCartConfirmationPopup;



    // Asysny Initials
    int catindex = -1;
    int venindex = -1;
    int vendorTaxindex = -1;
    Boolean firstTimeCat = true;

    //LOCK INITIALS
    DevicePolicyManager dpm;


    public static int width;
    DisplayMetrics metrics = new DisplayMetrics();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    Log.i("Clicked on" , "HOME");
                    if(navigation.getSelectedItemId() != R.id.navigation_home || flagAdvFirst) {
                        if(flagAdvFirst) {
//                            Log.i("Flag ADV FIRST " ,  "Flag ADV FIRST = TRUE");
//                            Log.i("Navigation " ,  "Changed");
                            stopImageSwitcher(false , true);
                            pushFragment(new AdvertisementFragment(), flagAdvFirst);
                            flagAdvFirst = false;
                        } else {
                            stopImageSwitcher(false , true);
//                            Log.i("Flag ADV SECOND " ,  "Flag ADV SECOND = FALSE");
                            pushFragment(new AdvertisementFragment(), flagAdvFirst);
                        }
                        if(advertisementStandbyTimer != null) { advertisementStandbyTimer.cancel(); }
                    }
                    return true;
                case R.id.navigation_menu:
//                    Log.i("Clicked on" , "MENU");
                    pushMenuFrag();
                    return true;

                case R.id.navigation_cart:
                    if(navigation.getSelectedItemId() != R.id.navigation_cart) {
                        stopImageSwitcher(true, true);
                        if(flagCartFirst) {
//                            Log.i("Flag CART FIRST " ,  "Flag CART FIRST = TRUE");
//                            Log.i("Navigation " ,  "Changed");
                            pushFragment(new CartFragment(), flagCartFirst);
                            flagCartFirst = false;
                        } else {
//                            Log.i("Flag CART SECOND " ,  "Flag CART SECOND = FALSE");
                            pushFragment(new CartFragment(), flagCartFirst);
                        }
                        setAdvCounter();
                    }
                    return true;
                case R.id.navigation_order:
                    pushOrdersFrag();
                    return true;
            }
            return false;
        }
    };




//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (dpm.isLockTaskPermitted(getApplicationContext().getPackageName())) {
//            this.startLockTask();
//        } else {
//            Toast.makeText(home, "ERROR LOCKING DEVICE", Toast.LENGTH_LONG).show();
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home);

        // Setting LOCKS
        Context context = getApplicationContext();
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.startLockTask();


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
        logoutImageView = findViewById(R.id.logout);
        logoutConfirmationPopup = new Dialog(this);
        logoutPasswordAuthenticationPopup = new Dialog(this);
        clearCartConfirmationPopup = new Dialog(this);
        autoClearCartConfirmationPopup = new Dialog(this);
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
        orderDetails = basicDetails;
        dbTransaction = firestore.collection("transactions/" + fcDetails.get("fcid") + "/orders");

        currencyFc = fcDetails.get("curky").toString();
        if(currencyFc.equals("INR")) {
            currencyFc = "\u20B9" + " ";
        }else { currencyFc = "$ "; }


        // Asysnc Task Start
        new AsysncTask().execute();
        new CountDownTimer(7500, 1000) {
            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
//                Log.i("Food Item Started from asysnc task in 7 sec" , "TRUE TRUE TRUE");
//                Toast.makeText(Home.this, "Food Item Started from asysnc task in 7 sec" + "", Toast.LENGTH_SHORT).show();
                getFoodItem();
                getVendorTax();
                navigation.setSelectedItemId(R.id.navigation_home);
            }
        }.start();




        // BOttom Navigation Initialization
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        setBottomNavigationIconSize();
        addCartBadgeView();
        addOrdersBadgeView();

        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });




    } // ONCREARTE DONE





    // BASIC =======================================================================================
    protected void pushFragment(Fragment fragment, Boolean flag) {
        if (fragment == null) { return ; }

        fragmentManager = getFragmentManager();
        ft = fragmentManager.beginTransaction();

        if (fragmentManager != null) {

            if(flag) {
//                Log.i("Push Fragment FIRST " ,  "Push Fragment  FIRST = TRUE");

                if (ft != null) {
                    ft.replace(R.id.rootLayout, fragment, fragment.getClass().getName());
//                    Log.i("TAGGGGGGG" , fragment.getClass().getName());
                    ft.addToBackStack(null);
//                    Log.i("BACKSTACK ENTRY LIST", String.valueOf(getFragmentManager().getBackStackEntryCount()));
//                    ft.replace(R.id.rootLayout, fragment);
//                    if(flagAdvFirst) {
//                    Log.i("Calling Commit", "TRUEEEEEE");
                    ft.commit();
//                    }

                }
            } else {
//                Log.i("Push Fragment Second " ,  "Push Fragment  Second = TRUE");
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
//                Log.i("Flag MENU FIRST " ,  "Flag MENU FIRST = TRUE");
//                Log.i("Navigation " ,  "Changed");
                stopImageSwitcher(true , false);
                pushFragment(new MenuFragment(), flagMenuFirst);
                flagMenuFirst = false;
            } else {
//                Log.i("Flag MENU SECOND " ,  "Flag MENU SECOND = FALSE");
                stopImageSwitcher(true , false);
                pushFragment(new MenuFragment(), flagMenuFirst);
            }
            setAdvCounter();
        }
    }


    public void pushOrdersFrag() {
        if(navigation.getSelectedItemId() != R.id.navigation_order) {
            notificationBadge.setVisibility(View.INVISIBLE);
            stopImageSwitcher(true, true);
            if(flagOrderFirst) {
//                Log.i("Flag ORDER FIRST " ,  "Flag ORDER FIRST = TRUE");
//                Log.i("Navigation " ,  "Changed");
                pushFragment(new OrdersPageFragment(), flagOrderFirst);
                flagOrderFirst = false;
            } else {
//                Log.i("Flag ORDER SECOND " ,  "Flag ORDER SECOND = FALSE");
                pushFragment(new OrdersPageFragment(), flagOrderFirst);
            }
            setAdvCounter();
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


    public void addCartBadgeView() {
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(2);
        View notificationBadge = LayoutInflater.from(this).inflate(R.layout.bottomview_notification_badge_cart, menuView, false);
        cartCounterTextView = notificationBadge.findViewById(R.id.cartCountBadgeTextView);
        cartCounterTextView.setText(String.valueOf(cartCounter));
        cartCounterTextView.setVisibility(View.INVISIBLE);
        itemView.addView(notificationBadge);
    }


    public void addOrdersBadgeView() {
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(3);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.bottomview_notification_badge_orders, menuView, false);
        itemView.addView(notificationBadge);
        notificationBadge.setVisibility(View.INVISIBLE);
    }


    public void setBottomNavigationIconSize() {
        for (int i = 0; i < menuView.getChildCount(); i++) {
            View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
    }




    // Cart ========================================================================================

    public void addtocart(HashMap foodItem) {

        setAdvCounter();
        ArrayList <HashMap> newFoodItemArrayList = new ArrayList<>();

        foodItem.put("quantity", 1);
        foodItem.put("totalAmount", foodItem.get("amount").toString());
        Boolean venidFoundFlag = false;

        cartCounter++;
        if(cartCounter == 1) {
            cartCounterTextView.setVisibility(View.VISIBLE);
        }
        cartCounterTextView.setText(String.valueOf(cartCounter));
//        Log.i("FoodItem At addtocart" , foodItem.toString());

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
                setClearCartCounter();
        }

    }

    public void removefromcart(HashMap foodItem) {
        setAdvCounter();
        for(int i=0; i<cartVendorArr.size(); i++){
            if(cartVendorArr.get(i).get("venid").toString().equals(foodItem.get("venid").toString())) {
                // venid Found in cart

                for(int j=0; j<cartArr.get(i).size(); j++){
                    if(cartArr.get(i).get(j).get("fdid").toString().equals(foodItem.get("fdid").toString())
                            && cartArr.get(i).get(j).get("catid").toString().equals(foodItem.get("catid").toString()) ) {

                        cartCounter = cartCounter - Integer.parseInt(cartArr.get(i).get(j).get("quantity").toString());
                        if(cartCounter > 0) {
                            cartCounterTextView.setText(String.valueOf(cartCounter));
                        } else {
                            clearCartStandbyTimer.cancel();
                            cartCounterTextView.setVisibility(View.INVISIBLE);
                        }

                        // FoodItem Found
                        if(cartArr.get(i).size() == 1){
                            cartArr.remove(i);
                            cartVendorArr.remove(i);
                            cartVendorTaxArr.remove(i);
//                            Log.i("cartArry Size at last item" , String.valueOf(cartArr.size()));
//                            Log.i("cartVendorArry Size at last item" , String.valueOf(cartVendorArr.size()));
//                            Log.i("cartVendorTaxArry Size at last item" , String.valueOf(cartVendorTaxArr.size()));
                            calculateIHFAndGrandTotal();
                        }else {
                            cartArr.get(i).remove(j);
//                            Log.i("cartArry Size " , String.valueOf(cartArr.size()));
//                            Log.i("cartVendorArry Size " , String.valueOf(cartVendorArr.size()));
//                            Log.i("cartVendorTaxArry Size " , String.valueOf(cartVendorTaxArr.size()));
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
//        Log.i("START cart vendor tax array size ", String.valueOf(cartVendorTaxArr.size()));
//        Log.i("passed pos ", String.valueOf(pos));
        ArrayList <HashMap> newVendorTaxArrayList = vendorTaxArr.get(pos);
        for(int i=0; i<newVendorTaxArrayList.size() ; i++) {
            newVendorTaxArrayList.get(i).put("amount", 00);
        }
        if(cartpos != -1){
            cartVendorTaxArr.set( cartpos , newVendorTaxArrayList);
//            Log.i("cart vendor tax array setted  ", cartVendorTaxArr.toString());
        }else {
            cartVendorTaxArr.add(newVendorTaxArrayList);
//            Log.i("cart vendor tax array  ", "added");
        }
//        Log.i("END cart vendor tax array size ", String.valueOf(cartVendorTaxArr.size()));
//        Log.i("cart vendor tax array ", "DONE");
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
//            Log.i("cart vendor tax array setted  ", cartVendorTaxArr.toString());
        }else {
            cartVendorArr.add(newVendorArrayList);
//            Log.i("cart vendor array  ", "added");
        }



    }

    public void calculateTaxAndVendorAmount(int position) {

//        Log.i("position passed" , String.valueOf(position));
        Double amount = 0.00;
        Double taxAmount = 0.00;

        for(int i=0; i<cartArr.get(position).size(); i++) {
            amount = amount +  Double.parseDouble(cartArr.get(position).get(i).get("totalAmount").toString());
            amount = Double.parseDouble(decimalFormatter.format(amount));
        }
        cartVendorArr.get(position).put("baseAmount" , amount);

//        Log.i("cartvendor tax array size" , String.valueOf(cartVendorTaxArr.size()));
        for(int i=0; i<cartVendorTaxArr.get(position).size(); i++) {
            taxAmount = taxAmount + ( (amount *  Double.parseDouble(Home.cartVendorTaxArr.get(position).get(i).get("taxPer").toString())) /100 );
            taxAmount = Double.parseDouble(Home.decimalFormatter.format(taxAmount));
            cartVendorTaxArr.get(position).get(i).put("amount" , Home.decimalFormatter.format((amount *  Double.parseDouble(Home.cartVendorTaxArr.get(position).get(i).get("taxPer").toString()))/100 )  );
        }
        cartVendorArr.get(position).put("totalTax" , taxAmount);
        cartVendorArr.get(position).put("subTotalAmount" , Double.parseDouble(Home.decimalFormatter.format(taxAmount + amount)) );

    }

    public void calculateIHFAndGrandTotal() {
        double baseAmount = 0.00;
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
            orderDetails.put("finalOrderStatus" , "OPEN" );
//        }

        if(CartFragment.textViewIHFPer != null) {
            CartFragment.textViewIHFPer.setText(removedecimalFormatter.format(fcTaxArr.get(0).get("taxPer")) + " %");
        }

//        progressDialog.dismiss();
//        Log.i("Every Thing Done" ,"TRUE ");
    }

    public void clearcartButton(View view) {
        Home.home.setAdvCounter();

        if(cartArr.size() > 0) {
            clearCartConfirmationPopup.setContentView(R.layout.clearcart_confirmation_popup_card);

            Button clearCartCancelButton = clearCartConfirmationPopup.findViewById(R.id.clearCartCancelButton);
            Button clearCartYesButton = clearCartConfirmationPopup.findViewById(R.id.clearCartYesButton);

            clearCartCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { clearCartConfirmationPopup.dismiss();
                }
            });
            clearCartYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearcart();
                    clearCartConfirmationPopup.dismiss();
                }
            });

            clearCartConfirmationPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            clearCartConfirmationPopup.show();
        } else {
            Toast.makeText(Home.this, "CART IS EMPTY", Toast.LENGTH_LONG).show();
        }


    }
    public void clearcart(){
        cartArr.clear();
        cartVendorTaxArr.clear();
        cartVendorArr.clear();
        calculateIHFAndGrandTotal();
        if(CartFragment.recyclerViewAdapterCartVendorList != null) {
            cartFragment.notifyForUpdates();
        }
        clearCartStandbyTimer.cancel();
        cartCounter = 0;
        cartCounterTextView.setVisibility(View.INVISIBLE);
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
//            Log.i("Notify Done vendorlist" ,"TRUE ");
            MenuFragment.recyclerViewAdapterVendorList.notifyDataSetChanged();
        }
        if(VendorItemListFragment.categorySliderAdapter != null) {
//            Log.i("Notify Done category slider" ,"TRUE ");
            VendorItemListFragment.categorySliderAdapter.notifyDataSetChanged();
            VendorItemListFragment.categorySliderAdapter = new CategorySliderAdapter(this);
            VendorItemListFragment.foodItemListViewPager.setAdapter(VendorItemListFragment.categorySliderAdapter);
        }
        if(CategorySliderAdapter.recyclerViewAdapterFoodItemList != null) {
//            Log.i("Notify Done fooditemlist" ,"TRUE ");
            CategorySliderAdapter.recyclerViewAdapterFoodItemList.notifyDataSetChanged();
        }
        if(CartFragment.recyclerViewAdapterCartVendorList != null) {
//            Log.i("Notify Done cartvendor list" ,"TRUE ");
//            Log.i("cart Array" , cartArr.toString());
//            Log.i("cart Vendor Array" , cartVendorArr.toString());
//            Log.i("cart vendor tax Array" , cartVendorTaxArr.toString());
//            Log.i("cart FC tax Array" , cartFCTaxArr.toString());
//            Log.i("cart order details Array" , orderDetails.toString());
            cartFragment.notifyForUpdates();
        }

//        progressDialog.dismiss();

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
//                        Log.i("Advertisemnt Array" , advertisementArr.toString());
                    }
                    if(AdvertisementFragment.advertisementSliderAdapter != null) {
                        AdvertisementFragment.advertisementSliderAdapter.notifyDataSetChanged();
                    }
                }
            });


            //Advertisement Standby Variable
            db.document(fcDetails.get("fcid") + "/TimerM/AdvertisementStandby").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        advertisementStandbyTimerLimit = Integer.parseInt(task.getResult().get("timer").toString());
                    }else {
//                        Log.i("ERROR" , "ERROR getting AdvertisementStandby Timer");
                    }
                }
            });


            //ClearCart Standby Variable
            db.document(fcDetails.get("fcid") + "/TimerM/ClearCartStandby").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        clearCartStandbyTimerLimit = Integer.parseInt(task.getResult().get("timer").toString());
                    }else {
                        Log.i("ERROR" , "ERROR getting ClearCartStandbyTimerLimit Timer");
                    }
                }
            });

            //AutoClear Order  Variable
            db.document(fcDetails.get("fcid") + "/TimerM/AutoClearOrder").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        autoOrderClearTimerLimit = Integer.parseInt(task.getResult().get("timer").toString());
                    }else {
                        Log.i("ERROR" , "ERROR getting AutoClearOrder Timer");
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
//                            Log.i("WhatsNew Array" , whatsNewArr.toString());
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
//                        Log.i("Vendor" , vendor.getData().toString());
                        if(vendorArr.size() == queryDocumentSnapshots.size()) {
//                            Log.i("CAlling for Category starting..........." , ".......... TRUE");
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
//                        Log.i("YMAL Array" , ymalArr.toString());
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
//                        Log.i("Freebie Array" , freebieArr.toString());
                    }
                    notifyforupdates();
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) { }

    }

    public void getCategoryItem() {

        // Category Item Started
//        Log.i("Vendor Array Size IN CATEGORY ITEM " , String.valueOf(vendorArr.size()));
//        Log.i("Category Array Size IN CATEGORY ITEM" , String.valueOf(categoryArr.size()));

        firstTimeCat = true;
        categoryArr.clear();

        for (int ven=0; ven<vendorArr.size(); ven++ ) {

//            Log.i(" searching for venid " , vendorArr.get(ven).get("venid").toString());
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
//                                    Log.i(" category VENDOR venid " , vendor.get("venid").toString());
//                                    Log.i(" category venid " , catItem.getString("venid"));
                                    break;
                                }
                            }
                        }
                        sample.add((HashMap) catItem.getData());

                    }
//                    Log.i("category size" , String.valueOf(categoryArr.size()));
//                    Log.i("category index" , String.valueOf(catindex));
                    if(categoryArr.size() > catindex && !sample.isEmpty() && !firstTimeCat) {
//                        Log.i("Category Second index" , String.valueOf(catindex));
                        categoryArr.set(catindex, sample);
                        firstTimeCat = false;
                        notifyforupdates();
                    }else if(!sample.isEmpty() && firstTimeCat) {
//                        Log.i("Category First" , "True");
                        categoryArr.add(sample);
                    }

//                    Log.i("Category Array Size" , String.valueOf(categoryArr.size()));
//                    Log.i("Category Array Lst" , String.valueOf(categoryArr.get(categoryArr.size()-1).size()));

                    if(categoryArr.size() == vendorArr.size()) {
                        // Sorting Category
//                        Log.i("Category Array Lst Sorting" , "TRUEEEEEEE");
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
//        Log.i("Vendor Array Size IN FOOD ITEM " , String.valueOf(vendorArr.size()));
//        Log.i("Category Array Size IN FOOD ITEM" , String.valueOf(categoryArr.size()));

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

//                        Log.i("Venindex" , String.valueOf(venindex));
//                        Log.i("catindex" , String.valueOf(catindex));

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
        progressDialog.setMessage("Please wait while the data is being updated..");
        progressDialog.show();

        new CountDownTimer(2500, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

//                Log.i("Counter started for updation in foodItem" , "TRUE TRUE");

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
//                        Log.i("Calling Notify" ,"TRUE");
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }




    // COUNTER =====================================================================================
    public void setAdvCounter() {

        Log.i("ADV Counter RESET :" , "TRUE"  );
        if(advertisementStandbyTimer != null) {
            advertisementStandbyTimer.cancel();
        }
        advertisementStandbyTimer = new CountDownTimer(advertisementStandbyTimerLimit*60000 , 1000) {
            public void onTick(long millisUntilFinished) {
//                Log.i("ADV Counter Remaining :" , String.valueOf(millisUntilFinished) );
            }
            public void onFinish() {
                closeAllPopUps();
                navigation.setSelectedItemId(R.id.navigation_home);
            }
        }.start();
    }


    public void setClearCartCounter() {
        Log.i("Clear Cart Counter RESET :" , "TRUE"  );

        if(clearCartStandbyTimer != null) {
            clearCartStandbyTimer.cancel();
        }
        clearCartStandbyTimer = new CountDownTimer(clearCartStandbyTimerLimit*60000 , 1000) {
            public void onTick(long millisUntilFinished) {
//                Log.i("Clear Cart Counter Remaining :" , String.valueOf(millisUntilFinished) );
            }
            public void onFinish() {
                showAutoClearCartConfirmationPopUp();
            }
        }.start();
    }


    public void showAutoClearCartConfirmationPopUp() {
        if(navigation.getSelectedItemId() != R.id.navigation_home) {
            setAdvCounter();
        }
        autoClearCartConfirmationPopup.setContentView(R.layout.auto_clearcart_confirmation_popup_card);
        final TextView textViewAutoClearCartPopup = autoClearCartConfirmationPopup.findViewById(R.id.textViewAutoClearCartPopup);
        final Button autoClearCartClearButton = autoClearCartConfirmationPopup.findViewById(R.id.autoClearCartClearButton);
        Button autoClearCartContinueButton = autoClearCartConfirmationPopup.findViewById(R.id.autoClearCartContinueButton);
        autoClearCartConfirmationPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        autoClearCartConfirmationPopup.show();

        final CountDownTimer clearCart = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewAutoClearCartPopup.setText("Your Cart will Auto-Clear in " + millisUntilFinished/1000 + " Seconds");
                autoClearCartClearButton.setText("Clear Cart ("+ millisUntilFinished/1000 +")");
            }
            @Override
            public void onFinish() {
                clearcart();
                autoClearCartConfirmationPopup.dismiss();
            }
        }.start();

        autoClearCartContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClearCartCounter();
                clearCart.cancel();
                autoClearCartConfirmationPopup.dismiss();
            }
        });
        autoClearCartClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearcart();
                clearCart.cancel();
                autoClearCartConfirmationPopup.dismiss();
            }
        });
    }


    public void closeAllPopUps() {
        if(logoutConfirmationPopup != null) {
            logoutConfirmationPopup.dismiss();
        }
        if(logoutPasswordAuthenticationPopup != null) {
            logoutPasswordAuthenticationPopup.dismiss();
        }
        if(clearCartConfirmationPopup != null) {
            clearCartConfirmationPopup.dismiss();
        }
        if(VendorItemListFragment.changeVendorPopUp != null) {
            VendorItemListFragment.changeVendorPopUp.dismiss();
        }
        if(RecyclerViewAdapterCartVendorList.MyViewHolderCartVendorList.vendorTaxPopUp != null) {
            RecyclerViewAdapterCartVendorList.MyViewHolderCartVendorList.vendorTaxPopUp.dismiss();
        }
        if(CartFragment.iHFPopUp != null) {
            CartFragment.iHFPopUp.dismiss();
        }
        if(CartFragment.payModeConfirmationPopUp != null) {
            CartFragment.payModeConfirmationPopUp.dismiss();
        }
        if(CartFragment.payModePopUp != null) {
            CartFragment.payModePopUp.dismiss();
        }
    }



    // LOGOUT =====================================================================================

    public void logOut() {
        if(logoutActive) {
            logoutCounter++;
            if(logoutCounter == 10) {

                logoutCounter = 0;
                logoutActive = false;

                logoutConfirmationPopup.setCanceledOnTouchOutside(false);
                logoutConfirmationPopup.setContentView(R.layout.logout_confirmation_popup_card);
                Button logoutCancelButton = logoutConfirmationPopup.findViewById(R.id.logoutCancelButton);
                Button logoutYesButton = logoutConfirmationPopup.findViewById(R.id.logoutYesButton);
                logoutCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logoutConfirmationPopup.dismiss();
                    }
                });
                logoutYesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logoutConfirmationPopup.dismiss();
                        passwordConfirmationPopupLogout();
                    }
                });
                logoutConfirmationPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                logoutConfirmationPopup.show();

            }
            return;
        }

        logoutCounter++;
        logoutActive = true;
        setAdvCounter();

        new CountDownTimer(7000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                logoutActive = false;
                logoutCounter = 0;
            }
        }.start();
    }


    public void passwordConfirmationPopupLogout() {

        logoutPasswordAuthenticationPopup.setCanceledOnTouchOutside(false);
        logoutPasswordAuthenticationPopup.setContentView(R.layout.logout_password_check_popup_card);
        TextView textViewLogoutPopupAdminName = logoutPasswordAuthenticationPopup.findViewById(R.id.textViewLogoutPopupAdminName);
        final EditText editTextLogoutPopupPassword = logoutPasswordAuthenticationPopup.findViewById(R.id.editTextLogoutPopupPassword);
        Button logoutButton = logoutPasswordAuthenticationPopup.findViewById(R.id.logoutButton);
        final Button closeLogoutPopupButton = logoutPasswordAuthenticationPopup.findViewById(R.id.closeLogoutPopupButton);

        textViewLogoutPopupAdminName.setText(basicDetails.get("displayName").toString());
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextLogoutPopupPassword.getText().toString().equals(adminDetails.get("password").toString())) {
                    db.document(fcDetails.get("fcid") + "/TableM/" + tableDetails.get("tabid")).update("status" , false)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    home.stopLockTask();
                                    System.exit(0);
                                    finish();
                                }
                            });
//                    logout = new Intent(Home.this, Login.class);
//                    startActivity(logout);
                } else {
                    Toast.makeText(Home.this, "Authentication Failed !!", Toast.LENGTH_SHORT).show();
                    editTextLogoutPopupPassword.setText("");
                }
            }
        });
        closeLogoutPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutPasswordAuthenticationPopup.dismiss();
            }
        });
        logoutPasswordAuthenticationPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        logoutPasswordAuthenticationPopup.show();
    }





}
