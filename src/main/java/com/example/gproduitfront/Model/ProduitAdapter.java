package com.example.gproduitfront.Model;

import android.app.ProgressDialog;

import android.content.ClipData;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gproduitfront.MainActivity;
import com.example.gproduitfront.MainActivity2;
import com.example.gproduitfront.Network.GetDataService;
import com.example.gproduitfront.Network.RetrofitClientInstance;
import com.example.gproduitfront.R;

import java.io.IOException;
import java.nio.Buffer;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProduitAdapter extends RecyclerView.Adapter<ProduitAdapter.ViewHolder>  implements  View.OnClickListener{
    ProgressDialog progressDoalog;
    Context context;
    List<RetroProduit> produitList;
    Button btnDelete ;

    public ProduitAdapter(Context context ,  List<RetroProduit> list ){
        this.context = context;
        this.produitList = list;
    }

    @NonNull
    @Override
    public ProduitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,viewGroup,false);
        progressDoalog = new ProgressDialog(context);

        return new ViewHolder(view);
    }


    RetroProduit produit;
    @Override
    public void onBindViewHolder(@NonNull ProduitAdapter.ViewHolder viewHolder, int i) {
     if (produitList != null && produitList.size() > 0){
         produit = produitList.get(i);
         viewHolder.libele_tv.setText(produit.getLabel());
         viewHolder.pu_tv.setText(String.valueOf(produit.getPu()));
         //viewHolder.get = produit;
         int position = viewHolder.getAdapterPosition();

     }
     else {
         return;
     }
    }

    @Override
    public int getItemCount() {
        return produitList.size();
    }

    @Override
    public void onClick(View v) {
        RetroProduit produit = new RetroProduit();
        //String id = produitList.get(getAdapterPosition()).getId();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        int nbr_produit;
        Double moyenne = 0.0;
       TextView libele_tv , pu_tv;
        public ViewHolder(@NonNull View itemView ) {
            super(itemView);
            libele_tv = itemView.findViewById(R.id.libele_tv);
            pu_tv = itemView.findViewById(R.id.pu_tv);
            btnDelete = itemView.findViewById(R.id.deletebtn);

            ViewHolder holder = (ViewHolder) itemView.getTag();
          //  int position = holder.getAdapterPosition();


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    RetroProduit p = produitList.get(position);

                    String mode = "mode_update";
                    Intent i = new Intent(context , MainActivity2.class);
                    i.putExtra("id" , p.getId());
                    i.putExtra("label" , p.getLabel());
                    i.putExtra("pu" , String.valueOf(p.getPu()));
                    i.putExtra("id_cat" , p.getId_cat());
                    i.putExtra("mode" , mode);
                    //startActivityForResult(i, 0);

                    context.startActivity(i);
                }
            });


            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    RetroProduit p = produitList.get(position);
                    Log.d("demo","product id : "+p.getId());
                    String id = p.getId();

                    delete_product(id,position);



                }


            });


        }
        private void delete_product(String id, int position) {
            Intent i1 = new Intent(context,MainActivity.class);
            Intent i= new Intent("statistic");
            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            Call call = service.deleteProduct(id);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    Toast.makeText(context ,"Produit est supprimé", Toast.LENGTH_LONG).show();
                    produitList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, produitList.size());
                    getStatistic(produitList);

                    i.putExtra("nbr_produit" ,String.valueOf(nbr_produit) );
                    i.putExtra("moyenne" ,String.valueOf(moyenne) );
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);

                    if (!response.isSuccessful()){
                        try {
                            Toast.makeText(context, "Server returned error : "+response.errorBody().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Server returned error : unknown error ", Toast.LENGTH_LONG).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    progressDoalog.dismiss();
                    System.out.println("onFailure=======>"+t.getMessage());
                    Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();

                }
            });
        }

        private void getStatistic(List<RetroProduit> produitList) {
            Double somme_prix =0.0;

            nbr_produit =produitList.size();

            for (RetroProduit p : produitList){
                somme_prix += p.getPu();
            }
            moyenne = somme_prix/nbr_produit;
            moyenne = (double)((int)(moyenne*100))/100;
            Log.d("moyenne","1---moyenne : "+moyenne);

        }
    }
}
