package br.com.limosapp.limos.holder;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import br.com.limosapp.limos.R;
import br.com.limosapp.limos.util.CalcularDistancia;
import br.com.limosapp.limos.util.VerificaDiaSemana;
import br.com.limosapp.limos.util.VerificaFechado;

import static br.com.limosapp.limos.MainActivity.enderecomain;
import static br.com.limosapp.limos.MainActivity.idusuario;

public class RestaurantesViewHolder extends ParentViewHolder {

    public View view;
    public ImageView imgfavorito;
    public TextView txtentrega;
    private TextView txtabertofechado, txtnome, txtcozinha, txtqtdeavaliacao;
    private SimpleDraweeView imgfotocapa, imgfotoperfil;
    private RatingBar rtbravaliacao;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private static Locale ptBr = new Locale("pt", "BR");

    private String estadoatual = "Aberto";

    public RestaurantesViewHolder(View itemView) {
        super(itemView);
        view=itemView;
        imgfavorito=view.findViewById(R.id.imgFavorito);
        txtabertofechado=view.findViewById(R.id.txtAbertoFechado);
        txtnome=view.findViewById(R.id.txtEndereco);
        txtcozinha=view.findViewById(R.id.txtCozinha);
        imgfotocapa=view.findViewById(R.id.imgCapa);
        imgfotoperfil=view.findViewById(R.id.imgPerfil);
        rtbravaliacao=view.findViewById(R.id.rtbAvaliar);
        txtqtdeavaliacao=view.findViewById(R.id.txtAvaliacoes);
        txtentrega=view.findViewById(R.id.txtEntrega);
    }

    public void setFavorito(final String idrestaurantesel){
        db.child("usuariorestaurantesfavoritos").child(idusuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(idrestaurantesel)) {
                    imgfavorito.setImageResource(R.drawable.favoritoon);
                }else{
                    imgfavorito.setImageResource(R.drawable.favoritooff);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setAbertoFechado(String idrestaurantesel)       {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = dateFormat.format(new Date());
        String diasemana = new VerificaDiaSemana().verifDiaSemana(dataFormatada);
        switch(diasemana){
            case "Domingo": diasemana = "dom";break;
            case "Segunda": diasemana = "seg";break;
            case "Terça": diasemana = "ter";break;
            case "Quarta": diasemana = "qua";break;
            case "Quinta": diasemana = "qui";break;
            case "Sexta": diasemana = "sex";break;
            case "Sábado": diasemana = "sab";break;
        }

        final DatabaseReference dbhorarios = db.child("restaurantehorarios").child(idrestaurantesel).child(diasemana);
        dbhorarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String abre = null, fecha = null, abre1 = null, fecha1 = null, abre2 = null, fecha2 = null;
                if (dataSnapshot.child("abre").exists() && !Objects.requireNonNull(dataSnapshot.child("abre").getValue()).toString().isEmpty()) abre = Objects.requireNonNull(dataSnapshot.child("abre").getValue()).toString();
                if (dataSnapshot.child("fecha").exists() && !Objects.requireNonNull(dataSnapshot.child("fecha").getValue()).toString().isEmpty()) fecha = Objects.requireNonNull(dataSnapshot.child("fecha").getValue()).toString();
                if (dataSnapshot.child("abre1").exists() && !Objects.requireNonNull(dataSnapshot.child("abre1").getValue()).toString().isEmpty()) abre1 = Objects.requireNonNull(dataSnapshot.child("abre1").getValue()).toString();
                if (dataSnapshot.child("fecha1").exists() && !Objects.requireNonNull(dataSnapshot.child("fecha1").getValue()).toString().isEmpty()) fecha1 = Objects.requireNonNull(dataSnapshot.child("fecha1").getValue()).toString();
                if (dataSnapshot.child("abre2").exists() && !Objects.requireNonNull(dataSnapshot.child("abre2").getValue()).toString().isEmpty()) abre2 = Objects.requireNonNull(dataSnapshot.child("abre2").getValue()).toString();
                if (dataSnapshot.child("fecha2").exists() && !Objects.requireNonNull(dataSnapshot.child("fecha2").getValue()).toString().isEmpty()) fecha2 = Objects.requireNonNull(dataSnapshot.child("fecha2").getValue()).toString();
                estadoatual = "Fechado";
                VerificaFechado verificacao = new VerificaFechado();
                if (abre != null && fecha != null) {
                    if (verificacao.verificaEstaFechado(abre, fecha)) {
                        if (abre1 != null && fecha1 != null) {
                            if (verificacao.verificaEstaFechado(abre1, fecha1)) {
                                if (abre2 != null && fecha2 != null) {
                                    if (!verificacao.verificaEstaFechado(abre2, fecha2)) {
                                        estadoatual = "Aberto";
                                    }
                                }
                            } else {
                                estadoatual = "Aberto";
                            }
                        }
                    } else {
                        estadoatual = "Aberto";
                    }
                }
                txtabertofechado.setText(estadoatual);
                if (estadoatual.equals("Aberto")){
                    txtabertofechado.setBackgroundColor(Color.parseColor("#42ba66"));
                }else{
                    txtabertofechado.setBackgroundColor(Color.parseColor("#d43651"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setFantasia(String fantasia){
        txtnome.setText(fantasia);
    }

    public void setCozinha(String cozinha){
        txtcozinha.setText(cozinha);
    }

    public void setEspera(String espera, double latituderest, double longituderest){
        String cozinha = txtcozinha.getText().toString() + " . " + espera + " . " +  new CalcularDistancia(enderecomain.getLatitudeatual(), enderecomain.getLongitudeatual(), latituderest, longituderest).carregarDistanciaString();
        txtcozinha.setText(cozinha);
    }

    public void setFotoCapa(String fotocapa){
        imgfotocapa.setImageURI(fotocapa);
    }

    public void setFotoPerfil(String fotoperfil){
        imgfotoperfil.setImageURI(fotoperfil);
    }

    public void setAvaliacaoMedia(double avaliacaomedia)       {
        rtbravaliacao.setRating(Float.parseFloat(String.valueOf(avaliacaomedia)));
    }

    public void setAvaliacao(long avaliacao)       {
        String qtdeavaliacaotexto;
        if (avaliacao == 1) {
            qtdeavaliacaotexto = avaliacao + " avaliação";
        }else {
            qtdeavaliacaotexto = avaliacao + " avaliações";
        }
        txtqtdeavaliacao.setText(qtdeavaliacaotexto);
    }

    public void setEntrega(String idrestaurantesel, double latituderest, double longituderest)       {
        final double distancia = new CalcularDistancia(enderecomain.getLatitudeatual(), enderecomain.getLongitudeatual(), latituderest, longituderest).carregarDistanciaDouble();
        final Query dbhorarios = db.child("restaurantefrete").child(idrestaurantesel);
        dbhorarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("")){
                    Double frete= null;
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        if (distancia <=  Double.parseDouble(postSnapshot.getKey())){
                            frete = Double.parseDouble(Objects.requireNonNull(postSnapshot.getValue()).toString());
                            break;
                        }
                    }

                    if (frete == null){
                        txtentrega.setText(R.string.naoentregabairro);
                    }else if(frete == 0.0){
                        txtentrega.setText(R.string.entregagratis);
                    }else {
                        txtentrega.setText(view.getContext().getString(R.string.valorfrete, NumberFormat.getCurrencyInstance(ptBr).format(frete)));
                    }
                }else{
                    txtentrega.setText(R.string.naoentregabairro);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
