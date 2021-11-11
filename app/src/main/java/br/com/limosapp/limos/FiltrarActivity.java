package br.com.limosapp.limos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.limosapp.limos.util.VerificaInternet;

public class FiltrarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }
}
