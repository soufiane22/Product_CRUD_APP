package com.example.gproduitfront.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gproduitfront.R;

import java.util.List;

public class ProduitAdapter extends RecyclerView.Adapter<ProduitAdapter.ViewHolder> {

    Context context;
    List<RetroProduit> produitList;
    public ProduitAdapter(Context context ,  List<RetroProduit> list ){
        this.context = context;
        this.produitList = list;
    }

    @NonNull
    @Override
    public ProduitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,viewGroup,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProduitAdapter.ViewHolder viewHolder, int i) {
     if (produitList != null && produitList.size() > 0){
         RetroProduit produit = produitList.get(i);
         viewHolder.libele_tv.setText(produit.getLabel());
         viewHolder.pu_tv.setText(String.valueOf(produit.getPu()));
     }
     else {
         return;
     }
    }

    @Override
    public int getItemCount() {
        return produitList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
       TextView libele_tv , pu_tv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            libele_tv = itemView.findViewById(R.id.libele_tv);
            pu_tv = itemView.findViewById(R.id.pu_tv);

        }
    }
}
