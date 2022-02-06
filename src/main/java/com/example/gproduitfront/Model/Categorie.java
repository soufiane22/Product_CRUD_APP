package com.example.gproduitfront.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Categorie {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("designation")
    @Expose
    private String designation;

    public Categorie(String id, String designation) {
        this.id = id;
        this.designation = designation;
    }

    public String getId() {
        return id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
