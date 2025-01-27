package com.example.werescue;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PetsFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    SharedPreferences sharedPreferences;
    String loggedInUserEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pets, container, false);

        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        loggedInUserEmail = sharedPreferences.getString("email", "");

        // Check for READ_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        RecyclerView recyclerView = view.findViewById(R.id.petsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        PetDatabaseHelper dbHelper = new PetDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query the database
        String[] projection = {
                "id",
                "name",
                "description",
                "gender",
                "species",
                "birthday",
                "location",
                "weight",
                "imagePath", // Use imagePath instead of imageBitmap
                "email"
        };

        String selection = "email = ?";
        String[] selectionArgs = {loggedInUserEmail};

        Cursor cursor = db.query(
                "Pets",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<DataClass> petList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            String species = cursor.getString(cursor.getColumnIndexOrThrow("species"));
            String birthday = cursor.getString(cursor.getColumnIndexOrThrow("birthday"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            String weight = cursor.getString(cursor.getColumnIndexOrThrow("weight"));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"));

            // Load the image from the file path
            Bitmap imageBitmap = null;
            if (imagePath != null) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    imageBitmap = BitmapFactory.decodeFile(imagePath);
                }
            }

            petList.add(new DataClass(id, name, description, gender, species, birthday, location, weight, imagePath, imageBitmap.toString()));
        }
        cursor.close();

        // Set up the RecyclerView adapter
        PetAdapter adapter = new PetAdapter(getActivity(), petList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                } else {
                    // Permission denied
                }
                return;
            }
        }
    }
}