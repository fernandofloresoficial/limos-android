package br.com.limosapp.limos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import br.com.limosapp.limos.classes.BuscarEndereco;
import br.com.limosapp.limos.pojos.Endereco;
import br.com.limosapp.limos.util.VerificaInternet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    SimpleDraweeView imgFoto, imgContador;
    ImageView imgTrocar, imgDelivery, imgPraca, imgNotificacao, imgFechar;
    TextView txtEndereco;
    TextView txtDelivery, txtPraca, txtNome, txtCashBackSaldo, txtmudarenredeco, txtContador;
    TextView txtCupons, txtVerRegras;
    ImageView imgSemCupons;
    ProgressBar pBarcarregaEndereco;
    RecyclerView rvCupons;
    CardView cvEndereco, cvNotificacao, cvDelivery, cvShopping;
    Button btnindicarrestaurante, btnIndicarRestauranteMain, btnConsultarCasBack, btnFila, btnMesa, btnReserva;

    Animation animacao;
    FirebaseAuth mAuth;
    DatabaseReference dbusuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");
    DatabaseReference dbrestaurantes = FirebaseDatabase.getInstance().getReference().child("restaurantes");
    DatabaseReference dbpedidos = FirebaseDatabase.getInstance().getReference().child("usuariopedidos");

    public static String idusuario = "", idendereco = "", nomeusuario = "";
    public static boolean trocouendereco = false, usarlocalizacao = false, usarendcadastrado = false, inserirendereco = false, permitircadend = false;
    public static Endereco enderecomain;
    String idusuarioaut, valorString;
    boolean temrestaurante;
    private Activity getActivity;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;


    AlertDialog alertDialog;
    AlertDialog.Builder mbuilder;
    Locale ptBr = new Locale("pt", "BR");

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
        setContentView(R.layout.activity_main);

        openOrCreateDatabase("limos.db", Context.MODE_PRIVATE, null);
        getActivity = MainActivity.this;

        // Registra o receiver para que o app seja avisado quando o usuário modificar as configurações de localização do dispositivo.
        IntentFilter filter = new IntentFilter(android.location.LocationManager.PROVIDERS_CHANGED_ACTION);
        this.registerReceiver(bReceiver, filter);

        inicializar();

        pBarcarregaEndereco.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        rvCupons.setHasFixedSize(true);
        rvCupons.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        dbusuarios.keepSynced(true);
        dbrestaurantes.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth!=null){
            idusuarioaut = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            String telefone = mAuth.getCurrentUser().getPhoneNumber();

            if (Objects.equals(telefone, "") || telefone == null) {
                CarregaDadosUsuario(Objects.requireNonNull(mAuth.getCurrentUser().getEmail()), "");
            }else {
                telefone =  telefone.replace("+55", "");
                CarregaDadosUsuario("", telefone);
            }

        }else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        navigationView.setNavigationItemSelectedListener(this);
        cvEndereco.setOnClickListener(this);
        cvShopping.setOnClickListener(this);
        cvDelivery.setOnClickListener(this);
        cvNotificacao.setOnClickListener(this);
        btnIndicarRestauranteMain.setOnClickListener(this);
        btnConsultarCasBack.setOnClickListener(this);
        btnFila.setOnClickListener(this);
        btnMesa.setOnClickListener(this);
        btnReserva.setOnClickListener(this);
        txtVerRegras.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvEndereco:
                trocarEndereco();
                break;
            case R.id.cardDelivery:
                abreListaRestaurantes();
                break;
            case R.id.cardShopping:
                abreListaRestaurantesPraca();
                break;
            case R.id.cvNotificacao:
                startActivity(new Intent(MainActivity.this, NotificacaoActivity.class));
                break;
            case R.id.btnFila:
                abredialogsemrestaurante();
                break;
            case R.id.btnMesa:
                abredialogsemrestaurante();
                break;
            case R.id.btnReserva:
                abredialogsemrestaurante();
                break;
            case R.id.btnConsultarCasBack:
                startActivity(new Intent(MainActivity.this, CashbackExtratoActivity.class));
                break;
            case R.id.txtVerRegras:
                if (alertDialog == null) {
                    AlertDialog.Builder mbuilder = new AlertDialog.Builder(MainActivity.this);
                    View mview = getLayoutInflater().inflate(R.layout.dialog_regras_cashback, (ViewGroup) findViewById(R.id.dialog_regras_cashback_root));
                    mbuilder.setView(mview);
                    alertDialog = mbuilder.create();
                    alertDialog.show();

                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            alertDialog=null;
                        }
                    });

                    Button btnEntendi = mview.findViewById(R.id.btnEntendi);
                    btnEntendi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            alertDialog = null;
                        }
                    });
                }

                break;
            case R.id.btnIndicarRestauranteMain:
                startActivity(new Intent(MainActivity.this, IndicarRestauranteActivity.class));
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (trocouendereco){
            carregarEndereco();
            trocouendereco = false;
        }
        new VerificaInternet().verificaConexao(this);
        mostrarNotificacao();
    }

    @Override
    protected void onDestroy()    {
        super.onDestroy();
        // Desregistra o Broadcast Receiver.
        unregisterReceiver(bReceiver);
    }

    @Override
    public void onBackPressed() {
        //this.moveTaskToBack(true);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (!txtEndereco.getText().equals("Carregando...")) {
            switch (item.getItemId()) {
                case R.id.mnuPerfil:
                    abrePerfil();
                    break;
                case R.id.mnuEnderecos:
                    permitircadend = true;
                    Intent intenderecos = new Intent(MainActivity.this, EnderecoCadastradoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("idusuario", idusuario);
                    intenderecos.putExtras(bundle);
                    startActivity(intenderecos);
                    break;
                case R.id.mnuPedidos:
                    startActivity(new Intent(MainActivity.this, PedidosActivity.class));
                    break;
                case R.id.mnuExtrato:
                    startActivity(new Intent(MainActivity.this, CashbackExtratoActivity.class));
                    break;
                case R.id.mnuCupons:
                    startActivity(new Intent(MainActivity.this, CuponsResgatadosActivity.class));
                    break;
                    case R.id.mnuIndicar:
                    startActivity(new Intent(MainActivity.this, IndicarRestauranteActivity.class));
                    break;
                case R.id.mnuSobre:
                    startActivity(new Intent(MainActivity.this, SobreActivity.class));
                    break;
                case R.id.mnuLogout:
                    FirebaseAuth.getInstance().signOut();
                    Intent intlogout = new Intent(MainActivity.this, LoginActivity.class);
                    intlogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intlogout);
                    finish();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
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
        txtEndereco = findViewById(R.id.txtEndereco);
        txtVerRegras = findViewById(R.id.txtVerRegras);
        pBarcarregaEndereco = findViewById(R.id.pBarCarregaEndereco);
        imgTrocar = findViewById(R.id.imgTrocar);
        txtCupons = findViewById(R.id.txtCupons);
        imgSemCupons = findViewById(R.id.imgSemCupons);
        imgDelivery = findViewById(R.id.imgDelivery);
        imgPraca = findViewById(R.id.imgPraca);
        txtDelivery = findViewById(R.id.txtDelivery);
        txtPraca = findViewById(R.id.txtPraca);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        rvCupons = findViewById(R.id.rvCupons);
        navigationView = findViewById(R.id.navigationView);
        btnIndicarRestauranteMain = findViewById(R.id.btnIndicarRestauranteMain);
        btnConsultarCasBack = findViewById(R.id.btnConsultarCasBack);
        btnFila = findViewById(R.id.btnFila);
        btnMesa = findViewById(R.id.btnMesa);
        btnReserva = findViewById(R.id.btnReserva);
        cvEndereco = findViewById(R.id.cvEndereco);
        imgContador = findViewById(R.id.imgContador);
        txtContador = findViewById(R.id.txtContador);
        cvNotificacao = findViewById(R.id.cvNotificacao);
        cvDelivery = findViewById(R.id.cardDelivery);
        cvShopping = findViewById(R.id.cardShopping);

        View hView =  navigationView.getHeaderView(0);
        imgFoto = hView.findViewById(R.id.imgFoto);
        txtNome = hView.findViewById(R.id.txtEndereco);
        txtCashBackSaldo = hView.findViewById(R.id.txtCashBackSaldo);

        imgNotificacao = findViewById(R.id.imgNotificacao);
    }

    public void carregarEndereco(){
        new BuscarEndereco(getActivity, txtEndereco, pBarcarregaEndereco , idendereco, usarlocalizacao, true, false, txtCupons, imgSemCupons, rvCupons).buscaEndereco();
    }

    private void CarregaDadosUsuario(final String email, final String telefone) {
        Query queryusuario;
        if (!email.equals("")) {
            queryusuario = dbusuarios.orderByChild("email").equalTo(email);
        } else{
            queryusuario = dbusuarios.orderByChild("telefone").equalTo(telefone);
        }
        queryusuario.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        idusuario = postSnapshot.getKey();
                        if (postSnapshot.child("urlfoto").exists() && !Objects.requireNonNull(postSnapshot.child("urlfoto").getValue()).toString().isEmpty()) {
                            String urlfoto = Objects.requireNonNull(postSnapshot.child("urlfoto").getValue()).toString();
                            imgFoto.setImageURI(urlfoto);
                        }
                        if (postSnapshot.child("nome").exists() && !Objects.requireNonNull(postSnapshot.child("nome").getValue()).toString().isEmpty()) {
                            txtNome.setText(getString(R.string.boasvindas, Objects.requireNonNull(postSnapshot.child("nome").getValue()).toString()));
                            nomeusuario = Objects.requireNonNull(postSnapshot.child("nome").getValue()).toString();
                        } else {
                            if (postSnapshot.child("telefone").exists() && !Objects.requireNonNull(postSnapshot.child("telefone").getValue()).toString().isEmpty()) {
                                txtNome.setText(getString(R.string.boasvindas, Objects.requireNonNull(postSnapshot.child("telefone").getValue()).toString()));
                                nomeusuario = Objects.requireNonNull(postSnapshot.child("telefone").getValue()).toString();
                            }
                        }

                        if (postSnapshot.child("cashbacksaldo").exists() && !Objects.requireNonNull(postSnapshot.child("cashbacksaldo").getValue()).toString().isEmpty()) {
                            valorString = NumberFormat.getCurrencyInstance(ptBr).format(Double.parseDouble(Objects.requireNonNull(postSnapshot.child("cashbacksaldo").getValue()).toString()));
                            txtCashBackSaldo.setText(getString(R.string.saldocashback, valorString));
                        }

                        carregarEndereco();
                        mostrarNotificacao();
                    }
                }else{
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void trocarEndereco(){
        if (!txtEndereco.getText().equals("Carregando...")) {
            startActivity(new Intent(MainActivity.this, EnderecoTrocarActivity.class));
        }
    }

    private void abrePerfil(){
        startActivity(new Intent(MainActivity.this, PerfilActivity.class));
    }

    private void abreListaRestaurantes(){
        if (!txtEndereco.getText().equals("Carregando...")) {
            temrestaurante = false;
            Query queryrestaurantes;
            queryrestaurantes = dbrestaurantes.orderByChild("ativo_cidade").equalTo("1_"+enderecomain.getCidade()).limitToFirst(1);
            queryrestaurantes.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ignored : dataSnapshot.getChildren()) {
                        temrestaurante = true;
                    }

                    if (temrestaurante) {
                        startActivity(new Intent(MainActivity.this, ListaRestaurantesActivity.class));
                    } else {
                        abredialogsemrestaurante();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void abredialogsemrestaurante(){

        if (alertDialog == null) {
            AlertDialog.Builder mbuilder = new AlertDialog.Builder(MainActivity.this);
            View mview = getLayoutInflater().inflate(R.layout.dialog_sem_restaurantes, (ViewGroup) findViewById(R.id.dialog_sem_restaurantes_root));
            mbuilder.setView(mview);
            alertDialog = mbuilder.create();
            alertDialog.show();

            imgFechar = mview.findViewById(R.id.imgFechar);
            btnindicarrestaurante = mview.findViewById(R.id.btnIndicarRestaurante);
            txtmudarenredeco = mview.findViewById(R.id.txtMudarEndereco);

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    alertDialog=null;
                }
            });

            btnindicarrestaurante.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    alertDialog.dismiss();
                    startActivity(new Intent(MainActivity.this, IndicarRestauranteActivity.class));
                    alertDialog=null;
                }
            });

            txtmudarenredeco.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    alertDialog.dismiss();
                    alertDialog=null;
                    trocarEndereco();
                }
            });

            imgFechar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    alertDialog.dismiss();
                    alertDialog=null;
                }
            });
        }

    }

    private void abreListaRestaurantesPraca(){
        if (!txtEndereco.getText().equals("Carregando...")) {
            startActivity(new Intent(MainActivity.this, ListaShoppingsActivity.class));
        }
    }

    private void mostrarNotificacao(){
        if (!idusuario.equals("")) {
            Query querypedidos;
            querypedidos = dbpedidos.child(idusuario).orderByChild("statuspedido").startAt(0).endAt(2);
            querypedidos.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("")){
                        imgContador.setVisibility(View.VISIBLE);
                        txtContador.setVisibility(View.VISIBLE);
                        long numero = dataSnapshot.getChildrenCount();
                        txtContador.setText(Long.toString(numero));
                        animacao = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shaking);
                        imgContador.startAnimation(animacao);
                        imgNotificacao.startAnimation(animacao);

                    }else{
                        imgContador.setVisibility(View.INVISIBLE);
                        txtContador.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}