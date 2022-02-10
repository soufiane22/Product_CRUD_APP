package com.example.gproduitfront.Network;

import com.example.gproduitfront.Model.Categorie;
import com.example.gproduitfront.Model.RetroProduit;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GetDataService {
    @GET("produit/getall")
    Call<List<RetroProduit>> getAllProduits() ;

    @GET("produit/list/10")
    Call<List<RetroProduit>> getlistProduits();

    @POST("produit/save")
    Call<RetroProduit> create_produit(@Body RetroProduit produit);

    @PUT("produit/update/{id}")
    Call<RetroProduit> updateProduct(@Path("id") String id ,@Body RetroProduit produit);

    @DELETE("produit/delete/{id}")
    Call<JSONObject> deleteProduct(@Path("id") String id);

    @GET("produit/getlist/{id_cat}")
    Call<List<RetroProduit>> getListProduits(@Path("id_cat") String cat) ;


    @Headers({"content-type: application/json"})
    @GET("category/getall")
    Call<List<Categorie>> getAllCategories();

//@Field("id") int id
}
