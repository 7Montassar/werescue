package com.example.werescue;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PetDescriptionFragment extends Fragment {

    private ImageView petImage;
    private TextView petGender;
    private TextView petWeight;
    private TextView petAge;
    private TextView petDescription;
    private TextView petName;
    private TextView petLocation;
    private TextView petSpecies;
    private TextView ownerEmail;
    private ImageView backButton;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet_description, container, false);

        // Initialize the views
        petImage = view.findViewById(R.id.petImage);
        petGender = view.findViewById(R.id.petGender);
        petWeight = view.findViewById(R.id.petWeight);
        petAge = view.findViewById(R.id.petAge);
        petDescription = view.findViewById(R.id.petDescription);
        petName = view.findViewById(R.id.petName);
        petLocation = view.findViewById(R.id.petLocation);
        petSpecies = view.findViewById(R.id.petSpecies);
        ownerEmail = view.findViewById(R.id.owner_email);
        backButton = view.findViewById(R.id.back_button);

        // Get the pet data from the Bundle
        Bundle args = getArguments();
        if (args != null) {
            DataClass petData = (DataClass) args.getSerializable("petData");

            // Check if petData is not null before using it
            if (petData != null) {
                populatePetDetails(petData);
            } else {
                // If petData is null, display a message
                Toast.makeText(getContext(), "No pet data found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If arguments are null, display a message
            Toast.makeText(getContext(), "No arguments found", Toast.LENGTH_SHORT).show();
        }

        // Handle back button click
        backButton.setOnClickListener(v -> {
            // Navigate back to the previous fragment
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void populatePetDetails(@NonNull DataClass petData) {
        // Load the pet image using Glide
        String imageUrl = petData.getImagePath();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.baseline_pets_24) // Placeholder image
                    .error(R.drawable.baseline_pets_24) // Error image
                    .into(petImage);
        } else {
            petImage.setImageResource(R.drawable.baseline_pets_24); // Set a default image if no URL is provided
        }

        // Set gender
        String gender = petData.getGender();
        if ("F".equals(gender)) {
            petGender.setText("Female");
        } else if ("M".equals(gender)) {
            petGender.setText("Male");
        } else {
            petGender.setText("Unknown");
        }

        // Set weight
        String weight = petData.getWeight();
        petWeight.setText(weight != null ? weight + " Kg" : "Unknown");

        // Parse and calculate age
        String birthdayStr = petData.getBirthday();
        if (birthdayStr != null && !birthdayStr.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate birthday = LocalDate.parse(birthdayStr, formatter);
                Period period = Period.between(birthday, LocalDate.now());
                int age = period.getYears();
                petAge.setText(age + " years");
            } catch (DateTimeParseException e) {
                petAge.setText("Invalid date");
            }
        } else {
            petAge.setText("Unknown");
        }

        // Set other pet details
        petDescription.setText(petData.getDescription() != null ? petData.getDescription() : "No description available");
        petName.setText(petData.getPetName() != null ? petData.getPetName() : "Unknown");
        petLocation.setText(petData.getLocation() != null ? petData.getLocation() : "Unknown");
        petSpecies.setText(petData.getSpecies() != null ? petData.getSpecies() : "Unknown");
        ownerEmail.setText(petData.getEmail() != null ? petData.getEmail() : "Unknown");
    }
}