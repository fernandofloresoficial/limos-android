package br.com.limosapp.limos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.ProdutoActivity.produto;

public class ProdutoObservacoesActivity extends AppCompatActivity {

    ImageView imgVoltar;
    TextView txtTitulo, txtProduto;
    EditText edtObservacoes;
    Button btnIncluirObservacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_observacoes);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_produto_observacoes);

        txtProduto.setText(produto);
        edtObservacoes.setText(ProdutoActivity.observacoes);

        btnIncluirObservacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProdutoActivity.observacoes = edtObservacoes.getText().toString();
                finish();
            }
        });

        imgVoltar.setOnClickListener(new View.OnClickListener() {
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
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        txtTitulo = findViewById(R.id.txtTitulo);
        btnIncluirObservacoes = findViewById(R.id.btnIncluirObservacoes);
        txtProduto = findViewById(R.id.txtProduto);
        edtObservacoes = findViewById(R.id.edtObservacoes);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
