package com.example.gproduitfront;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gproduitfront.Model.Categorie;
import com.example.gproduitfront.Model.RetroProduit;
import com.example.gproduitfront.Network.GetDataService;
import com.example.gproduitfront.Network.RetrofitClientInstance;
import com.example.gproduitfront.databinding.ActivityMain2Binding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    ListView superListView;
    EditText label ;
    EditText pu;
    TextView message ;
    Button btn;
    Button btn1;
    ProgressDialog progressDoalog;
    List<Categorie> categoriesList;
    ArrayList<String> categories = new ArrayList<>() ;
    String cat;
    String id_cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressDoalog = new ProgressDialog(MainActivity2.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Intent intent = getIntent();
        cat =  getIntent().getStringExtra("category");
        final TextView tv1 = (TextView)findViewById( R.id.tv );
        tv1.setText( cat );
        System.out.println("categorie=====>"+cat);

        message =(TextView) findViewById(R.id.message);
        label = findViewById(R.id.label);
        pu = findViewById(R.id.pu);


        String content = "label :"+label+"\n"+"prix : "+pu;



         btn = (Button) findViewById(R.id.btn);
         btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if( label.getText().toString().equals("") || pu.getText().toString().equals("")){
                     Toast.makeText(getApplicationContext(), "Remlissez les champs vides ", Toast.LENGTH_LONG).show();
                 }
                 else{
                     System.out.println("********Enregister nouveau produit");
                     RetroProduit p = new RetroProduit(label.getText().toString(),Double.valueOf(pu.getText().toString()),id_cat);
                     message.setText(content);
                     createProduit(p);
                 }
             }
         });

         btn1 = (Button) findViewById(R.id.button2);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });

        getAllCategories();

       // getAllProduits();

    }

    private void getAllCategories() {

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Categorie>> call = service.getAllCategories();

        call.enqueue(new Callback<List<Categorie>>() {
            @Override
            public void onResponse(Call<List<Categorie>> call, Response<List<Categorie>> response)   {
                progressDoalog.dismiss();
                categoriesList = response.body();
               // System.out.println("1----cat"+cat);
                for(Categorie c:categoriesList){
                    //System.out.println("2-----cat_designation====>"+c.getDesignation());
                   // System.out.println("3-----cat_designation====>"+c.getId());
                    if(c.getDesignation().equals(cat)){
                        id_cat = c.getId();
                        //System.out.println("3-----id categorie =======>"+id_cat);
                    }
                    categories.add(categoriesList.indexOf(c),c.getDesignation());
                    System.out.println("3-----id categorie =======>"+id_cat);

                }
               /* if (categoriesList.contains(cat)) {
                    // ...
                }

                */
                for(Object c:categories) {
                  //  System.out.println("id_cat====> :\n" + c.toString());


                }


                if (!response.isSuccessful()){
                    try {
                        Toast.makeText(MainActivity2.this, "Server returned error : "+response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity2.this, "Server returned error : unknown error ", Toast.LENGTH_LONG).show();
                    }
                }
                //response.body();

            }

            @Override
            public void onFailure(Call<List<Categorie>> call, Throwable t) {
                progressDoalog.dismiss();
                Toast.makeText(getApplicationContext(), call.toString(), Toast.LENGTH_LONG).show();
                System.out.println("onFailure=======>"+t.getMessage());
                Toast.makeText(MainActivity2.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getAllProduits() {

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<RetroProduit>> call = service.getAllProduits();

        call.enqueue(new Callback<List<RetroProduit>>() {

            @Override
            public void onResponse(Call<List<RetroProduit>> call, Response<List<RetroProduit>> response) {
                
                if(response.isSuccessful()){
                    List<RetroProduit> productList = response.body();
                    String[] oneHeroes = new String[productList.size()];
                    //  HashMap<?,?> productList = response.body.contains(data);
                    for (int i = 0; i < productList.size(); i++) {
                        oneHeroes[i] = productList.get(i).getLabel();
                        System.out.println("labels======>"+  oneHeroes[i]);
                    }
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(getApplicationContext(), "server returned error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<RetroProduit>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_LONG).show();
                System.out.println("error de récupération des données");
            }


        });
    }

    private void createProduit(RetroProduit p) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        System.out.println("label :"+p.getLabel()+"\n"+"prix :"+p.getPu()+"\n"+"id categorie :"+p.getId_cat());
        Call<RetroProduit> call = service.create_produit(p);
        call.enqueue(new Callback<RetroProduit>() {
            @Override
            public void onResponse(Call<RetroProduit> call, Response<RetroProduit> response) {
            if(!response.isSuccessful()){
                message.setText("code :"+response.code());
            }else{
                RetroProduit p = response.body();
                String content = "";
                 content += "id :"+p.getId()+"label :"+p.getLabel()+"\n"+"prix : "+p.getPu();
                message.setText(content);
                Toast.makeText(MainActivity2.this, "Produit ajouté avec  succès!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(i);
            }
            }

            @Override
            public void onFailure(Call<RetroProduit> call, Throwable t) {
               // message.setText(t.getMessage());

            }
        });
    }


}