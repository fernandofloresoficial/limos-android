package br.com.limosapp.limos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;

public class RestaurantesDetalhesActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtfantasia, txtendereco, txtapresentacao, txtinformacoes, txtveravaliacoes;
    RatingBar ratingbar;
    Button btncardapio;
    SimpleDraweeView imgfotocapa, imgfotoperfil;
    ImageView imgvoltar;

    DatabaseReference dbrestaurantes = FirebaseDatabase.getInstance().getReference().child("restaurantes");

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_restaurantes_detalhes);

        inicializar();

        if (restaurantes == null){finish();} //Caso estiver nulo fecho a telaß

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Carrinho"));
        carregaDados(restaurantes.getIdrestaurante());
        txtveravaliacoes.setOnClickListener(this);
        txtinformacoes.setOnClickListener(this);
        btncardapio.setOnClickListener(this);
        imgvoltar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtVerAvaliacoes:
                startActivity(new Intent(RestaurantesDetalhesActivity.this, RestaurantesAvaliacoesActivity.class));
                break;
            case R.id.txtInformacoes:
                startActivity(new Intent(RestaurantesDetalhesActivity.this, RestaurantesMaisDetalhesActivity.class));
                break;
            case R.id.btnIncluirObservacoes:
                startActivity(new Intent(RestaurantesDetalhesActivity.this, CardapioCategoriasActivity.class));
                break;
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

    @Override
    protected void onDestroy()    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        dbrestaurantes.onDisconnect();
    }

    private void inicializar(){
        txtfantasia = findViewById(R.id.txtFantasia);
        txtveravaliacoes = findViewById(R.id.txtVerAvaliacoes);
        txtendereco = findViewById(R.id.txtEndereco);
        txtapresentacao = findViewById(R.id.txtApresentacao);
        txtinformacoes = findViewById(R.id.txtInformacoes);
        imgfotocapa = findViewById(R.id.imgFotoCapa);
        imgfotoperfil = findViewById(R.id.imgFotoPerfil);
        btncardapio = findViewById(R.id.btnIncluirObservacoes);
        imgvoltar = findViewById(R.id.imgVoltar);
        ratingbar = findViewById(R.id.ratingBar);
    }

    private void carregaDados(final String id){
        txtfantasia.setText(restaurantes.getNomerestaurante());
        imgfotocapa.setImageURI(restaurantes.getFotocaparest());
        imgfotoperfil.setImageURI(restaurantes.getFotoperfilrest());
        ratingbar.setRating(Float.parseFloat(String.valueOf(restaurantes.getMediaavaliacao())));

        dbrestaurantes.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String endereco = "";
                if (dataSnapshot.child("endereco").exists()) endereco = Objects.requireNonNull(dataSnapshot.child("endereco").getValue()).toString();
                if (dataSnapshot.child("numero").exists()) endereco = endereco + ", " + Objects.requireNonNull(dataSnapshot.child("numero").getValue()).toString();
                if (dataSnapshot.child("complemento").exists()){
                    String complemento = Objects.requireNonNull(dataSnapshot.child("complemento").getValue()).toString();
                    if (!complemento.equals("")){ endereco = endereco + ", " + complemento; }
                }
                if (dataSnapshot.child("bairro").exists()) endereco = endereco + ", " + Objects.requireNonNull(dataSnapshot.child("bairro").getValue()).toString();
                if (dataSnapshot.child("cidade").exists()) endereco = endereco + ", " + Objects.requireNonNull(dataSnapshot.child("cidade").getValue()).toString();
                if (dataSnapshot.child("uf").exists()) endereco = endereco + ", " + Objects.requireNonNull(dataSnapshot.child("uf").getValue()).toString();
                txtendereco.setText(endereco);
                if (dataSnapshot.child("apresentacao").exists()) txtapresentacao.setText(Objects.requireNonNull(dataSnapshot.child("apresentacao").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}