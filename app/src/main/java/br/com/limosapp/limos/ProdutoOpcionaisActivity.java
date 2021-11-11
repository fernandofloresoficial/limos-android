package br.com.limosapp.limos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import br.com.limosapp.limos.classes.Restaurantes;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.ListaRestaurantesActivity.restaurantes;
import static br.com.limosapp.limos.ProdutoActivity.opcionais;
import static br.com.limosapp.limos.ProdutoActivity.produto;
import static br.com.limosapp.limos.ProdutoActivity.quantidade;
import static br.com.limosapp.limos.ProdutoActivity.vlradicionais;
import static br.com.limosapp.limos.ProdutoActivity.vlropcionais;
import static br.com.limosapp.limos.ProdutoActivity.vlrunit;

public class ProdutoOpcionaisActivity extends AppCompatActivity {

    ImageView imgVoltar;
    TextView txtTitulo, txtGrupo, txtProduto, txtObrigatorio, txtMin, txtMax, txtValorTotal;
    Button btnProx;

    Integer obrigatorio, qtdemin, grupoMax;

    public static Integer grupo, qtdemax;

    FragmentManager fm = getSupportFragmentManager();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_opcionais);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Produto"));

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_produto_opcionais);

        grupo = 0;
        if (opcionais != null) {
            grupoMax = opcionais.length;
        }else{
            grupoMax=0;
        }

        carregaDados();

        imgVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltar();
            }
        });

        btnProx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((grupo+1) == grupoMax){
                    Intent intent = new Intent(ProdutoOpcionaisActivity.this, ProdutoResumoActivity.class);
                    startActivity(intent);
                }else{
                    grupo ++;
                    carregaDados();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
        calculaValores();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Se essa activity for destruída por alguma outra forma,
        // você não vai precisar finalizá-la pela activity B,
        // pois ela já estará destruída
        // Então aqui o BroadcastReceiver é removido
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        voltar();
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        txtTitulo = findViewById(R.id.txtTitulo);
        btnProx = findViewById(R.id.btnProx);
        txtProduto = findViewById(R.id.txtProduto);
        txtValorTotal = findViewById(R.id.txtValorTotal);
        txtObrigatorio = findViewById(R.id.txtObrigatorio);
        txtGrupo = findViewById(R.id.txtGrupo);
        txtMax = findViewById(R.id.txtMax);
        txtMin = findViewById(R.id.txtMin);
    }

    private void voltar(){
        if (grupo == 0){
            finish();
        }else{
            grupo --;
            carregaDados();
        }
    }

    public void calculaMax(Integer qtdetotal){
        txtMax.setText(getString(R.string.maxopcionais, qtdetotal, qtdemax));
    }

    public void obrigatorio(){
        if (obrigatorio == 1) {
            boolean bloqueia = true;
            Integer qtdeminobrig = 0;
            if (opcionais != null){
                for (int i = 0; i < opcionais[grupo].length; i++) {
                    if (opcionais[grupo][i][3] != null && !opcionais[grupo][i][3].equals("0")) {
                        qtdeminobrig = qtdeminobrig + Integer.parseInt(opcionais[grupo][i][3]);
                        if (qtdemin <= qtdeminobrig){
                            bloqueia = false;
                            break;
                        }
                    }
                }
            }
            if (bloqueia){
                btnProx.setEnabled(false);
                btnProx.setTextColor(getApplicationContext().getResources().getColor(R.color.cinza));
                btnProx.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.botalogincinza));
            }else{
                btnProx.setEnabled(true);
                btnProx.setTextColor(getApplicationContext().getResources().getColor(R.color.branco));
                btnProx.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.botaologin));
            }
        }else{
            btnProx.setEnabled(true);
            btnProx.setTextColor(getApplicationContext().getResources().getColor(R.color.branco));
            btnProx.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.botaologin));
        }
    }

    public static void somaVlrOpcionais() {
        vlropcionais=0.0;
        if (opcionais != null){
            for (String[][] opcionai : opcionais) {
                for (String[] anOpcionai : opcionai) {
                    if (anOpcionai[3] != null && !anOpcionai[3].equals("0") && anOpcionai[4] != null && !anOpcionai[4].equals("0") && !anOpcionai[4].equals("")) {
                        Integer qtdeunit = Integer.parseInt(anOpcionai[3]);
                        Double valorunit = Double.parseDouble(anOpcionai[4]);
                        vlropcionais = vlropcionais + (qtdeunit * valorunit);
                    }
                }
            }
        }
    }

    public void calculaValores() {
        somaVlrOpcionais();

        Double valorproduto = (vlrunit + vlradicionais + vlropcionais) * quantidade;
        Locale ptBr = new Locale("pt", "BR");
        String valorString = NumberFormat.getCurrencyInstance(ptBr).format(valorproduto);
        txtValorTotal.setText(valorString);
    }

    private void carregaDados(){
        txtProduto.setText(produto);

        if (opcionais != null) {
            if (opcionais[grupo][0][0] != null) {
                DatabaseReference dbgrupo = FirebaseDatabase.getInstance().getReference().child("restaurantegrupos").child(restaurantes.getIdrestaurante()).child(opcionais[grupo][0][0]);
                dbgrupo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("nome").exists() && !Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString().isEmpty())
                            txtGrupo.setText(Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString());
                        if (dataSnapshot.child("obrigatorio").exists() && !Objects.requireNonNull(dataSnapshot.child("obrigatorio").getValue()).toString().isEmpty()) {
                            obrigatorio = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("obrigatorio").getValue()).toString());
                            if (obrigatorio == 1) {
                                txtObrigatorio.setVisibility(View.VISIBLE);
                            } else {
                                txtObrigatorio.setVisibility(View.INVISIBLE);
                            }
                        }
                        qtdemin=1;
                        qtdemax=1;
                        if (dataSnapshot.child("quantidade").exists() && !Objects.requireNonNull(dataSnapshot.child("quantidade").getValue()).toString().isEmpty()) {
                            int qtde = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("quantidade").getValue()).toString());
                            FragmentTransaction ft = fm.beginTransaction();
                            if (qtde == 1) {
                                txtMax.setVisibility(View.VISIBLE);
                                if (dataSnapshot.child("max").exists() && !Objects.requireNonNull(dataSnapshot.child("max").getValue()).toString().isEmpty()) {
                                    qtdemax = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("max").getValue()).toString());
                                    txtMax.setText(getString(R.string.maxopcionais, 0, qtdemax));
                                }
                                if (dataSnapshot.child("min").exists() && !Objects.requireNonNull(dataSnapshot.child("min").getValue()).toString().isEmpty()){
                                    qtdemin = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("min").getValue()).toString());
                                }
                                if (obrigatorio==1){
                                    if (qtdemin > 1) {
                                        txtMin.setText(getString(R.string.escolhapelomenos, qtdemin));
                                    } else {
                                        txtMin.setText(getString(R.string.escolhapelomenos, 1));
                                    }
                                }else{
                                    if (qtdemax > 1) {
                                        txtMin.setText(getString(R.string.escolhaate, qtdemax));
                                    } else {
                                        txtMin.setText(getString(R.string.escolhaate, 1));
                                    }
                                }
                                ft.replace(R.id.fragment_content, new OpcionaisQtdeFragment());
                            } else {
                                txtMin.setText(R.string.escolhaumaopcao);
                                txtMax.setVisibility(View.INVISIBLE);
                                ft.replace(R.id.fragment_content, new OpcionaisFragment());
                            }
                            ft.commit();
                        }
                        obrigatorio();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
