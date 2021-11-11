package br.com.limosapp.limos.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.com.limosapp.limos.classes.EnderecosAdapter;
import br.com.limosapp.limos.R;

/**
 * Created by Fabio on 21/04/2018.
 */

public class EnderecosViewHolderCep extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView cep, endereco, bairro, cidade,estado;

    private EnderecosAdapter.OnItemClickListener itemClickListener;

    public EnderecosViewHolderCep(View view) {
        super(view);
        cep = view.findViewById(R.id.txtCEP);
        endereco = view.findViewById(R.id.txtEndereco);
        bairro = view.findViewById(R.id.txtBairro);
        cidade = view.findViewById(R.id.txtCidade);
        estado = view.findViewById(R.id.txtEstado);

        view.setOnClickListener(this);
    }

    public void setItemClickListener(EnderecosAdapter.OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClick(v, getAdapterPosition(), false);

    }
}
