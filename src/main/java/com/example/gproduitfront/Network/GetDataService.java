package com.example.gproduitfront.Network;

import com.example.gproduitfront.Model.Categorie;
import com.example.gproduitfront.Model.RetroProduit;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetDataService {
    @GET("produit/getall")
    Call<List<RetroProduit>> getAllProduits() ;

    @GET("produit/list/10")
    Call<List<RetroProduit>> getlistProduits();

    @POST("produit/save")
    Call<RetroProduit> create_produit(@Body RetroProduit produit);



    @Headers({"content-type: application/json"})
    @GET("category/getall")
    Call<List<Categorie>> getAllCategories();

//@Field("id") int id
}
