package com.example.werescue;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UploadFragment extends Fragment {

    private AppCompatButton uploadButton;
    private ImageView uploadImage;
    EditText petName, petDescription, speciesET, birthdayET, locationET, weightET;
    private Uri imageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        uploadImage = view.findViewById(R.id.uploadImage);
        uploadButton = view.findViewById(R.id.uploadButton);
        petName = view.findViewById(R.id.nameET);
        petDescription = view.findViewById(R.id.descriptionET);
        RadioButton maleRadioButton = view.findViewById(R.id.maleRadioButton);
        RadioButton femaleRadioButton = view.findViewById(R.id.femaleRadioButton);

        speciesET = view.findViewById(R.id.speciesET);
        birthdayET = view.findViewById(R.id.birthdayET);
        locationET = view.findViewById(R.id.locationET);
        weightET = view.findViewById(R.id.weightET);

        maleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maleRadioButton.isChecked()) {
                    femaleRadioButton.setChecked(false);
                }
            }
        });

        femaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (femaleRadioButton.isChecked()) {
                    maleRadioButton.setChecked(false);
                }
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            uploadImage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if (imageUri != null){
            String name = petName.getText().toString().trim();
            String description = petDescription.getText().toString().trim();
            String gender = getGender();
            String species = speciesET.getText().toString().trim();
            String birthdayStr = birthdayET.getText().toString().trim();
            String location = locationET.getText().toString().trim();
            String weightStr = weightET.getText().toString().trim();

            // Check if all fields are filled
            if (name.isEmpty() || description.isEmpty() || gender == null || species.isEmpty() || birthdayStr.isEmpty() || location.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(getActivity(), "All fields should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if name is alphabetic
            if (!name.matches("[a-zA-Z]+")) {
                Toast.makeText(getActivity(), "Name should be alphabetic", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if species is a number of 8 characters
            if (!species.matches("\\d{8}")) {
                Toast.makeText(getActivity(), "Phone Number should be 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if birthday is in the format dd/mm/yyyy
            if (!birthdayStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                Toast.makeText(getActivity(), "Birthday should be in the format dd/mm/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the date values are valid
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            try {
                Date birthday = sdf.parse(birthdayStr);
                Date today = new Date();
                if (birthday.after(today)) {
                    Toast.makeText(getActivity(), "Birthday should not be after today's date", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                Toast.makeText(getActivity(), "Invalid date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if location is alphanumeric
            if (!location.matches("[a-zA-Z0-9 ]+")) {
                Toast.makeText(getActivity(), "Location should be alphanumeric", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if weight is a number of 1 or 2 digits
            if (!weightStr.matches("\\d{1,2}")) {
                Toast.makeText(getActivity(), "Weight should be a number of 1 or 2 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if description is alphanumeric and can contain spaces
            if (!description.matches("[a-zA-Z0-9 ]+")) {
                Toast.makeText(getActivity(), "Description should be alphanumeric and can contain spaces", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadToFirebase(imageUri);
        } else  {
            Toast.makeText(getActivity(), "Please select image", Toast.LENGTH_SHORT).show();
        }
    }
});

        return view;
    }

    private String getGender() {
    RadioButton maleRadioButton = getView().findViewById(R.id.maleRadioButton);
    RadioButton femaleRadioButton = getView().findViewById(R.id.femaleRadioButton);

    if (maleRadioButton.isChecked()) {
        return "M";
    } else if (femaleRadioButton.isChecked()) {
        return "F";
    } else {
        return null;
    }
}
    private void uploadToFirebase(Uri uri) {
        try {
            // Convert the image to a Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

            // Save the image to internal storage
            String fileName = System.currentTimeMillis() + ".png";
            String filePath = saveImageToInternalStorage(bitmap, fileName);

            if (filePath == null) {
                Toast.makeText(getActivity(), "Failed to save image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the values from the EditText fields
            String name = petName.getText().toString().trim();
            String description = petDescription.getText().toString().trim();
            String gender = getGender();
            String species = speciesET.getText().toString().trim();
            String birthdayStr = birthdayET.getText().toString().trim();
            String location = locationET.getText().toString().trim();
            String weightStr = weightET.getText().toString().trim();

            int weight;
            try {
                weight = Integer.parseInt(weightStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid weight value", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the owner's name and email from shared preferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            String ownerName = sharedPreferences.getString("name", "");
            String ownerEmail = sharedPreferences.getString("email", "");

            // Generate a unique key for the pet
            String key = String.valueOf(System.currentTimeMillis());

            // Save the pet to the local SQLite database
            insertIntoDatabase(key, name, description, gender, species, birthdayStr, location, weight, filePath, ownerEmail);

            // Show a success message for SQLite insertion
            Toast.makeText(getActivity(), "Pet successfully saved locally", Toast.LENGTH_SHORT).show();

            // Save the pet to Firebase
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images").child(key);
            DataClass petData = new DataClass(key, name, description, gender, species, birthdayStr, location, String.valueOf(weight), filePath, ownerEmail);

            // Log the data being uploaded to Firebase
            Log.d("Firebase Upload", "Uploading data: " + petData.toString());

            databaseReference.setValue(petData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Show a success message for Firebase upload
                    Toast.makeText(getActivity(), "Pet successfully uploaded to Firebase", Toast.LENGTH_SHORT).show();

                    // Navigate to the HomeFragment after successful upload
                    ((MainActivity) getActivity()).replaceFragment(new HomeFragment());
                } else {
                    // Log the error and show a failure message
                    Log.e("Firebase Upload", "Failed to upload to Firebase: " + task.getException());
                    Toast.makeText(getActivity(), "Failed to upload to Firebase", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Log the failure and show a failure message
                Log.e("Firebase Upload", "Error uploading to Firebase", e);
                Toast.makeText(getActivity(), "Error uploading to Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            Log.e("Upload Error", "Error processing image", e);
            Toast.makeText(getActivity(), "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        Context context = getActivity();
        File directory = context.getFilesDir(); // Internal storage directory
        File file = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            Log.e("Save Image", "Error saving image", e);
            return null;
        }

        return file.getAbsolutePath(); // Return the file path
    }

    private void insertIntoDatabase(String id, String name, String description, String gender, String species, String birthdayStr, String location, int weight, String localFilePath, String ownerEmail) {
        // Get a writable database
        PetDatabaseHelper dbHelper = new PetDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the id already exists in the Pets table
        String query = "SELECT id FROM Pets WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id});
        if (cursor.moveToFirst()) {
            Log.e("SQLite Error", "Failed to insert id: " + id + ". Id already exists.");
            cursor.close();
            return;
        }
        cursor.close();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("description", description);
        values.put("gender", gender);
        values.put("species", species);
        values.put("birthday", birthdayStr);
        values.put("location", location);
        values.put("weight", weight);
        values.put("imagePath", localFilePath); // Store the file path
        values.put("email", ownerEmail); // Add the email to the ContentValues

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert("Pets", null, values);
        if (newRowId == -1) {
            Log.e("SQLite Error", "Failed to insert data");
        } else {
            Log.i("SQLite Info", "Inserted Data: " + values.toString());
        }
    }



private String getFileExtension(Uri fileUri){
    ContentResolver contentResolver = getActivity().getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();
    return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
}
}