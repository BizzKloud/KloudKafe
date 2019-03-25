package com.example.smahadik.kloudkafe;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewAdapterVendorList extends RecyclerView.Adapter<RecyclerViewAdapterVendorList.MyViewHolder> {

    private Context mContext;
    StorageReference advStorageRef;
    View view;
    LayoutInflater layoutInflater;
    Boolean changeVendor;

    public RecyclerViewAdapterVendorList(Context mContext, Boolean changeVendor) {
        this.mContext = mContext;
        this.changeVendor = changeVendor;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        layoutInflater = LayoutInflater.from(mContext);
        if(changeVendor) {
            view = layoutInflater.inflate(R.layout.vendor_changevendor_cardview, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.vendor_cardview, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.vendorTitle.setText(Home.vendorArr.get(position).get("name").toString());
        holder.vendorRatingBar.setRating(Integer.parseInt(Home.vendorArr.get(position).get("rating").toString()));
        advStorageRef = Home.storageRef.child(Home.vendorArr.get(position).get("pic").toString());
        Glide.with(holder.vendorLogo.getContext())
                .using(new FirebaseImageLoader())
                .load(advStorageRef)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.vendorLogo);


        // Setting OnClick Listener
        holder.vendorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(VendorItemListFragment.changeVendorPopUp != null) {
                    VendorItemListFragment.changeVendorPopUp.dismiss();
                }

                Home.vendorPosition = position;
                Home.lastVendorPosition = position;
                Home.ft = Home.fragmentManager.beginTransaction();
                Fragment fragment  =  new VendorItemListFragment();
                Home.ft.replace(R.id.rootLayout,fragment , fragment.getClass().getName());
                Home.ft.addToBackStack(null);
                Home.ft.commit();
                Home.home.setAdvCounter();
            }
        });



    }



    @Override
    public int getItemCount() {
        return Home.vendorArr.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView vendorTitle;
        ImageView vendorLogo;
        RatingBar vendorRatingBar;
        ProgressBar progressBar;
        CardView vendorCard;

        public MyViewHolder(View itemView) {
            super(itemView);

            vendorTitle = itemView.findViewById(R.id.vendorTitle);
            vendorLogo = itemView.findViewById(R.id.vendorLogo);
            vendorRatingBar = itemView.findViewById(R.id.vendorRatingBar);
            progressBar = itemView.findViewById(R.id.progressBar);
            vendorCard = itemView.findViewById(R.id.vendorCard);

         }
    }


}
