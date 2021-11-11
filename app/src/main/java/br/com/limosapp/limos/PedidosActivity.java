package br.com.limosapp.limos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.com.limosapp.limos.firebase.UsuariosPedidosFirebase;
import br.com.limosapp.limos.holder.PedidosViewHolder;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.MainActivity.idusuario;

public class PedidosActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgVoltar;
    TextView txtTitulo;
    RecyclerView rvPedidos;
    DatabaseReference dbpedidos = FirebaseDatabase.getInstance().getReference().child("usuariopedidos").child(idusuario);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_pedidos);

        dbpedidos.keepSynced(true);

        rvPedidos.setHasFixedSize(true);
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        rvPedidos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        carregarPedidos();

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
        txtTitulo = findViewById(R.id.txtTitulo);
        rvPedidos = findViewById(R.id.rvPedidos);
    }

    private void carregarPedidos(){
        Query querypedidos;
        querypedidos = dbpedidos.orderByKey().limitToLast(20);

        FirebaseRecyclerAdapter<UsuariosPedidosFirebase, PedidosViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<UsuariosPedidosFirebase, PedidosViewHolder>
                (UsuariosPedidosFirebase.class,R.layout.recyclerview_pedidos, PedidosViewHolder.class, querypedidos) {
            @Override
            protected void populateViewHolder(final PedidosViewHolder viewHolder, UsuariosPedidosFirebase model, int position) {
                final String idpedido = getRef(position).getKey();
                final String idrestaurante = model.getRestaurante();
                final int avaliado, statuspedido;

                avaliado = model.getAvaliado();
                statuspedido = model.getStatuspedido();
                viewHolder.setDataPedido(model.getDatapedido());
                viewHolder.setFantasia(idrestaurante);
                viewHolder.setNota(avaliado, statuspedido, model.getNota());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PedidosActivity.this, PedidosDetalhesActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("idrestaurante", idrestaurante);
                        bundle.putString("restaurante", viewHolder.txtRestaurante.getText().toString());
                        bundle.putString("idpedido", idpedido);
                        bundle.putString("nota", viewHolder.txtNota.getText().toString());
                        bundle.putInt("statuspedido", statuspedido);
                        bundle.putInt("avaliado", avaliado);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        };
        rvPedidos.setAdapter(firebaseRecyclerAdapter);
    }
}