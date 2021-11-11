package br.com.limosapp.limos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.firebase.EnderecosFirebase;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.ValidarCampos;
import br.com.limosapp.limos.util.VerificaInternet;

import static br.com.limosapp.limos.MainActivity.idusuario;
import static br.com.limosapp.limos.MainActivity.inserirendereco;

public class EnderecoCEPActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imgVoltar;
    TextView txtTitulo, txtCEP;    
    EditText edtEstado, edtCidade, edtBairro, edtEndereco, edtNumero, edtComplemento;
    CheckBox chkEnderecoPadrao;
    Button btnSalvar;
    String idendereco;
    boolean erapadrao;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbenderecos, dbenderecopadrao;
    ValidarCampos validarCampos = new ValidarCampos();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco_cep);

        inicializar();

        dbenderecos = FirebaseDatabase.getInstance().getReference().child("usuarioenderecos").child(idusuario);
        dbenderecopadrao = FirebaseDatabase.getInstance().getReference().child("usuarioenderecospadrao");
        db.keepSynced(true);
        dbenderecos.keepSynced(true);
        dbenderecopadrao.keepSynced(true);

        Intent intent1 = getIntent();
        Bundle bundle = intent1.getExtras();
        idendereco = Objects.requireNonNull(bundle).getString("idendereco");
        txtCEP.setText(bundle.getString("cep"));
        edtEndereco.setText( bundle.getString("endereco"));
        edtBairro.setText(bundle.getString("bairro"));
        edtCidade.setText(bundle.getString("cidade"));
        edtEstado.setText(bundle.getString("estado"));

        edtEndereco.setFocusable(false);
        edtBairro.setFocusable(false);
        edtCidade.setFocusable(false);
        edtEstado.setFocusable(false);

        if (idendereco.equals(Integer.toString(0))) {
            txtTitulo.setText(R.string.titulo_activity_endereco_cep);
        } else{
            txtTitulo.setText(R.string.titulo_activity_endereco_cep_alterar);
        }

        imgVoltar.setOnClickListener(this);
        btnSalvar.setOnClickListener(this);

        carregaEndereco(idendereco);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVoltar:
                finish();
                break;
            case R.id.btnSalvar:
                if (!validarCampos.validarNotNull(edtEndereco, getString(R.string.preenchaocampo, "endereço"))) {
                    return;
                }
                if (!validarCampos.validarNotNull(edtNumero, getString(R.string.preenchaocampo, "número"))) {
                    return;
                }
                salvarEndereco(idendereco, txtCEP.getText().toString(), edtEndereco.getText().toString(), edtNumero.getText().toString(), edtComplemento.getText().toString(), edtBairro.getText().toString(), edtCidade.getText().toString(), edtEstado.getText().toString(), chkEnderecoPadrao.isChecked(), idusuario);

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
        txtCEP = findViewById(R.id.txtCEP);
        edtEstado = findViewById(R.id.edtEstado);
        edtCidade = findViewById(R.id.edtCidade);
        edtBairro = findViewById(R.id.edtBairro);
        edtEndereco = findViewById(R.id.edtEndereco);
        edtNumero = findViewById(R.id.edtNumero);
        edtComplemento = findViewById(R.id.edtComplemento);
        chkEnderecoPadrao = findViewById(R.id.chkEnderecoPadrao);
        btnSalvar = findViewById(R.id.btnSalvar);
    }

    private void salvarEndereco(String id, String cep, String endereco, String numero, String complemento, String bairro, String cidade, String estado, boolean enderecopadrao, String idusuario) {
        EnderecosFirebase enderecoFB = new EnderecosFirebase();
        enderecoFB.setCep(cep);
        enderecoFB.setEndereco(endereco);
        enderecoFB.setNumero(numero);
        enderecoFB.setComplemento(complemento);
        enderecoFB.setBairro(bairro);
        enderecoFB.setCidade(cidade);
        enderecoFB.setEstado(estado);
        enderecoFB.setPais("Brasil");
        enderecoFB.setUsuario(idusuario);
        if (id.equals(Integer.toString(0))) {
            idendereco = dbenderecos.push().getKey();
            dbenderecos.child(Objects.requireNonNull(idendereco)).setValue(enderecoFB);
        } else {
            dbenderecos.child(id).setValue(enderecoFB);
        }

        if(enderecopadrao){
            dbenderecopadrao.child(idusuario).setValue(idendereco);
        }else{
            if (erapadrao) {
                dbenderecopadrao.child(idusuario).removeValue();
            }
        }

        new Toast_layout(this).mensagem(getString(R.string.cadastradosucesso));

        if (inserirendereco){
            MainActivity.idendereco = idendereco;
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("Endereco"));
        finish();
    }

    private void carregaEndereco(final String id){
        dbenderecos.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("cep").exists() && !Objects.requireNonNull(dataSnapshot.child("cep").getValue()).toString().isEmpty()) txtCEP.setText(Objects.requireNonNull(dataSnapshot.child("cep").getValue()).toString());
                if (dataSnapshot.child("estado").exists() && !Objects.requireNonNull(dataSnapshot.child("estado").getValue()).toString().isEmpty()) edtEstado.setText(Objects.requireNonNull(dataSnapshot.child("estado").getValue()).toString());
                if (dataSnapshot.child("cidade").exists() && !Objects.requireNonNull(dataSnapshot.child("cidade").getValue()).toString().isEmpty()) edtCidade.setText(Objects.requireNonNull(dataSnapshot.child("cidade").getValue()).toString());
                if (dataSnapshot.child("bairro").exists() && !Objects.requireNonNull(dataSnapshot.child("bairro").getValue()).toString().isEmpty()) edtBairro.setText(Objects.requireNonNull(dataSnapshot.child("bairro").getValue()).toString());
                if (dataSnapshot.child("endereco").exists() && !Objects.requireNonNull(dataSnapshot.child("endereco").getValue()).toString().isEmpty()) edtEndereco.setText(Objects.requireNonNull(dataSnapshot.child("endereco").getValue()).toString());
                if (dataSnapshot.child("numero").exists() && !Objects.requireNonNull(dataSnapshot.child("numero").getValue()).toString().isEmpty()) edtNumero.setText(Objects.requireNonNull(dataSnapshot.child("numero").getValue()).toString());
                if (dataSnapshot.child("complemento").exists() && !Objects.requireNonNull(dataSnapshot.child("complemento").getValue()).toString().isEmpty()) edtComplemento.setText(Objects.requireNonNull(dataSnapshot.child("complemento").getValue()).toString());

                dbenderecopadrao.orderByKey().equalTo(idendereco).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(idendereco).exists() && !Objects.requireNonNull(dataSnapshot.child(idendereco).getValue()).toString().isEmpty()) {
                            chkEnderecoPadrao.setChecked(true);
                            erapadrao = true;
                        }else{
                            chkEnderecoPadrao.setChecked(false);
                            erapadrao = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}