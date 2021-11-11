package br.com.limosapp.limos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import br.com.limosapp.limos.classes.APIRetrofitService;
import br.com.limosapp.limos.classes.CEP;
import br.com.limosapp.limos.classes.CEPDeserializer;
import br.com.limosapp.limos.classes.EnderecosAdapter;
import br.com.limosapp.limos.util.EsconderTeclado;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.ValidarCampos;
import br.com.limosapp.limos.util.VerificaInternet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnderecoBuscarActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgVoltar, imgfechar;
    TextView txtTitulo;    
    EditText edtEndereco;
    Spinner spnEstado, spnCidade;
    Button btnBuscar;
    private ProgressBar pBarCEP;
    int totalcidade, estadoselecionado = 0, cidadeselecionado = 0;
    String estado, cidade;
    RecyclerView rvenderecos;
    List<String> listacidades = new ArrayList<>();
    public static AlertDialog dialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    DatabaseReference dbestadoscidades = FirebaseDatabase.getInstance().getReference().child("estadoscidades");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco_buscar);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_endereco_buscar);
        dbestadoscidades.keepSynced(true);

        carregaEstados();
        listacidades.add("Cidade");

        carregaCidades();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Endereco"));

        imgVoltar.setOnClickListener(this);
        btnBuscar.setOnClickListener(this);

        spnEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View v, int position, long id) {
                listacidades.clear();
                listacidades.add("Cidade");

                if(estadoselecionado != position){
                    estado = spnEstado.getItemAtPosition(position).toString();
                    verificaTotalCidadesPorEstado(estado);

                    dbestadoscidades.child(spnEstado.getItemAtPosition(position).toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot cidadeSnapshot: dataSnapshot.getChildren()) {
                                for(int cont = 0; cont <= totalcidade; cont++) {
                                    String cidade = cidadeSnapshot.child(Integer.toString(cont)).getValue(String.class);
                                    listacidades.add(cidade);
                                }

                            }
                            carregaCidades();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                estadoselecionado = position;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spnCidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View v, int position, long id) {
                cidade = spnCidade.getItemAtPosition(position).toString();
                cidadeselecionado = position;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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
            case R.id.btnBuscar:
                ValidarCampos validarCampos = new ValidarCampos();
                if (!validarCampos.validarNotNull(edtEndereco, getString(R.string.preenchaocampo,"endereço"))) {
                    return;
                }
                if (estadoselecionado == 0){
                    new Toast_layout(this).mensagem(getString(R.string.preenchaocampo,"estado"));
                    spnEstado.setFocusable(true);
                    spnEstado.requestFocus();
                    return;
                }
                if (cidadeselecionado == 0){
                    new Toast_layout(this).mensagem(getString(R.string.preenchaocampo,"cidade"));
                    spnCidade.setFocusable(true);
                    spnCidade.requestFocus();
                    return;
                }
                EsconderTeclado.hideKeyboard(this, edtEndereco);
                pBarCEP.setVisibility(View.VISIBLE);
                buscarEndereco(edtEndereco.getText().toString(), estado, cidade, this);
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
        edtEndereco = findViewById(R.id.edtEndereco);
        spnEstado = findViewById(R.id.spnEstado);
        spnCidade = findViewById(R.id.spnCidade);
        btnBuscar = findViewById(R.id.btnBuscar);
        pBarCEP = findViewById(R.id.pBarCEP);
    }

    private void buscarEndereco(String endereco, String estado, String cidade, final Context context) {
        Gson g = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIRetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        final APIRetrofitService service = retrofit.create(APIRetrofitService.class);

        Call<List<CEP>> callCEPByCidadeEstadoEndereco = service.getCEPByCidadeEstadoEndereco(estado, cidade, endereco);

        callCEPByCidadeEstadoEndereco.enqueue(new Callback<List<CEP>>() {
            @Override
            public void onResponse(@NonNull Call<List<CEP>> call, @NonNull Response<List<CEP>> response) {
                if (!response.isSuccessful()) {
                    new Toast_layout(EnderecoBuscarActivity.this).mensagem(getString(R.string.dadosinvalidos));
                    pBarCEP.setVisibility(View.INVISIBLE);
                } else {
                    pBarCEP.setVisibility(View.INVISIBLE);
                    List<CEP> CEPAux = response.body();
                    if (Objects.requireNonNull(CEPAux).size() != 0) {
                        if (dialog == null){
                            AlertDialog.Builder mbuilder = new AlertDialog.Builder(context);
                            View mview = getLayoutInflater().inflate(R.layout.dialog_endereco_encontrado, (ViewGroup) findViewById(R.id.dialog_endereco_encontrado_root));
                            rvenderecos = mview.findViewById(R.id.rvEnderecos);
                            imgfechar = mview.findViewById(R.id.imgFechar);
                            //rvenderecos.setHasFixedSize(true);
                            rvenderecos.setLayoutManager(new LinearLayoutManager(context));
                            rvenderecos.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                            rvenderecos.setAdapter(new EnderecosAdapter(CEPAux, context));
                            mbuilder.setView(mview);
                            dialog = mbuilder.create();
                            dialog.show();

                            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog2) {
                                    dialog=null;
                                }
                            });

                            imgfechar.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View arg0) {
                                    dialog.dismiss();
                                    dialog=null;
                                }
                            });
                        }
                    }else{
                        new Toast_layout(EnderecoBuscarActivity.this).mensagem(getString(R.string.dadosinvalidos));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CEP>> call, @NonNull Throwable t) {
                new Toast_layout(EnderecoBuscarActivity.this).mensagem(getString(R.string.errobuscarcep, t.getMessage()));
            }
        });
    }

    public static void carregaEnderecoSelecionado(Context context, String endereco, String bairro, String cidade, String estado, String cep){
        dialog.dismiss();
        dialog=null;
        Intent intcep = new Intent(context , EnderecoCEPActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("idendereco", "0");
        bundle.putString("endereco", endereco);
        bundle.putString("bairro", bairro);
        bundle.putString("cidade", cidade);
        bundle.putString("estado", estado);
        bundle.putString("cep", cep);
        intcep.putExtras(bundle);
        context.startActivity(intcep);
    }

    private void carregaEstados(){
        //Setando estado
        String[] listaestados = new String[]{
                "Estado",
                "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MG", "MS", "MT", "PA", "PB", "PE", "PI", "PR", "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO"
        };

        final List<String> estadosList = new ArrayList<>(Arrays.asList(listaestados));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> estadosArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,estadosList){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    //Altera a cor do primeiro item do spinner
                    tv.setTextColor(Color.GRAY);
                }else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        estadosArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnEstado.setAdapter(estadosArrayAdapter);
    }

    private void carregaCidades(){

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> cidadesArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,listacidades){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    //Altera a cor do primeiro item do spinner
                    tv.setTextColor(Color.GRAY);
                }else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        cidadesArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnCidade.setAdapter(cidadesArrayAdapter);
    }

    private void verificaTotalCidadesPorEstado(String estado){
        switch (estado){
            case "AC":
                totalcidade = 21;
                break;
            case "AL":
                totalcidade = 101;
                break;
            case "AM":
                totalcidade = 61;
                break;
            case "AP":
                totalcidade = 15;
                break;
            case "BA":
                totalcidade = 416;
                break;
            case "CE":
                totalcidade = 183;
                break;
            case "DF":
                totalcidade = 0;
                break;
            case "ES":
                totalcidade = 77;
                break;
            case "GO":
                totalcidade = 245;
                break;
            case "MA":
                totalcidade = 216;
                break;
            case "MG":
                totalcidade = 852;
                break;
            case "MS":
                totalcidade = 76;
                break;
            case "MT":
                totalcidade = 138;
                break;
            case "PA":
                totalcidade = 142;
                break;
            case "PB":
                totalcidade = 222;
                break;
            case "PE":
                totalcidade = 184;
                break;
            case "PI":
                totalcidade = 221;
                break;
            case "PR":
                totalcidade = 398;
                break;
            case "RJ":
                totalcidade = 91;
                break;
            case "RN":
                totalcidade = 166;
                break;
            case "RO":
                totalcidade = 51;
                break;
            case "RR":
                totalcidade = 14;
                break;
            case "RS":
                totalcidade = 495;
                break;
            case "SC":
                totalcidade = 292;
                break;
            case "SE":
                totalcidade = 74;
                break;
            case "SP":
                totalcidade = 644;
                break;
            case "TO":
                totalcidade = 138;
                break;
        }
    }
}
