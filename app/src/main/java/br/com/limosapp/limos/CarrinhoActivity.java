package br.com.limosapp.limos;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import br.com.limosapp.limos.classes.EnviarPedido;
import br.com.limosapp.limos.firebase.CuponsFirebase;
import br.com.limosapp.limos.firebase.EnderecosFirebase;
import br.com.limosapp.limos.holder.BrindesViewHolder;
import br.com.limosapp.limos.holder.CuponsViewHolder;
import br.com.limosapp.limos.holder.EnderecosViewHolder;
import br.com.limosapp.limos.holder.FormasPagViewHolder;
import br.com.limosapp.limos.holder.ProdutosCarrinhoHolder;
import br.com.limosapp.limos.sqlite.ProdutosCarrinho;
import br.com.limosapp.limos.sqlite.ProdutosCarrinhoDAO;
import br.com.limosapp.limos.util.CalcularDistancia;
import br.com.limosapp.limos.util.Mascara;
import br.com.limosapp.limos.util.MascaraCnpjCpf;
import br.com.limosapp.limos.util.MascaraMonetaria;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.ValidarCampos;
import br.com.limosapp.limos.util.VerificaCoordenadas;
import br.com.limosapp.limos.util.VerificaDiaSemana;
import br.com.limosapp.limos.util.VerificaFechado;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;
import static br.com.limosapp.limos.MainActivity.enderecomain;
import static br.com.limosapp.limos.MainActivity.idusuario;

public class CarrinhoActivity extends AppCompatActivity {

    TextView txtTitulo;
    ImageView imgVoltar;
    SimpleDraweeView imgCash, imgEndereco, imgBrinde, imgCupom;
    RecyclerView rvCarrinho, rvbrindes, rvcupons, rvpagamento, rvendereco;
    ProdutosCarrinhoAdapter adapter;
    TextView  txtRestaurante, txtTempo, txtMaisItens, txtValorProdutos;
    //Cards
    TextView  txtCashback, txtTrocarCash, txtTrocarcupom, txtcupons, txtBrinde, txtTrocarBrinde, txtTrocarendereco, txtEndereco;
    //Pagamento
    TextView txtTrocarPagamento, txtTrocarCpf, txtCpf, txtPagamento, txtTituloPagamento;
    //Valores
    TextView txtValorCash, txtValorDesconto, txtValorFrete, txtValorTotal, txtValorProdutosfinal;
    //DialogCupom
    TextView txtTextoCupom;
    Button btnEnviarPedido;
    RatingBar ratingBar;
    SimpleDraweeView imgFotoPerfil, imgFotoCapa;
    CardView cardViewBrinde, cardViewCupom, cardViewFrete, cardViewCash;

    Double vlrmin=0.0, saldocash, valorfrete=0.0, valorprodutos=0.0, valorpedido=0.0, cashutilizado=0.0, valordesconto=0.0, troco=0.0;
    String hashcupom, cpfusuario, endereco, numero, bairro, cidade, estado, complemento, cep, nomeusuario, telefoneusuario, emailusuario;
    Integer desconto=0, faixautilizada, retirabalcao=0, faixaverific;
    Boolean enderecoConf=false, pagamentoConf=false, aberto;
    public String[][] arrayfaixa, arrayprodutos;
    String[] brinde;
    
    AlertDialog alertDialog;
    AlertDialog.Builder mbuilder;
    Locale ptBr = new Locale("pt", "BR");    
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbproduto = db;
    DatabaseReference dbrestaurante = db.child("restaurantes").child(restaurantes.getIdrestaurante());
    DatabaseReference dbusuario = db.child("usuarios").child(idusuario);
    DatabaseReference dbhorarios = db;
    DatabaseReference dbbrindesdialog = db;
    DatabaseReference dbformaspag = dbrestaurante.child("pagamentos");
    DatabaseReference dbendereco = db.child("usuarioenderecos").child(idusuario);

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String dataFormatada = dateFormat.format(new Date());

