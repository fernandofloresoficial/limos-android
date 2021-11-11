package br.com.limosapp.limos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.firebase.CuponsFirebase;
import br.com.limosapp.limos.holder.CuponsViewHolder;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;

public class CuponsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imgVoltar;
    TextView txtTitulo, txtfantasia;
    RecyclerView rvcupons;
    SimpleDraweeView imgfotoperfil;
    
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbcupons, dbusuariocuponsresgatados, dbrestaurantes;
    String idrestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cupons);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_cupons);

        //Recupera hash do restaurante e da categoria
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        idrestaurante = Objects.requireNonNull(bundle).getString("idrestaurante");

        dbcupons = db.child("restaurantecupons").child(idrestaurante);
        dbrestaurantes = db.child("restaurantes").child(idrestaurante);

        dbcupons.keepSynced(true);

        rvcupons.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvcupons.setLayoutManager(linearLayoutManager);
        rvcupons.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        imgVoltar.setOnClickListener(this);

        carregaDados();

        carregaCupons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbcupons.onDisconnect();
        dbrestaurantes.onDisconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVoltar:
                finish();
                break;
        }
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        txtTitulo = findViewById(R.id.txtTitulo);
        rvcupons = findViewById(R.id.rvCupons);
        txtfantasia = findViewById(R.id.txtFantasia);
        imgfotoperfil = findViewById(R.id.imgFotoPerfil);
    }

    private void carregaCupons(){
        Query querycupons;
        //querycupons = dbcupons.child("cupons").orderByChild("validademseg").startAt(System.currentTimeMillis());
        querycupons = dbcupons.child("cupons").orderByChild("validademseg").limitToLast(20);

        FirebaseRecyclerAdapter<CuponsFirebase, CuponsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CuponsFirebase, CuponsViewHolder>(
                CuponsFirebase.class,
                R.layout.recyclerview_cupons,
                CuponsViewHolder.class,
                querycupons
        ) {
            @Override
            protected void populateViewHolder(CuponsViewHolder viewHolder, final CuponsFirebase model, final int position) {
                final String idcupom = getRef(position).getKey();
                viewHolder.setDesconto(model.getDesconto());
                viewHolder.setValidade(model.getValidade());
                viewHolder.verifValidade(model.getValidade());
                viewHolder.verifAtivo(model.getAtivo());
                viewHolder.setSeg(model.getSeg());
                viewHolder.setTer(model.getTer());
                viewHolder.setQua(model.getQua());
                viewHolder.setQui(model.getQui());
                viewHolder.setSex(model.getSex());
                viewHolder.setSab(model.getSab());
                viewHolder.setDom(model.getDom());
                viewHolder.setResgatado(MainActivity.idusuario, idrestaurante, idcupom);

                viewHolder.btnresgatarcupom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        salvarCupomUsuario(MainActivity.idusuario, idcupom, idrestaurante, model.getDesconto(), model.getValidade(), model.getValidademseg(), model.getSeg(), model.getTer(), model.getQua(), model.getQui(), model.getSex(), model.getSab(), model.getDom());
                    }
                });
            }
        };
        rvcupons.setAdapter(firebaseRecyclerAdapter);
    }

    private void salvarCupomUsuario(String idusuario, String idcupom, String idrestaurante, Integer desconto, String validade, Long validademseg, Integer seg, Integer ter, Integer qua, Integer qui, Integer sex, Integer sab, Integer dom) {
        dbusuariocuponsresgatados = db.child("usuariocuponsresgatados").child(idusuario).child(idrestaurante).child(idcupom);

        CuponsFirebase cuponsResgatadosFirebase = new CuponsFirebase();
        cuponsResgatadosFirebase.setDesconto(desconto);
        cuponsResgatadosFirebase.setValidade(validade);
        cuponsResgatadosFirebase.setValidademseg(validademseg);
        cuponsResgatadosFirebase.setSeg(seg);
        cuponsResgatadosFirebase.setTer(ter);
        cuponsResgatadosFirebase.setQua(qua);
        cuponsResgatadosFirebase.setQui(qui);
        cuponsResgatadosFirebase.setSex(sex);
        cuponsResgatadosFirebase.setSab(sab);
        cuponsResgatadosFirebase.setDom(dom);

        dbusuariocuponsresgatados.setValue(cuponsResgatadosFirebase);
        new Toast_layout(this).mensagem(getString(R.string.aproveitedesconto));
    }

    private void carregaDados() {
        dbrestaurantes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                if (dataSnapshot.child("fantasia").exists()) txtfantasia.setText(Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString());
                if (dataSnapshot.child("fotoperfil").exists() && !Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString().isEmpty()) {
                    String urlfoto = Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString();
                    imgfotoperfil.setImageURI(urlfoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}