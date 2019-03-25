package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ExpandableListAdapterOrdersList extends BaseExpandableListAdapter {

    Context context;
    private ArrayList <LinkedHashMap> vendorOrderList;
    private ArrayList<ArrayList<LinkedHashMap>> foodItemOrderList;
    LayoutInflater infalInflater;
    LayoutInflater infalInflaterheader;
    private StorageReference storageRefVenImage;
    StorageReference storageRefFoodItemImage;


    ExpandableListAdapterOrdersList (Context context, ArrayList <LinkedHashMap> vendorOrderList, ArrayList<ArrayList<LinkedHashMap>> foodItemOrderList) {

        this.context = context;
        this.vendorOrderList = vendorOrderList;
        this.foodItemOrderList = foodItemOrderList;
    }

    @Override
    public int getGroupCount() {
        return vendorOrderList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return foodItemOrderList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return vendorOrderList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return foodItemOrderList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        HashMap vendor = (HashMap) getGroup(groupPosition);
        if (convertView == null) {
            infalInflaterheader = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (infalInflaterheader != null) {
                convertView = infalInflaterheader.inflate(R.layout.order_list_header_card, parent, false);
            }
        }

        final ProgressBar progressBarVenImage = convertView.findViewById(R.id.progressBarVenImage);
        ImageView venImageOrdersList = convertView.findViewById(R.id.venImageOrdersList);
        TextView venNameOrdersList = convertView.findViewById(R.id.venNameOrdersList);
        TextView textViewVenOrderId = convertView.findViewById(R.id.textViewVenOrderId);
        TextView textViewOrderList1 = convertView.findViewById(R.id.textViewOrderList1);
        TextView textViewOrderList2 = convertView.findViewById(R.id.textViewOrderList2);
        TextView textViewOrderListStatus1 = convertView.findViewById(R.id.textViewOrderListStatus1);
        TextView textViewOrderListStatus2 = convertView.findViewById(R.id.textViewOrderListStatus2);
        TextView subTotalAmountOrdersList = convertView.findViewById(R.id.subTotalAmountOrdersList);

        String pic = vendor.get("pic").toString();
        storageRefVenImage = Home.storageRef.child(pic);
        Glide.with(venImageOrdersList.getContext()).using(new FirebaseImageLoader()).load(storageRefVenImage)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBarVenImage.setVisibility(View.GONE);
                        return false;
                    }
                }).into(venImageOrdersList);
        venNameOrdersList.setText(vendor.get("name").toString());
        textViewVenOrderId.setText(vendor.get("orderId").toString());
        subTotalAmountOrdersList.setText(Home.currencyFc + vendor.get("subTotalAmount").toString());

        switch (vendor.get("orderStatus").toString()) {

            case "OPEN":
                textViewOrderList1.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.transparent));
                textViewOrderList1.setTextColor(ContextCompat.getColor(context, R.color.bizzorange));
                textViewOrderList2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.transparent));
                textViewOrderList2.setTextColor(ContextCompat.getColor(context, R.color.bizzorange));
                textViewOrderListStatus1.setText("Order Accepted");
                textViewOrderListStatus2.setText("Ready to Pick");
                break;

            case "ACCEPTED":
                textViewOrderList1.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bizzorange));
                textViewOrderList1.setTextColor(ContextCompat.getColor(context, R.color.white));
                textViewOrderList2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.transparent));
                textViewOrderList2.setTextColor(ContextCompat.getColor(context, R.color.bizzorange));
                textViewOrderListStatus1.setText("Order Accepted");
                textViewOrderListStatus2.setText("Ready to Pick");
                break;

            case "CANCELLED":
                textViewOrderList1.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bizzorange));
                textViewOrderList1.setTextColor(ContextCompat.getColor(context, R.color.white));
                textViewOrderList2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.transparent));
                textViewOrderList2.setTextColor(ContextCompat.getColor(context, R.color.bizzorange));
                textViewOrderListStatus1.setText("Order Cancelled");
                textViewOrderListStatus2.setText("   ---");
                break;


            case "CLOSED":
                textViewOrderList1.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bizzorange));
                textViewOrderList1.setTextColor(ContextCompat.getColor(context, R.color.white));
                textViewOrderList2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.bizzorange));
                textViewOrderList2.setTextColor(ContextCompat.getColor(context, R.color.white));
                textViewOrderListStatus1.setText("Order Accepted");
                textViewOrderListStatus2.setText("Ready to Pick");
                break;
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView1, ViewGroup parent) {

        final HashMap foodItem = (HashMap) getChild(groupPosition, childPosition);

        if (convertView1 == null) {
            infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (infalInflater != null) {
                convertView1 = infalInflater.inflate(R.layout.order_list_fooditem_card, parent, false);
            }
        }
        final ProgressBar progressBarFoodItemImage = convertView1.findViewById(R.id.progressBarFoodItemImage);
        ImageView foodItemImageOrdersList = convertView1.findViewById(R.id.foodItemImageOrdersList);
        TextView foodItemNameOrdersList = convertView1.findViewById(R.id.foodItemNameOrdersList);
        TextView foodItemSdespOrdersList = convertView1.findViewById(R.id.foodItemSdespOrdersList);
        TextView foodItemQtyOrdersList = convertView1.findViewById(R.id.foodItemQtyOrdersList);

        String pic = foodItem.get("pic").toString();
        storageRefFoodItemImage = Home.storageRef.child(pic);
        Glide.with(foodItemImageOrdersList.getContext()).using(new FirebaseImageLoader()).load(storageRefFoodItemImage)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBarFoodItemImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(foodItemImageOrdersList);
        foodItemNameOrdersList.setText(foodItem.get("name").toString());
        foodItemSdespOrdersList.setText(foodItem.get("sdesp").toString());
        foodItemQtyOrdersList.setText(foodItem.get("quantity").toString());

        return  convertView1;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
