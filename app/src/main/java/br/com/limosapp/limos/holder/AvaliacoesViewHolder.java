package br.com.limosapp.limos.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.R;

public class AvaliacoesViewHolder extends RecyclerView.ViewHolder{

    private SimpleDraweeView imgFoto;
    private TextView txtUsuario, txtNotausuario, txtData, txtComentario, txtResposta;
    private RatingBar rtbAvaliacaoUsuario;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private String urlfoto = "";

    public AvaliacoesViewHolder(View view) {
        super(view);
        imgFoto = view.findViewById(R.id.imgFoto);
        txtUsuario = view.findViewById(R.id.txtUsuario);
        txtNotausuario = view.findViewById(R.id.txtNotaUsuario);
        txtData = view.findViewById(R.id.txtData);
        rtbAvaliacaoUsuario = view.findViewById(R.id.rtbAvaliacaoUsuario);
        txtComentario = view.findViewById(R.id.txtComentario);
        txtResposta = view.findViewById(R.id.txtResposta);

        db.keepSynced(true);
    }

    public void setFoto(String idusuarioavaliacoes){
        DatabaseReference dbusuarios = db.child("usuarios").child(idusuarioavaliacoes);
        dbusuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("urlfoto").exists() && !Objects.requireNonNull(dataSnapshot.child("urlfoto").getValue()).toString().isEmpty()) {
                    urlfoto = Objects.requireNonNull(dataSnapshot.child("urlfoto").getValue()).toString();
                    imgFoto.setImageURI(urlfoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setNomeusuario(String nomeusuario){
        txtUsuario.setText(nomeusuario);
    }


    public void setNota(Float nota){
        txtNotausuario.setText(String.valueOf(nota));
        rtbAvaliacaoUsuario.setRating(nota);
    }

    public void setDataPedido(String dataPedido){
        txtData.setText(dataPedido);
    }

    public void setComentario(String comentario){
        txtComentario.setText(comentario);
    }
    public void setResposta(String restaurante, String resposta){
        if(!resposta.equals("")){
            String resposta1 = "Restaurante - " + restaurante + "\n" + resposta;
            txtResposta.setText(resposta1);
        }else {
            txtResposta.setText("");
            txtResposta.setVisibility(View.INVISIBLE);
        }
    }
}
