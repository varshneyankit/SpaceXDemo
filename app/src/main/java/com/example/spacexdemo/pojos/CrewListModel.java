package com.example.spacexdemo.pojos;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "crew_data")
public class CrewListModel {

    @SerializedName("id")
    @PrimaryKey
    @NonNull
    private final String id;

    @SerializedName("name")
    private final String name;

    @SerializedName("agency")
    private final String agency;
    @SerializedName("wikipedia")
    private final String wikipediaUrl;
    @SerializedName("status")
    private final String status;
    @SerializedName("image")
    private String imageUrl;

    public CrewListModel(@NonNull String id, String name, String agency, String imageUrl, String wikipediaUrl, String status) {
        this.id = id;
        this.name = name;
        this.agency = agency;
        this.imageUrl = imageUrl;
        this.wikipediaUrl = wikipediaUrl;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAgency() {
        return agency;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getWikipediaUrl() {
        return wikipediaUrl;
    }

    public String getStatus() {
        return status;
    }
}
