package br.com.limosapp.limos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.ValidarCampos;
import br.com.limosapp.limos.util.VerificaInternet;

public class LoginEmailActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editEmail, editSenha;
    Button btnEntrar;
    ProgressBar pbEmail;

    FirebaseAuth mAuth;
    DatabaseReference dbusuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");
    static final String TAG = "EmailAuth";

    String idusuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        inicializar();

        btnEntrar.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEntrar:
                ValidarCampos validarCampos = new ValidarCampos();

                if (editEmail.getText().length() != 0) {
                    boolean email_valido = validarCampos.validarEmail(editEmail.getText().toString());
                    if (!email_valido) {
                        editEmail.setError(getString(R.string.emailinvalido));
                        editEmail.setFocusable(true);
                        editEmail.requestFocus();
                        return;
                    }
                }

                if (editSenha.getText().length() < 6) {
                    editSenha.setError(getString(R.string.senha));
                    editSenha.setFocusable(true);
                    editSenha.requestFocus();
                    return;
                }

                pbEmail.setVisibility(View.VISIBLE);
                btnEntrar.setVisibility(View.INVISIBLE);
                editEmail.setEnabled(false);
                editSenha.setEnabled(false);
                criarEmail(editEmail.getText().toString(), editSenha.getText().toString());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void criarEmail(final String email, final String senha) {
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pbEmail.setVisibility(View.INVISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            verifUsuarioCadastrado(Objects.requireNonNull(user).getEmail());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            entrarComEmail(email, senha);
                        }
                    }
                });
    }

    private void entrarComEmail(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            pbEmail.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            verifUsuarioCadastrado(Objects.requireNonNull(user).getEmail());
                        } else {
                            pbEmail.setVisibility(View.INVISIBLE);
                            btnEntrar.setVisibility(View.VISIBLE);
                            editEmail.setEnabled(true);
                            editSenha.setEnabled(true);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            new Toast_layout(LoginEmailActivity.this).mensagem("Não foi possível logar com este email e senha");
                        }

                        // ...
                    }
                });
    }

    private void inicializar(){
        editEmail = findViewById(R.id.edtEmail);
        editSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        pbEmail = findViewById(R.id.pBarEmail);
    }

    private void verifUsuarioCadastrado(final String email){
        Query queryusuarios;
        queryusuarios = dbusuarios.orderByChild("email").equalTo(email);
        queryusuarios.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    idusuario = postSnapshot.getKey();
                }
                if (idusuario == null){
                    startActivity(new Intent(LoginEmailActivity.this, LoginPreCadastroActivity.class));
                }else{
                    startActivity(new Intent(LoginEmailActivity.this, MainActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
