package br.com.limosapp.limos.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.limosapp.limos.R;


public class ProdutosCarrinhoHolder extends RecyclerView.ViewHolder  {

    public TextView txtprodutocarrinho, txtdescricaocarrinho, txtprecocarrinho, txtquantidadecarrinho, txtremover;
    public ImageView imgMaisCarrinho, imgMenosCarrinho;

    public ProdutosCarrinhoHolder(View itemView) {
        super(itemView);

        txtprodutocarrinho = itemView.findViewById(R.id.txtProdutoCarrinho);
        txtprecocarrinho = itemView.findViewById(R.id.txtPrecoCarrinho);
        txtquantidadecarrinho = itemView.findViewById(R.id.txtQuantidade);
        txtdescricaocarrinho = itemView.findViewById(R.id.txtDescricaoCarrinho);
        imgMenosCarrinho = itemView.findViewById(R.id.imgMenos);
        imgMaisCarrinho = itemView.findViewById(R.id.imgMais);
        txtremover = itemView.findViewById(R.id.txtRemoverCarrinho);
    }
}
