package br.com.limosapp.limos.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.limosapp.limos.R;

public class AdicionaisViewHolder extends RecyclerView.ViewHolder{

    public ImageView imgMais, imgMenos;
    public TextView txtQuantidade;
    private TextView txtAdicional, txtValor;

    public AdicionaisViewHolder(View view) {
        super(view);
        imgMais = view.findViewById(R.id.imgMais);
        imgMenos = view.findViewById(R.id.imgMenos);
        txtQuantidade = view.findViewById(R.id.txtQuantidade);
        txtAdicional = view.findViewById(R.id.txtAdicional);
        txtValor = view.findViewById(R.id.txtValor);
    }

    public void setAdicional(String adicional) {
        txtAdicional.setText(adicional);
    }

    public void setValorAdicional(Double valorAdicional) {
        Locale ptBr = new Locale("pt", "BR");
        String valorString = NumberFormat.getCurrencyInstance(ptBr).format(valorAdicional);
        txtValor.setText(valorString);
    }
}
