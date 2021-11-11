package br.com.limosapp.limos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Objects;

import br.com.limosapp.limos.holder.OpcionaisViewHolder;
import br.com.limosapp.limos.pojos.OpcionalLista;

import static br.com.limosapp.limos.ProdutoActivity.opcionais;
import static br.com.limosapp.limos.ProdutoOpcionaisActivity.grupo;

public class OpcionaisFragment extends android.support.v4.app.Fragment {

    RecyclerView rvopcionais;
    View v;
    ArrayList<OpcionalLista> opcinalLista = new ArrayList<>();

    public OpcionaisFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_opcionais, container, false);
        rvopcionais = v.findViewById(R.id.rcOpcionais);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        rvopcionais.setHasFixedSize(true);
        rvopcionais.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();

        opcinalLista.clear();
        if (opcionais != null) {
            if (opcionais[grupo] != null) {
                for (int i = 0; i < opcionais[grupo].length; i++) {
                    if (opcionais[grupo][i][2] != null){
                        OpcionalLista addopcional = new OpcionalLista(opcionais[grupo][i][2], opcionais[grupo][i][4]);
                        opcinalLista.add(addopcional);
                    }
                }
                rvopcionais.setAdapter(new OpcionaisAdapter(opcinalLista));
            }
        }
    }

    public void salvarArray(int posicao, String qtde){
        opcionais[grupo][posicao][3] = qtde;

        for (int i = 0; i < opcionais[grupo].length; i++) {
            if (i != posicao) { opcionais[grupo][i][3] = null; }
        }
    }

    public class OpcionaisAdapter extends RecyclerView.Adapter<OpcionaisViewHolder> {

        private final ArrayList<OpcionalLista> opcinallista;

        private OpcionaisAdapter(ArrayList opcinallista) {
            this.opcinallista = opcinallista;
        }

        @NonNull
        public OpcionaisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OpcionaisViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_opcionais, parent, false));
        }

        public void onBindViewHolder(@NonNull final OpcionaisViewHolder viewHolder, final int position) {

            viewHolder.setValor(Double.parseDouble(opcinallista.get(position).getValor()));
            viewHolder.setOpcional(opcinallista.get(position).getOpcional());

            if (opcionais != null) {
                if (opcionais[grupo][position][3] != null) {
                    viewHolder.radioOpcional.setChecked(true);
                }else{
                    viewHolder.radioOpcional.setChecked(false);
                }
            }

            viewHolder.radioOpcional.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.radioOpcional.isChecked()){
                        if (opcionais[grupo][position][3] == null) {
                            viewHolder.radioOpcional.setChecked(true);
                            salvarArray(position,"1");
                        }else{
                            viewHolder.radioOpcional.setChecked(false);
                            salvarArray(position,null);
                        }
                    }else{
                        salvarArray(position,null);
                    }
                    ((ProdutoOpcionaisActivity) Objects.requireNonNull(getActivity())).obrigatorio();
                    ((ProdutoOpcionaisActivity)getActivity()).calculaValores();
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return opcinallista != null ? opcinallista.size() : 0;
        }
    }
}
