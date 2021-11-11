package br.com.limosapp.limos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import br.com.limosapp.limos.util.VerificaInternet;
import me.drakeet.materialdialog.MaterialDialog;

import static br.com.limosapp.limos.MainActivity.idendereco;
import static br.com.limosapp.limos.MainActivity.trocouendereco;
import static br.com.limosapp.limos.MainActivity.usarlocalizacao;
import static br.com.limosapp.limos.MainActivity.usarendcadastrado;
import static br.com.limosapp.limos.MainActivity.inserirendereco;
import static br.com.limosapp.limos.MainActivity.permitircadend;

public class EnderecoTrocarActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int REQUEST_PERMISSIONS_CODE = 128;

    private MaterialDialog mMaterialDialog;
    ImageView imgVoltar;
    CardView cvPadrao, cvLocalizacao, cvInserir, cvCadastrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco_trocar);
        inicializar();

        imgVoltar.setOnClickListener(this);
        cvPadrao.setOnClickListener(this);
        cvLocalizacao.setOnClickListener(this);
        cvInserir.setOnClickListener(this);
        cvCadastrados.setOnClickListener(this);
    }

    private void callDialog( String message, final String[] permissions ){
        mMaterialDialog = new MaterialDialog(this)
                .setTitle(R.string.permissao)
                .setMessage( message )
                .setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ActivityCompat.requestPermissions(EnderecoTrocarActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancelar, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        cvPadrao = findViewById(R.id.cvPadrao);
        cvLocalizacao = findViewById(R.id.cvLocalizacao);
        cvInserir = findViewById(R.id.cvInserir);
        cvCadastrados = findViewById(R.id.cvCadastrados);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvPadrao:
                trocouendereco = true;
                idendereco = "";
                usarlocalizacao = false;
                usarendcadastrado = false;
                inserirendereco = false;
                finish();
                break;
            case R.id.cvLocalizacao:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    int PERMISSAO_REQUEST = 2;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                        callDialog(getString(R.string.necessariopermissao, "da localização"), new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                    }else{
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSAO_REQUEST);
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)){
                        callDialog(getString(R.string.necessariopermissao, "da localização"), new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                    }else{
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSAO_REQUEST);
                    }
                }else{
                    trocouendereco = true;
                    idendereco = "";
                    usarlocalizacao = true;
                    usarendcadastrado = false;
                    inserirendereco = false;
                    finish();
                }
                break;
            case R.id.cvInserir:
                trocouendereco = true;
                usarlocalizacao = false;
                usarendcadastrado = false;
                inserirendereco = true;
                startActivity(new Intent(EnderecoTrocarActivity.this, EnderecoNovoActivity.class));
                finish();
                break;
            case R.id.cvCadastrados:
                trocouendereco = true;
                usarlocalizacao = false;
                usarendcadastrado = true;
                inserirendereco = false;
                permitircadend = false;
                startActivity(new Intent(EnderecoTrocarActivity.this, EnderecoCadastradoActivity.class));
                finish();
                break;
            case R.id.imgVoltar:
                finish();
                break;
        }
    }
}