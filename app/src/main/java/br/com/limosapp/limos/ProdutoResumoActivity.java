package br.com.limosapp.limos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.CardapioProdutosActivity.idproduto;
import static br.com.limosapp.limos.ProdutoActivity.adicionaislista;
import static br.com.limosapp.limos.ProdutoActivity.habilitaAdicionais;
import static br.com.limosapp.limos.ProdutoActivity.observacoes;
import static br.com.limosapp.limos.ProdutoActivity.opcionais;
import static br.com.limosapp.limos.ProdutoActivity.produto;
import static br.com.limosapp.limos.ProdutoActivity.quantidade;
import static br.com.limosapp.limos.ProdutoActivity.vlradicionais;
import static br.com.limosapp.limos.ProdutoActivity.vlropcionais;
import static br.com.limosapp.limos.ProdutoActivity.vlrunit;

public class ProdutoResumoActivity extends AppCompatActivity {

    SimpleDraweeView imgfotoproduto;
    ImageView imgvoltar, imgmais, imgmenos, imgtemobservacoes, imgtemadicionais;
    TextView txtproduto, txtserve, txttempo, txtpreco, txtquantidade, txtprecototal, txtcomplemento;
    Button btnitensadicionais, btnobservacoes, btnadicionarcarrinho;

    Locale ptBr = new Locale("pt", "BR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        // remove title

        setContentView(R.layout.activity_produto_resumo);

        inicializar();
        carregaDados();

        if (adicionaislista == null){
            btnitensadicionais.setEnabled(false);
            btnitensadicionais.setTextColor(getResources().getColor(R.color.cinza));
        }

        btnadicionarcarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProdutoActivity.adicionarCarrinho(ProdutoResumoActivity.this);
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(new Intent("Produto"));
                finish();
            }
        });
        imgmais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantidade++;
                txtquantidade.setText(quantidade.toString());
                calculaValores();
            }
        });
        imgmenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantidade> 1) {
                    quantidade--;
                    txtquantidade.setText(quantidade.toString());
                    calculaValores();
                }
            }
        });
        btnitensadicionais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProdutoResumoActivity.this, ProdutoAdicionaisActivity.class);
                startActivity(intent);
            }
        });
        btnobservacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentobservacoes = new Intent(ProdutoResumoActivity.this, ProdutoObservacoesActivity.class);
                startActivity(intentobservacoes);
            }
        });
        imgvoltar.setOnClickListener(new View.OnClickListener() {
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
        detalhesProduto();
        habilitaAdcionais();

        txtquantidade.setText(quantidade.toString());
        calculaValores();
        if (observacoes != null && observacoes.length()>0) {
            imgtemobservacoes.setVisibility(View.VISIBLE);
        } else {
            imgtemobservacoes.setVisibility(View.INVISIBLE);
        }

        imgtemadicionais.setVisibility(View.INVISIBLE);

        if (adicionaislista != null){
            for (int i = 0; i < adicionaislista.size(); i++) {
                if (adicionaislista.get(i).getQtde() != null && !adicionaislista.get(i).getQtde().equals("0")) {
                    imgtemadicionais.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    public void calculaValores() {
        Double precototal = (vlrunit + vlradicionais + vlropcionais) * quantidade;
        String valorString = NumberFormat.getCurrencyInstance(ptBr).format(precototal);
        txtprecototal.setText(valorString);
    }

    public void detalhesProduto() {
        String detalhe="";
        if (observacoes != null){detalhe = observacoes + "\n";}
        if (adicionaislista != null){
            for (int i = 0; i < adicionaislista.size(); i++) {
                if (adicionaislista.get(i).getQtde() != null && !adicionaislista.get(i).getQtde().equals("0")) {
                    if (detalhe.equals("")) {
                        detalhe = adicionaislista.get(i).getQtde() + "x " + adicionaislista.get(i).getNome();
                    } else {
                        detalhe = detalhe + "\n" + adicionaislista.get(i).getQtde() + "x " + adicionaislista.get(i).getNome();
                    }
                }
            }
        }
        if (opcionais != null){
            for (String[][] opcionai : opcionais) {
                for (String[] anOpcionai : opcionai) {
                    if (anOpcionai[3] != null && !anOpcionai[3].equals("0")) {
                        if (detalhe.equals("")) {
                            detalhe = anOpcionai[3] + "x " + anOpcionai[2];
                        } else {
                            detalhe = detalhe + "\n" + anOpcionai[3] + "x " + anOpcionai[2];
                        }
                    }
                }
            }
        }
        txtcomplemento.setText(detalhe);
    }

    private void habilitaAdcionais(){
        if (!habilitaAdicionais){
            btnitensadicionais.setEnabled(false);
            btnitensadicionais.setTextColor(getResources().getColor(R.color.cinza));
        }
    }

    private void inicializar(){
        imgfotoproduto = findViewById(R.id.imgFotoProduto);
        imgvoltar = findViewById(R.id.imgVoltar);
        imgmais = findViewById(R.id.imgMais);
        imgmenos = findViewById(R.id.imgMenos);
        imgtemobservacoes = findViewById(R.id.imgTemObservacoes);
        imgtemadicionais = findViewById(R.id.imgTemAdicionais);
        txtproduto = findViewById(R.id.txtProduto);
        txtcomplemento = findViewById(R.id.txtComplemento);
        txtquantidade = findViewById(R.id.txtQuantidade);
        txtprecototal = findViewById(R.id.txtPrecoTotal);
        btnitensadicionais = findViewById(R.id.btnItensAdicionaisCarrinho);
        btnobservacoes = findViewById(R.id.btnObservacoesCarrinho);
        btnadicionarcarrinho = findViewById(R.id.btnIncluirObservacoes);
        txtserve = findViewById(R.id.txtServe);
        txttempo = findViewById(R.id.txtTempo);
        txtpreco = findViewById(R.id.txtPreco);
    }

    private void carregaDados(){
        txtproduto.setText(produto);

        DatabaseReference dbproduto = FirebaseDatabase.getInstance().getReference() .child("produtos").child(idproduto);
        dbproduto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("foto").exists() && !dataSnapshot.child("foto").getValue().toString().isEmpty()) {
                    String urlfoto = dataSnapshot.child("foto").getValue().toString();
                    imgfotoproduto.setImageURI(urlfoto);
                }
                if (dataSnapshot.child("serve").exists() && !dataSnapshot.child("serve").getValue().toString().isEmpty()) txtserve.setText("Serve " + dataSnapshot.child("serve").getValue().toString() + " pessoa(s)");
                if (dataSnapshot.child("tempo").exists() && !dataSnapshot.child("tempo").getValue().toString().isEmpty()) txttempo.setText(dataSnapshot.child("tempo").getValue().toString());
                if (dataSnapshot.child("preco").exists() && !dataSnapshot.child("preco").getValue().toString().isEmpty()) {
                    double valor = Double.parseDouble(dataSnapshot.child("preco").getValue().toString());
                    String valorString = NumberFormat.getCurrencyInstance(ptBr).format(valor);
                    if (dataSnapshot.child("apartir").exists() && !dataSnapshot.child("apartir").getValue().toString().isEmpty() && dataSnapshot.child("apartir").getValue().toString().equals("1")) {
                        txtpreco.setText("A partir "+valorString);
                    }else{
                        txtpreco.setText(valorString);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}