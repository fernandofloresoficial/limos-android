package br.com.limosapp.limos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.limosapp.limos.util.Mascara;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.ValidarCampos;
import br.com.limosapp.limos.util.VerificaInternet;
import me.drakeet.materialdialog.MaterialDialog;

import static br.com.limosapp.limos.MainActivity.idusuario;

public class PerfilActivity extends AppCompatActivity implements View.OnClickListener {

    private final int PERMISSAO_REQUEST = 2;
    public static final int REQUEST_PERMISSIONS_CODE = 128;

    private MaterialDialog mMaterialDialog;
    ProgressDialog dialog;

    ImageView imgVoltar;
    TextView txtlogout;

    SimpleDraweeView imgfoto;
    EditText edtnome, edtemail, edttelefone, edtcpf;
    Button btnsalvar;
    String urlfoto = "";

    DatabaseReference dbusuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");
    Uri uri;
    UploadTask uploadTask;

    StorageReference storageReference;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_perfil);
        inicializar();

        storageReference = FirebaseStorage.getInstance().getReference();
        edttelefone.addTextChangedListener(new Mascara().insert("(##)#####-####", edttelefone));
        edtcpf.addTextChangedListener(new Mascara().insert("###.###.###-##", edtcpf));

        if(mAuth!=null) {
            String telefone = Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber();
            if (Objects.equals(telefone, "") || telefone == null) {
                edtemail.setEnabled(false);
                edtemail.setFocusable(false);
            } else {
                edttelefone.setEnabled(false);
                edttelefone.setFocusable(false);
            }
        }

        imgfoto.setOnClickListener(this);
        imgVoltar.setOnClickListener(this);
        txtlogout.setOnClickListener(this);
        btnsalvar.setOnClickListener(this);

        carregaUsuario(idusuario);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFoto:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        callDialog(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSAO_REQUEST);
                    }
                }else{
                    BuscarImagem();
                }
                break;
            case R.id.imgVoltar:
                finish();
                break;
            case R.id.txtLogout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.btnSalvar:
                ValidarCampos validarCampos = new ValidarCampos();
                if (!validarCampos.validarNotNull(edtnome,getString(R.string.preenchaocampo,"nome"))) {
                    return;
                }
                if (edtemail.getText().length() != 0) {
                    boolean email_valido = validarCampos.validarEmail(edtemail.getText().toString());
                    if (!email_valido) {
                        edtemail.setError(getString(R.string.emailinvalido));
                        edtemail.setFocusable(true);
                        edtemail.requestFocus();
                        return;
                    }
                }

                String telefone = "", cpf = "";
                if (edttelefone.getText().length() != 0) {
                    telefone = edttelefone.getText().toString();
                    telefone = telefone.replace("(", "");
                    telefone = telefone.replace(")", "");
                    telefone = telefone.replace("-", "");
                    boolean telefone_valido = validarCampos.validarTelefone(telefone);
                    if (!telefone_valido) {
                        edttelefone.setError(getString(R.string.telefoneinvalido));
                        edttelefone.setFocusable(true);
                        edttelefone.requestFocus();
                        return;
                    }
                }
                if (edtcpf.getText().length() != 0) {
                    boolean cpf_valido = validarCampos.validarCPF(edtcpf.getText().toString());
                    if (!cpf_valido) {
                        edtcpf.setError(getString(R.string.cpfinvalido));
                        edtcpf.setFocusable(true);
                        edtcpf.requestFocus();
                        return;
                    }
                    cpf = edtcpf.getText().toString();
                    cpf = cpf.replace(".", "");
                    cpf = cpf.replace("-", "");
                }
                Upload(idusuario, telefone, cpf);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }

    private void inicializar(){
        imgfoto = findViewById(R.id.imgFoto);
        imgVoltar = findViewById(R.id.imgVoltar);
        txtlogout = findViewById(R.id.txtLogout);
        edtnome = findViewById(R.id.edtNome);
        edtemail = findViewById(R.id.edtEmail);
        edttelefone = findViewById(R.id.edtTelefone);
        edtcpf = findViewById(R.id.edtCPF);
        btnsalvar = findViewById(R.id.btnSalvar);
    }

    private void SalvarUsuario(String id, String nome, String email, String telefone, String cpf) {
        Map<String,Object> taskMap = new HashMap<>();
        taskMap.put("nome", nome);
        taskMap.put("email", email);
        taskMap.put("telefone", telefone);
        taskMap.put("cpf", cpf);
        taskMap.put("urlfoto", urlfoto);
        dbusuarios.child(id).updateChildren(taskMap);
        new Toast_layout(this).mensagem(getString(R.string.cadastradosucesso));
        finish();
    }

    private void carregaUsuario(final String id){
         dbusuarios.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("urlfoto").exists() && !Objects.requireNonNull(dataSnapshot.child("urlfoto").getValue()).toString().isEmpty()) {
                    urlfoto = Objects.requireNonNull(dataSnapshot.child("urlfoto").getValue()).toString();
                    imgfoto.setImageURI(urlfoto);
                }
                if (dataSnapshot.child("nome").exists() && !Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString().isEmpty()) edtnome.setText(Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString());
                if (dataSnapshot.child("email").exists() && !Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString().isEmpty()) edtemail.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
                if (dataSnapshot.child("telefone").exists() && !Objects.requireNonNull(dataSnapshot.child("telefone").getValue()).toString().isEmpty())
                        edttelefone.setText(Objects.requireNonNull(dataSnapshot.child("telefone").getValue()).toString());
                if (dataSnapshot.child("cpf").exists() && !Objects.requireNonNull(dataSnapshot.child("cpf").getValue()).toString().isEmpty()) edtcpf.setText(Objects.requireNonNull(dataSnapshot.child("cpf").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("IntentReset")
    private void BuscarImagem() {
        @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selecione, "a imagem")),1);
    }

    private void callDialog(final String[] permissions){
        mMaterialDialog = new MaterialDialog(this)
                .setTitle(R.string.permissao)
                .setMessage(getString(R.string.necessariopermissao,"da galeria de fotos"))
                .setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ActivityCompat.requestPermissions(PerfilActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancelar, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

    private  void  Upload(final String id, final String telefone, final String cpf){
        if (uri != null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.aguarde, "enquanto estamos fazendo upload da foto"));
            dialog.show();
            dialog.setCancelable(false);

            final StorageReference ref = storageReference.child("usuarios/" + id + ".jpg");
            uploadTask = ref.putFile(uri);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    dialog.setProgress(((int) progress));
                }
            });

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        urlfoto = downloadUri.toString();
                        dialog.dismiss();
                        SalvarUsuario(idusuario, edtnome.getText().toString(), edtemail.getText().toString(), telefone, cpf);
                    } else {
                        new Toast_layout(PerfilActivity.this).mensagem(getString(R.string.erroupload));
                        dialog.dismiss();
                    }
                }
            });
        }
        else{
            SalvarUsuario(idusuario, edtnome.getText().toString(), edtemail.getText().toString(), telefone, cpf);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            CropImage.activity(uri).setCropShape(CropImageView.CropShape.OVAL).setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.FIT_CENTER).setMinCropResultSize(500,500).setMaxCropResultSize(1000,1000).start(this);
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                imgfoto.setImageURI(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, result.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSAO_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                BuscarImagem();
            }
        }
    }
}