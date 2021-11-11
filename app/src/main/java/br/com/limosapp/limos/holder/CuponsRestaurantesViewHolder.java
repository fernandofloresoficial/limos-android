package br.com.limosapp.limos.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import br.com.limosapp.limos.R;

public  class CuponsRestaurantesViewHolder extends RecyclerView.ViewHolder {
    public View view;
    private SimpleDraweeView imgFotoPerfil;
    public TextView txtFantasia;
    private TextView txtDesconto;

    private DatabaseReference dbrestaurante = FirebaseDatabase.getInstance().getReference().child("restaurantes");
    private DatabaseReference dbcupons = FirebaseDatabase.getInstance().getReference().child("restaurantecupons");

    public CuponsRestaurantesViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        imgFotoPerfil = view.findViewById(R.id.imgFotoPerfil);
        txtFantasia = view.findViewById(R.id.txtFantasia);
        txtDesconto = view.findViewById(R.id.txtDesconto);

        dbrestaurante.keepSynced(true);
        dbcupons.keepSynced(true);
    }

    public void setRestaurante(final String idrestaurante){
        dbrestaurante.child(idrestaurante).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fantasia").exists() && !Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString().isEmpty()){ txtFantasia.setText(Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString());}
                if (dataSnapshot.child("fotoperfil").exists() && !Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString().isEmpty()){ imgFotoPerfil.setImageURI(Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString()); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setDesconto(final String idrestaurante){
        //querycupons = dbcupons.child("cupons").orderByChild("validademseg").startAt(System.currentTimeMillis());
        Query querycuponsdesc = dbcupons.child(idrestaurante).child("cupons").orderByChild("validademseg").startAt(System.currentTimeMillis());
        querycuponsdesc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int desconto=0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    int bancoativo = Integer.parseInt(Objects.requireNonNull(postSnapshot.child("ativo").getValue()).toString());
                    int desconto2 = Integer.parseInt(Objects.requireNonNull(postSnapshot.child("desconto").getValue()).toString());
                    if (bancoativo == 1 && desconto < desconto2) {
                        desconto = Integer.parseInt(Objects.requireNonNull(postSnapshot.child("desconto").getValue()).toString());
                    }
                }
                txtDesconto.setText(view.getContext().getString(R.string.percentual, desconto+"%"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
