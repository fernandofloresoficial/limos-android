package br.com.limosapp.limos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
import br.com.limosapp.limos.util.Mascara;
import br.com.limosapp.limos.util.VerificaInternet;

public class LoginTelActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txttelefone;
    Button btnentrar;
    ProgressBar pbtel;

    FirebaseAuth mAuth;
    DatabaseReference dbusuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    static final String TAG = "PhoneAuthActivity";

    String idusuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tel);
        inicializar();

        txttelefone.addTextChangedListener(new Mascara().insert("(##)#####-####", txttelefone));
        btnentrar.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                pbtel.setVisibility(View.INVISIBLE);
                entrarComCodigo(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                txttelefone.setEnabled(true);
                pbtel.setVisibility(View.INVISIBLE);
                btnentrar.setVisibility(View.VISIBLE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    txttelefone.setError(getString(R.string.sms));
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Não foi possível enviar o SMS, favor tentar mais tarde.",
                            Snackbar.LENGTH_SHORT).show();
                    txttelefone.setError(getString(R.string.sms));
                }
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                txttelefone.setError(getString(R.string.sms));
                txttelefone.setEnabled(true);
                pbtel.setVisibility(View.INVISIBLE);
                btnentrar.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEntrar:
                String telefone = txttelefone.getText().toString();
                telefone = telefone.replace("(", "");
                telefone = telefone.replace(")", "");
                telefone = telefone.replace("-", "");

                if (!validarNumeroTelefone(telefone)) {
                    return;
                }

                telefone = "+55" + telefone;
                pbtel.setVisibility(View.VISIBLE);
                btnentrar.setVisibility(View.INVISIBLE);
                txttelefone.setEnabled(false);
                iniciarVerificacao(telefone);
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

    private void entrarComCodigo(PhoneAuthCredential credential) {
        pbtel.setVisibility(View.VISIBLE);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pbtel.setVisibility(View.INVISIBLE);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            String telefone = user.getPhoneNumber();
                            telefone =  telefone.replace("+55", "");
                            verifUsuarioCadastrado(telefone);
                        }
                    }
                });
    }


    private void iniciarVerificacao(String telefone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                telefone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    private boolean validarNumeroTelefone(String telefone) {
        if (TextUtils.isEmpty(telefone)) {
            txttelefone.setError(getString(R.string.telefoneinvalido));
            return false;
        }
        return true;
    }

    private void inicializar(){
        txttelefone = findViewById(R.id.txtTelefone);
        btnentrar = findViewById(R.id.btnEntrar);
        pbtel = findViewById(R.id.pBarTel);
    }

    private void verifUsuarioCadastrado(final String telefone){
        Query queryusuarios;
        queryusuarios = dbusuarios.orderByChild("telefone").equalTo(telefone);
        queryusuarios.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    idusuario = postSnapshot.getKey();
                }
                if (idusuario == null){
                    startActivity(new Intent(LoginTelActivity.this, LoginPreCadastroActivity.class));
                }else{
                    startActivity(new Intent(LoginTelActivity.this, MainActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
