package br.com.limosapp.limos.classes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import br.com.limosapp.limos.holder.EnderecosViewHolderCep;
import br.com.limosapp.limos.R;

import static br.com.limosapp.limos.EnderecoBuscarActivity.carregaEnderecoSelecionado;

/**
 * Created by Fabio on 21/04/2018.
 */

public class EnderecosAdapter extends RecyclerView.Adapter {
    private List<CEP> ceps;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View v, int adapterPosition, boolean b);
    }

    public EnderecosAdapter(List<CEP> ceps, Context context) {
        this.ceps = ceps;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_buscar_endereco, parent, false);
        EnderecosViewHolderCep holder = new EnderecosViewHolderCep(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final EnderecosViewHolderCep holder = (EnderecosViewHolderCep) viewHolder;
        final CEP cep  = ceps.get(position) ;
        holder.cep.setText(cep.getCep());
        holder.endereco.setText(cep.getLogradouro());
        holder.bairro.setText(cep.getBairro());
        holder.cidade.setText(cep.getLocalidade());
        holder.estado.setText(cep.getUf());

        holder.setItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(View v, int adapterPosition, boolean b) {
                carregaEnderecoSelecionado(context, holder.endereco.getText().toString(), holder.bairro.getText().toString(), holder.cidade.getText().toString(), holder.estado.getText().toString(), holder.cep.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ceps.size();
    }
}
