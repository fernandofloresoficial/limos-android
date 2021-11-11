package br.com.limosapp.limos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.limosapp.limos.firebase.IndicarRestauranteFirebase;
import br.com.limosapp.limos.firebase.RestaurantesFirebase;
import br.com.limosapp.limos.holder.IndicarRestauranteViewHolder;
import br.com.limosapp.limos.util.Mascara;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.ValidarCampos;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.MainActivity.enderecomain;
import static br.com.limosapp.limos.MainActivity.idusuario;
import static br.com.limosapp.limos.MainActivity.nomeusuario;

public class IndicarRestauranteActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView imgVoltar;
    TextView txtTitulo;
    RecyclerView rvIndicarRestaurante;
    EditText edtBuscar, edtNome, edtTelefone;
    Button btnEnviar;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    final String dataFormatada = dateFormat.format(new Date());

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbrestaurantes = db.child("restaurantes");
    DatabaseReference dbrestaurantesindicados = db.child("usuarioindicacoes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicar_restaurante);

        inicializar();
        txtTitulo.setText(R.string.titulo_activity_indicar_restaurante);

        edtTelefone.addTextChangedListener(new Mascara().insert("(##)####-####", edtTelefone));

        dbrestaurantes.keepSynced(true);
        dbrestaurantesindicados.keepSynced(true);

        rvIndicarRestaurante.setHasFixedSize(true);
        rvIndicarRestaurante.setLayoutManager(new LinearLayoutManager(this));

        imgVoltar.setOnClickListener(this);
        btnEnviar.setOnClickListener(this);

        carregarRestaurantes(dbrestaurantes.orderByChild("ativo_cidade").equalTo("0_"+enderecomain.getCidade()));

        edtBuscar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == KeyEvent.ACTION_DOWN ) {

                    String filtropadrao = "0_"+enderecomain.getCidade();
                    String restaurantebuscar = edtBuscar.getText().toString();
                    if(!restaurantebuscar.equals("")){
                        carregarRestaurantes(dbrestaurantes.orderByChild("ativo_fantasia").startAt(filtropadrao+"_"+restaurantebuscar).endAt(filtropadrao+"_"+restaurantebuscar + "\uf8ff"));
                    }else{
                        carregarRestaurantes(dbrestaurantes.orderByChild("ativo_fantasia").startAt(filtropadrao).endAt(filtropadrao + "\uf8ff"));
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
        new VerificaInternet().verificaConexao(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVoltar:
                finish();
                break;
            case R.id.btnEnviar:
                ValidarCampos validarCampos = new ValidarCampos();
                if (!validarCampos.validarNotNull(edtNome,getString(R.string.preenchaocampo, "nome"))) {
                    return;
                }
                String telefone = "";
                if (edtTelefone.getText().length() != 0) {
                    telefone = edtTelefone.getText().toString();
                    telefone = telefone.replace("(", "");
                    telefone = telefone.replace(")", "");
                    telefone = telefone.replace("-", "");
                    boolean telefone_valido = validarCampos.validarTelefoneFixo(telefone);
                    if (!telefone_valido) {
                        edtTelefone.setError(getString(R.string.telefoneinvalido));
                        edtTelefone.setFocusable(true);
                        edtTelefone.requestFocus();
                        return;
                    }
                }
                salvarIndicacaoUsuario(MainActivity.idusuario, telefone);
                break;

        }
    }

    private void inicializar(){
        imgVoltar = findViewById(R.id.imgVoltar);
        rvIndicarRestaurante = findViewById(R.id.rvIndicarRestaurante);
        edtBuscar = findViewById(R.id.edtBuscar);
        edtNome = findViewById(R.id.edtNome);
        edtTelefone = findViewById(R.id.edtTelefone);
        btnEnviar = findViewById(R.id.btnEnviar);
        txtTitulo = findViewById(R.id.txtTitulo);
    }

    private void carregarRestaurantes(Query queryindicarrestaurante){
        FirebaseRecyclerAdapter<RestaurantesFirebase, IndicarRestauranteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RestaurantesFirebase, IndicarRestauranteViewHolder>(
                RestaurantesFirebase.class,
                R.layout.recyclerview_indicar_restaurante,
                IndicarRestauranteViewHolder.class,
                queryindicarrestaurante
        ) {
            @Override
            protected void populateViewHolder(IndicarRestauranteViewHolder viewHolder, RestaurantesFirebase model, int position) {
                final String idrestaurante = getRef(position).getKey();
                viewHolder.setRestaurante(model.getFantasia());
                viewHolder.setCidade(model.getCidade());
                viewHolder.setIndicado(idusuario, idrestaurante);

                viewHolder.btnIndicar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        salvarIndicacaoRestaurante(MainActivity.idusuario, idrestaurante);
                    }
                });

            }
        };
        rvIndicarRestaurante.setAdapter(firebaseRecyclerAdapter);
    }

    private void salvarIndicacaoRestaurante(final String idusuario, final String idrestaurante) {
        db.child("usuarios").child(idusuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("nome").exists() && !Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString().isEmpty()) {
                    dbrestaurantesindicados = db.child("restauranteindicacoes").child(idrestaurante).child(idusuario);
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("data", dataFormatada);
                    taskMap.put("nome", Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString());
                    dbrestaurantesindicados.updateChildren(taskMap);

                    new Toast_layout(IndicarRestauranteActivity.this).mensagem("Obrigado pela sua indicação");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void salvarIndicacaoUsuario(final String idusuario, final String telefone) {
        IndicarRestauranteFirebase indicarrestauranteFB = new IndicarRestauranteFirebase();
        indicarrestauranteFB.setData(dataFormatada);
        indicarrestauranteFB.setFantasia(edtNome.getText().toString());
        indicarrestauranteFB.setNomeusuario(nomeusuario);
        indicarrestauranteFB.setTelefone(telefone);
        indicarrestauranteFB.setUsuario(idusuario);
        String idindicacao = dbrestaurantesindicados.push().getKey();
        dbrestaurantesindicados.child(Objects.requireNonNull(idindicacao)).setValue(indicarrestauranteFB);

        new Toast_layout(IndicarRestauranteActivity.this).mensagem("Obrigado pela sua indicação");
        edtNome.setText("");
        edtTelefone.setText("");
    }
}