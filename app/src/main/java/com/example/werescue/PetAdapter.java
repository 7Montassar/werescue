package com.example.werescue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private Context context;
    private List<DataClass> petList;

    public PetAdapter(Context context, List<DataClass> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        DataClass pet = petList.get(position);

        // Set pet name and location
        holder.recyclerCaption.setText(pet.getPetName() != null ? pet.getPetName() : "Unknown");
        holder.recyclerCaptionLocation.setText(pet.getLocation() != null ? pet.getLocation() : "Unknown");

        // Load the image from the file path
        String imagePath = pet.getImagePath();
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                holder.recyclerImage.setImageBitmap(bitmap);
            } else {
                holder.recyclerImage.setImageResource(R.drawable.baseline_pets_24); // Set a default image if the file doesn't exist
            }
        } else {
            holder.recyclerImage.setImageResource(R.drawable.baseline_pets_24); // Set a default image if no image path is provided
        }

        // Set an OnClickListener to navigate to the DescriptionOwnerFragment
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedPet", (Serializable) pet);
            DescriptionOwnerFragment fragment = new DescriptionOwnerFragment();
            fragment.setArguments(bundle);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return petList != null ? petList.size() : 0;
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerCaption;
        TextView recyclerCaptionLocation;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCaption = itemView.findViewById(R.id.recyclerCaptionName);
            recyclerCaptionLocation = itemView.findViewById(R.id.recyclerCaptionLocation);
        }
    }
}