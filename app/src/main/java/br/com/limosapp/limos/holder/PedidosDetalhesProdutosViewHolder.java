package br.com.limosapp.limos.holder;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.limosapp.limos.R;

public class PedidosDetalhesProdutosViewHolder extends ParentViewHolder {

    View view;
    private TextView txtqt, txtproduto, txtcomplemento, txtpreco;
    private String obsproduto;
    private Locale ptbr = new Locale("pt", "BR");

    public PedidosDetalhesProdutosViewHolder(View itemView) {
        super(itemView);
        view=itemView;
        txtqt = view.findViewById(R.id.txtQuantidade);
        txtproduto = view.findViewById(R.id.txtProduto);
        txtcomplemento = view.findViewById(R.id.txtComplemento);
        txtpreco = view.findViewById(R.id.txtPreco);
    }

    public void setQuantidade(int qt){
        txtqt.setText(view.getContext().getString(R.string.qtproduto, qt));
    }

    public void setProduto(String produto){
        txtproduto.setText(produto);
    }

    public void setObs(String obs){
        obsproduto = obs;
    }

    public void setComplemento(String complemento){
        if (!obsproduto.equals("")) {
            txtcomplemento.setText(view.getContext().getString(R.string.complementoproduto, obsproduto, complemento));
        }else{
            txtcomplemento.setText(complemento);
        }
    }

    public void setValortotal(double valortotal){
        txtpreco.setText(NumberFormat.getCurrencyInstance(ptbr).format(valortotal));
    }
}
