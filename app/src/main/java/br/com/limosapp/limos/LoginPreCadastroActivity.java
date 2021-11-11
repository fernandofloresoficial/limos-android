package br.com.limosapp.limos;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import br.com.limosapp.limos.firebase.UsuariosFirebase;
import br.com.limosapp.limos.util.ValidarCampos;

public class LoginPreCadastroActivity extends AppCompatActivity implements View.OnClickListener  {

    EditText edtnome, edtemail, edtTelefone;
    TextInputLayout inputTelefone, inputEmail;
    Button btnLogin;

    String idusuario, telefone, email;

    DatabaseReference dbusuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_precadastro);

        inicializar();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser().getPhoneNumber() != null){
            telefone = Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber();
            telefone =  Objects.requireNonNull(telefone).replace("+55", "");
            edtTelefone.setVisibility(View.GONE);
            inputTelefone.setVisibility(View.GONE);
        }else{
            email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
            edtemail.setVisibility(View.GONE);
            inputEmail.setVisibility(View.GONE);
        }

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnLogin:
                ValidarCampos validarCampos = new ValidarCampos();
                if (!validarCampos.validarNotNull(edtnome, getString(R.string.preenchaocampo, "nome"))) {
                    return;
                }
                if (email == null){
                    if (!validarCampos.validarNotNull(edtemail, getString(R.string.preenchaocampo, "email"))) { return; }

                    if (edtemail.getText().length() != 0) {
                        boolean email_valido = validarCampos.validarEmail(edtemail.getText().toString());
                        if (!email_valido) {
                            edtemail.setError(getString(R.string.emailinvalido));
                            edtemail.setFocusable(true);
                            edtemail.requestFocus();
                            return;
                        }
                    }

                    email = edtemail.getText().toString();
                }

                if (telefone == null){
                    if (!validarCampos.validarNotNull(edtTelefone, getString(R.string.preenchaocampo, "telefone"))) { return; }

                    if (edtTelefone.getText().length() != 0) {
                        String tel;
                        tel = edtTelefone.getText().toString();
                        tel = tel.replace("(", "");
                        tel = tel.replace(")", "");
                        tel = tel.replace("-", "");
                        boolean telefone_valido = validarCampos.validarTelefone(tel);
                        if (!telefone_valido) {
                            edtTelefone.setError(getString(R.string.telefoneinvalido));
                            edtTelefone.setFocusable(true);
                            edtTelefone.requestFocus();
                            return;
                        }

                        telefone = tel;
                    }
                }


                salvarUsuario(edtnome.getText().toString());
                break;
        }
    }

    private void inicializar(){
        edtnome = findViewById(R.id.edtNome);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtemail = findViewById(R.id.edtEmail);
        btnLogin = findViewById(R.id.btnLogin);
        inputTelefone = findViewById(R.id.textInputTelefone);
        inputEmail = findViewById(R.id.edtInputEmail);
    }

    private void salvarUsuario(String nome) {

        UsuariosFirebase usuario = new UsuariosFirebase();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setCpf("");
        usuario.setUrlfoto("");
        idusuario = dbusuarios.push().getKey();
        dbusuarios.child(Objects.requireNonNull(idusuario)).setValue(usuario);

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("Login"));
        startActivity(new Intent(LoginPreCadastroActivity.this, MainActivity.class));
        finish();
    }
}
