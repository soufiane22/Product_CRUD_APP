package com.example.gproduitfront;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gproduitfront.Model.Categorie;
import com.example.gproduitfront.Model.ProduitAdapter;
import com.example.gproduitfront.Model.RetroProduit;
import com.example.gproduitfront.Network.CustomAdapter;
import com.example.gproduitfront.Network.GetDataService;
import com.example.gproduitfront.Network.RetrofitClientInstance;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ProduitAdapter adapter;
    private RecyclerView recyclerView;
    ProgressDialog progressDoalog;

    RecyclerView recycler_view;
    Button btn ;
    Spinner myspinner ;
    List<RetroProduit> produitList = new ArrayList<>();
    List<Categorie> categoriesList = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>() ;
    public static String selecteCategorie ="test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        setContentView(R.layout.activity_main);
        recycler_view = findViewById(R.id.recycler_view);
         myspinner = (Spinner) findViewById(R.id.spinner);

        ArrayList<String> values = new ArrayList<>();
        values.add(0,"TV");
        values.add(1,"Tele");
        values.add(2,"pc");
        values.add(3,"Electroménagé");

        getAllCategories();

        myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selecteCategorie = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this,selecteCategorie, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
       //View v = (View) findViewById(R.layout.activity_main2) ;

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,MainActivity2.class);
                i.putExtra("category" , selecteCategorie);
               // startActivity(i);
                startActivityForResult(i, 0);
            }
        });



        getAllProduits();




    }

    private void getAllCategories() {

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Categorie>> call = service.getAllCategories();

        call.enqueue(new Callback<List<Categorie>>() {
            @Override
            public void onResponse(Call<List<Categorie>> call, Response<List<Categorie>> response)   {
                progressDoalog.dismiss();
                categoriesList = response.body();

                for(Categorie c:categoriesList){
                    categories.add(categoriesList.indexOf(c),c.getDesignation());

                }
                for(Object c:categories) {
                    System.out.println("id_cat====> :\n" + c.toString());


                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this , android.R.layout.simple_spinner_dropdown_item, categories);
                myspinner.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                System.out.println("adapter is here"+categories.size());
                //System.out.println("response.body =======>"+response.body().toString());
                if (!response.isSuccessful()){
                    try {
                        Toast.makeText(MainActivity.this, "Server returned error : "+response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Server returned error : unknown error ", Toast.LENGTH_LONG).show();
                    }
                }
                //response.body();

            }

            @Override
            public void onFailure(Call<List<Categorie>> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(getApplicationContext(), call.toString(), Toast.LENGTH_LONG).show();
                System.out.println("onFailure=======>"+t.getMessage());
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getAllProduits() {
        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<RetroProduit>> call = service.getAllProduits();

        call.enqueue(new Callback<List<RetroProduit>>() {
            @Override
            public void onResponse(Call<List<RetroProduit>> call, Response<List<RetroProduit>> response) {
                progressDoalog.dismiss();
                produitList = response.body();
                generateDataList(response.body());
                setRecycleview();

                //System.out.println("response.body =======>"+response.body().toString());
                if (!response.isSuccessful()){
                    try {
                        Toast.makeText(MainActivity.this, "Server returned error : "+response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Server returned error : unknown error ", Toast.LENGTH_LONG).show();
                    }
                }
                //response.body();

            }

            @Override
            public void onFailure(Call<List<RetroProduit>> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(getApplicationContext(), call.toString(), Toast.LENGTH_LONG).show();
                System.out.println("onFailure=======>"+t.getMessage());
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void setRecycleview() {
       // recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProduitAdapter(this ,produitList );
        recycler_view.setAdapter(adapter);
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList(List<RetroProduit> produitList) {
        //System.out.println("produit list =======>"+produitList);
        List<RetroProduit> listProduits = produitList;
        for (RetroProduit p : listProduits){
            String content ="";
            String libelle = p.getLabel();
            Double pu = p.getPu();
            content += "libellé :"+libelle+"\n";
            content +=  "prix unitaire :"+pu+"\n\n";
             System.out.println("produit :"+content);
           //  textReselt.append(content);
        }
       // recyclerView = findViewById(R.id.customRecyclerView);
     //   adapter = new CustomAdapter(this, produitList);
        //ListAdapter costumAdapter = new ListAdapter(this, R.layout.itemlistrow, List<yourItem>);
     /*   RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        */

    }
}