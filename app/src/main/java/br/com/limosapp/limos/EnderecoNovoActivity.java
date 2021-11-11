package br.com.limosapp.limos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import br.com.limosapp.limos.classes.APIRetrofitService;
import br.com.limosapp.limos.classes.CEP;
import br.com.limosapp.limos.classes.CEPDeserializer;
import br.com.limosapp.limos.util.EsconderTeclado;
import br.com.limosapp.limos.util.Mascara;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.ValidarCampos;
import br.com.limosapp.limos.util.VerificaInternet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnderecoNovoActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imgVoltar;
    TextView txtTitulo, txtBuscarCEP, txtNaoSeiCEP;
    EditText edtCEP;    
    String ncep, endereco, bairro, cidade, estado;
    private ProgressBar pBarCEP;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco_novo);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_endereco_novo);
        
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Endereco"));

        edtCEP.addTextChangedListener(new Mascara().insert("##.###-###", edtCEP));
        imgVoltar.setOnClickListener(this);
        txtBuscarCEP.setOnClickListener(this);
        txtNaoSeiCEP.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVoltar:
                finish();
                break;
            case R.id.txtBuscarCEP:
                ValidarCampos validarCampos = new ValidarCampos();
                if (!validarCampos.validarNotNull(edtCEP, "Preencha o campo CEP")) {
                    return;
                }
                if (edtCEP.getText().length() > 0 && edtCEP.getText().length() < 8) {
                    edtCEP.setError("CEP inválido");
                    edtCEP.setFocusable(true);
                    edtCEP.requestFocus();
                    return;
                }
                pBarCEP.setVisibility(View.VISIBLE);
                ncep = edtCEP.getText().toString();
                ncep = ncep.replace(".", "");
                ncep = ncep.replace("-", "");
                EsconderTeclado.hideKeyboard(this, edtCEP);
                buscarEndereco(ncep);
                break;
            case R.id.txtNaoSeiCEP:
                startActivity(new Intent(EnderecoNovoActivity.this, EnderecoBuscarActivity.class));
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
        edtCEP = findViewById(R.id.edtCEP);
        txtBuscarCEP = findViewById(R.id.txtBuscarCEP);
        txtNaoSeiCEP = findViewById(R.id.txtNaoSeiCEP);
        pBarCEP = findViewById(R.id.pBarCEP);
    }

    private void buscarEndereco(String ncep) {
        Gson g = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIRetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        final APIRetrofitService service = retrofit.create(APIRetrofitService.class);

        Call<CEP> callCEPByCEP = service.getEnderecoByCEP(ncep);

        callCEPByCEP.enqueue(new Callback<CEP>() {
            @Override
            public void onResponse(@NonNull Call<CEP> call, @NonNull Response<CEP> response) {
                if (!response.isSuccessful()) {
                    new Toast_layout(EnderecoNovoActivity.this).mensagem("CEP inválido");
                    pBarCEP.setVisibility(View.INVISIBLE);
                } else {
                    pBarCEP.setVisibility(View.INVISIBLE);

                    CEP cep = response.body();
                    if(Objects.requireNonNull(cep).getLogradouro() != null) {
                        endereco = cep.getLogradouro();
                        bairro = cep.getBairro();
                        cidade = cep.getLocalidade();
                        estado = cep.getUf();

                        Intent intent = new Intent(EnderecoNovoActivity.this, EnderecoCEPActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("idendereco", "0");
                        bundle.putString("cep", edtCEP.getText().toString());
                        bundle.putString("endereco", endereco);
                        bundle.putString("bairro", bairro);
                        bundle.putString("cidade", cidade);
                        bundle.putString("estado", estado);

                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        new Toast_layout(EnderecoNovoActivity.this).mensagem(getString(R.string.cepinvalido));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CEP> call, @NonNull Throwable t) {
                new Toast_layout(EnderecoNovoActivity.this).mensagem(getString(R.string.errobuscarendereco, t.getMessage()));
            }
        });
    }
}