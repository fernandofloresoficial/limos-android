package br.com.limosapp.limos.holder;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import br.com.limosapp.limos.R;

public class CategoriasViewHolder extends ParentViewHolder {

    public View view;
    private TextView txtcategoria;

    public CategoriasViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        txtcategoria = view.findViewById(R.id.txtCategoria);
    }

    public void setCategoria(String categoria) {
        txtcategoria.setText(categoria);
    }
}
