package br.com.limosapp.limos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.util.VerificaInternet;
import br.com.limosapp.limos.firebase.EnderecosFirebase;
import br.com.limosapp.limos.holder.EnderecosViewHolder;

import static br.com.limosapp.limos.MainActivity.idusuario;
import static br.com.limosapp.limos.MainActivity.permitircadend;
import static br.com.limosapp.limos.MainActivity.usarendcadastrado;

public class EnderecoCadastradoActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imgVoltar;
    TextView txtTitulo;
    RecyclerView rvEnderecos;

    FloatingActionButton fabAdicionar;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco_cadastrado);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_endereco_cadastrado);
        db.keepSynced(true);

        rvEnderecos.setHasFixedSize(true);
        rvEnderecos.setLayoutManager(new LinearLayoutManager(this));
        rvEnderecos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        if (!permitircadend){
            fabAdicionar.setVisibility(View.INVISIBLE);
        }

        carregarEnderecos();

        imgVoltar.setOnClickListener(this);
        fabAdicionar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAdicionar:
                startActivity(new Intent(EnderecoCadastradoActivity.this, EnderecoNovoActivity.class));
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

    private void inicializar(){
        txtTitulo = findViewById(R.id.txtTitulo);
        imgVoltar = findViewById(R.id.imgVoltar);
        rvEnderecos = findViewById(R.id.rvEnderecos);
        fabAdicionar = findViewById(R.id.fabAdicionar);
    }

    public void carregarEnderecos(){
        DatabaseReference dbendereco = db.child("usuarioenderecos").child(idusuario);

        FirebaseRecyclerAdapter<EnderecosFirebase, EnderecosViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<EnderecosFirebase, EnderecosViewHolder>
                (EnderecosFirebase.class,R.layout.recyclerview_enderecos_cadastrados, EnderecosViewHolder.class, dbendereco) {
            @Override
            protected void populateViewHolder(EnderecosViewHolder viewHolder, EnderecosFirebase model, int position) {
                final String idendereco = getRef(position).getKey();
                final String enderecopadrao, numeropadrao, complementopadrao, bairropadrao, cidadepadrao, estadopadrao;

                enderecopadrao = model.getEndereco();
                numeropadrao = model.getNumero();
                complementopadrao = model.getComplemento();
                bairropadrao = model.getBairro();
                cidadepadrao = model.getCidade();
                estadopadrao = model.getEstado();
                viewHolder.setPadrao(idendereco);
                viewHolder.setEndereco(EnderecoCadastradoActivity.this, enderecopadrao, numeropadrao, complementopadrao, bairropadrao, cidadepadrao, estadopadrao);

                if (!permitircadend) {
                    viewHolder.chkpadrao.setVisibility(View.INVISIBLE);
                    viewHolder.imgeditar.setVisibility(View.INVISIBLE);
                    viewHolder.imgexcluir.setVisibility(View.INVISIBLE);
                }

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    if (usarendcadastrado){                        
                        MainActivity.idendereco = idendereco;
                        finish();
                    }
                    }
                });

                viewHolder.chkpadrao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseReference dbenderecopadrao = db.child("usuarioenderecospadrao");
                        if ( ((CheckBox)v).isChecked() ) {
                            dbenderecopadrao.child(idusuario).setValue(idendereco);
                        }else{
                            dbenderecopadrao.child(idusuario).removeValue();
                        }
                    }
                });

                viewHolder.imgeditar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EnderecoCadastradoActivity.this, EnderecoCEPActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("idendereco", idendereco);
                        bundle.putString("cep", "");
                        bundle.putString("endereco", "");
                        bundle.putString("bairro", "");
                        bundle.putString("cidade", "");
                        bundle.putString("estado", "");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                viewHolder.imgexcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Confirmação")
                                .setMessage("Tem certeza que deseja remover este endereço?")
                                .setPositiveButton("Remover", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final DatabaseReference dbendereco = db.child("usuarioenderecos").child(idusuario);
                                        dbendereco.child(Objects.requireNonNull(idendereco)).removeValue();

                                        Query dbenderecopadrao = FirebaseDatabase.getInstance().getReference().child("usuarioenderecospadrao").orderByValue().equalTo(idendereco);
                                        dbenderecopadrao.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                                    appleSnapshot.getRef().removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .create()
                                .show();
                    }
                });
            }
        };
        rvEnderecos.setAdapter(firebaseRecyclerAdapter);
    }
}