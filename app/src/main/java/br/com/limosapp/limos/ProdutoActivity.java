package br.com.limosapp.limos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import br.com.limosapp.limos.pojos.AdicionaisLista;
import br.com.limosapp.limos.sqlite.ProdutosCarrinhoDAO;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.VerificaDiaSemana;
import br.com.limosapp.limos.util.VerificaFechado;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.CardapioProdutosActivity.idproduto;
import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;
import static br.com.limosapp.limos.MainActivity.enderecomain;
import static br.com.limosapp.limos.ProdutoOpcionaisActivity.grupo;

public class ProdutoActivity extends AppCompatActivity implements View.OnClickListener {

    SimpleDraweeView imgFotoProduto;
    ImageView  imgVoltar, imgMaisCarrinho, imgMenosCarrinho, imgTemObservacoes, imgTemAdicionais;
    TextView txtProduto, txtServe, txtTempo, txtPreco, txtDescProduto, txtQuantidadeCarrinho, txtPrecototal;
    Button btnItensAdicionaisCarrinho, btnObservacoesCarrinho, btnAdicionarCarrinho;

    String valorString;
    Boolean prox, aberto;
    ArrayList<String> grupoOpiArray = new ArrayList<>();
    ArrayList<String> adicionaisArray = new ArrayList<>();
    public static ArrayList<AdicionaisLista> adicionaislista = new ArrayList<>();
    Locale ptbr = new Locale("pt", "BR");

    public static String observacoes, produto;
    public static Double vlrunit, vlradicionais, vlropcionais;
    public static Integer quantidade;
    public static boolean habilitaAdicionais;
    public static String[][][] opcionais;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbproduto = db;
    DatabaseReference dbadicionais = db;
    DatabaseReference dbadicionais2 = db;
    DatabaseReference dbopicionais = db;
    DatabaseReference dbopicionais2 = db;

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
        setContentView(R.layout.activity_produto);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Produto"));

        inicializar();

        //Fecho a tela quando algum deles esta nulo por garantiaß
        if (idproduto == null || restaurantes == null) { finish(); }

        dbproduto = db.child("produtos").child(idproduto);
        dbadicionais = db.child("produtoadicionais").child(idproduto);
        dbopicionais = db.child("produtoopcionais").child(idproduto);
        dbadicionais2 = db.child("restauranteadicionais").child(restaurantes.getIdrestaurante());
        dbopicionais2 = db.child("restaurantegruposopcionais").child(restaurantes.getIdrestaurante());

        adicionaislista.clear();
        opcionais = null;
        observacoes = null;
        vlradicionais = 0.00;
        vlropcionais = 0.00;
        vlrunit = 0.00;
        quantidade = 1;
        habilitaAdicionais=true;

        imgMaisCarrinho.setOnClickListener(this);
        imgMenosCarrinho.setOnClickListener(this);
        btnItensAdicionaisCarrinho.setOnClickListener(this);
        btnObservacoesCarrinho.setOnClickListener(this);
        imgVoltar.setOnClickListener(this);

        carregaDados();

        btnAdicionarCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifiEntregaEndereco();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        new VerificaInternet().verificaConexao(this);

        txtQuantidadeCarrinho.setText(String.valueOf(quantidade));
        calculavalores();
        if (observacoes != null && observacoes.length()>0) {
            imgTemObservacoes.setVisibility(View.VISIBLE);
        } else {
            imgTemObservacoes.setVisibility(View.INVISIBLE);
        }

