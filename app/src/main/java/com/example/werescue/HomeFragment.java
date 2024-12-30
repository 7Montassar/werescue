package com.example.werescue;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<DataClass> dataList;
    private MyAdapter adapter;
    private PetDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the user's profile image
        ImageView profileImage = view.findViewById(R.id.home_profile_image);
        Glide.with(this)
                .load(R.drawable.baseline_pets_24) // Default profile image
                .into(profileImage);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the dataList ArrayList
        dataList = new ArrayList<>();

        // Initialize the adapter
        adapter = new MyAdapter(getActivity(), dataList);
        recyclerView.setAdapter(adapter);

        // Initialize SQLite database helper
        dbHelper = new PetDatabaseHelper(getActivity());

        // Load data from SQLite database
        loadDataFromSQLite();

        // Add mock data to the dataList
        addMockData();

        // Notify the adapter of the combined data
        adapter.setDataList(dataList);
        adapter.notifyDataSetChanged();

        // Add the TextWatcher to the EditText
        EditText searchBar = view.findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    // If search bar is empty, display all items
                    adapter.showAllItems();
                } else {
                    // If search bar is not empty, filter the items
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });
    }

    private void addMockData() {
        // Add mock data directly to the dataList
        String buddyImagePath = "android.resource://" + getActivity().getPackageName() + "/" + R.drawable.buddy_image;
        String mittensImagePath = "android.resource://" + getActivity().getPackageName() + "/" + R.drawable.mittens_image;
        String charlieImagePath = "android.resource://" + getActivity().getPackageName() + "/" + R.drawable.charlie_image;
        String HansImagePath = "android.resource://" + getActivity().getPackageName() + "/" + R.drawable.hans_image;

        // Add mock data to the dataList
        dataList.add(new DataClass("101", "Buddy", "Friendly dog", "M", "Dog", "01/01/2020", "New York", "15", buddyImagePath, "mock_owner1@example.com"));
        dataList.add(new DataClass("102", "Mittens", "Playful cat", "F", "Cat", "15/05/2019", "Los Angeles", "5", mittensImagePath, "mock_owner2@example.com"));
        dataList.add(new DataClass("103", "Charlie", "Energetic rabbit", "M", "Rabbit", "10/10/2021", "Chicago", "2", charlieImagePath, "mock_owner3@example.com"));
        dataList.add(new DataClass("104", "Hans", "Friendly cat", "M", "Cat", "01/01/2020", "New York", "15", HansImagePath, "mock_owner4@example.com"));
        Log.d("HomeFragment", "Mock data added to dataList");
    }

    private void loadDataFromSQLite() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query the Pets table
        String query = "SELECT * FROM Pets";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Get data from the cursor
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                String species = cursor.getString(cursor.getColumnIndexOrThrow("species"));
                String birthday = cursor.getString(cursor.getColumnIndexOrThrow("birthday"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                int weight = cursor.getInt(cursor.getColumnIndexOrThrow("weight"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

                // Create a DataClass object and add it to the dataList
                DataClass dataClass = new DataClass(id, name, description, gender, species, birthday, location, String.valueOf(weight), imagePath, email);
                dataList.add(dataClass);
            } while (cursor.moveToNext());
        }

        cursor.close();

        Log.d("HomeFragment", "Data loaded from SQLite: " + dataList.size() + " items");
    }
}