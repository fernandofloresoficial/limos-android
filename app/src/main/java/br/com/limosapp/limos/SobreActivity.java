package br.com.limosapp.limos;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.limosapp.limos.util.VerificaInternet;

public class SobreActivity extends AppCompatActivity {

    ImageView imgVoltar;
    TextView txtTitulo, txtVersao;
    CardView cardTermos, cardPolitica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_sobre);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert pInfo != null;
        txtVersao.setText(getString(R.string.versao, pInfo.versionName));
        //int verCode = pInfo.versionCode; Deixei aqui se um dia for usar

        imgVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cardTermos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivity(new Intent(SobreActivity.this, TermosActivity.class)); }
        });
        cardPolitica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivity(new Intent(SobreActivity.this, PrivacidadeActivity.class)); }
        });
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        txtTitulo = findViewById(R.id.txtTitulo);
        cardTermos = findViewById(R.id.cardTermos);
        cardPolitica = findViewById(R.id.cardPolitica);
        txtVersao = findViewById(R.id.txtVersao);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