        imgTemAdicionais.setVisibility(View.INVISIBLE);
        if (adicionaislista != null){
            for (int i = 0; i < adicionaislista.size(); i++) {
                if (adicionaislista.get(i).getQtde() != null && !adicionaislista.get(i).getQtde().equals("0")) {
                    imgTemAdicionais.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        db.onDisconnect();
        dbproduto.onDisconnect();
        dbadicionais.onDisconnect();
        dbopicionais.onDisconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgMais:
                quantidade++;
                txtQuantidadeCarrinho.setText(String.valueOf(quantidade));
                calculavalores();
                break;
            case R.id.imgMenos:
                if (quantidade> 1) {
                    quantidade--;
                    txtQuantidadeCarrinho.setText(String.valueOf(quantidade));
                    calculavalores();
                }
                break;
            case R.id.btnItensAdicionaisCarrinho:
                Intent intent = new Intent(ProdutoActivity.this, ProdutoAdicionaisActivity.class);
                startActivity(intent);
                break;

            case R.id.btnObservacoesCarrinho:
                Intent intentobservacoes = new Intent(ProdutoActivity.this, ProdutoObservacoesActivity.class);
                startActivity(intentobservacoes);
                break;

            case R.id.imgVoltar:
                finish();
                break;
        }
    }

    public static void adicionarCarrinho(Activity activity){
        //pegando os valores
        Double valorunitario = (vlrunit + vlradicionais + vlropcionais);

        ProdutosCarrinhoDAO dao = new ProdutosCarrinhoDAO(activity.getApplicationContext());
        long idprodutoDAO = dao.salvar(restaurantes.getIdrestaurante(),  idproduto, produto , quantidade, valorunitario, observacoes);
        if(idprodutoDAO>0) {
            if (adicionaislista != null){
                for (int i = 0; i < adicionaislista.size(); i++) {
                    if (adicionaislista.get(i).getQtde() != null && !adicionaislista.get(i).getQtde().equals("0")) {
                        dao.salvarAdicionais(restaurantes.getIdrestaurante(), Long.toString(idprodutoDAO), idproduto, adicionaislista.get(i).getHash(), adicionaislista.get(i).getNome(), Integer.parseInt(adicionaislista.get(i).getQtde()), Double.parseDouble(adicionaislista.get(i).getVlr()));
                    }
                }
            }
            if (opcionais != null){
                for (String[][] opcionai : opcionais) {
                    for (String[] anOpcionai : opcionai) {
                        if (anOpcionai[3] != null && !anOpcionai[3].equals("0")) {
                            dao.salvarOpcionais(restaurantes.getIdrestaurante(), Long.toString(idprodutoDAO), idproduto, anOpcionai[1], anOpcionai[2], Integer.parseInt(anOpcionai[3]), Double.parseDouble(anOpcionai[4]));
                        }
                    }
                }
            }
            new Toast_layout(activity).mensagem("Produto adicionado ao carrinho");
        }else{
            new Toast_layout(activity).mensagem("Erro ao adicionar");
        }
    }

    public void calculavalores() {
        Double precototal = (vlrunit + vlradicionais + vlropcionais) * quantidade;
        valorString = NumberFormat.getCurrencyInstance(ptbr).format(precototal);
        txtPrecototal.setText(valorString);
    }

    private void inicializar(){
        imgFotoProduto = findViewById(R.id.imgFotoProduto);
        imgVoltar = findViewById(R.id.imgVoltar);
        imgMaisCarrinho = findViewById(R.id.imgMais);
        imgMenosCarrinho = findViewById(R.id.imgMenos);
        imgTemObservacoes = findViewById(R.id.imgTemObservacoes);
        imgTemAdicionais = findViewById(R.id.imgTemAdicionais);
        txtProduto = findViewById(R.id.txtProduto);
        txtServe = findViewById(R.id.txtServe);
        txtTempo = findViewById(R.id.txtTempo);
        txtPreco = findViewById(R.id.txtPreco);
        txtDescProduto = findViewById(R.id.txtDescProduto);
        txtQuantidadeCarrinho = findViewById(R.id.txtQuantidade);
        txtPrecototal = findViewById(R.id.txtPrecoTotal);
        btnItensAdicionaisCarrinho = findViewById(R.id.btnItensAdicionaisCarrinho);
        btnObservacoesCarrinho = findViewById(R.id.btnObservacoesCarrinho);
        btnAdicionarCarrinho = findViewById(R.id.btnIncluirObservacoes);
    }

    private void carregaDados(){
        dbproduto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("foto").exists() && !Objects.requireNonNull(dataSnapshot.child("foto").getValue()).toString().isEmpty()) {
                    String urlfoto = Objects.requireNonNull(dataSnapshot.child("foto").getValue()).toString();
                    imgFotoProduto.setImageURI(urlfoto);
                }
                if (dataSnapshot.child("produto").exists() && !Objects.requireNonNull(dataSnapshot.child("produto").getValue()).toString().isEmpty()) {
                    txtProduto.setText(Objects.requireNonNull(dataSnapshot.child("produto").getValue()).toString());
                    produto = Objects.requireNonNull(dataSnapshot.child("produto").getValue()).toString();
                }
                if (dataSnapshot.child("descricao").exists() && !Objects.requireNonNull(dataSnapshot.child("descricao").getValue()).toString().isEmpty()) {
                    txtDescProduto.setText(Objects.requireNonNull(dataSnapshot.child("descricao").getValue()).toString());
                }else{
                    txtDescProduto.setText("");
                }

                if (dataSnapshot.child("serve").exists() && !Objects.requireNonNull(dataSnapshot.child("serve").getValue()).toString().isEmpty()) txtServe.setText(getString(R.string.servepessoas, Objects.requireNonNull(dataSnapshot.child("serve").getValue()).toString()));
                if (dataSnapshot.child("tempo").exists() && !Objects.requireNonNull(dataSnapshot.child("tempo").getValue()).toString().isEmpty()) txtTempo.setText(Objects.requireNonNull(dataSnapshot.child("tempo").getValue()).toString());
                if (dataSnapshot.child("preco").exists() && !Objects.requireNonNull(dataSnapshot.child("preco").getValue()).toString().isEmpty()) {
                    double valor = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("preco").getValue()).toString());
                    valorString = NumberFormat.getCurrencyInstance(ptbr).format(valor);
                    if (dataSnapshot.child("apartir").exists() && !Objects.requireNonNull(dataSnapshot.child("apartir").getValue()).toString().isEmpty() && Objects.requireNonNull(dataSnapshot.child("apartir").getValue()).toString().equals("1")) {
                        vlrunit = 0.0;
                        txtPreco.setText(getString(R.string.apartir, valorString));
                    }else{
                        vlrunit = valor;
                        txtPreco.setText(valorString);
                    }
                    valorString = NumberFormat.getCurrencyInstance(ptbr).format(vlrunit);
                    txtPrecototal.setText(valorString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        carregaAdcionais();
        carregaOpcionais();
    }

