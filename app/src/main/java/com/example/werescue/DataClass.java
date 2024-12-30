package com.example.werescue;


import android.graphics.Bitmap;

import java.io.Serializable;

public class DataClass {
    private String id;
    private String petName;
    private String description;
    private String gender;
    private String species;
    private String birthday;
    private String location;
    private String weight;
    private String imagePath;
    private String email;

    // Default constructor required for calls to DataSnapshot.getValue(DataClass.class)
    public DataClass() {
    }

    public DataClass(String id, String petName, String description, String gender, String species, String birthday, String location, String weight, String imagePath, String email) {
        this.id = id;
        this.petName = petName;
        this.description = description;
        this.gender = gender;
        this.species = species;
        this.birthday = birthday;
        this.location = location;
        this.weight = weight;
        this.imagePath = imagePath;
        this.email = email;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}