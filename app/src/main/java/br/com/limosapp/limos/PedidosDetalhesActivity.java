package br.com.limosapp.limos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import br.com.limosapp.limos.firebase.PedidosDetalhesFirebase;
import br.com.limosapp.limos.firebase.RestaurantesAvaliacoesFirebase;
import br.com.limosapp.limos.holder.PedidosDetalhesProdutosViewHolder;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.MainActivity.idusuario;

public class PedidosDetalhesActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imgVoltar, imgfechar;
    TextView txtTitulo, txtRestaurante, txtPedido, txtDataPedido, txtFormaPagamento, txtStatusPedido, txtNota, txtAvaliar, txtValorProdutos, txtValorCashback, txtValorDesconto, txtValorFrete, txtValorTotal, txtEndereco, txtEndereco1, txtNotaavaliar, txtcomentarioavaliacao, txtrespostaavaliacao;
    RecyclerView rvProdutos;
    RatingBar rtbAvaliar, rtbravaliar;
    EditText edtcomentarioavaliar;
    Button btnavaliar;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    String idrestaurante, idpedido, nota;
    String valorstring;
    int statuspedido, avaliado;
    Double vlrprodutos = 0.0;
    Double vlrcash = 0.0;
    Double vlrdesconto = 0.0;
    Double vlrfrete = 0.0;
    Double vlrtotal = 0.0;
    Locale ptbr = new Locale("pt", "BR");
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_detalhes);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_pedidos_detalhes);

        //Recupera ID do pedido, nota e status da avaliação
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        idrestaurante = Objects.requireNonNull(bundle).getString("idrestaurante");
        idpedido = bundle.getString("idpedido");
        nota = bundle.getString("nota");
        statuspedido = bundle.getInt("statuspedido");
        avaliado = bundle.getInt("avaliado");

        txtRestaurante.setText(bundle.getString("restaurante"));
        if (statuspedido == 5) {
            txtAvaliar.setVisibility(View.INVISIBLE);
            txtNota.setVisibility(View.INVISIBLE);
            rtbAvaliar.setVisibility(View.INVISIBLE);
        }else{
            if (avaliado == 0){
                txtAvaliar.setText(R.string.avaliarpedido);
                txtNota.setText("0.0");
                rtbAvaliar.setRating((float) 0.0);
            }else{
                txtAvaliar.setText(R.string.detalhesavaliacao);
                txtNota.setText(nota);
                rtbAvaliar.setRating(Float.parseFloat(nota));
            }
        }

        carregaPedido(idpedido);

        rvProdutos.setLayoutManager(new LinearLayoutManager(this));
        rvProdutos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        imgVoltar.setOnClickListener(this);
        txtAvaliar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAvaliar:
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
                if (txtAvaliar.getText().equals("Avaliar pedido")) {
                    View mview = getLayoutInflater().inflate(R.layout.dialog_avaliacao_pedido, (ViewGroup) v.findViewById(R.id.dialog_avaliacao_pedido_root));
                    mbuilder.setView(mview);
                    dialog = mbuilder.create();
                    dialog.show();

                    rtbravaliar = mview.findViewById(R.id.rtbAvaliar);
                    txtNotaavaliar = mview.findViewById(R.id.txtNota);
                    edtcomentarioavaliar = mview.findViewById(R.id.edtComentario);
                    btnavaliar = mview.findViewById(R.id.btnAvaliar);

                    rtbravaliar.setOnRatingBarChangeListener(
                            new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                    txtNotaavaliar.setText(String.valueOf(rating));
                                }
                            }
                    );

                    btnavaliar.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {
                            final DatabaseReference dbusuariopedidos = db.child("usuariopedidos").child(idusuario).child(idpedido);

                            final String notaavaliar;
                            notaavaliar = txtNotaavaliar.getText().toString();
                            final Float numnotaavaliar = Float.parseFloat(notaavaliar.replaceAll(",", "."));
                            dbusuariopedidos.child("nota").setValue(numnotaavaliar);
                            dbusuariopedidos.child("comentario").setValue(edtcomentarioavaliar.getText().toString());
                            dbusuariopedidos.child("avaliado").setValue(1);

                            dbusuariopedidos.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    salvaAvaliacaoRestaurante(idpedido, edtcomentarioavaliar.getText().toString(), Objects.requireNonNull(dataSnapshot.child("datapedido").getValue()).toString(), numnotaavaliar, Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("numeropedido").getValue()).toString()), idusuario, Objects.requireNonNull(dataSnapshot.child("nomeusuario").getValue()).toString());

                                    new Toast_layout(PedidosDetalhesActivity.this).mensagem("Avaliação cadastrada com sucesso");

                                    txtAvaliar.setText(R.string.detalhesavaliacao);
                                    txtNota.setText(txtNotaavaliar.getText().toString());
                                    rtbAvaliar.setRating(numnotaavaliar);

                                    dialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            
                            atualizaAvaliacaoRestaurante(txtNotaavaliar.getText().toString());
                        }
                    });
                }else{
                    View mview = getLayoutInflater().inflate(R.layout.dialog_detalhes_avaliacao, (ViewGroup) v.findViewById(R.id.dialog_detalhes_avaliacao_root));
                    mbuilder.setView(mview);
                    dialog = mbuilder.create();
                    dialog.show();

                    txtcomentarioavaliacao = mview.findViewById(R.id.txtComentarioAvaliacao);
                    txtrespostaavaliacao = mview.findViewById(R.id.txtRespostaAvaliacao);
                    imgfechar = mview.findViewById(R.id.imgFechar);

                    imgfechar.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {
                            dialog.dismiss();
                        }
                    });

                    DatabaseReference dbusuariopedidos = db.child("usuariopedidos").child(idusuario).child(idpedido);
                    dbusuariopedidos.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("comentario").exists() && !Objects.requireNonNull(dataSnapshot.child("comentario").getValue()).toString().isEmpty()){
                                txtcomentarioavaliacao.setText(Objects.requireNonNull(dataSnapshot.child("comentario").getValue()).toString());
                            }else{
                                txtcomentarioavaliacao.setText("");
                            }

                            if (dataSnapshot.child("resposta").exists() && !Objects.requireNonNull(dataSnapshot.child("resposta").getValue()).toString().isEmpty()){
                                txtrespostaavaliacao.setText(Objects.requireNonNull(dataSnapshot.child("resposta").getValue()).toString());
                            }else{
                                txtrespostaavaliacao.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
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
        imgVoltar = findViewById(R.id.imgVoltar);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtValorCashback = findViewById(R.id.txtValorCashback);
        txtValorDesconto = findViewById(R.id.txtValorDesconto);
        txtRestaurante = findViewById(R.id.txtRestaurante);
        txtPedido = findViewById(R.id.txtPedido);
        txtDataPedido = findViewById(R.id.txtDataPedido);
        txtFormaPagamento = findViewById(R.id.txtFormaPagamento);
        txtStatusPedido = findViewById(R.id.txtStatusPedido);
        txtNota = findViewById(R.id.txtNota);
        rtbAvaliar = findViewById(R.id.rtbAvaliar);
        txtAvaliar = findViewById(R.id.txtAvaliar);
        rvProdutos = findViewById(R.id.rvProdutos);
        txtValorFrete = findViewById(R.id.txtValorFrete);
        txtValorTotal = findViewById(R.id.txtValorTotal);
        txtEndereco = findViewById(R.id.txtEndereco);
        txtEndereco1 = findViewById(R.id.txtEndereco1);
        txtValorProdutos = findViewById(R.id.txtValorProdutos);
    }

    private void salvaAvaliacaoRestaurante(String idpedido, String comentario, String datapedido, Float nota, Integer numeropedido, String usuario, String nomeusuario){
        DatabaseReference dbrestaurantesavaliacoes = db.child("restauranteavaliacoes").child(idrestaurante).child(idpedido);
        RestaurantesAvaliacoesFirebase restaurantesavaliacoes = new RestaurantesAvaliacoesFirebase();
        restaurantesavaliacoes.setComentario(comentario);
        restaurantesavaliacoes.setDatapedido(datapedido);
        restaurantesavaliacoes.setNota(nota);
        restaurantesavaliacoes.setNumeropedido(numeropedido);
        restaurantesavaliacoes.setUsuario(usuario);
        restaurantesavaliacoes.setNomeusuario(nomeusuario);
        restaurantesavaliacoes.setResposta("");
        dbrestaurantesavaliacoes.setValue(restaurantesavaliacoes);
    }
    
    private void atualizaAvaliacaoRestaurante(final String numnotaavaliar){
        DatabaseReference dbrestaurantesqtavaliacao = db.child("restaurantes/"+idrestaurante+"/qtavaliacao");
        dbrestaurantesqtavaliacao.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                final Long qtdeavaliacaorest = (Long) Objects.requireNonNull(dataSnapshot).getValue();

                DatabaseReference dbrestaurantesnotaavaliacao = db.child("restaurantes/"+idrestaurante+"/avaliacao");
                dbrestaurantesnotaavaliacao.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull final MutableData currentData) {
                        Double nota = Double.parseDouble(numnotaavaliar);
                        if (currentData.getValue() == null) {
                            currentData.setValue(nota);
                        } else {
                            currentData.setValue(Double.parseDouble(currentData.getValue().toString()) + nota);
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        if (databaseError == null) {
                            final Double notaavaliacaorest = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(dataSnapshot).getValue()).toString());

                            DatabaseReference dbrestaurantes = db.child("restaurantes/"+idrestaurante+"/mediaavaliacao");
                            DecimalFormat formata = new DecimalFormat("0.00");
                            String valorstring = formata.format(notaavaliacaorest / Objects.requireNonNull(qtdeavaliacaorest)).replace(",", ".");
                            dbrestaurantes.setValue(Double.valueOf(valorstring));
                        }
                    }
                });
            }
        });
    }

    private void carregaPedido(final String id){
        DatabaseReference dbpedidos = db.child("pedidos");
        dbpedidos.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("pedido").exists() && !Objects.requireNonNull(dataSnapshot.child("pedido").getValue()).toString().isEmpty()) txtPedido.setText(getString(R.string.numeropedido, Objects.requireNonNull(dataSnapshot.child("pedido").getValue()).toString()));
                if (dataSnapshot.child("data").exists() && !Objects.requireNonNull(dataSnapshot.child("data").getValue()).toString().isEmpty() && dataSnapshot.child("hora").exists() && !Objects.requireNonNull(dataSnapshot.child("hora").getValue()).toString().isEmpty()) txtDataPedido.setText(getString(R.string.datapedido, Objects.requireNonNull(dataSnapshot.child("data").getValue()).toString() + " . " + Objects.requireNonNull(dataSnapshot.child("hora").getValue()).toString()));
                if (dataSnapshot.child("formapagamento").exists() && !Objects.requireNonNull(dataSnapshot.child("formapagamento").getValue()).toString().isEmpty()) txtFormaPagamento.setText(getString(R.string.formapagamentopedido, Objects.requireNonNull(dataSnapshot.child("formapagamento").getValue()).toString()));
                if (dataSnapshot.child("status").exists() && !Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString().isEmpty()){
                    String statuspedido = "";
                    switch (Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString()) {
                        case "0":
                            statuspedido = "aguardando";
                            break;
                        case "1":
                            statuspedido = "preparando";
                            break;
                        case "2":
                            statuspedido = "enviado";
                            break;
                        case "3":
                            statuspedido = "recusado";
                            break;
                        case "4":
                            statuspedido = "concluído";
                            break;
                        case "5":
                            statuspedido = "cancelado";
                            break;

                    }
                    txtStatusPedido.setText(getString(R.string.statuspedido, statuspedido));
                }
                if (dataSnapshot.child("valorprodutos").exists() && !Objects.requireNonNull(dataSnapshot.child("valorprodutos").getValue()).toString().isEmpty()) {
                    vlrprodutos = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("valorprodutos").getValue()).toString());
                    valorstring = NumberFormat.getCurrencyInstance(ptbr).format((dataSnapshot.child("valorprodutos").getValue()));
                    txtValorProdutos.setText(valorstring);
                }
                if (dataSnapshot.child("valorcash").exists() && !Objects.requireNonNull(dataSnapshot.child("valorcash").getValue()).toString().isEmpty()) {
                    vlrcash = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("valorcash").getValue()).toString());
                    valorstring = NumberFormat.getCurrencyInstance(ptbr).format((dataSnapshot.child("valorcash").getValue()));
                    txtValorCashback.setText(valorstring);
                }
                if (dataSnapshot.child("valordesconto").exists() && !Objects.requireNonNull(dataSnapshot.child("valordesconto").getValue()).toString().isEmpty()) {
                    vlrdesconto = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("valordesconto").getValue()).toString());
                    valorstring = NumberFormat.getCurrencyInstance(ptbr).format((dataSnapshot.child("valordesconto").getValue()));
                    txtValorDesconto.setText(valorstring);
                }
                if (dataSnapshot.child("valorfrete").exists() && !Objects.requireNonNull(dataSnapshot.child("valorfrete").getValue()).toString().isEmpty()) {
                    vlrfrete = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("valorfrete").getValue()).toString());
                    valorstring = NumberFormat.getCurrencyInstance(ptbr).format((dataSnapshot.child("valorfrete").getValue()));
                    txtValorFrete.setText(valorstring);
                }
                if (dataSnapshot.child("valortotal").exists() && !Objects.requireNonNull(dataSnapshot.child("valortotal").getValue()).toString().isEmpty()) {
                    vlrtotal = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("valortotal").getValue()).toString());
                    valorstring = NumberFormat.getCurrencyInstance(ptbr).format((dataSnapshot.child("valortotal").getValue()));
                    txtValorTotal.setText(valorstring);
                }
                if (dataSnapshot.child("endereco").exists() && !Objects.requireNonNull(dataSnapshot.child("endereco").getValue()).toString().isEmpty()){
                    String endereco, endereco1;
                    endereco = Objects.requireNonNull(dataSnapshot.child("endereco").getValue()).toString() + ", " + Objects.requireNonNull(dataSnapshot.child("numero").getValue()).toString();
                    if (dataSnapshot.child("complemento").exists() && !Objects.requireNonNull(dataSnapshot.child("complemento").getValue()).toString().isEmpty()){
                        endereco = endereco + " - " + Objects.requireNonNull(dataSnapshot.child("complemento").getValue()).toString();
                    }
                    txtEndereco.setText(endereco);

                    endereco1 = Objects.requireNonNull(dataSnapshot.child("bairro").getValue()).toString() + ", " + Objects.requireNonNull(dataSnapshot.child("cidade").getValue()).toString() + " - " + Objects.requireNonNull(dataSnapshot.child("uf").getValue()).toString();
                    txtEndereco1.setText(endereco1);
                }

                carregaProdutos(id);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void carregaProdutos(final String idpedido){
        DatabaseReference dbprodutos = db.child("pedidos").child(idpedido).child("produtos");
        FirebaseRecyclerAdapter<PedidosDetalhesFirebase, PedidosDetalhesProdutosViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PedidosDetalhesFirebase, PedidosDetalhesProdutosViewHolder>
                (PedidosDetalhesFirebase.class,R.layout.recyclerview_pedidos_detalhes, PedidosDetalhesProdutosViewHolder.class, dbprodutos) {
            @Override
            protected void populateViewHolder(PedidosDetalhesProdutosViewHolder viewHolder, PedidosDetalhesFirebase model, int position) {
                viewHolder.setQuantidade(model.getQuantidade());
                viewHolder.setProduto(model.getProduto());
                viewHolder.setObs(model.getObs());
                viewHolder.setComplemento(model.getComplemento());
                viewHolder.setValortotal(model.getValortotal());
            }

        };
        rvProdutos.setAdapter(firebaseRecyclerAdapter);
    }
}