package br.com.limosapp.limos.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.limosapp.limos.R;

public class OpcionaisViewHolder extends RecyclerView.ViewHolder{

    View view;
    private TextView txtvalor;
    private TextView txtopcional;
    public RadioButton radioOpcional;

    public OpcionaisViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        txtopcional = view.findViewById(R.id.txtOpcional);
        txtvalor = view.findViewById(R.id.txtValor);
        radioOpcional = view.findViewById(R.id.radioOpcional);
    }

    public void setOpcional(String opcional) {
        txtopcional.setText(opcional);
    }

    public void setValor(Double valorOpcional) {
        String valorString;
        if ((valorOpcional) > 0.0) {
            Locale ptBr = new Locale("pt", "BR");
            valorString = NumberFormat.getCurrencyInstance(ptBr).format(valorOpcional);
        }else{
            valorString = "";
        }
        txtvalor.setText(valorString);
    }
}
