package br.com.limosapp.limos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


import java.util.Objects;

import br.com.limosapp.limos.holder.ProdutosViewHolder;
import br.com.limosapp.limos.util.VerificaInternet;
import br.com.limosapp.limos.firebase.ProdutosFirebase;
import br.com.limosapp.limos.sqlite.ProdutosCarrinhoDAO;

public class CardapioProdutosActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtTitulo;
    ImageView imgVoltar, imgCarrinho;
    RecyclerView rvProdutos;
    Animation animacao;
    EditText edtBuscar;

    public static String idproduto;
    String idcategoria;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

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
        setContentView(R.layout.activity_cardapio_produtos);

        inicializar();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Carrinho"));

        //Recupera hash do restaurante e da categoria
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        idcategoria = Objects.requireNonNull(bundle).getString("idcategoria");
        txtTitulo.setText(bundle.getString("categoria"));

        db.keepSynced(true);

        rvProdutos.setHasFixedSize(true);
        rvProdutos.setLayoutManager(new LinearLayoutManager(this));
        imgVoltar.setOnClickListener(this);
        imgCarrinho.setOnClickListener(this);

        edtBuscar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == KeyEvent.ACTION_DOWN ) {

                    String produtobuscar = edtBuscar.getText().toString();
                    if(!produtobuscar.equals("")){
                        carregarProdutos(produtobuscar);
                    }else{
                        carregarProdutos(null);
                    }

                    return true;
                }
                else {
                    return false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mostrarCarrinho();
        carregarProdutos(null);
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
                Intent intent = new Intent(CardapioProdutosActivity.this, CarrinhoActivity.class);
                startActivity(intent);
        }
    }

    private void inicializar() {
        imgVoltar = findViewById(R.id.imgVoltar);
        txtTitulo = findViewById(R.id.txtTitulo);
        edtBuscar = findViewById(R.id.edtBuscar);
        rvProdutos = findViewById(R.id.rvProdutos);
        imgCarrinho = findViewById(R.id.imgCarrinho);
    }

    private void mostrarCarrinho() {
        ProdutosCarrinhoDAO dao = new ProdutosCarrinhoDAO(this);
        boolean mostrar = dao.verificaRegistro();
        if (mostrar) {
            imgCarrinho.setVisibility(View.VISIBLE);
            // load the animation
            animacao = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.bouncing);
            imgCarrinho.startAnimation(animacao);
        } else {
            imgCarrinho.setVisibility(View.INVISIBLE);
        }
    }

    private void carregarProdutos(String buscar){
        Query queryprodutos;
        if (buscar==null){
            queryprodutos = db.child("categoriaprodutos").child(idcategoria).orderByChild("ativo").startAt("1");
        }else{
            queryprodutos = db.child("categoriaprodutos").child(idcategoria).orderByChild("ativo_produto").startAt("1_" + buscar).endAt("1_" + buscar + "\uf8ff");
        }
        FirebaseRecyclerAdapter<ProdutosFirebase, ProdutosViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ProdutosFirebase, ProdutosViewHolder>(
                ProdutosFirebase.class,
                R.layout.recyclerview_cardapio_produtos,
                ProdutosViewHolder.class,
                queryprodutos
        ) {
            @Override
            protected void populateViewHolder(ProdutosViewHolder viewHolder, ProdutosFirebase model, int position) {

                final String idprodutolista = getRef(position).getKey();
                viewHolder.setProduto(model.getProduto());
                viewHolder.setDescricao(model.getDescricao());
                viewHolder.setFoto(model.getFoto());
                viewHolder.setServe(model.getServe());
                viewHolder.setTempo(model.getTempo());
                viewHolder.setPreco(model.getPreco());
                viewHolder.setApartir(model.getApartir());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CardapioProdutosActivity.this, ProdutoActivity.class);
                        idproduto = idprodutolista;
                        startActivity(intent);
                    }
                });
            }
        };
        rvProdutos.setAdapter(firebaseRecyclerAdapter);
    }
}