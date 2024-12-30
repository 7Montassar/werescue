package com.example.werescue;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DescriptionOwnerFragment extends Fragment {

    private ImageView backButton;
    private ImageView petImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description_owner, container, false);

        // Retrieve the pet object from the arguments
        Bundle args = getArguments();
        DataClass pet = null;
        if (args != null) {
            pet = (DataClass) args.getSerializable("selectedPet");
        }

        // Initialize UI elements
        backButton = view.findViewById(R.id.back_button);
        petImage = view.findViewById(R.id.petImage);
        TextView petName = view.findViewById(R.id.petName);
        TextView petDescription = view.findViewById(R.id.petDescription);
        TextView petGender = view.findViewById(R.id.petGender);
        TextView petAge = view.findViewById(R.id.petAge);
        TextView petLocation = view.findViewById(R.id.petLocation);
        TextView petWeight = view.findViewById(R.id.petWeight);
        TextView ownerEmail = view.findViewById(R.id.owner_email);
        TextView petSpecies = view.findViewById(R.id.petSpecies);

        // Populate the UI with pet data
        if (pet != null) {
            petName.setText(pet.getPetName());
            petDescription.setText(pet.getDescription());

            // Set gender
            String gender = pet.getGender();
            if ("F".equals(gender)) {
                petGender.setText("Female");
            } else if ("M".equals(gender)) {
                petGender.setText("Male");
            } else {
                petGender.setText("Unknown");
            }

            // Parse and calculate age
            String birthdayStr = pet.getBirthday();
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
            petLocation.setText(pet.getLocation() != null ? pet.getLocation() : "Unknown");
            petWeight.setText(pet.getWeight() + " Kg");
            petSpecies.setText(pet.getSpecies() != null ? pet.getSpecies() : "Unknown");

            // Retrieve owner's email from SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            String ownerEmailStr = sharedPreferences.getString("email", "Unknown");
            ownerEmail.setText(ownerEmailStr);

            // Set pet image
            String imagePath = pet.getImagePath();
            if (imagePath != null) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    petImage.setImageBitmap(bitmap);
                } else {
                    petImage.setImageResource(R.drawable.baseline_pets_24); // Set a default image if the file doesn't exist
                }
            } else {
                petImage.setImageResource(R.drawable.baseline_pets_24); // Set a default image if no image path is provided
            }
        }

        // Handle back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack(); // Navigate back to the previous fragment
            }
        });

        return view;
    }
}