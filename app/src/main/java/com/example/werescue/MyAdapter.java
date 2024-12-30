package com.example.werescue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<DataClass> dataList; // Current list of items displayed
    private List<DataClass> originalDataList; // Original unfiltered list
    private Context context;
    private DatabaseReference databaseReference;

    // Constructor for local data
    public MyAdapter(Context context, List<DataClass> pets) {
        this.context = context;
        this.dataList = pets != null ? pets : new ArrayList<>();
        this.originalDataList = new ArrayList<>(this.dataList);
    }

    // Constructor for Firebase data
    public MyAdapter(Context context, DatabaseReference databaseReference) {
        this.context = context;
        this.databaseReference = databaseReference;
        this.dataList = new ArrayList<>();
        this.originalDataList = new ArrayList<>();
        loadDataFromFirebase();
    }

    // Load data from Firebase
    private void loadDataFromFirebase() {
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DataClass data = snapshot.getValue(DataClass.class);
                        if (data != null) {
                            dataList.add(data);
                        }
                    }
                    originalDataList = new ArrayList<>(dataList); // Keep a copy of the original list
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DataClass currentItem = dataList.get(position);

        // Load image using Glide
        Glide.with(context)
                .load(currentItem.getImagePath())
                .placeholder(R.drawable.baseline_pets_24) // Placeholder image
                .error(R.drawable.baseline_pets_24) // Error image
                .into(holder.recyclerImage);

        // Set pet name and location
        holder.recyclerCaption.setText(currentItem.getPetName());
        holder.recyclerCaptionLocation.setText(currentItem.getLocation());

        // Set an OnClickListener to navigate to the PetDescriptionFragment
        holder.itemView.setOnClickListener(v -> {
            PetDescriptionFragment petDescriptionFragment = new PetDescriptionFragment();

            // Pass the selected pet data to the fragment
            Bundle bundle = new Bundle();
            bundle.putSerializable("petData", (Serializable) currentItem);
            petDescriptionFragment.setArguments(bundle);

            // Replace the current fragment with PetDescriptionFragment
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, petDescriptionFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    // Update the data list and notify the adapter
    public void updateData(List<DataClass> pets) {
        this.dataList = pets != null ? pets : new ArrayList<>();
        this.originalDataList = new ArrayList<>(this.dataList);
        notifyDataSetChanged();
    }

    // Filter the data list based on a search query
    public void filter(String text) {
        if (text == null || text.isEmpty()) {
            showAllItems();
            return;
        }

        List<DataClass> filteredList = new ArrayList<>();
        for (DataClass item : originalDataList) {
            if (item.getPetName() != null && item.getPetName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        this.dataList = filteredList;
        notifyDataSetChanged();
    }

    // Show all items (reset the filtered list)
    public void showAllItems() {
        this.dataList = new ArrayList<>(this.originalDataList);
        notifyDataSetChanged();
    }

    // Set a new data list
    public void setDataList(List<DataClass> dataList) {
        this.dataList = dataList != null ? dataList : new ArrayList<>();
        this.originalDataList = new ArrayList<>(this.dataList);
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerCaption;
        TextView recyclerCaptionLocation;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCaption = itemView.findViewById(R.id.recyclerCaptionName);
            recyclerCaptionLocation = itemView.findViewById(R.id.recyclerCaptionLocation);
        }
    }
}