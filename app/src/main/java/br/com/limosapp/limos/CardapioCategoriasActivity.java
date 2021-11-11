package br.com.limosapp.limos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.limosapp.limos.holder.CategoriasViewHolder;
import br.com.limosapp.limos.util.VerificaInternet;
import br.com.limosapp.limos.firebase.CategoriasFirebase;
import br.com.limosapp.limos.sqlite.ProdutosCarrinhoDAO;

import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;

public class CardapioCategoriasActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView imgVoltar, imgCarrinho;
    TextView txtTitulo;
    RecyclerView rvCategorias;
    ProgressBar pbCarregar;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    Animation animacao;

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
        setContentView(R.layout.activity_cardapio_categorias);

        inicializar();

        if (restaurantes == null){finish();} //Mexi aqui pois estava dando erro quando não tinha o restaurante, fecho a tela para garatir
        if (restaurantes.getNomerestaurante() == null){finish();} //Mexi aqui pois estava dando erro quando não tinha o restaurante, fecho a tela para garatir

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Carrinho"));

        txtTitulo.setText(restaurantes.getNomerestaurante());
        rvCategorias.setHasFixedSize(true);
        rvCategorias.setLayoutManager(new GridLayoutManager(this, 2));

        imgVoltar.setOnClickListener(this);
        imgCarrinho.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        pbCarregar.setVisibility(View.VISIBLE);

        mostrarCarrinho();
        DatabaseReference dbcategoria = db.child("restaurantecategorias").child(restaurantes.getIdrestaurante());

        FirebaseRecyclerAdapter<CategoriasFirebase, CategoriasViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CategoriasFirebase, CategoriasViewHolder>(
                CategoriasFirebase.class,
                R.layout.recyclerview_cardapio_categorias,
                CategoriasViewHolder.class,
                dbcategoria
        ) {
            @Override
            protected void populateViewHolder(final CategoriasViewHolder viewHolder, CategoriasFirebase model, final int position) {

                final String idcategoria = getRef(position).getKey();

                final String categoria = model.getCategoria();

                viewHolder.setCategoria(categoria);

                pbCarregar.setVisibility(View.INVISIBLE);

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(CardapioCategoriasActivity.this, CardapioProdutosActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("idcategoria", idcategoria);
                        bundle.putString("categoria", categoria);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                pbCarregar.setVisibility(View.INVISIBLE);
            }
        };
        rvCategorias.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVoltar:
                finish();
                break;
            case R.id.imgCarrinho:
                startActivity(new Intent(CardapioCategoriasActivity.this, CarrinhoActivity.class));
                break;
        }
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        txtTitulo = findViewById(R.id.txtTitulo);
        rvCategorias = findViewById(R.id.rvCategorias);
        imgCarrinho = findViewById(R.id.imgCarrinho);
        pbCarregar = findViewById(R.id.pbCarregar);
    }

    private void mostrarCarrinho(){
        ProdutosCarrinhoDAO dao = new ProdutosCarrinhoDAO(this);
        boolean mostrar = dao.verificaRegistro();
        if (mostrar){
            imgCarrinho.setVisibility(View.VISIBLE);
            // load the animation
            animacao = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bouncing);
            imgCarrinho.startAnimation(animacao);
        }else{
            imgCarrinho.setVisibility(View.INVISIBLE);
        }
    }
}
