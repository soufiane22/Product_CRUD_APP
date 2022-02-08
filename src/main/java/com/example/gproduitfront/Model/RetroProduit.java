package com.example.gproduitfront.Model;


import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetroProduit {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("pu")
    @Expose
    private Double pu;

    @SerializedName("idCat")
    @Expose
    private String idCat;

    public RetroProduit(String label, Double pu, String id_cat) {
        this.label = label;
        this.pu = pu;
        this.idCat = id_cat;
    }

    public RetroProduit(String label, Double pu) {

        this.label = label;
        this.pu = pu;
    }

    public RetroProduit() {

    }

    public String getId_cat() {
        return idCat;
    }

    public void setId_cat(String id_cat) {
        this.idCat = id_cat;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String id) {
        this.label = label;
    }


    public Double getPu() {
        return this.pu;
    }

    public void setPu(String id) {
        this.pu = pu;
    }

    @Override
    public String toString() {
        return "RetroProduit{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", pu=" + pu +
                '}';
    }
}
