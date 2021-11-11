package br.com.limosapp.limos.classes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.firebase.RestaurantesFirebase;
import br.com.limosapp.limos.R;
import br.com.limosapp.limos.RestaurantesDetalhesActivity;
import br.com.limosapp.limos.holder.RestaurantesViewHolder;
import br.com.limosapp.limos.sqlite.ProdutosCarrinhoDAO;

import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;
import static br.com.limosapp.limos.MainActivity.enderecomain;
import static br.com.limosapp.limos.MainActivity.idusuario;

public class Restaurantes {

    private Activity activity;
    private String idrestaurante, nomerestaurante, fotocaparest, fotoperfilrest;
    private Double mediaavaliacao, lt, lg, frete;
    private Long qtavaliacao;
    private Boolean entregaEndereco;

    private RecyclerView rvrestaurantes;
    
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    private boolean favorito = false;
    public AlertDialog dialog;

    public Restaurantes(Activity activity, RecyclerView rvrestaurantes) {
        this.activity = activity;
        this.rvrestaurantes = rvrestaurantes;
    }

    public String getIdrestaurante() {
        return idrestaurante;
    }

    private void setIdrestaurante(String idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public String getNomerestaurante() {
        return nomerestaurante;
    }

    private void setNomerestaurante(String nomerestaurante) { this.nomerestaurante = nomerestaurante; }

    public String getFotocaparest() {
        return fotocaparest;
    }

    private void setFotocaparest(String fotocaparest) {
        this.fotocaparest = fotocaparest;
    }

    public String getFotoperfilrest() {
        return fotoperfilrest;
    }

    private void setFotoperfilrest(String fotoperfilrest) {
        this.fotoperfilrest = fotoperfilrest;
    }

    public Double getMediaavaliacao() {
        return mediaavaliacao;
    }

    private void setMediaavaliacao(Double mediaavaliacao) {
        this.mediaavaliacao = mediaavaliacao;
    }

    public Long getQtavaliacao() { return qtavaliacao; }

    private void setQtavaliacao(Long qtavaliacao) { this.qtavaliacao = qtavaliacao; }

    public Double getLt() { return lt; }

    private void setLt(Double lt) { this.lt = lt; }

    public Double getLg() { return lg; }

    private void setLg(Double lg) { this.lg = lg;}

    public Boolean getEntregaEndereco() { return entregaEndereco; }

    private void setEntregaEndereco(Boolean entregaEndereco) { this.entregaEndereco = entregaEndereco; }

    public Double getFrete() { return frete; }

    public void setFrete(Double frete) { this.frete = frete; }

    public void carregarRestaurantes(Boolean fantasia, String buscar){
        db.keepSynced(true);

        Query queryrestaurantes;
        if (fantasia){
            queryrestaurantes = db.child("restaurantes").orderByChild("ativo_fantasia").startAt("1_"+enderecomain.getCidade()+"_"+buscar).endAt("1_"+enderecomain.getCidade()+"_"+buscar + "\uf8ff");
        }else{
            queryrestaurantes = db.child("restaurantes").orderByChild("ativo_fantasia").startAt("1_"+enderecomain.getCidade()).endAt("1_"+enderecomain.getCidade() + "\uf8ff");
        }
        FirebaseRecyclerAdapter<RestaurantesFirebase, RestaurantesViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<RestaurantesFirebase, RestaurantesViewHolder>
                (RestaurantesFirebase.class,R.layout.recyclerview_restaurantes,RestaurantesViewHolder.class, queryrestaurantes) {
            @Override
            protected void populateViewHolder(final RestaurantesViewHolder viewHolder, RestaurantesFirebase model, int position) {
                final String idrestaurantesel = getRef(position).getKey(), nomerestaurantesel, fotocapasel, fotoperfilsel;
                final double mediaavaliacaosel, ltsel, lgsel;
                final long qtavaliacaosel;

                viewHolder.setFavorito(idrestaurantesel);
                viewHolder.setAbertoFechado(idrestaurantesel);
                nomerestaurantesel = model.getFantasia();
                viewHolder.setFantasia(nomerestaurantesel);
                viewHolder.setCozinha(model.getCozinha());
                viewHolder.setEspera(model.getEspera(), model.getLt(), model.getLg());
                fotocapasel = model.getFotocapa();
                viewHolder.setFotoCapa(fotocapasel);
                fotoperfilsel = model.getFotoperfil();
                viewHolder.setFotoPerfil(fotoperfilsel);
                mediaavaliacaosel = model.getMediaavaliacao();
                viewHolder.setAvaliacaoMedia(mediaavaliacaosel);
                qtavaliacaosel = model.getQtavaliacao();
                viewHolder.setAvaliacao(qtavaliacaosel);
                viewHolder.setEntrega(idrestaurantesel, model.getLt(), model.getLg());
                ltsel = model.getLt();
                lgsel = model.getLg();

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restaurantes.setIdrestaurante(idrestaurantesel);
                        restaurantes.setNomerestaurante(nomerestaurantesel);
                        restaurantes.setFotocaparest(fotocapasel);
                        restaurantes.setFotoperfilrest(fotoperfilsel);
                        restaurantes.setMediaavaliacao(mediaavaliacaosel);
                        restaurantes.setQtavaliacao(qtavaliacaosel);
                        restaurantes.setLg(lgsel);
                        restaurantes.setLt(ltsel);
                        if (viewHolder.txtentrega.getText().equals(activity.getString(R.string.naoentregabairro))){
                            restaurantes.setEntregaEndereco(false);
                        }else{
                            restaurantes.setEntregaEndereco(true);
                            if (viewHolder.txtentrega.getText().equals(activity.getString(R.string.entregagratis))){
                                restaurantes.setFrete(0.0);
                            }else{
                                String fretestring = viewHolder.txtentrega.getText().toString().replace(",",".");
                                fretestring = fretestring.replaceAll("[^\\d.]", "");
                                restaurantes.setFrete(Double.parseDouble(fretestring));
                            }
                        }

                        final ProdutosCarrinhoDAO dao = new ProdutosCarrinhoDAO(activity) ;
                        if(dao.verificaCarrinhoRestaurante(idrestaurantesel)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Você já tem produtos de outro restaurante no seu carrinho")
                                    .setMessage("Deseja limpar o carrinho?")
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dao.excluirCarrinhoRestaurante();
                                            activity.startActivity(new Intent(activity, RestaurantesDetalhesActivity.class));
                                        }
                                    })
                                    .setNegativeButton("Não", null)
                                    .create()
                                    .show();
                        }else{
                            activity.startActivity(new Intent(activity, RestaurantesDetalhesActivity.class));
                        }
                    }
                });

                viewHolder.imgfavorito.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SalvarFavorito(idrestaurantesel);
                    }
                });
            }
        };
        rvrestaurantes.setAdapter(firebaseRecyclerAdapter);
    }

    public void carregarRestaurantesFavoritos(){
        DatabaseReference dbrestaurantesfav;
        dbrestaurantesfav = db.child("usuariorestaurantesfavoritos").child(idusuario);
        dbrestaurantesfav.keepSynced(true);
        final FirebaseRecyclerAdapter<String, RestaurantesViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<String, RestaurantesViewHolder>
                (String.class,R.layout.recyclerview_restaurantes,RestaurantesViewHolder.class, dbrestaurantesfav) {

            @Override
            protected void populateViewHolder(final RestaurantesViewHolder viewHolder, String model, int position) {
                final String idrestaurantefavsel = getRef(position).getKey();
                final String[] nomerestaurantefavsel = {null}, fotocapafavsel = {null}, fotoperfilfavsel = {null};
                final Double[] mediaavaliacaofavsel = new Double[1], ltsel = new Double[1], lgsel = new Double[1];
                final Long[] qtavaliacaofavsel = new Long[1];

                db.child("restaurantes").child(Objects.requireNonNull(idrestaurantefavsel)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("lt").exists() && !dataSnapshot.child("lt").getValue().toString().isEmpty()) ltsel[0] = Double.parseDouble(dataSnapshot.child("lt").getValue().toString());
                        if (dataSnapshot.child("lg").exists() && !dataSnapshot.child("lg").getValue().toString().isEmpty()) lgsel[0] = Double.parseDouble(dataSnapshot.child("lg").getValue().toString());
                        viewHolder.imgfavorito.setImageResource(R.drawable.favoritoon);
                        viewHolder.setAbertoFechado(idrestaurantefavsel);
                        if (dataSnapshot.child("fantasia").exists() && !Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString().isEmpty())
                            nomerestaurantefavsel[0] = Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString();
                        viewHolder.setFantasia(nomerestaurantefavsel[0]);
                        if (dataSnapshot.child("cozinha").exists() && !Objects.requireNonNull(dataSnapshot.child("cozinha").getValue()).toString().isEmpty()) viewHolder.setCozinha(Objects.requireNonNull(dataSnapshot.child("cozinha").getValue()).toString());
                        if (dataSnapshot.child("espera").exists() && !Objects.requireNonNull(dataSnapshot.child("espera").getValue()).toString().isEmpty()) viewHolder.setEspera(Objects.requireNonNull(dataSnapshot.child("espera").getValue()).toString(), Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("lt").getValue()).toString()), Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("lg").getValue()).toString()));
                        if (dataSnapshot.child("fotocapa").exists() && !Objects.requireNonNull(dataSnapshot.child("fotocapa").getValue()).toString().isEmpty())
                            fotocapafavsel[0] = Objects.requireNonNull(dataSnapshot.child("fotocapa").getValue()).toString();
                        viewHolder.setFotoCapa(fotocapafavsel[0]);
                        if (dataSnapshot.child("fotoperfil").exists() && !Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString().isEmpty())
                            fotoperfilfavsel[0] = Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString();
                        viewHolder.setFotoPerfil(fotoperfilfavsel[0]);
                        if (dataSnapshot.child("mediaavaliacao").exists() && !Objects.requireNonNull(dataSnapshot.child("mediaavaliacao").getValue()).toString().isEmpty())
                            mediaavaliacaofavsel[0] = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("mediaavaliacao").getValue()).toString());
                        viewHolder.setAvaliacaoMedia(mediaavaliacaofavsel[0]);
                        if (dataSnapshot.child("qtavaliacao").exists() && !Objects.requireNonNull(dataSnapshot.child("qtavaliacao").getValue()).toString().isEmpty())
                            qtavaliacaofavsel[0] =  Long.parseLong(Objects.requireNonNull(dataSnapshot.child("qtavaliacao").getValue()).toString());
                        viewHolder.setAvaliacao(qtavaliacaofavsel[0]);
                        viewHolder.setEntrega(idrestaurantefavsel, Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("lt").getValue()).toString()), Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("lg").getValue()).toString()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restaurantes.setIdrestaurante(idrestaurantefavsel);
                        restaurantes.setNomerestaurante(nomerestaurantefavsel[0]);
                        restaurantes.setFotocaparest(fotocapafavsel[0]);
                        restaurantes.setFotoperfilrest(fotoperfilfavsel[0]);
                        restaurantes.setMediaavaliacao(mediaavaliacaofavsel[0]);
                        restaurantes.setQtavaliacao(qtavaliacaofavsel[0]);
                        restaurantes.setLt(ltsel[0]);
                        restaurantes.setLg(lgsel[0]);

                        if (viewHolder.txtentrega.getText().equals(activity.getString(R.string.naoentregabairro))){
                            restaurantes.setEntregaEndereco(false);
                        }else{
                            restaurantes.setEntregaEndereco(true);
                            if (viewHolder.txtentrega.getText().equals(activity.getString(R.string.entregagratis))){
                                restaurantes.setFrete(0.0);
                            }else{
                                String fretestring = viewHolder.txtentrega.getText().toString().replace(",",".");
                                fretestring = fretestring.replaceAll("[^\\d.]", "");
                                restaurantes.setFrete(Double.parseDouble(fretestring));
                            }
                        }

                        activity.startActivity(new Intent(activity, RestaurantesDetalhesActivity.class));
                    }
                });

                viewHolder.imgfavorito.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SalvarFavorito(idrestaurantefavsel);
                    }
                });
            }
        };
        rvrestaurantes.setAdapter(firebaseRecyclerAdapter);
    }

    private void SalvarFavorito(final String idrestaurantefav){
        final DatabaseReference dbfavoritos = db.child("usuariorestaurantesfavoritos").child(idusuario);
        favorito = true;

        dbfavoritos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(favorito) {
                    if (dataSnapshot.hasChild(idrestaurantefav)) {
                        dbfavoritos.child(idrestaurantefav).removeValue();
                        favorito = false;
                    } else {
                        dbfavoritos.child(idrestaurantefav).setValue(idrestaurantefav);
                        favorito = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
