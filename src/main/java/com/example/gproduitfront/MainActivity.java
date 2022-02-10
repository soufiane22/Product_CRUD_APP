package com.example.gproduitfront;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
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
    TextView nbr_tv;
    TextView moyenne_tv;
    List<RetroProduit> produitList = new ArrayList<>();
    List<Categorie> categoriesList = new ArrayList<>();
    ArrayList<String> categories = new ArrayList<>() ;
    String category_item ;
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
        nbr_tv =findViewById(R.id.nbr);
        moyenne_tv = findViewById(R.id.moyenne);

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

                 String idCat = getIdCat();
                Toast.makeText(MainActivity.this,selecteCategorie, Toast.LENGTH_LONG).show();

                getListProduits(idCat);

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getAllProduits();

            }
        });


        // set value selected in spinner
        category_item =  getIntent().getStringExtra("category");




        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,MainActivity2.class);
                i.putExtra("category" , selecteCategorie);
                i.putExtra("mode" , "mode_add");
               // startActivity(i);
                startActivityForResult(i, 0);
            }
        });

        getListProduits(selecteCategorie);
        String  nbr1 =  getIntent().getStringExtra("nbr_produit1");
        String  moyenne1 =  getIntent().getStringExtra("moyenne1");


        // get statistic after delete product

        System.out.println("1----nbr_produit====>"+nbr1);
        System.out.println("2-----moyenne====>"+moyenne1);


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("statistic"));

    }


    private int getIndex(Spinner myspinner, String category_item) {

        for(int i =0 ; i < myspinner.getCount() ; i++){
            if(myspinner.getItemAtPosition(i).toString().equals(category_item)){
                //System.out.println(" index of category selected  "+i);
                return i;
            }
        }
        return 0;
    }


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            String nbr = intent.getStringExtra("nbr_produit");

            String moyenne = intent.getStringExtra("moyenne");

            nbr_tv.setText(String.valueOf(nbr));
            moyenne_tv.setText(moyenne);
        }
    };

    private void getStatistic(List<RetroProduit> produitList) {
        Double somme_prix =0.0;
        Double moyenne = 0.0;
        int nbr_produit =produitList.size();
        nbr_tv.setText(String.valueOf(nbr_produit) );
        for (RetroProduit p : produitList){
            somme_prix += p.getPu();
        }
        moyenne = somme_prix/nbr_produit;
        moyenne = (double)((int)(moyenne*100))/100;
        Log.d("moyenne","moyenne : "+moyenne);
        moyenne_tv.setText(String.valueOf(moyenne));
    }

    private String getIdCat() {
        String  id_cat = null;
        for(Categorie c:categoriesList){

            if(c.getDesignation().equals(selecteCategorie)){
                id_cat = c.getId();

            }



        }
        return id_cat;
    }

    private void getAllProduits() {

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<RetroProduit>> call = service.getAllProduits();

        call.enqueue(new Callback<List<RetroProduit>>() {
            @Override
            public void onResponse(Call<List<RetroProduit>> call, Response<List<RetroProduit>> response) {
                progressDoalog.dismiss();
                produitList = response.body();
                generateDataList(response.body());
                setRecycleview();

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
                int index = getIndex(myspinner,category_item);
                System.out.println("index of category selected"+index);
                myspinner.setSelection(index);
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

    private void getListProduits(String cat) {
        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<RetroProduit>> call = service.getListProduits(cat);

        call.enqueue(new Callback<List<RetroProduit>>() {
            @Override
            public void onResponse(Call<List<RetroProduit>> call, Response<List<RetroProduit>> response) {
                progressDoalog.dismiss();
                produitList = response.body();
                generateDataList(response.body());
                setRecycleview();
                getStatistic(produitList);

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
            content += "3------libellé :"+libelle+"\n";
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