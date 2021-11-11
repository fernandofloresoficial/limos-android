package br.com.limosapp.limos.holder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.R;
import br.com.limosapp.limos.util.VerificaDiaSemana;
import br.com.limosapp.limos.util.VerificaMes;

public class PedidosViewHolder extends ParentViewHolder {

    public View view;
    public TextView txtRestaurante, txtNota;
    private TextView txtDia, txtMes, txtDiasemana, txtAvaliar;
    private RatingBar rtbAvaliar;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public PedidosViewHolder(View itemView) {
        super(itemView);
        view=itemView;
        txtDia = view.findViewById(R.id.txtDia);
        txtMes = view.findViewById(R.id.txtMes);
        txtDiasemana = view.findViewById(R.id.txtDiadasemana);
        txtRestaurante = view.findViewById(R.id.txtRestaurante);
        txtNota = view.findViewById(R.id.txtNota);
        rtbAvaliar = view.findViewById(R.id.rtbAvaliar);
        txtAvaliar = view.findViewById(R.id.txtAvaliar);
    }

    public void setDataPedido(String data){
        txtDia.setText(data.substring(0, 2));
        txtMes.setText(new VerificaMes().verifMes(data));
        txtDiasemana.setText(new VerificaDiaSemana().verifDiaSemana(data));
    }

    public void setFantasia(String idrestaurante){
        DatabaseReference dbrestaurantes = db.child("restaurantes").child(idrestaurante);
        dbrestaurantes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fantasia").exists() && !Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString().isEmpty()) txtRestaurante.setText(Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setNota(Integer avaliado, Integer statuspedido, Double nota){
        if (statuspedido == 5) {
            txtAvaliar.setVisibility(View.VISIBLE);
            txtAvaliar.setText(R.string.pedidocancelado);
            txtAvaliar.setTextColor(Color.parseColor("#931125"));
            txtNota.setVisibility(View.INVISIBLE);
            rtbAvaliar.setVisibility(View.INVISIBLE);
        }else{
            if (avaliado == 0){
                txtAvaliar.setVisibility(View.VISIBLE);
                txtNota.setVisibility(View.INVISIBLE);
                rtbAvaliar.setVisibility(View.INVISIBLE);
            }else{
                txtAvaliar.setVisibility(View.INVISIBLE);
                txtNota.setVisibility(View.VISIBLE);
                rtbAvaliar.setVisibility(View.VISIBLE);

                txtNota.setText(String.valueOf(nota));
                rtbAvaliar.setRating(Float.parseFloat(String.valueOf(nota)));
            }
        }
    }
}
