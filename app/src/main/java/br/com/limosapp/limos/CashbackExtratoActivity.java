package br.com.limosapp.limos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import br.com.limosapp.limos.firebase.CashbackExtratoFirebase;
import br.com.limosapp.limos.holder.CashbackExtratoViewHolder;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.MainActivity.idusuario;

public class CashbackExtratoActivity extends AppCompatActivity {

    ImageView imgVoltar, imgExtratoCashback;
    TextView txtTitulo, txtSaldo;
    RecyclerView rvExtrato;
    ProgressBar pbCarregar;

    String valorString;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    Query dbextrato = db.child("usuariocashbackextrato").child(idusuario).orderByKey().limitToLast(30);
    DatabaseReference dbusuario = db.child("usuarios").child(idusuario);

    Locale ptbr = new Locale("pt", "BR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashback_extrato);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_cashback_extrato);

        dbextrato.keepSynced(true);

        rvExtrato.setHasFixedSize(true);
        rvExtrato.setLayoutManager(new LinearLayoutManager(this));

        imgVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        carregaExtrato();
        carregaSaldo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbusuario.onDisconnect();
    }

    private void inicializar(){
        txtTitulo = findViewById(R.id.txtTitulo);
        imgVoltar = findViewById(R.id.imgVoltar);
        rvExtrato = findViewById(R.id.rvExtrato);
        txtSaldo = findViewById(R.id.txtSaldo);
        imgExtratoCashback = findViewById(R.id.imgExtratoCashback);
        pbCarregar = findViewById(R.id.pbCarregar);
    }

    private void carregaExtrato(){
        pbCarregar.setVisibility(View.VISIBLE);

        FirebaseRecyclerAdapter<CashbackExtratoFirebase, CashbackExtratoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CashbackExtratoFirebase, CashbackExtratoViewHolder>(
                CashbackExtratoFirebase.class,
                R.layout.recyclerview_extrato,
               CashbackExtratoViewHolder.class,
                dbextrato
        ) {
            @Override
            protected void populateViewHolder(CashbackExtratoViewHolder viewHolder, final CashbackExtratoFirebase model, final int position) {

                pbCarregar.setVisibility(View.INVISIBLE);

                viewHolder.setRestaurante(model.getRestaurante());
                viewHolder.setPedido(model.getPedido());
                viewHolder.setData(model.getDatahora());
                viewHolder.setValor(model.getValor());
                viewHolder.setImage(model.getValor());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                pbCarregar.setVisibility(View.INVISIBLE);
            }
        };
        rvExtrato.setAdapter(firebaseRecyclerAdapter);
    }

    private void carregaSaldo(){
        dbusuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("cashbacksaldo").exists() && !Objects.requireNonNull(dataSnapshot.child("cashbacksaldo").getValue()).toString().isEmpty()) {
                    Double saldocash = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("cashbacksaldo").getValue()).toString());
                    valorString = NumberFormat.getCurrencyInstance(ptbr).format(saldocash);
                    txtSaldo.setText(valorString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}