package br.com.limosapp.limos.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import br.com.limosapp.limos.R;

public class CuponsResgatadosRestaurantesViewHolder extends ParentViewHolder {
    public View view;
    private SimpleDraweeView imgfotoperfil;
    private ImageView imgexpandir;
    private TextView txtnome;

    public CuponsResgatadosRestaurantesViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        imgfotoperfil = view.findViewById(R.id.imgFotoPerfil);
        txtnome = view.findViewById(R.id.txtFantasia);
        imgexpandir = view.findViewById(R.id.imgExpandir);
    }

    public void setFotoPerfil(String fotoperfil){
        imgfotoperfil.setImageURI(fotoperfil);
    }

    public void setFantasia(String fantasia){
        txtnome.setText(fantasia);
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        super.setExpanded(isExpanded);
        if (isExpanded) {
            imgexpandir.setImageResource(R.drawable.ic_esconder);
        }else{
            imgexpandir.setImageResource(R.drawable.ic_mostrar);
        }
    }
}