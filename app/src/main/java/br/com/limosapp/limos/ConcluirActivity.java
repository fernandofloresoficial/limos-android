package br.com.limosapp.limos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.limosapp.limos.util.VerificaInternet;

public class ConcluirActivity extends AppCompatActivity {

    TextView txtmensagem;
    CardView btncontinuar;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concluir);

        inicializar();

        Intent intent = getIntent();
        String valorcash = intent.getStringExtra("valorcash");

        if (valorcash != null){
            txtmensagem.setText("Você ganhou "+valorcash+" de cashback nesse pedido");
        }else{
            txtmensagem.setText("O seu pedido foi enviado para o restaurante");
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Carrinho"));

        btncontinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(new Intent("Carrinho"));
            }
        });
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
    public void onBackPressed() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("Carrinho"));
    }

    private void inicializar(){
        txtmensagem = findViewById(R.id.txtMensagemCash);
        btncontinuar = findViewById(R.id.btnContinuar);
    }
}