    /*private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_carrinho);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_carrinho);

        brinde = new String[2];

        configurarRecycler();
        carregaDadosRestaurante();
        carregaEndereco();
        carregaUsuario();
        criaArray();

        mbuilder = new AlertDialog.Builder(CarrinhoActivity.this);

        imgVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtMaisItens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cardViewBrinde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (faixautilizada!=null && arrayfaixa != null){ dialogBrindes(); }
            }
        });
        cardViewCupom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCupons();
            }
        });

        cardViewCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saldocash>0.00){
                    dialogCash();
                }
            }
        });

        txtTrocarPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPag();
            }
        });
        txtTrocarCpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCpf();
            }
        });
        cardViewFrete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEndereco();
            }
        });
        btnEnviarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayfaixa != null && faixautilizada != null && brinde[0] == null) {
                    dialogBrindes();
                }else if (!enderecoConf) {
                    dialogEndereco();
                }else if (!pagamentoConf && valorpedido > 0.0){
                    dialogPag();
                }else if (telefoneusuario == null){
                    dialogTel();
                }else{
                    verificaAbertoFechado();
                }
            }
        });

        //LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Carrinho"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);

        calculaprodutos();
        verificaBrindes();
        verificaObrigatorio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.onDisconnect();
        dbproduto.onDisconnect();
        dbrestaurante.onDisconnect();
        dbusuario.onDisconnect();
        dbhorarios.onDisconnect();
        dbbrindesdialog.onDisconnect();
        dbformaspag.onDisconnect();
        dbendereco.onDisconnect();
    }

    public void calculaprodutos() {
        valorprodutos=0.0;
        if (arrayprodutos != null){
            for (String[] arrayproduto : arrayprodutos) {
                if (arrayproduto != null) {
                    if (arrayproduto[2] != null && !arrayproduto[2].equals("0")) {
                        Integer qtdeunit = Integer.parseInt(arrayproduto[2]);
                        Double valorunit = Double.parseDouble(arrayproduto[3]);
                        valorprodutos = valorprodutos + (qtdeunit * valorunit);
                    }
                }
            }
        }
        verificaFaixa();
        calculapedido();
        txtValorProdutos.setText(NumberFormat.getCurrencyInstance(ptBr).format(valorprodutos));
        txtValorProdutosfinal.setText(NumberFormat.getCurrencyInstance(ptBr).format(valorprodutos));
        verificaObrigatorio();
    }

    public void calculapedido() {
        valordesconto = (valorprodutos * desconto)/100;
        valorpedido = (valorprodutos + valorfrete) - valordesconto - cashutilizado;

        if (valorpedido == 0.0){
            txtTituloPagamento.setText(R.string.semformapagamento);
            txtPagamento.setVisibility(View.INVISIBLE);
            txtTrocarPagamento.setVisibility(View.INVISIBLE);
            troco=0.0;
        }else{
            if (valorpedido < 0.0) {
                valorpedido = valorpedido + cashutilizado;
                cashutilizado = 0.0;
                txtTrocarCash.setText(R.string.utilizar);
                txtCashback.setText(getString(R.string.usarsaldocashback, NumberFormat.getCurrencyInstance(ptBr).format(saldocash)));

                new Toast_layout(this).mensagem(getString(R.string.revisarchashback));
            }
            if (troco > 0.0 && troco < valorpedido) {
                txtTituloPagamento.setText(R.string.formapagamento);
                txtPagamento.setVisibility(View.INVISIBLE);
                txtTrocarPagamento.setText(R.string.escolher);
                troco=0.0;
                new Toast_layout(this).mensagem(getString(R.string.revisartroco));
            }
            if (txtTrocarPagamento.getVisibility() == View.INVISIBLE) {
                txtTituloPagamento.setText(R.string.formapagamento);
                txtTrocarPagamento.setVisibility(View.VISIBLE);
                txtTrocarPagamento.setText(R.string.escolher);
            }
        }

        txtValorCash.setText(NumberFormat.getCurrencyInstance(ptBr).format(cashutilizado));
        txtValorFrete.setText(NumberFormat.getCurrencyInstance(ptBr).format(valorfrete));
        txtValorDesconto.setText(NumberFormat.getCurrencyInstance(ptBr).format(valordesconto));
        txtValorTotal.setText(NumberFormat.getCurrencyInstance(ptBr).format(valorpedido));
    }

    private void verificaFaixa(){
        faixaverific = null;
        // verifica se a existe alguma faixa no array e se o valor se encontra na faixa se sim ele seta a variavel faixaverific
        if (arrayfaixa != null) {
            for (int x = 0; x < arrayfaixa.length; x++) {
                if (arrayfaixa[x][1] != null) {
                    Double valorfaixa = Double.parseDouble(arrayfaixa[x][1]);
                    if (valorprodutos >= valorfaixa){
                        faixaverific=x;
                    }
                }
            }

            if (faixaverific == null){
                txtBrinde.setText(getString(R.string.ganhebrinde, NumberFormat.getCurrencyInstance(ptBr).format(Double.parseDouble(arrayfaixa[0][1]))));
                txtTrocarBrinde.setVisibility(View.GONE);
                faixautilizada=null; //faixa utilizada
            }else{
                if (faixautilizada == null){ //se não tinha nenhuma faixa selecionada antes ele seta a que encontrou no array
                    atualizaBrinde();
                }else if(!faixautilizada.equals(faixaverific)){ //se a faixa utilizada for igual a faixa que encontrou no array não precisa setar de novo
                    atualizaBrinde();
                }
            }
        }
    }

    private void atualizaBrinde() {
        new Toast_layout(this).mensagem(getString(R.string.ganhoubrinde, arrayfaixa[faixaverific][1].replace(".",",")));
        faixautilizada=faixaverific;
        brinde = new String[2];
        txtTrocarBrinde.setText(R.string.escolher);
        txtBrinde.setText(getString(R.string.ganhoubrinde, NumberFormat.getCurrencyInstance(ptBr).format(Double.parseDouble(arrayfaixa[faixautilizada][1]))));
        txtTrocarBrinde.setVisibility(View.VISIBLE);
    }

    private void verificaObrigatorio() {
        if (valorprodutos < vlrmin && vlrmin!=0.0) {
            btnEnviarPedido.setText(getString(R.string.valorpedidominimo, NumberFormat.getCurrencyInstance(ptBr).format(vlrmin)));
            btnEnviarPedido.setEnabled(false);
            btnEnviarPedido.setTextColor(getResources().getColor(R.color.cinza));
            btnEnviarPedido.setBackground(getResources().getDrawable(R.drawable.botalogincinza));
        } else {
            btnEnviarPedido.setEnabled(true);
            btnEnviarPedido.setTextColor(getResources().getColor(R.color.branco));
            btnEnviarPedido.setBackground(getResources().getDrawable(R.drawable.botaologinvermelho));
            if (arrayfaixa != null && faixautilizada != null && brinde[0] == null) {
                btnEnviarPedido.setText(R.string.escolhabrinde);
            } else if (!enderecoConf) {
                btnEnviarPedido.setText(R.string.escolhalocalentrega);
            } else if (!pagamentoConf && valorpedido > 0.0) {
                btnEnviarPedido.setText(R.string.escolhaformapagamento);
            } else if (telefoneusuario == null) {
                btnEnviarPedido.setText(R.string.informetelefone);
            } else {
                btnEnviarPedido.setText(R.string.enviarpedido);
            }
        }
    }

    public void criaArray() {
        ProdutosCarrinhoDAO dao = new ProdutosCarrinhoDAO(this);
        arrayprodutos = dao.arrayProdutos();
    }

    public void inicializar(){
        //inicio e dados restaurante
        txtTitulo = findViewById(R.id.txtTitulo);
        imgVoltar = findViewById(R.id.imgVoltar);
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        imgFotoCapa = findViewById(R.id.imgFotoCapa);
        txtRestaurante = findViewById(R.id.txtRestaurante);
        ratingBar = findViewById(R.id.ratingBar);
        txtTempo = findViewById(R.id.txtTempo);
        //Produtos
        txtValorProdutos = findViewById(R.id.txtValorProdutos);
        txtMaisItens = findViewById(R.id.txtMaisItens);
        //Brindes
        cardViewBrinde = findViewById(R.id.cardViewBrinde);
        txtBrinde = findViewById(R.id.txtBrinde);
        txtTrocarBrinde = findViewById(R.id.txtTrocarBrinde);
        //Desconto
        cardViewCupom = findViewById(R.id.cardViewCupom);
        txtcupons = findViewById(R.id.txtCupom);
        txtTrocarcupom = findViewById(R.id.txtTrocarCupom);
        //Frete
        cardViewFrete = findViewById(R.id.cardViewFrete);
        txtTrocarendereco = findViewById(R.id.txtTrocarEndereco);
        txtEndereco = findViewById(R.id.txtEndereco);
        //Cashback
        cardViewCash = findViewById(R.id.cardViewCash);
        txtCashback = findViewById(R.id.txtCashback);
        txtTrocarCash = findViewById(R.id.txtTrocarCash);
        //Pagamento
        txtPagamento = findViewById(R.id.txtPagamento);
        txtTrocarPagamento = findViewById(R.id.txtTrocarPagamento);
        txtCpf = findViewById(R.id.txtCpf);
        txtTrocarCpf = findViewById(R.id.txtTrocarCpf);
        txtTituloPagamento = findViewById(R.id.txtTituloPagamento);
        //Final
        txtValorProdutosfinal = findViewById(R.id.txtValorProdutosFinal);
        txtValorCash = findViewById(R.id.txtValorCash);
        txtValorDesconto = findViewById(R.id.txtValorDesconto);
        txtValorFrete = findViewById(R.id.txtValorFrete);
        txtValorTotal = findViewById(R.id.txtValorTotal);
        btnEnviarPedido = findViewById(R.id.btnEnviarPedido);

        imgBrinde = findViewById(R.id.imgBrinde);
        imgCupom = findViewById(R.id.imgCupom);
        imgCash = findViewById(R.id.imgCash);
        imgEndereco = findViewById(R.id.imgEndereco);
    }

    private void configurarRecycler() {
        // Configurando o gerenciador de layout para ser uma lista.
        rvCarrinho = findViewById(R.id.rvCarrinho);
        rvCarrinho.setLayoutManager(new LinearLayoutManager(this));
        rvCarrinho.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Adiciona o adapter que irá anexar os objetos à lista.
        ProdutosCarrinhoDAO dao = new ProdutosCarrinhoDAO(this);
        adapter = new ProdutosCarrinhoAdapter(dao.retornaProdutosCarrinho());
        rvCarrinho.setAdapter(adapter);
    }

    // DADOS DO FIREBASE
    private void carregaDadosRestaurante(){
        dbrestaurante.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fotoperfil").exists() && !Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString().isEmpty()) { imgFotoPerfil.setImageURI(Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString()); }
                if (dataSnapshot.child("fotocapa").exists() && !Objects.requireNonNull(dataSnapshot.child("fotocapa").getValue()).toString().isEmpty()) { imgFotoCapa.setImageURI(Objects.requireNonNull(dataSnapshot.child("fotocapa").getValue()).toString()); }
                if (dataSnapshot.child("fantasia").exists() && !Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString().isEmpty()) txtRestaurante.setText(Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString());
                if (dataSnapshot.child("espera").exists() && !Objects.requireNonNull(dataSnapshot.child("espera").getValue()).toString().isEmpty()) txtTempo.setText(Objects.requireNonNull(dataSnapshot.child("espera").getValue()).toString());
                if (dataSnapshot.child("mediaavaliacao").exists() && !Objects.requireNonNull(dataSnapshot.child("mediaavaliacao").getValue()).toString().isEmpty()){ ratingBar.setRating(Float.parseFloat(String.valueOf(Objects.requireNonNull(dataSnapshot.child("mediaavaliacao").getValue()).toString()))); }
                if (dataSnapshot.child("retirabalcao").exists() && !Objects.requireNonNull(dataSnapshot.child("retirabalcao").getValue()).toString().isEmpty()){ retirabalcao = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("retirabalcao").getValue()).toString()); }
                if (dataSnapshot.child("valor_minimo_pedido").exists() && !Objects.requireNonNull(dataSnapshot.child("valor_minimo_pedido").getValue()).toString().isEmpty()){
                    vlrmin = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("valor_minimo_pedido").getValue()).toString()) ;
                    if (vlrmin>0.0){ verificaObrigatorio();}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verificaBrindes(){
        Query dbbrindes = db.child("restaurantebrindes").child(restaurantes.getIdrestaurante()).orderByChild("faixa");
        dbbrindes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("")){
                    int contadorbrinde=0;
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        if (postSnapshot.child("produtos").exists()) {
                            contadorbrinde++;
                        }
                    }
                    if (contadorbrinde==0){
                        esconderBrinde();
                    }else{
                        arrayfaixa = new String[contadorbrinde][2];

                        contadorbrinde =0;
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            if (postSnapshot.child("faixa").exists() && !Objects.requireNonNull(postSnapshot.child("faixa").getValue()).toString().isEmpty() && postSnapshot.child("produtos").exists()) {
                                arrayfaixa[contadorbrinde][0] = postSnapshot.getKey();
                                arrayfaixa[contadorbrinde][1] = Objects.requireNonNull(postSnapshot.child("faixa").getValue()).toString();
                                contadorbrinde++;
                            }
                        }
                        imgBrinde.setActualImageResource(R.drawable.brindes);
                        verificaFaixa();
                        verificaObrigatorio();
                    }
                }else{
                    esconderBrinde();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void esconderBrinde(){
        cardViewBrinde.setVisibility((View.GONE));
        arrayfaixa=null;
    }

    private void carregaUsuario(){
        dbusuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("cashbacksaldo").exists() && !Objects.requireNonNull(dataSnapshot.child("cashbacksaldo").getValue()).toString().isEmpty()) {
                    saldocash = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("cashbacksaldo").getValue()).toString());
                    if (saldocash==0.0){
                        txtCashback.setText(R.string.cashbackzerado);
                        txtTrocarCash.setVisibility(View.GONE);
                    }else{
                        txtCashback.setText(getString(R.string.usarsaldocashback, NumberFormat.getCurrencyInstance(ptBr).format(saldocash)));
                        txtTrocarCash.setVisibility(View.VISIBLE);
                    }

                }
                if (dataSnapshot.child("nome").exists() && !Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString().isEmpty()) { nomeusuario = Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString(); }
                if (dataSnapshot.child("email").exists() && !Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString().isEmpty()) { emailusuario = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString(); }
                if (dataSnapshot.child("telefone").exists() && !Objects.requireNonNull(dataSnapshot.child("telefone").getValue()).toString().isEmpty()) { telefoneusuario = Objects.requireNonNull(dataSnapshot.child("telefone").getValue()).toString(); }
                if (dataSnapshot.child("cpf").exists() && !Objects.requireNonNull(dataSnapshot.child("cpf").getValue()).toString().isEmpty()) { cpfusuario = Objects.requireNonNull(dataSnapshot.child("cpf").getValue()).toString(); }

                imgEndereco.setActualImageResource(R.drawable.endereco);
                imgCash.setActualImageResource(R.drawable.cashback2);
                imgCupom.setActualImageResource(R.drawable.cupons);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void carregaEndereco(){
        endereco = enderecomain.getEndereco();
        numero = enderecomain.getNumero();
        bairro = enderecomain.getBairro();
        complemento = enderecomain.getComplemento();
        cidade = enderecomain.getCidade();
        estado = enderecomain.getEstado();
        cep = enderecomain.getCep();
        enderecoConf=false;
        if (restaurantes.getEntregaEndereco()){
            valorfrete = restaurantes.getFrete();
            if (numero != null){
                if (!complemento.equals("")) {
                    txtEndereco.setText(getString(R.string.enderecocomcomplemento, endereco, numero, complemento, bairro, cidade, estado));
                }else {
                    txtEndereco.setText(getString(R.string.enderecosemcomplemento, endereco, numero, bairro, cidade, estado));
                }
                enderecoConf=true;
            }else{
                txtEndereco.setText(getString(R.string.proximode, bairro));
            }
            txtTrocarendereco.setText(R.string.trocar);
            txtEndereco.setVisibility(View.VISIBLE);
            calculapedido();
            verificaObrigatorio();
        }
    }

    private void verificaAbertoFechado(){
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

        dbhorarios = db.child("restaurantehorarios").child(restaurantes.getIdrestaurante()).child(diasemana);
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
                    enviarPedido();
                }else{
                    new Toast_layout(CarrinhoActivity.this).mensagem("O restaurante esta fechado.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enviarPedido(){
        new EnviarPedido(CarrinhoActivity.this, numero, hashcupom, endereco, bairro, cep, cidade, complemento, estado, emailusuario, txtRestaurante.getText().toString(),txtPagamento.getText().toString(),nomeusuario,telefoneusuario,txtCpf.getText().toString(),valorfrete,valorprodutos,cashutilizado,valordesconto,valorpedido-valorfrete, valorpedido, txtEndereco.getText().toString(), arrayprodutos, brinde[1]).salvarNumeroPedido();
        Intent intent = new Intent(CarrinhoActivity.this, ConcluirActivity.class);
        Bundle bundle = new Bundle();
        if (cashutilizado==0.0){
            bundle.putString("valorcash", NumberFormat.getCurrencyInstance(ptBr).format((valorpedido-valorfrete)*0.03));
        }else{
            bundle.putString("valorcash", null);
        }
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    //DIALOGS
    private void dialogBrindes(){
        if (alertDialog == null) {
            mbuilder = new AlertDialog.Builder(CarrinhoActivity.this);
            View mview = getLayoutInflater().inflate(R.layout.dialog_brindes, (ViewGroup) findViewById(R.id.dialog_brindes_root));
            mbuilder.setView(mview);
            alertDialog = mbuilder.create();
            alertDialog.show();

            rvbrindes = mview.findViewById(R.id.rvBrindes);
            ImageView imgfecharbrinde = mview.findViewById(R.id.imgFechar);

            carregaListaDiologBrindes();

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog=null;
                }
            });

            imgfecharbrinde.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
            });
        }
    }

    private void dialogCupons(){
        if (alertDialog == null){
            View mview = getLayoutInflater().inflate(R.layout.dialog_cupons, (ViewGroup) findViewById(R.id.dialog_cupons_root));
            mbuilder.setView(mview);
            alertDialog = mbuilder.create();
            alertDialog.show();
            rvcupons = mview.findViewById(R.id.rvCupons);
            ImageView imgfecharcupom = mview.findViewById(R.id.imgFechar);
            txtTextoCupom = mview.findViewById(R.id.txtTextoCupom);
            carregaListaDiologCupons();

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog=null;
                }
            });

            imgfecharcupom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    alertDialog=null;
                }
            });
        }
    }

    private void dialogCash(){
        if (alertDialog == null) {
            View mview = getLayoutInflater().inflate(R.layout.dialog_cashback, (ViewGroup) findViewById(R.id.dialog_cashback_root));
            mbuilder.setView(mview);
            alertDialog = mbuilder.create();
            alertDialog.show();

            TextView txtsaldocash = mview.findViewById(R.id.txtSaldo);
            TextView txtValorTotalcash = mview.findViewById(R.id.txtValorPedido);
            final EditText edtDescontoCashback = mview.findViewById(R.id.edtDescontoCashback);
            Button btnsalvarcash = mview.findViewById(R.id.btnSalvar);
            ImageView imgfecharcash = mview.findViewById(R.id.imgFechar);

            final Double cashMaispedido = valorpedido + cashutilizado;

            txtsaldocash.setText(getString(R.string.seusaldocashback, NumberFormat.getCurrencyInstance(ptBr).format(saldocash)));
            txtValorTotalcash.setText(getString(R.string.valorpedido, NumberFormat.getCurrencyInstance(ptBr).format(cashMaispedido)));
            if (cashutilizado > 0.0) {
                edtDescontoCashback.setText(NumberFormat.getCurrencyInstance(ptBr).format(cashutilizado));
            }
            edtDescontoCashback.addTextChangedListener(new MascaraMonetaria(edtDescontoCashback, ptBr));

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog=null;
                }
            });

            imgfecharcash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    alertDialog=null;
                }
            });

            btnsalvarcash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String strpreco = edtDescontoCashback.getText().toString().replaceAll("[^0-9-,]*", "");
                    strpreco = strpreco.replaceAll(",", ".");
                    Double cash = Double.parseDouble(strpreco);
                    if (cash > saldocash) { //verifica se utilizado é maior que o saldo
                        new Toast_layout(CarrinhoActivity.this).mensagem(getString(R.string.cashbackmaiorsaldo));
                    } else if (cash > cashMaispedido) {
                        new Toast_layout(CarrinhoActivity.this).mensagem(getString(R.string.cashbackmaiorpedido));
                    } else {
                        cashutilizado = cash; //pega o número
                        txtCashback.setText(getString(R.string.utilizandocashback, NumberFormat.getCurrencyInstance(ptBr).format(cashutilizado)));
                        txtTrocarCash.setText(R.string.trocar);
                        calculapedido();
                        verificaObrigatorio();
                        alertDialog.dismiss();
                        alertDialog=null;
                    }
                }
            });
        }
    }

    private void dialogPag(){
        if (alertDialog == null) {
            View mview = getLayoutInflater().inflate(R.layout.dialog_pagto, (ViewGroup) findViewById(R.id.dialog_pagto_root));
            mbuilder.setView(mview);
            alertDialog = mbuilder.create();
            alertDialog.show();

            rvpagamento = mview.findViewById(R.id.rvPagto);
            ImageView imgfecharpagto = mview.findViewById(R.id.imgFechar);

            CarregaFormaPag();

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog=null;
                }
            });

            imgfecharpagto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    alertDialog=null;
                }
            });
        }
    }

    private void dialogDinheiro(){
        if (alertDialog == null) {
            View mview = getLayoutInflater().inflate(R.layout.dialog_dinheiro, (ViewGroup) findViewById(R.id.dialog_dinheiro_root));
            mbuilder.setView(mview);
            alertDialog = mbuilder.create();
            alertDialog.show();

            TextView txtvlrpedidodinheiro = mview.findViewById(R.id.txtValorPedido);
            final EditText edtDescontoCashback = mview.findViewById(R.id.edtDinheiro);
            Button btnsalvardinheiro = mview.findViewById(R.id.btnSalvar);

            txtvlrpedidodinheiro.setText(getString(R.string.valorpedido, NumberFormat.getCurrencyInstance(ptBr).format(valorpedido)));
            edtDescontoCashback.addTextChangedListener(new MascaraMonetaria(edtDescontoCashback, ptBr));

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog=null;
                }
            });
            btnsalvardinheiro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String strpreco = edtDescontoCashback.getText().toString().replaceAll("[^0-9-,]*", "");
                    strpreco = strpreco.replaceAll(",", ".");
                    Double vlrtroco = Double.parseDouble(strpreco);
                    if (vlrtroco <= valorpedido) { //verifica se p troco é menor que o pedido
                        new Toast_layout(CarrinhoActivity.this).mensagem("Você deve pedir troco para um valor maior que " + NumberFormat.getCurrencyInstance(ptBr).format(valorpedido));
                    } else {
                        troco = vlrtroco;
                        txtPagamento.setText(getString(R.string.trocopara, NumberFormat.getCurrencyInstance(ptBr).format(troco)));
                        txtTituloPagamento.setText(R.string.dinheiro);
                        pagamentoConf = true;
                        verificaObrigatorio();
                        alertDialog.dismiss();
                        alertDialog=null;
                    }
                }
            });
        }
    }

    private void dialogCpf(){
        if (alertDialog == null) {
            View mview = getLayoutInflater().inflate(R.layout.dialog_cpf, (ViewGroup) findViewById(R.id.dialog_cpf_root));
            mbuilder.setView(mview);
            alertDialog = mbuilder.create();
            alertDialog.show();

            Button btnsalvarcpf = mview.findViewById(R.id.btnSalvar);
            ImageView imgfecharcpf = mview.findViewById(R.id.imgFechar);
            final EditText editcpf = mview.findViewById(R.id.edtCPF);

            editcpf.addTextChangedListener(new MascaraCnpjCpf().insert(editcpf));
            if (cpfusuario != null) {
                editcpf.setText(cpfusuario);
            }

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog=null;
                }
            });

            imgfecharcpf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    alertDialog=null;
                }
            });

            btnsalvarcpf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editcpf.getText().length() > 0) {
                        if (editcpf.getText().length() <= 14) {
                            boolean cpf_valido = new ValidarCampos().validarCPF(editcpf.getText().toString());
                            if (!cpf_valido) {
                                editcpf.setError(getString(R.string.cpfinvalido));
                                editcpf.setFocusable(true);
                                editcpf.requestFocus();
                                return;
                            }
                        } else {
                            boolean cnpj_valido = new ValidarCampos().validarCNPJ(editcpf.getText().toString());
                            if (!cnpj_valido) {
                                editcpf.setError(getString(R.string.cnpjinvalido));
                                editcpf.setFocusable(true);
                                editcpf.requestFocus();
                                return;
                            }
                        }
                        txtCpf.setText(editcpf.getText().toString());
                        txtCpf.setVisibility(View.VISIBLE);
                        txtTrocarCpf.setText(R.string.trocar);
                    } else {
                        txtCpf.setText("");
                        txtCpf.setVisibility(View.INVISIBLE);
                        txtTrocarCpf.setText(R.string.adicionar);
                    }
                    alertDialog.dismiss();
                    alertDialog=null;
                }
            });
        }
    }

    private void dialogTel(){
        if (alertDialog == null) {
            View mview = getLayoutInflater().inflate(R.layout.dialog_telefone, (ViewGroup) findViewById(R.id.dialog_tel_root));
            mbuilder.setView(mview);
            alertDialog = mbuilder.create();
            alertDialog.show();

            Button btnSalvarTel = mview.findViewById(R.id.btnSalvar);
            ImageView imgFecharTel = mview.findViewById(R.id.imgFechar);
            final EditText editTel = mview.findViewById(R.id.edtTel);

            editTel.addTextChangedListener(new Mascara().insert("(##)#####-####", editTel));

            imgFecharTel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    alertDialog=null;
                }
            });

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog=null;
                }
            });

            btnSalvarTel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editTel.getText().length() != 0) {
                        String telefone = editTel.getText().toString();
                        telefone = telefone.replace("(", "");
                        telefone = telefone.replace(")", "");
                        telefone = telefone.replace("-", "");
                        ValidarCampos validarCampos = new ValidarCampos();
                        boolean telefone_valido = validarCampos.validarTelefone(telefone);
                        if (!telefone_valido) {
                            editTel.setError("Número de telefone inválido");
                            editTel.setFocusable(true);
                            editTel.requestFocus();
                            return;
                        }
                        telefoneusuario = editTel.getText().toString();
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("telefone", telefoneusuario);
                        FirebaseDatabase.getInstance().getReference().child("usuarios").child(idusuario).updateChildren(taskMap);
                        verificaObrigatorio();
                        alertDialog.dismiss();
                        alertDialog=null;
                    } else {
                        editTel.setError("Informe seu número de telefone");
                    }

                }
            });
        }
    }

    private void dialogEndereco(){
        if (alertDialog == null) {
            View mview = getLayoutInflater().inflate(R.layout.dialog_endereco, (ViewGroup) findViewById(R.id.dialog_endereco_root));
            mbuilder.setView(mview);
            alertDialog = mbuilder.create();
            alertDialog.show();

            rvendereco = mview.findViewById(R.id.rvEndereco);
            ImageView imgfecharendereco = mview.findViewById(R.id.imgFechar);
            CardView cardViewBalcao = mview.findViewById(R.id.cardViewBalcao);
            CardView cardViewNovoEndereco = mview.findViewById(R.id.cardViewNovoEndereco);
            if (retirabalcao == 0) {
                cardViewBalcao.setVisibility(View.GONE);
            }
            carregaListaEndereco();

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog=null;
                }
            });

            imgfecharendereco.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    alertDialog=null;
                }
            });
            cardViewNovoEndereco.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CarrinhoActivity.this, EnderecoNovoActivity.class);
                    startActivity(intent);
                }
            });
            cardViewBalcao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    endereco = "";
                    complemento = "";
                    numero = "";
                    bairro = "";
                    cidade = "";
                    cep = "";
                    estado = "";
                    valorfrete = 0.0;
                    txtEndereco.setText(R.string.retiradabalcao);
                    txtEndereco.setVisibility(View.VISIBLE);
                    enderecoConf = true;
                    alertDialog.dismiss();
                    alertDialog=null;
                    calculapedido();
                    verificaObrigatorio();
                }
            });
        }
    }

    //Carrega listas
    private void carregaListaDiologBrindes(){
        dbbrindesdialog = db.child("restaurantebrindes").child(restaurantes.getIdrestaurante()).child(arrayfaixa[faixautilizada][0]).child("produtos");
        dbbrindesdialog.keepSynced(true);
        //rvbrindes.setHasFixedSize(true);
        rvbrindes.setLayoutManager(new LinearLayoutManager(this));
        rvbrindes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        FirebaseRecyclerAdapter<String, BrindesViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<String, BrindesViewHolder>(
                String.class,
                R.layout.recyclerview_brindes,
                BrindesViewHolder.class,
                dbbrindesdialog
        ) {
            @Override
            protected void populateViewHolder(final BrindesViewHolder viewHolder, String model, final int position) {
                final String idbrinde = getRef(position).getKey();
                dbproduto = db.child("produtos").child(Objects.requireNonNull(idbrinde));
                dbproduto.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("foto").exists() && !Objects.requireNonNull(dataSnapshot.child("foto").getValue()).toString().isEmpty()) { viewHolder.setFoto(Objects.requireNonNull(dataSnapshot.child("foto").getValue()).toString()); }
                        if (dataSnapshot.child("produto").exists() && !Objects.requireNonNull(dataSnapshot.child("produto").getValue()).toString().isEmpty()) { viewHolder.setProduto(Objects.requireNonNull(dataSnapshot.child("produto").getValue()).toString()); }
                        if (dataSnapshot.child("descricao").exists() && !Objects.requireNonNull(dataSnapshot.child("descricao").getValue()).toString().isEmpty()) viewHolder.setDescricao(Objects.requireNonNull(dataSnapshot.child("descricao").getValue()).toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        brinde[0] = idbrinde;
                        brinde[1] = viewHolder.txtBrindeProduto.getText().toString();
                        txtTrocarBrinde.setText(R.string.trocar);
                        txtBrinde.setText(getString(R.string.voceescolheu, brinde[1]));
                        verificaObrigatorio();
                        alertDialog.dismiss();
                        alertDialog=null;
                    }
                });

            }
        };
        rvbrindes.setAdapter(firebaseRecyclerAdapter);
    }

    private void carregaListaDiologCupons(){
        Query querycupons = db.child("usuariocuponsresgatados/"+idusuario+"/"+restaurantes.getIdrestaurante()).orderByChild("validademseg").startAt(System.currentTimeMillis());
        querycupons.keepSynced(true);
        //rvcupons.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rvcupons.setLayoutManager(linearLayoutManager);
        rvcupons.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        FirebaseRecyclerAdapter<CuponsFirebase, CuponsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<CuponsFirebase, CuponsViewHolder>(
                CuponsFirebase.class,
                R.layout.recyclerview_cupons,
                CuponsViewHolder.class,
                querycupons
        ) {
            @Override
            protected void populateViewHolder(final CuponsViewHolder viewHolder, CuponsFirebase model, int position) {
                final int porcentagem = model.getDesconto();
                final String hash = getRef(position).getKey();
                final int dom = model.getDom();
                final int seg = model.getSeg();
                final int ter = model.getTer();
                final int qua = model.getQua();
                final int qui = model.getQui();
                final int sex = model.getSex();
                final int sab = model.getSab();
                txtTextoCupom.setText(R.string.carrinhocupomcom);
                viewHolder.setDesconto(porcentagem);
                viewHolder.setValidade(model.getValidade());
                viewHolder.setSeg(seg);
                viewHolder.setTer(ter);
                viewHolder.setQua(qua);
                viewHolder.setQui(qui);
                viewHolder.setSex(sex);
                viewHolder.setSab(sab);
                viewHolder.setDom(dom);
                viewHolder.setTipoBotao();

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cliqueBotaoCupom(dom, seg, ter, qua, qui, sex, sab, viewHolder, hash, porcentagem);
                    }
                });

                viewHolder.btnresgatarcupom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cliqueBotaoCupom(dom, seg, ter, qua, qui, sex, sab, viewHolder, hash, porcentagem);
                    }
                });
            }
        };
        rvcupons.setAdapter(firebaseRecyclerAdapter);
    }

    private void cliqueBotaoCupom(int dom, int seg, int ter, int qua, int qui, int sex, int sab, CuponsViewHolder viewHolder, String hash, int porcentagem) {
        if (!viewHolder.verifDia(dom,seg,ter,qua,qui,sex,sab)){ //Verifica se o cupom é valido no dia
            new Toast_layout(CarrinhoActivity.this).mensagem(getString(R.string.cupominvalidohoje));
        }else{
            hashcupom = hash;
            desconto = porcentagem;
            txtcupons.setText(getString(R.string.utilizandodesconto,desconto.toString() + "%"));
            txtTrocarcupom.setText(R.string.trocar);
            calculapedido();
            alertDialog.dismiss();
            alertDialog=null;
        }
    }

    private void CarregaFormaPag(){
        
        dbformaspag.keepSynced(true);
        //rvpagamento.setHasFixedSize(true);
        rvpagamento.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        final FirebaseRecyclerAdapter<Integer, FormasPagViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Integer, FormasPagViewHolder>
                (Integer.class,R.layout.recyclerview_formas_pagamento,FormasPagViewHolder.class, dbformaspag) {
            @Override
            protected void populateViewHolder(final FormasPagViewHolder viewHolder, Integer model, int position) {
                final String formapag = getRef(position).getKey();
                viewHolder.setDadosFormaPag(formapag);

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Objects.requireNonNull(formapag).equals("dinheiro")){

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getContext());
                            builder.setTitle(R.string.dinheiro)
                                    .setMessage(R.string.precisatroco)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialogDinheiro();
                                            setPagamento();
                                        }
                                    })
                                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            troco = 0.0;
                                            txtPagamento.setText(R.string.semtroco);
                                            txtTituloPagamento.setText(R.string.dinheiro);
                                            setPagamento();
                                        }
                                    })
                                    .create()
                                    .show();
                        }else{
                            txtPagamento.setText(viewHolder.txtformapag.getText().toString());
                            txtTituloPagamento.setText(R.string.formapagamento);
                            troco=0.0;
                            setPagamento();
                        }
                        alertDialog.dismiss();
                        alertDialog=null;
                    }
                });
            }
        };
        rvpagamento.setAdapter(firebaseRecyclerAdapter);
    }

    private void setPagamento(){
        txtTrocarPagamento.setText(R.string.trocar);
        txtPagamento.setVisibility(View.VISIBLE);
        pagamentoConf=true;
        verificaObrigatorio();
    }

    private void carregaListaEndereco(){
        dbendereco.keepSynced(true);
        rvendereco.setLayoutManager(new LinearLayoutManager(this));
        rvendereco.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        FirebaseRecyclerAdapter<EnderecosFirebase, EnderecosViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<EnderecosFirebase, EnderecosViewHolder>
                (EnderecosFirebase.class,R.layout.recyclerview_enderecos, EnderecosViewHolder.class, dbendereco) {
            @Override
            protected void populateViewHolder(EnderecosViewHolder viewHolder, EnderecosFirebase model, int position) {
                final String enderecolista, numerolista, bairrolista, cidadelista, ceplista, estadolista, complementolista;

                enderecolista = model.getEndereco();
                numerolista = model.getNumero();
                bairrolista =  model.getBairro();
                ceplista =  model.getCep();
                cidadelista =  model.getCidade();
                estadolista = model.getEstado();
                complementolista = model.getComplemento();
                viewHolder.setEndereco(CarrinhoActivity.this, enderecolista, numerolista,complementolista , bairrolista, cidadelista, estadolista);

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Address enderecolocalizacao = new VerificaCoordenadas().verificaCoordenadasPorEnd(CarrinhoActivity.this, enderecolista + ", " + numerolista + ", " + bairrolista + ", " + cidadelista + ", " + estadolista);
                        if (enderecolocalizacao != null) {
                            final double distancia = new CalcularDistancia(enderecolocalizacao.getLatitude(), enderecolocalizacao.getLongitude(), restaurantes.getLt(), restaurantes.getLg()).carregarDistanciaDouble();
                            Query dbfrete = db.child("restaurantefrete").child(restaurantes.getIdrestaurante());
                            dbfrete.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("")){
                                        Double frete= null;
                                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                            if (distancia <=  Double.parseDouble(postSnapshot.getKey())){
                                                frete = Double.parseDouble(Objects.requireNonNull(postSnapshot.getValue()).toString());
                                                break;
                                            }
                                        }

                                        if (frete == null){
                                            new Toast_layout(CarrinhoActivity.this).mensagem("O restaurante não entrega neste endereço.");
                                        }else {
                                            valorfrete = frete;
                                            endereco = enderecolista;
                                            complemento = complementolista;
                                            numero  = numerolista;
                                            bairro = bairrolista;
                                            cidade = cidadelista;
                                            estado = estadolista;
                                            cep = ceplista;
                                            if (!complemento.equals("")) {
                                                txtEndereco.setText(getString(R.string.enderecocomcomplemento, endereco, numero, complemento, bairro, cidade, estado));
                                            }else {
                                                txtEndereco.setText(getString(R.string.enderecosemcomplemento, endereco, numero, bairro, cidade, estado));
                                            }
                                            txtTrocarendereco.setText(R.string.trocar);
                                            txtEndereco.setVisibility(View.VISIBLE);
                                            enderecoConf=true;
                                            alertDialog.dismiss();
                                            alertDialog=null;
                                            calculapedido();
                                            verificaObrigatorio();
                                        }
                                    }else{
                                        new Toast_layout(CarrinhoActivity.this).mensagem("O restaurante não entrega neste endereço.");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else{
                            new Toast_layout(CarrinhoActivity.this).mensagem("O restaurante não entrega neste endereço.");
                        }
                    }
                });

            }
        };
        rvendereco.setAdapter(firebaseRecyclerAdapter);
    }

    public class ProdutosCarrinhoAdapter extends RecyclerView.Adapter<ProdutosCarrinhoHolder> {

        Integer quantidade;
        String  detalhes;
        Locale ptBr = new Locale("pt", "BR");

        private final List<ProdutosCarrinho> listProdutosCarrinho;

        private ProdutosCarrinhoAdapter(List<ProdutosCarrinho> listProdutosCarrinho) {
            this.listProdutosCarrinho = listProdutosCarrinho;
        }

        @NonNull
        public ProdutosCarrinhoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProdutosCarrinhoHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_carrinho, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(@NonNull final ProdutosCarrinhoHolder holder, @SuppressLint("RecyclerView") final int position) {
            final ProdutosCarrinho produto = listProdutosCarrinho.get(position);
            holder.txtprodutocarrinho.setText(listProdutosCarrinho.get(position).getProduto());
            holder.txtquantidadecarrinho.setText(listProdutosCarrinho.get(position).getQuantidade().toString());

            if (arrayprodutos[position][3] != null){
                holder.txtprecocarrinho.setText( NumberFormat.getCurrencyInstance(ptBr).format(Double.parseDouble(arrayprodutos[position][2])*Double.parseDouble(arrayprodutos[position][3])));
            }else{
                holder.txtprecocarrinho.setText(NumberFormat.getCurrencyInstance(ptBr).format(listProdutosCarrinho.get(position).getValorunitario()));
            }

            //Carrega obs, adicionais e opcionais em uma string
            ProdutosCarrinhoDAO dao = new ProdutosCarrinhoDAO(holder.imgMaisCarrinho.getContext());
            detalhes = dao.carregaDetalhes(produto.getId(), listProdutosCarrinho.get(position).getObs());
            holder.txtdescricaocarrinho.setText(detalhes);

            holder.imgMaisCarrinho.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quantidade = Integer.parseInt(holder.txtquantidadecarrinho.getText().toString());
                    quantidade++;
                    holder.txtquantidadecarrinho.setText(quantidade.toString());
                    arrayprodutos[position][2] = quantidade.toString();
                    holder.txtprecocarrinho.setText( NumberFormat.getCurrencyInstance(ptBr).format(Double.parseDouble(arrayprodutos[position][2])*Double.parseDouble(arrayprodutos[position][3])));
                    calculaprodutos();
                }
            });

            holder.imgMenosCarrinho.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quantidade = Integer.parseInt(holder.txtquantidadecarrinho.getText().toString());
                    if (quantidade>1){
                        quantidade--;
                        holder.txtquantidadecarrinho.setText(quantidade.toString());
                        arrayprodutos[position][2] = quantidade.toString();
                        holder.txtprecocarrinho.setText( NumberFormat.getCurrencyInstance(ptBr).format(Double.parseDouble(arrayprodutos[position][2])*Double.parseDouble(arrayprodutos[position][3])));
                        calculaprodutos();
                    }
                }
            });

            holder.txtremover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View view = v;

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());
                    builder.setTitle(R.string.confirmacao)
                            .setMessage(R.string.removeritem)
                            .setPositiveButton(R.string.remover, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ProdutosCarrinhoDAO dao = new ProdutosCarrinhoDAO(view.getContext());
                                    boolean sucesso = dao.excluir(produto.getId());
                                    if(!sucesso) {
                                        Snackbar.make(view, R.string.erroremoveritem, Snackbar.LENGTH_LONG)
                                                .setAction(R.string.erro, null).show();
                                    }
                                    boolean sair = dao.verificaRegistro();
                                    if (!sair){
                                        CarrinhoActivity.this.finish();
                                        new Toast_layout(CarrinhoActivity.this).mensagem(getString(R.string.carrinhovazio));
                                    }else{
                                        quantidade = Integer.parseInt(holder.txtquantidadecarrinho.getText().toString());
                                        //CarrinhoActivity classe = new CarrinhoActivity();
                                        arrayprodutos[position] = null;
                                        calculaprodutos();
                                    }
                                    removerProduto(produto);
                                }
                            })
                            .setNegativeButton(R.string.cancelar, null)
                            .create()
                            .show();

                }
            });
        }

        private void removerProduto(ProdutosCarrinho produto){
            int position = listProdutosCarrinho.indexOf(produto);
            listProdutosCarrinho.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public int getItemCount() {
            return listProdutosCarrinho != null ? listProdutosCarrinho.size() : 0;
        }
    }

}


