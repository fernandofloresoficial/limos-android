package br.com.limosapp.limos.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.limosapp.limos.R;

public  class ProdutosViewHolder extends RecyclerView.ViewHolder {

    public View view;
    SimpleDraweeView imgfotoproduto;
    TextView txtproduto, txtdescricao, txtserve, txttempo, txtpreco, txtapartir;
    Locale ptBr = new Locale("pt", "BR");

    public ProdutosViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        imgfotoproduto = view.findViewById(R.id.imgFotoProduto);
        txtproduto = view.findViewById(R.id.txtProduto);
        txtdescricao = view.findViewById(R.id.txtDescricao);
        txtserve = view.findViewById(R.id.txtServe);
        txttempo = view.findViewById(R.id.txtTempo);
        txtpreco = view.findViewById(R.id.txtPreco);
        txtapartir = view.findViewById(R.id.txtApartir);
    }

    public void setProduto(String produto) {
        txtproduto.setText(produto);
    }

    public void setDescricao(String descricao) {
        txtdescricao.setText(descricao);
    }

    public void setServe(Long serve) {
        txtserve.setText(String.valueOf(serve));
    }

    public void setTempo(String tempo) {
        txttempo.setText(tempo);
    }

    public void setPreco(Double preco) {
        txtpreco.setText(NumberFormat.getCurrencyInstance(ptBr).format(preco));
    }

    public void setApartir(String apartir) {
        if (apartir.equals("1")) {
            txtapartir.setVisibility(View.VISIBLE);
        } else {
            txtapartir.setVisibility(View.INVISIBLE);
        }
    }

    public void setFoto(String foto) {
        imgfotoproduto.setImageURI(foto);
    }
}
