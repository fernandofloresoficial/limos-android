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

import br.com.limosapp.limos.R;

public class NotificacaoPedidosViewHolder extends ParentViewHolder {

    View view;
    private ImageView imgpreparando, imgenviado;
    private TextView txtrestaurante, txtnpedido, txtprevisaoentrega, txtrealizado, txtpreparando, txtenviado;

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public NotificacaoPedidosViewHolder(View itemView) {
        super(itemView);
        view=itemView;
        txtrestaurante = view.findViewById(R.id.txtRestaurante);
        txtnpedido = view.findViewById(R.id.txtPedido);
        txtprevisaoentrega = view.findViewById(R.id.txtPrevisaoentrega);
        txtrealizado = view.findViewById(R.id.txtRealizado);
        imgpreparando = view.findViewById(R.id.imgPreparando);
        txtpreparando = view.findViewById(R.id.txtPreparando);
        imgenviado = view.findViewById(R.id.imgEnviado);
        txtenviado = view.findViewById(R.id.txtEnviado);
    }

    public void setDadosRestaurante(String idrestaurante){
        DatabaseReference dbrestaurantes = db.child("restaurantes").child(idrestaurante);
        dbrestaurantes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fantasia").exists() && !dataSnapshot.child("fantasia").getValue().toString().isEmpty()) txtrestaurante.setText(dataSnapshot.child("fantasia").getValue().toString());
                if (dataSnapshot.child("espera").exists() && !dataSnapshot.child("espera").getValue().toString().isEmpty()) txtprevisaoentrega.setText("Previsão de entrega: " + dataSnapshot.child("espera").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setNumeroPedido(Long npedido){
        txtnpedido.setText("N° do pedido: " + String.valueOf(npedido));
    }

    public void setStatus(String idpedido){
        DatabaseReference dbhorarios = db.child("pedidohorarios").child(idpedido);
        dbhorarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String textoentrega = "Saiu para entrega: ";

                if (dataSnapshot.child("realizado").exists() && !dataSnapshot.child("realizado").getValue().toString().isEmpty()){
                    txtrealizado.setText("Realizado: " + dataSnapshot.child("realizado").getValue().toString());
                }
                if (dataSnapshot.child("aprovado").exists() && !dataSnapshot.child("aprovado").getValue().toString().isEmpty()) {
                    imgpreparando.setImageResource(R.drawable.imgnotificacaoon);
                    txtpreparando.setText("Preparando: " + dataSnapshot.child("aprovado").getValue().toString());
                }else{
                    imgpreparando.setImageResource(R.drawable.imgnotificacaooff);
                    txtpreparando.setText("Preparando: ");
                }
                if (dataSnapshot.child("retirabalcao").exists() && !dataSnapshot.child("retirabalcao").getValue().toString().isEmpty()) {
                    if (Integer.parseInt(dataSnapshot.child("retirabalcao").getValue().toString()) == 1) textoentrega = "Liberado para retirada: ";
                }

                if (dataSnapshot.child("enviado").exists() && !dataSnapshot.child("enviado").getValue().toString().isEmpty()) {
                    imgenviado.setImageResource(R.drawable.imgnotificacaoon);
                    txtenviado.setText(textoentrega + dataSnapshot.child("enviado").getValue().toString());
                }else{
                    imgenviado.setImageResource(R.drawable.imgnotificacaooff);
                    txtenviado.setText(textoentrega);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
