package br.com.limosapp.limos.holder;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.R;

import static br.com.limosapp.limos.MainActivity.idusuario;

public class EnderecosViewHolder extends RecyclerView.ViewHolder{

    public View view;
    private TextView txtendereco, txtendereco1;
    public CheckBox chkpadrao;
    public ImageView imgeditar, imgexcluir;

    public EnderecosViewHolder(View itemView) {
        super(itemView);
        view=itemView;
        txtendereco = view.findViewById(R.id.txtEndereco);
        txtendereco1 = view.findViewById(R.id.txtEndereco1);
        chkpadrao = view.findViewById(R.id.chkPadrao);
        imgeditar = view.findViewById(R.id.imgEditar);
        imgexcluir = view.findViewById(R.id.imgExcluir);
    }

    public void setPadrao(final String idendereco){
        FirebaseDatabase.getInstance().getReference().child("usuarioenderecospadrao").orderByKey().equalTo(idusuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(idusuario).exists() && !Objects.requireNonNull(dataSnapshot.child(idusuario).getValue()).toString().isEmpty() && Objects.requireNonNull(dataSnapshot.child(idusuario).getValue()).toString().equals(idendereco)) {
                    chkpadrao.setChecked(true);
                }else{
                    chkpadrao.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setEndereco(Activity activity, String endereco, String numero, String complemento, String bairro, String cidade, String estado){
        if (!complemento.equals("")) {
            txtendereco.setText(activity.getString(R.string.enderecolistacomcomplemento, endereco, numero, complemento));
        }else {
            txtendereco.setText(activity.getString(R.string.enderecolistasemcomplemento, endereco, numero));
        }

        txtendereco1.setText(activity.getString(R.string.enderecolistacomcomplemento, bairro, cidade, estado));
    }
}
