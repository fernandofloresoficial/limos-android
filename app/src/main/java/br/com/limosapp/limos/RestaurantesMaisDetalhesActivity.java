package br.com.limosapp.limos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import br.com.limosapp.limos.util.VerificaInternet;
import br.com.limosapp.limos.holder.FormasPagViewHolder;

import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;

public class RestaurantesMaisDetalhesActivity extends AppCompatActivity implements View.OnClickListener{

    SimpleDraweeView imgPerfil, imgCapa;
    TextView  txthdom, txthseg, txthter, txthqua, txthqui, txthsex, txthsab, txtvalor;
    ImageView imgvoltar;
    CardView crvbrinde, crvvalor, crvbalcao;
    RecyclerView rvformaspag;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbhorarios = db;
    DatabaseReference dbformaspag = db;
    Locale ptBr = new Locale("pt", "BR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantes_mais_detalhes);
        Fresco.initialize(this);
        inicializar();

        if (restaurantes == null){finish();} //fecho a tela caso o restaurante estiver nulo

        dbhorarios = db.child("restaurantehorarios").child(restaurantes.getIdrestaurante());
        dbformaspag = db.child("restaurantes").child(restaurantes.getIdrestaurante()).child("pagamentos");

        //rvformaspag.setHasFixedSize(true);
        rvformaspag.setLayoutManager(new GridLayoutManager(this, 2));

        imgCapa.setImageURI(restaurantes.getFotocaparest());
        imgPerfil.setImageURI(restaurantes.getFotoperfilrest());

        carregaHorarios();
        verificaTemBrinde();
        carregaFormaPag();
        verificaValor();
        verificaBalcao();

        imgvoltar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVoltar:
                finish();
                break;
            case R.id.btnIncluirObservacoes:
                Intent intent = new Intent(RestaurantesMaisDetalhesActivity.this, CardapioCategoriasActivity.class);
                startActivity(intent);
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
    protected void onDestroy() {
        super.onDestroy();
        db.onDisconnect();
        dbhorarios.onDisconnect();
        dbformaspag.onDisconnect();
    }

    private void inicializar(){
        imgvoltar = findViewById(R.id.imgVoltar);
        imgPerfil = findViewById(R.id.imgPerfil);
        imgCapa = findViewById(R.id.imgFotoCapa);
        txtvalor = findViewById(R.id.txtValor);
        txthdom = findViewById(R.id.txtHdom);
        txthseg = findViewById(R.id.txtHseg);
        txthter = findViewById(R.id.txtHter);
        txthqua = findViewById(R.id.txtHqua);
        txthqui = findViewById(R.id.txtHqui);
        txthsex = findViewById(R.id.txtHsex);
        txthsab = findViewById(R.id.txtHsab);
        txthdom = findViewById(R.id.txtHdom);
        crvbrinde = findViewById(R.id.cardViewbrinde);
        crvvalor = findViewById(R.id.cardViewValor);
        crvbalcao = findViewById(R.id.cardViewBalcao);
        rvformaspag = findViewById(R.id.rvFormaPag);
    }

    private void carregaHorarios(){
        dbhorarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dia, abre, abre1, abre2, fecha, fecha1, fecha2, horarios;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    abre = "";
                    abre1 = "";
                    abre2 = "";
                    fecha = "";
                    fecha1 = "";
                    fecha2 = "";
                    horarios = "Fechado";
                    dia = postSnapshot.getKey();
                    if (postSnapshot.child("abre").exists() && !Objects.requireNonNull(postSnapshot.child("abre").getValue()).toString().isEmpty()) abre = Objects.requireNonNull(postSnapshot.child("abre").getValue()).toString();
                    if (postSnapshot.child("fecha").exists() && !Objects.requireNonNull(postSnapshot.child("fecha").getValue()).toString().isEmpty()) fecha = Objects.requireNonNull(postSnapshot.child("fecha").getValue()).toString();
                    if (postSnapshot.child("abre1").exists() && !Objects.requireNonNull(postSnapshot.child("abre1").getValue()).toString().isEmpty()) abre1 = Objects.requireNonNull(postSnapshot.child("abre1").getValue()).toString();
                    if (postSnapshot.child("fecha1").exists() && !Objects.requireNonNull(postSnapshot.child("fecha1").getValue()).toString().isEmpty()) fecha1 = Objects.requireNonNull(postSnapshot.child("fecha1").getValue()).toString();
                    if (postSnapshot.child("abre2").exists() && !Objects.requireNonNull(postSnapshot.child("abre2").getValue()).toString().isEmpty()) abre2 = Objects.requireNonNull(postSnapshot.child("abre2").getValue()).toString();
                    if (postSnapshot.child("fecha2").exists() && !Objects.requireNonNull(postSnapshot.child("fecha2").getValue()).toString().isEmpty()) fecha2 = Objects.requireNonNull(postSnapshot.child("fecha2").getValue()).toString();

                    if (!abre.equals("") && !fecha.equals("") && !abre1.equals("") && !fecha1.equals("") && !abre2.equals("") && !fecha2.equals("")){
                        horarios = abre + " às " + fecha + "     " + abre1 + " às " + fecha1 + "     " + abre2 + " às " + fecha2;
                    }else if (!abre.equals("") && !fecha.equals("") && !abre1.equals("") && !fecha1.equals("")){
                        horarios = abre + " às " + fecha + "     " + abre1 + " às " + fecha1;
                    }else if (!abre.equals("") && !fecha.equals("")) {
                        horarios = abre + " às " + fecha;
                    }

                    assert dia != null;
                    switch (dia){
                        case "dom":
                            txthdom.setText(horarios);
                            break;
                        case "seg":
                            txthseg.setText(horarios);
                            break;
                        case "ter":
                            txthter.setText(horarios);
                            break;
                        case "qua":
                            txthqua.setText(horarios);
                            break;
                        case "qui":
                            txthqui.setText(horarios);
                            break;
                        case "sex":
                            txthsex.setText(horarios);
                            break;
                        case "sab":
                            txthsab.setText(horarios);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    private void verificaTemBrinde() {
        db.child("restaurantebrindes").child(restaurantes.getIdrestaurante()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()) {
                    crvbrinde.setVisibility(View.GONE);
                }else {
                    crvbrinde.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verificaValor() {
        db.child("restaurantes").child(restaurantes.getIdrestaurante()).child("valor_minimo_pedido").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(Double.parseDouble(dataSnapshot.getValue().toString()) > 0.00) {
                    crvvalor.setVisibility(View.VISIBLE);
                    txtvalor.setText(getString(R.string.valorpedidominimo, NumberFormat.getCurrencyInstance(ptBr).format(Double.parseDouble(dataSnapshot.getValue().toString()))));
                }else {
                    crvvalor.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verificaBalcao() {
        db.child("restaurantes").child(restaurantes.getIdrestaurante()).child("retirabalcao").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(Integer.parseInt(dataSnapshot.getValue().toString()) == 1) {
                    crvbalcao.setVisibility(View.VISIBLE);
                }else {
                    crvbalcao.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void carregaFormaPag(){
        final FirebaseRecyclerAdapter<Integer, FormasPagViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Integer, FormasPagViewHolder>
                (Integer.class,R.layout.recyclerview_formas_pagamento,FormasPagViewHolder.class, dbformaspag) {
            @Override
            protected void populateViewHolder(FormasPagViewHolder viewHolder, Integer model, int position) {
                final String formapag = getRef(position).getKey();
                viewHolder.setDadosFormaPag(formapag);
            }
        };
        rvformaspag.setAdapter(firebaseRecyclerAdapter);
    }
}