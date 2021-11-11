package br.com.limosapp.limos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import br.com.limosapp.limos.holder.AdicionaisViewHolder;
import br.com.limosapp.limos.pojos.AdicionaisLista;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.CardapioProdutosActivity.idproduto;
import static br.com.limosapp.limos.ProdutoActivity.adicionaislista;
import static br.com.limosapp.limos.ProdutoActivity.produto;

public class ProdutoAdicionaisActivity extends AppCompatActivity {

    ImageView imgVoltar;
    TextView txtTitulo, txtProduto, txtValorTotal;
    RecyclerView rvAdicionais;
    Button btnSalvar;

    DatabaseReference dbprodutosadicionais = FirebaseDatabase.getInstance().getReference().child("produtoadicionais").child(idproduto);

    Integer quantidade;
    Double preco;
    ArrayList<String> adicionaisLocal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_adicionais);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_produto_adicionais);

        txtProduto.setText(produto);

        //Contando adicionais e setando valor total
        preco=0.0;
        if (adicionaislista != null) {
            for (int i = 0; i < adicionaislista.size(); i++) {
                if (adicionaislista.get(i).getQtde() != null && !adicionaislista.get(i).getQtde().equals("0")) {
                    adicionaisLocal.add(adicionaislista.get(i).getQtde());
                }else{
                    adicionaisLocal.add("0");
                }
            }
        }
        calculaValores();

        rvAdicionais.setHasFixedSize(true);
        rvAdicionais.setLayoutManager(new LinearLayoutManager(this));

        imgVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adicionaisLocal != null) {
                    for (int i = 0; i < adicionaisLocal.size(); i++) {
                        adicionaislista.get(i).setQtde(adicionaisLocal.get(i));
                    }
                }

                String precototal = txtValorTotal.getText().toString().replaceAll("[^0-9-,]*", "");
                precototal= precototal.replaceAll(",", ".");
                ProdutoActivity.vlradicionais = Double.parseDouble(precototal);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        rvAdicionais.setAdapter(new AdicionaisAdapter(adicionaislista));

//        FirebaseRecyclerAdapter<AdicionaisFirebase, AdicionaisViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AdicionaisFirebase, AdicionaisViewHolder>(
//                AdicionaisFirebase.class,
//                R.layout.recyclerview_adicionais,
//                AdicionaisViewHolder.class,
//                dbprodutosadicionais
//        ) {
//            @Override
//            protected void populateViewHolder(final AdicionaisViewHolder viewHolder, AdicionaisFirebase model, final int position) {
//                final String idAdicional = getRef(position).getKey();
//                final String adicional = model.getAdicional();
//                final String valorunit = Double.toString(model.getValoradicional());
//                viewHolder.setAdicional(model.getAdicional());
//                viewHolder.setValorAdicional(model.getValoradicional());
//
//                if (adicionais != null) {
//                    if (adicionais[position][1] != null) {
//                        viewHolder.txtQuantidade.setText(adicionais[position][1]);
//                    }else{
//                        viewHolder.txtQuantidade.setText("0");
//                    }
//                }
//
//                viewHolder.imgMais.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    quantidade = Integer.parseInt(viewHolder.txtQuantidade.getText().toString());
//                    quantidade++;
//                    viewHolder.txtQuantidade.setText(String.valueOf(quantidade));
//
//                    array(position,idAdicional,String.valueOf(quantidade),adicional,valorunit);
//                    calculaValores();
//                    }
//                });
//
//                viewHolder.imgMenos.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        quantidade = Integer.parseInt(viewHolder.txtQuantidade.getText().toString());
//                        if (quantidade>0){
//                            quantidade--;
//                            viewHolder.txtQuantidade.setText(String.valueOf(quantidade));
//                        }
//                        array(position,idAdicional,quantidade.toString(),adicional,valorunit);
//                        calculaValores();
//                    }
//                });
//            }
//        };
//        rvAdicionais.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbprodutosadicionais.onDisconnect();
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtProduto = findViewById(R.id.txtProduto);
        rvAdicionais = findViewById(R.id.rvAdicionais);
        txtValorTotal = findViewById(R.id.txtValorTotal);
        btnSalvar = findViewById(R.id.btnSalvar);
    }

    public class AdicionaisAdapter extends RecyclerView.Adapter<AdicionaisViewHolder> {

        private final ArrayList<AdicionaisLista> opcinallista;

        private AdicionaisAdapter(ArrayList opcinallista) {
            this.opcinallista = opcinallista;
        }

        @NonNull
        public AdicionaisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AdicionaisViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_adicionais, parent, false));
        }

        public void onBindViewHolder(@NonNull final AdicionaisViewHolder viewHolder, final int position) {

            viewHolder.setAdicional(adicionaislista.get(position).getNome());
            viewHolder.setValorAdicional(Double.parseDouble(adicionaislista.get(position).getVlr()));

            if (adicionaisLocal != null) {
                if (adicionaisLocal.get(position) != null) {
                    viewHolder.txtQuantidade.setText(adicionaisLocal.get(position));
                }else{
                    viewHolder.txtQuantidade.setText("0");
                }
            }

            viewHolder.imgMais.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quantidade = Integer.parseInt(viewHolder.txtQuantidade.getText().toString());
                    quantidade++;
                    viewHolder.txtQuantidade.setText(String.valueOf(quantidade));

                    adicionaisLocal.set(position, quantidade.toString());
                    calculaValores();
                }
            });

            viewHolder.imgMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quantidade = Integer.parseInt(viewHolder.txtQuantidade.getText().toString());
                    if (quantidade>0){
                        quantidade--;
                        viewHolder.txtQuantidade.setText(String.valueOf(quantidade));
                    }
                    adicionaisLocal.set(position, quantidade.toString());
                    calculaValores();
                }
            });

        }

        @Override
        public int getItemCount() {
            return opcinallista != null ? opcinallista.size() : 0;
        }
    }

    public void soma() {
        preco=0.0;
        if (adicionaisLocal != null){
            for (int i = 0; i < adicionaisLocal.size(); i++) {
                if (adicionaisLocal.get(i) != null && !adicionaisLocal.get(i).equals("0")) {
                    Integer qtdeunit = Integer.parseInt(adicionaisLocal.get(i));
                    Double valorunit = Double.parseDouble(adicionaislista.get(i).getVlr());
                    preco = preco + (qtdeunit * valorunit);
                }
            }
        }
    }

    public void calculaValores() {
        soma();
        Locale ptBr = new Locale("pt", "BR");
        String valorString = NumberFormat.getCurrencyInstance(ptBr).format(preco);
        txtValorTotal.setText(valorString);
    }
}

