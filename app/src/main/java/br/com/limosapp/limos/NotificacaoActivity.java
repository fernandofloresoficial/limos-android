package br.com.limosapp.limos;

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
import br.com.limosapp.limos.holder.NotificacaoPedidosViewHolder;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.MainActivity.idusuario;

public class NotificacaoActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgVoltar;
    TextView txtTitulo;
    RecyclerView rvNotificacao;
    DatabaseReference dbusuariopedidos = FirebaseDatabase.getInstance().getReference().child("usuariopedidos").child(idusuario);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_notificacao);

        rvNotificacao.setHasFixedSize(true);
        rvNotificacao.setLayoutManager(new LinearLayoutManager(this));
        rvNotificacao.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        dbusuariopedidos.keepSynced(true);
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
        rvNotificacao = findViewById(R.id.rvNotificacao);
    }

    public void carregarPedidos(){
        Query querypedidos;
        querypedidos = dbusuariopedidos.orderByChild("statuspedido").startAt(0).endAt(2);

        FirebaseRecyclerAdapter<UsuariosPedidosFirebase, NotificacaoPedidosViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<UsuariosPedidosFirebase, NotificacaoPedidosViewHolder>
                (UsuariosPedidosFirebase.class,R.layout.recyclerview_notificacoes, NotificacaoPedidosViewHolder.class, querypedidos) {
            @Override
            protected void populateViewHolder(NotificacaoPedidosViewHolder viewHolder, UsuariosPedidosFirebase model, int position) {
                final String idpedido = getRef(position).getKey();
                final String idrestaurante = model.getRestaurante();

                viewHolder.setDadosRestaurante(idrestaurante);
                viewHolder.setNumeroPedido(model.getNumeropedido());
                viewHolder.setStatus(idpedido);
            }
        };
        rvNotificacao.setAdapter(firebaseRecyclerAdapter);
    }
}