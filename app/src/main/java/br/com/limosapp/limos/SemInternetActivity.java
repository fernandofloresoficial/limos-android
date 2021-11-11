package br.com.limosapp.limos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.limosapp.limos.util.Toast_layout;

public class SemInternetActivity extends AppCompatActivity {

    Button btnTentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem_internet);
        btnTentar = findViewById(R.id.btnTentar);

        btnTentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificaConexao();
            }
        });
    }

    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
            finish();
        } else {
            conectado = false;
            new Toast_layout(this).mensagem("Você ainda está sem conexão com a internet, verifique as configuraçãoes do seu aparelho");
        }
        return conectado;
    }
}
