package br.com.limosapp.limos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

import br.com.limosapp.limos.classes.BuscarEndereco;
import br.com.limosapp.limos.classes.Restaurantes;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.MainActivity.enderecomain;
import static br.com.limosapp.limos.MainActivity.idendereco;
import static br.com.limosapp.limos.MainActivity.trocouendereco;
import static br.com.limosapp.limos.MainActivity.usarlocalizacao;

public class ListaRestaurantesActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txtEndereco;
    ProgressBar pBarCarregaEndereco;
    ImageView imgVoltar, imgTrocar;
    RecyclerView rvRestaurantes;
    BottomNavigationView bottomMenu;
    EditText edtBuscar;
    CardView cvEndereco;

    public static AlertDialog dialog;
    @SuppressLint("StaticFieldLeak")
    public static Restaurantes restaurantes;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

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
        setContentView(R.layout.activity_lista_restaurantes);

        // Registra o receiver para que o app seja avisado quando o usuário modificar as configurações de localização do dispositivo.
        IntentFilter filter = new IntentFilter(android.location.LocationManager.PROVIDERS_CHANGED_ACTION);
        this.registerReceiver(bReceiver, filter);

        inicializar();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Carrinho"));

        rvRestaurantes.setHasFixedSize(true);
        rvRestaurantes.setLayoutManager(new LinearLayoutManager(this));

        //Mexi aqui pois em cel de usuario aconteceu do enderecomain estar nulo
        if (enderecomain.getEnderecomostrar() != null) {txtEndereco.setText(enderecomain.getEnderecomostrar());}
        restaurantes = new Restaurantes(this, rvRestaurantes);
        restaurantes.carregarRestaurantes(false, "");

        imgVoltar.setOnClickListener(this);
        cvEndereco.setOnClickListener(this);
        bottomMenu.setOnNavigationItemSelectedListener(navlistener);

        edtBuscar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == KeyEvent.ACTION_DOWN ) {

                    String restaurantebuscar = edtBuscar.getText().toString();
                    if(!restaurantebuscar.equals("")){
                        restaurantes.carregarRestaurantes(true, restaurantebuscar);
                    }else{
                        restaurantes.carregarRestaurantes(false, "");
                    }

                    return true;
                }
                else {
                    return false;
                }
            }
        });
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVoltar:
                finish();
                break;
            case R.id.cvEndereco:
                trocarEndereco(this);
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
        edtBuscar = findViewById(R.id.edtBuscar);
        rvRestaurantes = findViewById(R.id.rvRestaurantes);
        bottomMenu = findViewById(R.id.bottomMenu);
        cvEndereco = findViewById(R.id.cvEndereco);
    }

    public void carregarEndereco(){
        new BuscarEndereco(this, txtEndereco, pBarCarregaEndereco , idendereco, usarlocalizacao, false, true, rvRestaurantes).buscaEndereco();
    }

    public static void trocarEndereco(Activity activity){
        activity.startActivity(new Intent(activity, EnderecoTrocarActivity.class));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_home:
                        restaurantes.carregarRestaurantes(false,"");
                        break;
                    case R.id.bottom_fav:
                        restaurantes.carregarRestaurantesFavoritos();
                        break;
//                    case R.id.bottom_filtrar:
//                        //Intent intent = new Intent(ListaRestaurantesActivity.this, FiltrarActivity.class);
//                        //startActivity(intent);
//                        break;

                }
                return true;
            }
        };
}