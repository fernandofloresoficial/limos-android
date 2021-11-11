package br.com.limosapp.limos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.firebase.RestaurantesAvaliacoesFirebase;
import br.com.limosapp.limos.holder.AvaliacoesViewHolder;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;

public class RestaurantesAvaliacoesActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rvAvaliacoes;
    ImageView imgVoltar;
    TextView txtRestaurante, txtNota, txtAvaliacoes;
    RatingBar rtbAvaliar;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantes_avaliacoes);

        inicializar();

        txtRestaurante.setText(restaurantes.getNomerestaurante());

        DatabaseReference dbavaliacoesrest = db.child("restaurantes").child(restaurantes.getIdrestaurante());
        dbavaliacoesrest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("mediaavaliacao").exists() && !Objects.requireNonNull(dataSnapshot.child("mediaavaliacao").getValue()).toString().isEmpty()) {
                    double mediaavaliacao = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("mediaavaliacao").getValue()).toString());
                    txtNota.setText(String.valueOf(mediaavaliacao));
                    rtbAvaliar.setRating(Float.parseFloat(String.valueOf(mediaavaliacao)));
                }
                if (dataSnapshot.child("qtavaliacao").exists() && !Objects.requireNonNull(dataSnapshot.child("qtavaliacao").getValue()).toString().isEmpty()) txtAvaliacoes.setText(getString(R.string.qtavaliacoes, Objects.requireNonNull(dataSnapshot.child("qtavaliacao").getValue()).toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rvAvaliacoes.setHasFixedSize(true);
        rvAvaliacoes.setLayoutManager(new LinearLayoutManager(this));
        rvAvaliacoes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        carregaAvaliacoes();

        imgVoltar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVoltar:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        txtRestaurante = findViewById(R.id.txtRestaurante);
        txtNota = findViewById(R.id.txtNota);
        rtbAvaliar = findViewById(R.id.rtbAvaliar);
        txtAvaliacoes = findViewById(R.id.txtAvaliacoes);
        rvAvaliacoes = findViewById(R.id.rvAvaliacoes);
    }

    public void carregaAvaliacoes(){

        DatabaseReference dbavaliacoes = db.child("restauranteavaliacoes").child(restaurantes.getIdrestaurante());
        FirebaseRecyclerAdapter<RestaurantesAvaliacoesFirebase, AvaliacoesViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<RestaurantesAvaliacoesFirebase, AvaliacoesViewHolder>
                (RestaurantesAvaliacoesFirebase.class,R.layout.recyclerview_restaurantes_avaliacoes, AvaliacoesViewHolder.class, dbavaliacoes) {
            @Override
            protected void populateViewHolder(AvaliacoesViewHolder viewHolder, RestaurantesAvaliacoesFirebase model, int position) {
                final String idusuarioavaliacao = model.getUsuario();

                viewHolder.setFoto(idusuarioavaliacao);
                viewHolder.setNomeusuario(model.getNomeusuario());
                viewHolder.setDataPedido(model.getDatapedido());
                viewHolder.setNota(model.getNota());
                viewHolder.setComentario(model.getComentario());
                viewHolder.setResposta(restaurantes.getNomerestaurante(), model.getResposta());
            }

        };
        rvAvaliacoes.setAdapter(firebaseRecyclerAdapter);
    }
}