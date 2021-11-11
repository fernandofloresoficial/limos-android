package br.com.limosapp.limos.holder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import br.com.limosapp.limos.R;

public class CashbackExtratoViewHolder extends ParentViewHolder {

    View view;
    private TextView txtFantasia, txtPedido, txtData, txtValorCashback;
    private ImageView imgExtratoCashBack;
    private DatabaseReference dbrestaurantes = FirebaseDatabase.getInstance().getReference();

    public CashbackExtratoViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        txtFantasia = view.findViewById(R.id.txtFantasia);
        txtPedido = view.findViewById(R.id.txtPedido);
        txtData = view.findViewById(R.id.txtData);
        txtValorCashback = view.findViewById(R.id.txtValorCashback);
        imgExtratoCashBack = view.findViewById(R.id.imgExtratoCashback);

    }

    public void setRestaurante(String idrestaurante) {

        dbrestaurantes.child("restaurantes").child(idrestaurante).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fantasia").exists()) txtFantasia.setText(Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setPedido(Integer pedido) {
        DecimalFormat df = new DecimalFormat("00000000");
        txtPedido.setText(df.format(pedido));
    }

    public void setData(String data) {
        txtData.setText(data);
    }

    public void setValor(Double valor) {
        Locale ptbr = new Locale("pt", "BR");
        txtValorCashback.setText(NumberFormat.getCurrencyInstance(ptbr).format(valor));
        if (valor < 0){
            txtValorCashback.setTextColor(view.getResources().getColor(R.color.vermelholimos));
        }
    }

    public void setImage(Double valor) {
        Locale ptbr = new Locale("pt", "BR");
        if (valor < 0) {
            imgExtratoCashBack.setBackgroundResource(R.drawable.imgextratonegativo);
        }else{
            imgExtratoCashBack.setBackgroundResource(R.drawable.imgextratopositivo);
        }
    }
}
