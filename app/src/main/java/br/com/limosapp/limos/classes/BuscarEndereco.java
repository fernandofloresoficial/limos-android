package br.com.limosapp.limos.classes;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.limosapp.limos.EnderecoTrocarActivity;
import br.com.limosapp.limos.R;
import br.com.limosapp.limos.util.Toast_layout;
import br.com.limosapp.limos.util.VerificaCoordenadas;
import br.com.limosapp.limos.pojos.Endereco;

import static br.com.limosapp.limos.MainActivity.enderecomain;
import static br.com.limosapp.limos.MainActivity.idusuario;

public class BuscarEndereco {
    private Activity activity;
    private TextView txtEndereco;
    private ProgressBar pBarCarregaEndereco;
    private String idendereco, bairro, cidade;
    private boolean usarlocalizacao, atualizaenderecomain, atualizaenderecrest;

    private TextView txtCupons;
    private ImageView imgSemCupons;
    private RecyclerView rvCupons, rvRestaurantes_Shoppings;

    private Address enderecolocalizacao;

    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference dbenderecos;

    public BuscarEndereco(Activity activity, TextView txtEndereco, ProgressBar pBarCarregaEndereco, String idendereco, boolean usarlocalizacao, boolean atualizaenderecomain, boolean atualizaenderecrest, TextView txtCupons, ImageView imgSemCupons, RecyclerView rvCupons) {
        this.activity = activity;
        this.txtEndereco = txtEndereco;
        this.pBarCarregaEndereco = pBarCarregaEndereco;
        this.idendereco = idendereco;
        this.usarlocalizacao = usarlocalizacao;
        this.atualizaenderecomain = atualizaenderecomain;
        this.atualizaenderecrest = atualizaenderecrest;
        this.txtCupons = txtCupons;
        this.imgSemCupons = imgSemCupons;
        this.rvCupons = rvCupons;
    }

    public BuscarEndereco(Activity activity, TextView txtEndereco, ProgressBar pBarCarregaEndereco, String idendereco, boolean usarlocalizacao, boolean atualizaenderecomain, boolean atualizaenderecrest, RecyclerView rvRestaurantes_Shoppings) {
        this.activity = activity;
        this.txtEndereco = txtEndereco;
        this.pBarCarregaEndereco = pBarCarregaEndereco;
        this.idendereco = idendereco;
        this.usarlocalizacao = usarlocalizacao;
        this.atualizaenderecomain = atualizaenderecomain;
        this.atualizaenderecrest = atualizaenderecrest;
        this.rvRestaurantes_Shoppings = rvRestaurantes_Shoppings;
    }

     public void buscaEndereco() {

         if (idendereco.equals("")){
             txtEndereco.setText(activity.getString(R.string.carregando));
             pBarCarregaEndereco.setVisibility(View.VISIBLE);
         }

         enderecomain = new Endereco();
         if (usarlocalizacao) {
             carregaLocalizacao();
         } else {
             dbenderecos = db.child("usuarioenderecos");
             if (!idendereco.equals("")) {
                 dbenderecos.child(idusuario).child(idendereco).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshotendereco) {
                         setEndereco(dataSnapshotendereco.child("endereco").getValue().toString(), dataSnapshotendereco.child("numero").getValue().toString(), dataSnapshotendereco.child("complemento").getValue().toString(), dataSnapshotendereco.child("bairro").getValue().toString(), dataSnapshotendereco.child("cidade").getValue().toString(), dataSnapshotendereco.child("estado").getValue().toString(), dataSnapshotendereco.child("cep").getValue().toString());
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
             } else {
                 DatabaseReference dbenderecopadrao = db.child("usuarioenderecospadrao");
                 dbenderecopadrao.orderByKey().equalTo(idusuario).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if (dataSnapshot.child(idusuario).exists() && !dataSnapshot.child(idusuario).getValue().toString().isEmpty()) {
                             if (dataSnapshot.getKey() != null) {
                                 idendereco = dataSnapshot.child(idusuario).getValue().toString();
                                 dbenderecos.child(idusuario).child(idendereco).addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshotendereco) {
                                         setEndereco(dataSnapshotendereco.child("endereco").getValue().toString(), dataSnapshotendereco.child("numero").getValue().toString(), dataSnapshotendereco.child("complemento").getValue().toString(), dataSnapshotendereco.child("bairro").getValue().toString(), dataSnapshotendereco.child("cidade").getValue().toString(), dataSnapshotendereco.child("estado").getValue().toString(), dataSnapshotendereco.child("cep").getValue().toString());
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 });
                             }
                         }
                         if (idendereco.equals("")) {
                             new Toast_layout(activity).mensagem(activity.getString(R.string.enderecopadraonaoencontrado));
                             activity.startActivity(new Intent(activity, EnderecoTrocarActivity.class));
                             txtEndereco.setText(activity.getString(R.string.naoconseguimoslocalizar));
                             pBarCarregaEndereco.setVisibility(View.INVISIBLE);
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
             }
         }
    }

    private void carregaLocalizacao() {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = new BuscarEnderecoLocalizacao().buscaCoordenadas(activity);
            if (location != null) {
                enderecolocalizacao = new VerificaCoordenadas().verificaCoordenadasPorLatLon(activity, location.getLatitude(), location.getLongitude());
                bairro = enderecolocalizacao.getSubLocality();
                if(bairro == null || bairro.equals("")){
                    bairro = null;
                    cidade = null;
                }else {
                    if (enderecolocalizacao.getLocality() == null) {
                        cidade = enderecolocalizacao.getSubAdminArea();
                    } else {
                        cidade = enderecolocalizacao.getLocality();
                    }
                }
            } else {
                bairro = null;
                cidade = null;
            }
            setEndereco(null, null, null, bairro, cidade, null, null);
        }
    }

    private void setEndereco(String endereco, String numero, String complemento, String bairro, String cidade, String estado, String cep){
        this.bairro = bairro;
        this.cidade = cidade;

        String enderecomostrar, enderecocoordenada;

        if (bairro == null){
            enderecomostrar = activity.getString(R.string.naoconseguimoslocalizar);
        }else{
            if (usarlocalizacao) {
                enderecomostrar = activity.getString(R.string.proximode, bairro);
            } else {
                enderecomostrar = endereco + ", " + numero;
            }
            enderecocoordenada = endereco + ", " + numero + ", " + bairro + ", " + cidade + ", " + estado;
            enderecolocalizacao = new VerificaCoordenadas().verificaCoordenadasPorEnd(activity, enderecocoordenada);
            if (enderecolocalizacao != null) {
                enderecomain.setLatitudeatual(enderecolocalizacao.getLatitude());
                enderecomain.setLongitudeatual(enderecolocalizacao.getLongitude());
            }
        }

        enderecomain.setEndereco(endereco);
        enderecomain.setNumero(numero);
        enderecomain.setComplemento(complemento);
        enderecomain.setBairro(bairro);
        enderecomain.setCidade(cidade);
        enderecomain.setEstado(estado);
        enderecomain.setCep(cep);
        enderecomain.setEnderecomostrar(enderecomostrar);

        txtEndereco.setText(enderecomostrar);
        pBarCarregaEndereco.setVisibility(View.INVISIBLE);

        if (atualizaenderecomain) {
            new Cupons(activity, txtCupons, imgSemCupons, rvCupons).verificaTemCupons();
        } else if (atualizaenderecrest) {
            new Restaurantes(activity, rvRestaurantes_Shoppings).carregarRestaurantes(false, "");
        } else {
            new Shoppings(activity, rvRestaurantes_Shoppings).carregarShoppings();
        }
    }
}