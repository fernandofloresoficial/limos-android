package br.com.limosapp.limos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

import br.com.limosapp.limos.classes.BuscarEndereco;
import br.com.limosapp.limos.classes.Shoppings;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.MainActivity.enderecomain;
import static br.com.limosapp.limos.MainActivity.idendereco;
import static br.com.limosapp.limos.MainActivity.trocouendereco;
import static br.com.limosapp.limos.MainActivity.usarlocalizacao;

public class ListaShoppingsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtEndereco;
    ProgressBar pBarCarregaEndereco;
    ImageView imgVoltar, imgTrocar;
    RecyclerView rvShoppings;
    IntentFilter filter;

    public AlertDialog dialog;

    // Cria um Broadcast Receiver para que a MainActivity seja avisada caso o usuário mude as configurações de localização por fora do app
    // (deslizando a tela para baixo e clicando no ícone do GPS, por exemplo).
    // Isso é necessário porque durante a execução, o usuário tem como mudar as configurações de localização sem usar o próprio aplicativo.
    BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1){
        if (usarlocalizacao) {
            carregarEndereco();
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_lista_shoppings);

        // Registra o receiver para que o app seja avisado quando o usuário modificar as configurações de localização do dispositivo.
        filter = new IntentFilter(android.location.LocationManager.PROVIDERS_CHANGED_ACTION);
        this.registerReceiver(bReceiver, filter);

        inicializar();

        rvShoppings.setHasFixedSize(true);
        rvShoppings.setLayoutManager(new LinearLayoutManager(this));

        txtEndereco.setText(enderecomain.getEnderecomostrar());
        new Shoppings(this, rvShoppings).carregarShoppings();

        imgVoltar.setOnClickListener(this);
        txtEndereco.setOnClickListener(this);
        imgTrocar.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (trocouendereco) {
            carregarEndereco();
        }
        new VerificaInternet().verificaConexao(this);
    }

    @Override
    protected void onDestroy()    {
        super.onDestroy();
        // Desregistra o Broadcast Receiver.
        unregisterReceiver(bReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVoltar:
                finish();
                break;
            case R.id.txtEndereco:
                trocarEndereco();
                break;
            case R.id.imgTrocar:
                trocarEndereco();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (usarlocalizacao && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    carregarEndereco();
                }
            }
        }
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        txtEndereco = findViewById(R.id.txtEndereco);
        pBarCarregaEndereco = findViewById(R.id.pBarCarregaEndereco);
        imgTrocar = findViewById(R.id.imgTrocar);
        rvShoppings = findViewById(R.id.rvShoppings);
    }

    public void carregarEndereco(){
        new BuscarEndereco(this, txtEndereco, pBarCarregaEndereco , idendereco, usarlocalizacao, false, false, rvShoppings).buscaEndereco();
    }

    private void trocarEndereco(){
        startActivity(new Intent(ListaShoppingsActivity.this, EnderecoTrocarActivity.class));
    }
}