package br.com.limosapp.limos.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import br.com.limosapp.limos.R;

public class BrindesViewHolder extends RecyclerView.ViewHolder{

    public View view;
    public TextView txtBrindeProduto;
    private TextView txtBrindeDescricao;
    private SimpleDraweeView imgBrindeFoto;

    public BrindesViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        txtBrindeProduto = view.findViewById(R.id.txtBrindeProduto);
        txtBrindeDescricao = view.findViewById(R.id.txtBrindeDescricao);
        imgBrindeFoto = view.findViewById(R.id.imgBrindeFoto);
    }

    public void setProduto(String Produto) {
        txtBrindeProduto.setText(Produto);
    }

    public void setDescricao(String Descricao) {
        txtBrindeDescricao.setText(Descricao);
    }

    public void setFoto(String Foto) {
        imgBrindeFoto.setImageURI(Foto);
    }
}
