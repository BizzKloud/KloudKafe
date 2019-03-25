package com.example.smahadik.kloudkafe;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import static android.view.View.VISIBLE;

public class Login extends AppCompatActivity {


    FirebaseFirestore firestore;
    CollectionReference db;

    LinearLayout linearlayoutFoodcourt;
    LinearLayout linearlayoutTable;
    LinearLayout linearlayoutActivate;
    TextView statusFoodcourt;
    TextView statusTable;
    TextView statusActivate;
    TextView foodcourtTextView;
    TextView adminTextView;
    TextView tableTextView;
    ImageView imageViewCircleFd;
    ImageView imageViewCircleTb;
    ImageView imageViewCircleActivate;
    Spinner foodcourtSpnr;
    Spinner adminSpnr;
    Spinner tableSpnr;
    EditText passEditText;
    ProgressDialog progressDialog;

    ArrayList<String> fcNames = new ArrayList<String>();
    ArrayList<String> adminNames = new ArrayList<String>();;
    ArrayList<String> tableNames = new ArrayList<String>();;
    ArrayList<HashMap> foodCourtArr = new ArrayList<HashMap>();
    ArrayList<HashMap> adminArr = new ArrayList<HashMap>();
    ArrayList<HashMap> employeeArr = new ArrayList<HashMap>();
    ArrayList<HashMap> tableArr = new ArrayList<HashMap>();
    ArrayAdapter<String> adapterfc;
    ArrayAdapter<String> adapteradmin;
    ArrayAdapter<String> adaptertable;
    String passcode;
    String foodcourtsPath = "foodcourts";
    String fcid;
//    String adminid;
    String adminPath;
    String tablePath;
    int posfc;
    int posadmin;
    int postable;
    HashMap tableDetails = new HashMap();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        checkInternetConnection();


//        FIRESTORE
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
        firestore.setFirestoreSettings(settings);
        db = firestore.collection("foodcourts");



//        INITIALIZATION
        linearlayoutFoodcourt = findViewById(R.id.linearlayoutFoodcourt);
        linearlayoutTable = findViewById(R.id.linearlayoutTable);
        linearlayoutActivate = findViewById(R.id.linearlayoutActivate);
        statusFoodcourt = findViewById(R.id.statusFoodcourt);
        statusTable = findViewById(R.id.statusTable);
        statusActivate = findViewById(R.id.statusActivate);
        foodcourtTextView = findViewById(R.id.foodcourtTextView);
        tableTextView = findViewById(R.id.tableTextView);
        adminTextView = findViewById(R.id.adminTextView);
        imageViewCircleFd = findViewById(R.id.imageViewCircleFd);
        imageViewCircleTb = findViewById(R.id.imageViewCircleTb);
        imageViewCircleActivate = findViewById(R.id.imageViewCircleActivate);
        progressDialog = new ProgressDialog(this);
        linearlayoutTable.setTranslationX(-1000f);
        linearlayoutActivate.setTranslationX(-1000f);
        foodcourtSpnr = findViewById(R.id.foodcourt);
        adminSpnr = findViewById(R.id.admin);
        tableSpnr = findViewById(R.id.table);
        passEditText = findViewById(R.id.passEditText);
        passEditText.setEnabled(false);
        adminSpnr.setEnabled(false);


        // GETTING DATA FROM FIRESTORE
        new AsysncTask().execute(foodcourtsPath);


        // SPINNER
        fcNames.add("Select Food Court");
        adapterfc = new ArrayAdapter<String>(this, R.layout.spinner_item, fcNames);
        foodcourtSpnr.setAdapter(adapterfc);

        foodcourtSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                foodcourtSpnr.setSelection(position);

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }

                if(position > 0) {
                    passEditText.setEnabled(false);
                    passEditText.setText("");
                    adminSpnr.setEnabled(true);
                    adminNames.clear();
                    adminNames.add("Select Admin");
                    posfc = position-1;
                    fcid = foodCourtArr.get(position-1).get("fcid").toString();
                    adminPath = foodcourtsPath + "/" + fcid +"/AdminM";
                    new AsysncTask().execute(adminPath);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        adminNames.add("Select Admin");
        adapteradmin = new ArrayAdapter<String>(this, R.layout.spinner_item, adminNames);
        adminSpnr.setAdapter(adapteradmin);

        adminSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adminSpnr.setSelection(position);

                TextView tv = (TextView) view;
                if (position == 0) {
                    if(tv != null) {
                        tv.setTextColor(Color.GRAY);
                    }
//                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                if(position > 0) {
                    passEditText.setText("");
                    posadmin = position-1;
//                    adminid = adminArr.get(position-1).get("venid").toString();
                    passcode = adminArr.get(position-1).get("password").toString();
                    passEditText.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tableNames.add("Select Table");
        adaptertable = new ArrayAdapter<String>(this, R.layout.spinner_item, tableNames);
        tableSpnr.setAdapter(adaptertable);

        tableSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tableSpnr.setSelection(position);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                if(position > 0) {
                    postable = position-1;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






    } // OnCreate Done




    // Activate Button
    public void activate(View view) {

        FirebaseUser user = mAuth.getCurrentUser();
        signInAnonymously();


        HashMap basicDetails = new HashMap();
        basicDetails.put("adminId" , adminArr.get(posadmin).get("usrid").toString() );
        basicDetails.put("empid" , adminArr.get(posadmin).get("empid").toString() );
        basicDetails.put("displayName" , employeeArr.get(posadmin).get("displayName").toString()  );
        basicDetails.put("tabid" , tableDetails.get("tabid").toString()  );
        basicDetails.put("tableName" , tableDetails.get("name").toString()  );

        Intent home = new Intent(this, Home.class);
        home.putExtra("fcDetails" , foodCourtArr.get(posfc));
        home.putExtra("adminDetails" , adminArr.get(posadmin));
        home.putExtra("employeeDetails" , employeeArr.get(posadmin));
        home.putExtra("tableDetails" , tableDetails);
        home.putExtra("basicDetails" , basicDetails);

        startActivity(home);
        finish();
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
//                Log.i("signInAnonymously: SUCCESS ", "TRUE");
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
//                        Log.i("signInAnonymously:FAILURE", exception.toString());
                    }
        });
    }


    // Authenticate Button
    public void authenticate(View view) {

        progressDialog.setMessage("Authenticating Credentials");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

//        Log.i("Login" , "Clicked");
        String password = passEditText.getText().toString().trim();
        if(foodcourtSpnr.getSelectedItem() == "Select Food Court") {
            progressDialog.dismiss();
            Toast.makeText(this, "Select 'FoodCourt' ", Toast.LENGTH_LONG).show();
        }
        else if (adminSpnr.getSelectedItem() == "Select Admin") {
            progressDialog.dismiss();
            Toast.makeText(this, "Select 'Admin' ", Toast.LENGTH_LONG).show();
        }
        else if(password.equals("")) {
            progressDialog.dismiss();
            Toast.makeText(this, "Enter Password", Toast.LENGTH_LONG).show();
        }
        else {

            if(password.equals(passcode)) {

                tablePath = foodcourtsPath + "/" + fcid +"/TableM";
                new AsysncTask().execute(tablePath);
                progressDialog.dismiss();
//                Change UI to Table
                linearlayoutFoodcourt.animate().translationX(1000f).alpha(0.0f).setDuration(500);
                linearlayoutTable.animate().translationX(0f).alpha(1.0f).setDuration(500);
                statusTable.setTextColor(ResourcesCompat.getColor(getResources(), R.color.bizzorange, null));
                statusFoodcourt.setTextColor(getResources().getColor(R.color.grey));
                imageViewCircleFd.setImageResource(R.drawable.circlegrey);
                imageViewCircleTb.setImageResource(R.drawable.circleorange);

            }else {
                progressDialog.dismiss();
                Toast.makeText(this, "Invalid Login Credentials", Toast.LENGTH_LONG).show();
            }
        }

    }



    // Continue Button
    public void continuetable(View view) {

        progressDialog.setMessage("Checking Table Availability");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        if(tableSpnr.getSelectedItem() == "Select Table") {
            progressDialog.dismiss();
            Toast.makeText(this, "Select 'Table' ", Toast.LENGTH_LONG).show();
        }else {
            final String path = fcid + "/TableM/" + tableArr.get(postable).get("tabid").toString();
            db.document(path).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        HashMap check = (HashMap) task.getResult().getData();
//                        Log.i("CHECK TABLE STATUS" , check.get("status").toString());
                        if(!(Boolean) check.get("status")) {
                            tableDetails = tableArr.get(postable);
                            db.document(path).update("status", true);
                            progressDialog.dismiss();

                            // Change UI to Table
                            foodcourtTextView.setText(foodcourtSpnr.getSelectedItem().toString());
                            adminTextView.setText(adminSpnr.getSelectedItem().toString());
                            tableTextView.setText(tableSpnr.getSelectedItem().toString());

                            linearlayoutTable.animate().translationX(1000f).alpha(0.0f).setDuration(500);
                            linearlayoutActivate.animate().translationX(0f).alpha(1.0f).setDuration(500);
                            statusActivate.setTextColor(ResourcesCompat.getColor(getResources(), R.color.bizzorange, null));
//                            statusTable.setTextColor(Color.parseColor("@color/grey"));
                            statusTable.setTextColor(getResources().getColor(R.color.grey));
                            imageViewCircleTb.setImageResource(R.drawable.circlegrey);
                            imageViewCircleActivate.setImageResource(R.drawable.circleorange);
                        }
                    }
                    else {
                        Toast.makeText(Login.this, "ERROR LOGING IN.", Toast.LENGTH_SHORT).show();
//                        Log.i("CHECK TABLE STATUS" , "FAILED to check");
                    }
                }
            });
        }
    }



    // Set FoodCourts/Admin Names
    public ArrayList<String> setnames(ArrayList<String> names, ArrayList<HashMap> hashmapArr, Boolean label) {
        String name = names.get(0);
        names.clear();
        names.add(name);
        if(label) {
            for (int i = 0; i < hashmapArr.size(); i++) {
                names.add(hashmapArr.get(i).get("name").toString());
            }
        }else {
            for (int i = 0; i < hashmapArr.size(); i++) {
                names.add(hashmapArr.get(i).get("displayName").toString());
            }
        }
        return (names);
    }



    // Spinner Initializer
    public ArrayAdapter<String> initSpinner(ArrayList<String> list) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0;

