package com.pramod.apartmentrental.Renter.my_listings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pramod.apartmentrental.R;

import java.util.ArrayList;
import java.util.List;


public class Renter_my_listings extends Fragment {

    ImageView listImage;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mListingAdapter;
    private RecyclerView.LayoutManager mListingLayoutManager;
    private String currentUserID;

    public Renter_my_listings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_renter_my_listings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        mRecyclerView = getView().findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mListingLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mListingLayoutManager);

        mListingAdapter = new Renter_listing_Adapter(getDataSetListings(), getActivity());
        mRecyclerView.setAdapter(mListingAdapter);

        //Apartment Listing ID
        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID).child("listings");


        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot showlist : dataSnapshot.getChildren()){

                        GetApartmentListingInfo(showlist.getKey());
                    }

                }
                else
                {
                    Toast.makeText(getContext(), "It's Empty! Add some Apartments!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mListingAdapter.notifyDataSetChanged();

    }

    private ArrayList<RenterApartmentObject> resultListings= new ArrayList<RenterApartmentObject>();

    private void GetApartmentListingInfo(final String key) {


        DatabaseReference listDb = FirebaseDatabase.getInstance().getReference().child("listings").child(key);

        listDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {


                    String listingname = "";
                    String listingimageurl = "";
                    String listingdescription = "";
                    String listingprice = "";


                    if (dataSnapshot.child("listing_name").getValue() != null) {
                        listingname = dataSnapshot.child("listing_name").getValue().toString();
                    }

                    if (dataSnapshot.child("listing_description").getValue() != null) {
                        listingdescription = dataSnapshot.child("listing_description").getValue().toString();
                    }

                    if (dataSnapshot.child("listing_price").getValue() != null) {
                        listingprice = dataSnapshot.child("listing_price").getValue().toString();
                    }

                    if (!dataSnapshot.child("listing_image").getValue().equals("default")) {
                        listingimageurl = dataSnapshot.child("listing_image").getValue().toString();
                    }
                    else
                    {
                        listingimageurl = "default";
                    }

                    RenterApartmentObject obj = new RenterApartmentObject(key, listingname, listingdescription, listingprice, listingimageurl);
                    resultListings.add(obj);
                    mListingAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private List<RenterApartmentObject> getDataSetListings() {
        return resultListings;
    }
}
