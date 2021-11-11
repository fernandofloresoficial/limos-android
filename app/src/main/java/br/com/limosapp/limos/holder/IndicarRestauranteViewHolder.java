package br.com.limosapp.limos.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.limosapp.limos.R;

public  class IndicarRestauranteViewHolder extends RecyclerView.ViewHolder {
    public View view;
    private TextView txtRestaurante, txtCidade;
    public Button btnIndicar;

    private DatabaseReference dbindicacoes = FirebaseDatabase.getInstance().getReference().child("restauranteindicacoes");

    public IndicarRestauranteViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        txtRestaurante = view.findViewById(R.id.txtRestaurante);
        txtCidade = view.findViewById(R.id.txtCidade);
        btnIndicar = view.findViewById(R.id.btnIndicar);

        dbindicacoes.keepSynced(true);
    }

    public void setRestaurante(String restaurante) {
        txtRestaurante.setText(restaurante);
    }

    public void setCidade(String cidade) {
        txtCidade.setText(cidade);
    }

    public void setIndicado(final String idusuario, final String idrestaurante){
        dbindicacoes.child(idrestaurante).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(idusuario)) {
                    btnIndicar.setEnabled(false);
                    btnIndicar.setText(R.string.indicado);
                    btnIndicar.setBackgroundResource(R.drawable.botalogincinza);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