//                if (position == 0) {
//                    return false;
//                } else {
//                    return true;
//                }
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextSize(24);
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;

    }



    // AsyncTask FireStore
    private class AsysncTask extends AsyncTask<String , Void, Void> {

        @Override
        protected Void doInBackground(final String... strings) {

            if(strings[0].contains("AdminM")) {

                firestore.collection(strings[0]).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        adminArr.clear();
                        employeeArr.clear();

                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            adminArr.add((HashMap) document.getData() );
                        }

                        new AsysncTask().execute("EmployeeM");
//                        Log.i("Admin Array" , adminArr.toString());
                    }
                });

            }else if (strings[0].contains("EmployeeM")) {

                db.document("Employee").collection("EmployeeM").whereEqualTo("status", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        employeeArr.clear();

                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            HashMap sample = (HashMap) document.getData();

                            for(int i=0; i<adminArr.size(); i++) {
                               if(sample.get("empid").toString().equals(adminArr.get(i).get("empid").toString())) {
                                   employeeArr.add((HashMap) sample);
                               }
                            }

                        }

                        // Initiliaze
                        Boolean label = false;
                        adminNames = setnames(adminNames, employeeArr, label );
//                        Log.i("Admin Names" , adminNames.toString());
                        adminSpnr.setAdapter(initSpinner(adminNames));
                        adapteradmin.notifyDataSetChanged();
                    }
                });

            } else if(strings[0].contains("TableM")) {

                firestore.collection(strings[0]).whereEqualTo("status", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        tableArr.clear();

                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            tableArr.add((HashMap) document.getData() );
                        }

                        //Initialize
                        Boolean label = true;
                        tableNames = setnames(tableNames, tableArr, label );
//                        Log.i("FCNAME" , tableNames.toString());
                        tableSpnr.setAdapter(initSpinner(tableNames));
                        adaptertable.notifyDataSetChanged();

                    }
                });

            } else {
                db.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(e != null) {
//                            Log.i("ERROR" , e.getMessage());
                            Toast.makeText(Login.this, e.toString() , Toast.LENGTH_LONG).show();
                        }else {
                            foodCourtArr.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                foodCourtArr.add((HashMap) document.getData());
                            }

                            //Initialize
                            Boolean label = true;
                            fcNames = setnames(fcNames, foodCourtArr, label );
//                            Log.i("FCNAME" , fcNames.toString());
                            foodcourtSpnr.setAdapter(initSpinner(fcNames));
                            adapterfc.notifyDataSetChanged();
                            adminSpnr.setAdapter(initSpinner(adminNames));
                            adapteradmin.notifyDataSetChanged();
                        }
                    }


                });
            }
            return null;
        }
    }




    // INTERNET CONNECTION
    private void checkInternetConnection() {

        ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){
            Toast.makeText(this, "Check INTERNET Connection", Toast.LENGTH_SHORT).show();
        }

    }



}
