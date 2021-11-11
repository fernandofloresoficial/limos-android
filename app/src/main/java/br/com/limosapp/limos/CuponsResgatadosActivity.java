package br.com.limosapp.limos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.limosapp.limos.classes.CuponsResgatadosAdapter;
import br.com.limosapp.limos.firebase.CuponsResgatadosFirebase;
import br.com.limosapp.limos.firebase.CuponsRestaurantesFirebase;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.MainActivity.idusuario;

public class CuponsResgatadosActivity extends AppCompatActivity {
    ImageView imgVoltar;
    TextView txtTitulo;
    RecyclerView rvCuponsResgatados;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    List<ParentObject> parentObject = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cupons_resgatados);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_cupons_resgatados);

        rvCuponsResgatados.setHasFixedSize(true);
        rvCuponsResgatados.setLayoutManager(new LinearLayoutManager(this));
        rvCuponsResgatados.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        db.keepSynced(true);

        carregaRestaurantes();

        imgVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        txtTitulo = findViewById(R.id.txtTitulo);
        rvCuponsResgatados = findViewById(R.id.rvCuponsResgatados);
    }

    private void carregaRestaurantes(){
        DatabaseReference dbrestaurantecuponsresgatados = db.child("usuariocuponsresgatados").child(idusuario);
        dbrestaurantecuponsresgatados.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    criaAdapter(postSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void criaAdapter(final String idrestaurante){
        final CuponsRestaurantesFirebase cuponsResgatadosRestaurantesFirebase = new CuponsRestaurantesFirebase();

        DatabaseReference dbrestaurantes = FirebaseDatabase.getInstance().getReference().child("restaurantes").child(idrestaurante);
        dbrestaurantes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fotoperfil").exists() && !Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString().isEmpty()) cuponsResgatadosRestaurantesFirebase.setFotoperfil(Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString());
                if (dataSnapshot.child("fantasia").exists() && !Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString().isEmpty()) cuponsResgatadosRestaurantesFirebase.setRestaurante(Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString());

                DatabaseReference dbcuponsresgatados = db.child("usuariocuponsresgatados").child(idusuario).child(idrestaurante);
                dbcuponsresgatados.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Object> childList = new ArrayList<>();
                        
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            childList.add(new CuponsResgatadosFirebase(Integer.parseInt(Objects.requireNonNull(postSnapshot.child("desconto").getValue()).toString()), Integer.parseInt(Objects.requireNonNull(postSnapshot.child("seg").getValue()).toString()), Integer.parseInt(Objects.requireNonNull(postSnapshot.child("ter").getValue()).toString()), Integer.parseInt(Objects.requireNonNull(postSnapshot.child("qua").getValue()).toString()), Integer.parseInt(Objects.requireNonNull(postSnapshot.child("qui").getValue()).toString()), Integer.parseInt(Objects.requireNonNull(postSnapshot.child("sex").getValue()).toString()), Integer.parseInt(Objects.requireNonNull(postSnapshot.child("sab").getValue()).toString()), Integer.parseInt(Objects.requireNonNull(postSnapshot.child("dom").getValue()).toString()), Objects.requireNonNull(postSnapshot.child("validade").getValue()).toString(), Long.parseLong(Objects.requireNonNull(postSnapshot.child("validademseg").getValue()).toString())));
                            cuponsResgatadosRestaurantesFirebase.setChildObjectList(childList);
                        }

                        parentObject.add(cuponsResgatadosRestaurantesFirebase);

                        CuponsResgatadosAdapter adapter = new CuponsResgatadosAdapter(CuponsResgatadosActivity.this,initData());
                        adapter.setParentClickableViewAnimationDefaultDuration();
                        adapter.setParentAndIconExpandOnClick(true);
                        rvCuponsResgatados.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private List<ParentObject> initData() {
        return parentObject;
    }
}