    private void carregaAdcionais(){
        dbadicionais.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("")){
                    btnItensAdicionaisCarrinho.setEnabled(true);
                    adicionaisArray.clear();
                    adicionaislista.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) { //seto no array todos os grupos que achar no produto
                        adicionaisArray.add(Objects.requireNonNull(postSnapshot.getValue()).toString());
                    }
                    montaAdicionais();
                    habilitaAdicionais=true;
                }else{
                    btnItensAdicionaisCarrinho.setEnabled(false);
                    btnItensAdicionaisCarrinho.setTextColor(getResources().getColor(R.color.cinza));
                    habilitaAdicionais=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void montaAdicionais(){
        dbadicionais2.addListenerForSingleValueEvent(new ValueEventListener() { //nó restaurantegrupoopcionais
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("")) {
                    for (String grupoOpi : adicionaisArray) { //Varro o array de grupo
                        if (dataSnapshot.child(grupoOpi).exists() && dataSnapshot.child(grupoOpi).child("ativo").getValue().toString().equals("1")) {
                            AdicionaisLista addopcional = new AdicionaisLista(dataSnapshot.child(grupoOpi).child("adicional").getValue().toString(), dataSnapshot.child(grupoOpi).child("valor").getValue().toString(), "0", dataSnapshot.child(grupoOpi).child("valor").getKey());
                            adicionaislista.add(addopcional);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void carregaOpcionais(){
        dbopicionais.addListenerForSingleValueEvent(new ValueEventListener() { //nó produtoopcionais
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("")){
                    btnAdicionarCarrinho.setText(R.string.proximo);
                    prox = true;
                    grupoOpiArray.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) { //seto no array todos os grupos que achar no produto
                        grupoOpiArray.add(postSnapshot.getKey());
                    }
                    montaOpcionais();
                }else{
                    prox = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void montaOpcionais(){
        dbopicionais2.addListenerForSingleValueEvent(new ValueEventListener() { //nó restaurantegrupoopcionais
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("")) {
                    int contadorOp = 0;
                    for (String grupoOpi : grupoOpiArray) { //Varro o array de produto com grupo de opcionais que achei antes
                        int contadorOp2 = (int) (long) dataSnapshot.child(grupoOpi).getChildrenCount(); //Vejo se existe grupo no nó restaurante e conto quantos opcionais tem dentro
                        if (contadorOp2 > contadorOp) {
                            contadorOp = contadorOp2;
                        }
                    }
                    opcionais = new String[grupoOpiArray.size()][contadorOp][5]; //Seto os vetores para ver quantos grupos tenho e o maximo de opcionais
                    int contadorOp2 = 0, contadorOp3;
                    for (String grupoOpi : grupoOpiArray) { //Varro o array de grupo
                        contadorOp3 = 0;
                        for (DataSnapshot postSnapshot2 : dataSnapshot.child(grupoOpi).getChildren()) {
                            if (Objects.requireNonNull(postSnapshot2.child("ativo").getValue()).toString().equals("1")){
                                opcionais[contadorOp2][contadorOp3][0] = grupoOpi; //hashgrupo
                                opcionais[contadorOp2][contadorOp3][1] = Objects.requireNonNull(postSnapshot2.getKey()); //hashopcional
                                opcionais[contadorOp2][contadorOp3][2] = Objects.requireNonNull(postSnapshot2.child("nome").getValue()).toString(); //nome opcional
                                opcionais[contadorOp2][contadorOp3][4] = Objects.requireNonNull(postSnapshot2.child("valor").getValue()).toString(); //valor opcional
                                contadorOp3++;
                            }
                        }
                        contadorOp2++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verifiEntregaEndereco(){
        if (restaurantes.getEntregaEndereco()) {
            verificaAbertoFechado();
        }else{
            new Toast_layout(ProdutoActivity.this).mensagem(getString(R.string.naoentregaenredeco));
        }

    }

    private void verificaAbertoFechado(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = dateFormat.format(new Date());
        String diasemana = new VerificaDiaSemana().verifDiaSemana(dataFormatada);
        switch(diasemana){
            case "Domingo": diasemana = "dom";break;
            case "Segunda": diasemana = "seg";break;
            case "Terça": diasemana = "ter";break;
            case "Quarta": diasemana = "qua";break;
            case "Quinta": diasemana = "qui";break;
            case "Sexta": diasemana = "sex";break;
            case "Sábado": diasemana = "sab";break;
        }

        DatabaseReference dbhorarios = db.child("restaurantehorarios").child(restaurantes.getIdrestaurante()).child(diasemana);
        dbhorarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String abre = null, fecha = null, abre1 = null, fecha1 = null, abre2 = null, fecha2 = null;
                if (dataSnapshot.child("abre").exists() && !Objects.requireNonNull(dataSnapshot.child("abre").getValue()).toString().isEmpty()) abre = Objects.requireNonNull(dataSnapshot.child("abre").getValue()).toString();
                if (dataSnapshot.child("fecha").exists() && !Objects.requireNonNull(dataSnapshot.child("fecha").getValue()).toString().isEmpty()) fecha = Objects.requireNonNull(dataSnapshot.child("fecha").getValue()).toString();
                if (dataSnapshot.child("abre1").exists() && !Objects.requireNonNull(dataSnapshot.child("abre1").getValue()).toString().isEmpty()) abre1 = Objects.requireNonNull(dataSnapshot.child("abre1").getValue()).toString();
                if (dataSnapshot.child("fecha1").exists() && !Objects.requireNonNull(dataSnapshot.child("fecha1").getValue()).toString().isEmpty()) fecha1 = Objects.requireNonNull(dataSnapshot.child("fecha1").getValue()).toString();
                if (dataSnapshot.child("abre2").exists() && !Objects.requireNonNull(dataSnapshot.child("abre2").getValue()).toString().isEmpty()) abre2 = Objects.requireNonNull(dataSnapshot.child("abre2").getValue()).toString();
                if (dataSnapshot.child("fecha2").exists() && !Objects.requireNonNull(dataSnapshot.child("fecha2").getValue()).toString().isEmpty()) fecha2 = Objects.requireNonNull(dataSnapshot.child("fecha2").getValue()).toString();
                aberto = false;
                VerificaFechado verificacao = new VerificaFechado();
                if (abre != null && fecha != null) {
                    if (verificacao.verificaEstaFechado(abre, fecha)) {
                        if (abre1 != null && fecha1 != null) {
                            if (verificacao.verificaEstaFechado(abre1, fecha1)) {
                                if (abre2 != null && fecha2 != null) {
                                    if (!verificacao.verificaEstaFechado(abre2, fecha2)) {
                                        aberto = true;
                                    }
                                }
                            } else {
                                aberto = true;
                            }
                        }
                    } else {
                        aberto = true;
                    }
                }
                if (aberto){
                    if (prox){
                        Intent intent = new Intent(ProdutoActivity.this, ProdutoOpcionaisActivity.class);
                        startActivity(intent);
                    }else{
                        adicionarCarrinho(ProdutoActivity.this);
                        finish();
                    }
                }else{
                    new Toast_layout(ProdutoActivity.this).mensagem(getString(R.string.restaurantefechado));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}