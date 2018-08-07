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
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import io.grpc.Context;

public class Home extends AppCompatActivity {

    public static FirebaseFirestore firestore;
    public static CollectionReference db;
    public static StorageReference storageRef;
    CollectionReference vendorItemRef;
    CollectionReference categoryItemRef;


    Intent login;
    public static HashMap fcDetails;
    public static HashMap tableDetails;
    public static HashMap adminDetails;
    public static HashMap employeeDetails;

    BottomNavigationView navigation;
    Boolean flagAdvFirst = true;
    Boolean flagMenuFirst = true;
    Boolean flagCartFirst = true;
    Boolean flagOrderFirst = true;
    public static long counter = 0;
    public static CountDownTimer inActiveCounternew;
    Boolean getVendorIdVar;
    Boolean getCatIdVar;
    public static FragmentManager fragmentManager;
    public static FragmentTransaction ft;
    ProgressDialog progressDialog;



    //Home Initials
    public static ArrayList<HashMap> advertisementArr;
    public static ArrayList<HashMap> whatsNewArr;
    public static ArrayList<HashMap> vendorArr;
    public static ArrayList<ArrayList <HashMap>> categoryArr;
    public static ArrayList<ArrayList <ArrayList <HashMap>>> foodItemArr;
    public static int vendorPosition;
    public static int catPosition;



    // Asysny Initials
    int catindex = -1;
    int venindex = -1;



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
                    if(navigation.getSelectedItemId() != R.id.navigation_menu) {
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
                    return true;
                case R.id.navigation_cart:
                    if(navigation.getSelectedItemId() != R.id.navigation_cart) {
                        stopImageSwitcher(true, true);
//                        getSupportActionBar().show();
                        return true;
                    }
                case R.id.navigation_order:
                    if(navigation.getSelectedItemId() != R.id.navigation_order) {
                        stopImageSwitcher(true, true);
//                        getSupportActionBar().show();
                        return true;
                    }
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


        // FIRESTORE
        firestore = FirebaseFirestore.getInstance();
        db = firestore.collection("foodcourts");
        storageRef = FirebaseStorage.getInstance().getReference();


        // INITIALIZATION
        login = getIntent();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting Up Your App");
        progressDialog.show();
        fcDetails = (HashMap) login.getSerializableExtra("fcDetails");
        adminDetails = (HashMap) login.getSerializableExtra("adminDetails");
        tableDetails = (HashMap) login.getSerializableExtra("tableDetails");
        employeeDetails = (HashMap) login.getSerializableExtra("employeeDetails");
        menuInit();



        // Asysnc Task Start
        new AsysncTask().execute();
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                getFoodItem();
                Toast.makeText(Home.this, "Food Item Started", Toast.LENGTH_SHORT).show();
            }
        }.start();



        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(navigation);


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
                    Fragment frag =  getFragmentManager().findFragmentByTag(fragment.getClass().getName());
                    ft.replace(R.id.rootLayout, frag);
                    ft.commit();
                }
            }

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
        vendorArr  = new ArrayList<>();
        categoryArr  = new ArrayList<>();
        foodItemArr  = new ArrayList<>();
        advertisementArr = new ArrayList<>();
    }




    // ASYSNC TASK =================================================================================
    private class AsysncTask extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            // Advertisement
            db.document(fcDetails.get("fcid").toString()).collection("AdvertisementM").whereEqualTo("status", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    advertisementArr.clear();
                    for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        advertisementArr.add( (HashMap) document.getData());
                    }
                }
            });





            // Menu
            db.document(fcDetails.get("fcid").toString()).collection("WhatsNewM").whereEqualTo("status", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        whatsNewArr.clear();
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            whatsNewArr.add((HashMap) document.getData());
                        }
                        Log.i("WhatsNew Array" , whatsNewArr.toString());
                    }
            });

            db.document(fcDetails.get("fcid").toString()).collection("VendorM").whereEqualTo("status", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    vendorArr.clear();
                    categoryArr.clear();
                    foodItemArr.clear();

                    for (final QueryDocumentSnapshot vendor : queryDocumentSnapshots) {
                        vendorArr.add((HashMap) vendor.getData());

//                        Log.i("Vendor" , vendor.getData().toString());
                        vendorItemRef = db.document(fcDetails.get("fcid").toString()).collection("VendorM/" + vendor.getString("venid") + "/CategoryM");
                        vendorItemRef.whereEqualTo("status" , true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                                // Category Item Started
                                getVendorIdVar = false;
                                final ArrayList<HashMap> sample = new ArrayList<HashMap>();
                                for(QueryDocumentSnapshot catItem : queryDocumentSnapshots) {

                                    if (!getVendorIdVar) {
                                        for(HashMap vendor : vendorArr) {
                                            if (vendor.get("venid").toString().equals((String)catItem.get("venid")) & !getVendorIdVar ) {
                                                catindex = vendorArr.indexOf(vendor);
                                                getVendorIdVar = true;
                                                break;
                                            }
                                        }
                                    }
                                    sample.add((HashMap) catItem.getData());

                                    //===> food Item

                                }
                                if(categoryArr.size() > catindex && !sample.isEmpty()) {
//                                    Log.i("Category Second index" , String.valueOf(catindex));
//                                    Log.i("Second Cat" , sample.toString());
                                    categoryArr.set(catindex, sample);

                                }else if(!sample.isEmpty()) {
//                                    Log.i("Category First" , "True");
                                    categoryArr.add(sample);
                                }
                                Log.i("Category Array Size" , String.valueOf(categoryArr.size()));
                                Log.i("Category Array Lst" , String.valueOf(categoryArr.get(categoryArr.size()-1).size()));
                            }
                        });

                    }

                    Log.i("Vendor Array" , vendorArr.toString());
                    Log.i("Vendor Array Size" , String.valueOf(vendorArr.size()));

                }
            });




            return null;
        }


        @Override
        protected void onPostExecute(Void unused) {
            Toast.makeText(Home.this, " PROGRESS COMPLETED", Toast.LENGTH_SHORT).show();
            Log.i("PROGRESS" , "COMPLETED");
            progressDialog.dismiss();

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

                categoryItemRef.whereEqualTo("status" , true).orderBy("fspos").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
//                                                    Log.i("Second Food Item" , "True");
                                foodItemArr.get(venindex).set(catindex, samplefdItem);
                            }else {
//                                                    Log.i("First Food Item" , "True");
                                foodItemArr.get(venindex).add(samplefdItem);
                            }
                        }else if(!samplefdItem.isEmpty()) {
//                                                Log.i("First Food Item" , "True");
                            ArrayList< ArrayList<HashMap>> newfditem = new ArrayList<>();
                            newfditem.add(samplefdItem);
                            foodItemArr.add(newfditem);
                        }
                    }
                });





            }
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
