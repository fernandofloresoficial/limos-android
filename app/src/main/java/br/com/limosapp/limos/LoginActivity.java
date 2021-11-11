package br.com.limosapp.limos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.VerificaInternet;
import br.com.limosapp.limos.firebase.UsuariosFirebase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    Button btnTelefone, btnEmail;
    //public static final int SIGN_IN_CODE = 777;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;
    //DatabaseReference dbusuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");

    String idusuario;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnTelefone = findViewById(R.id.btnTelefone);
        btnEmail = findViewById(R.id.btnEmail);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("Login"));

        btnTelefone.setOnClickListener(this);
        btnEmail.setOnClickListener(this);

        //Permissões
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            int PERMISSAO_REQUEST_READ_EXTERNAL_STORAGE = 1;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSAO_REQUEST_READ_EXTERNAL_STORAGE);
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        googleApiClient = new GoogleApiClient.Builder(this)
//                //.enableAutoManage(this,this )
//                .addConnectionCallbacks(this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == SIGN_IN_CODE){
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
//    }

//    private void handleSignInResult(GoogleSignInResult result) {
//        if(result.isSuccess()){
//            firebaseAuthWithGoogle(result.getSignInAccount());
//        }else{
//            new Toast_layout(this).mensagem("A sessão não pode ser iniciada");
//        }
//    }

//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//
//        pbgmail.setVisibility(View.VISIBLE);
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        pbgmail.setVisibility(View.GONE);
//
//                        if (!task.isSuccessful()) {
//                            new Toast_layout(LoginActivity.this).mensagem("A sessão não pode ser iniciada");
//                        }else{
//                            FirebaseUser user = task.getResult().getUser();
//                            verifUsuarioCadastrado(user.getDisplayName(), user.getEmail());
//                        }
//                    }
//                });
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null){
            mAuth.removeAuthStateListener(firebaseAuthListener);
        }
        //googleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTelefone:
                startActivity(new Intent(getApplicationContext(), LoginTelActivity.class));
                break;
            case R.id.btnEmail:
                startActivity(new Intent(getApplicationContext(), LoginEmailActivity.class));
                break;
        }
    }

//    private void verifUsuarioCadastrado(final String nome, final String email){
//        Query queryusuarios;
//        queryusuarios = dbusuarios.orderByChild("email").equalTo(email);
//        queryusuarios.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    idusuario = postSnapshot.getKey();
//                }
//                if (idusuario == null){
//                    salvarUsuario(nome, email);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        int PERMISSAO_REQUEST_ACCESS_FINE_LOCATION = 2;
//        switch (requestCode) {
//            case PERMISSAO_REQUEST_READ_EXTERNAL_STORAGE:
//                /*
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //premissão concedida
//                } else {
//
//                }
//                */
//                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSAO_REQUEST_ACCESS_FINE_LOCATION);
//                }
//                break;
//            case PERMISSAO_REQUEST_ACCESS_FINE_LOCATION:
//                return;
//
//        }
//    }

//    public void salvarUsuario(String nome, String email) {
//        UsuariosFirebase usuario = new UsuariosFirebase();
//        usuario.setNome(nome);
//        usuario.setEmail(email);
//        usuario.setTelefone("");
//        usuario.setCpf("");
//        usuario.setUrlfoto("");
//        usuario.setCashbacksaldo(0);
//        idusuario = dbusuarios.push().getKey();
//        dbusuarios.child(Objects.requireNonNull(idusuario)).setValue(usuario);
//    }

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
//        startActivityForResult(intent,SIGN_IN_CODE);
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
}