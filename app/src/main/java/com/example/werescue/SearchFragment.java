package com.example.werescue;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {

    private TextView petName;
    private TextView petAge;
    private RadioButton petGenderMale;
    private RadioButton petGenderFemale;
    private Button buttonSearch;

    private PetDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize SQLite database helper
        dbHelper = new PetDatabaseHelper(getActivity());

        initializeViews(view);
        setupSearchButton();

        return view;
    }

    private void initializeViews(View view) {
        petName = view.findViewById(R.id.nameET);
        petAge = view.findViewById(R.id.ageET);
        petGenderMale = view.findViewById(R.id.maleRadioButton);
        petGenderFemale = view.findViewById(R.id.femaleRadioButton);
        buttonSearch = view.findViewById(R.id.button_search);
    }

    private void setupSearchButton() {
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String name = petName.getText().toString().trim();
        String age = petAge.getText().toString().trim();
        boolean isGenderSelected = petGenderMale.isChecked() || petGenderFemale.isChecked();

        if (name.isEmpty() && age.isEmpty() && !isGenderSelected) {
            Toast.makeText(getContext(), "Please fill at least one field", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = petGenderMale.isChecked() ? "M" : petGenderFemale.isChecked() ? "F" : "";

        // Perform the search in SQLite
        List<DataClass> pets = searchInSQLite(name, age, gender);

        if (pets.isEmpty()) {
            Toast.makeText(getContext(), "There are no matches", Toast.LENGTH_SHORT).show();
        } else {
            // Pass the search results to the SearchFilter fragment
            Bundle bundle = new Bundle();
            bundle.putSerializable("pets", (Serializable) pets);
            SearchFilter searchFilter = new SearchFilter();
            searchFilter.setArguments(bundle);

            // Replace the current fragment with the SearchFilter fragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, searchFilter)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private List<DataClass> searchInSQLite(String name, String age, String gender) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<DataClass> pets = new ArrayList<>();

        // Build the query dynamically based on the search criteria
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Pets WHERE 1=1");
        if (!name.isEmpty()) {
            queryBuilder.append(" AND name LIKE '%").append(name).append("%'");
        }
        if (!age.isEmpty()) {
            queryBuilder.append(" AND birthday LIKE '%").append(age).append("%'");
        }
        if (!gender.isEmpty()) {
            queryBuilder.append(" AND gender = '").append(gender).append("'");
        }

        Cursor cursor = db.rawQuery(queryBuilder.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                // Get data from the cursor
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String petName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String petGender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                String species = cursor.getString(cursor.getColumnIndexOrThrow("species"));
                String birthday = cursor.getString(cursor.getColumnIndexOrThrow("birthday"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                int weight = cursor.getInt(cursor.getColumnIndexOrThrow("weight"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

                // Create a DataClass object and add it to the list
                DataClass dataClass = new DataClass(id, petName, description, petGender, species, birthday, location, String.valueOf(weight), imagePath, email);
                pets.add(dataClass);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // Add mock data to the search results
        addMockData(pets, name, age, gender);

        return pets;
    }

    private void addMockData(List<DataClass> pets, String name, String age, String gender) {
        // Mock data
        List<DataClass> mockData = new ArrayList<>();
        // Add mock data directly to the dataList
        String buddyImagePath = "android.resource://" + getActivity().getPackageName() + "/" + R.drawable.buddy_image;
        String mittensImagePath = "android.resource://" + getActivity().getPackageName() + "/" + R.drawable.mittens_image;
        String charlieImagePath = "android.resource://" + getActivity().getPackageName() + "/" + R.drawable.charlie_image;
        String HansImagePath = "android.resource://" + getActivity().getPackageName() + "/" + R.drawable.hans_image;


        // Add mock data to the dataList
        mockData.add(new DataClass("101", "Buddy", "Friendly dog", "M", "Dog", "01/01/2020", "New York", "15", buddyImagePath, "mock_owner1@example.com"));
        mockData.add(new DataClass("102", "Mittens", "Playful cat", "F", "Cat", "15/05/2019", "Los Angeles", "5", mittensImagePath, "mock_owner2@example.com"));
        mockData.add(new DataClass("103", "Charlie", "Energetic rabbit", "M", "Rabbit", "10/10/2021", "Chicago", "2", charlieImagePath, "mock_owner3@example.com"));
        mockData.add(new DataClass("104", "Hans", "Friendly cat", "M", "Cat", "01/01/2020", "New York", "15", HansImagePath, "mock_owner4@example.com"));


        // Filter mock data based on the search criteria
        for (DataClass mock : mockData) {
            boolean matchesName = name.isEmpty() || mock.getPetName().toLowerCase().contains(name.toLowerCase());
            boolean matchesAge = age.isEmpty() || mock.getBirthday().contains(age);
            boolean matchesGender = gender.isEmpty() || mock.getGender().equalsIgnoreCase(gender);

            if (matchesName && matchesAge && matchesGender) {
                pets.add(mock);
            }
        }
    }
}