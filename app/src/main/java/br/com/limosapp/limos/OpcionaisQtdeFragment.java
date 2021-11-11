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

import br.com.limosapp.limos.holder.AdicionaisViewHolder;
import br.com.limosapp.limos.holder.OpcionaisViewHolder;
import br.com.limosapp.limos.pojos.OpcionalLista;

import static br.com.limosapp.limos.ProdutoActivity.opcionais;
import static br.com.limosapp.limos.ProdutoOpcionaisActivity.grupo;
import static br.com.limosapp.limos.ProdutoOpcionaisActivity.qtdemax;

public class OpcionaisQtdeFragment extends android.support.v4.app.Fragment {

    RecyclerView rvopcionais;
    Integer quantidade, qtdetotal;
    ArrayList<OpcionalLista> opcinalLista = new ArrayList<>();

    View v;

    public OpcionaisQtdeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_opcionais_qtde, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rvopcionais = v.findViewById(R.id.rcOpcionais);
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

        qtdetotal=0;
        if (opcionais != null) {
            for (int x = 0; x < opcionais[grupo].length; x++) {
                if (opcionais[grupo][x][3] != null && !opcionais[grupo][x][3].equals("0")) {
                    qtdetotal = qtdetotal + Integer.parseInt(opcionais[grupo][x][3]);
                }
            }
        }
        ((ProdutoOpcionaisActivity) Objects.requireNonNull(getActivity())).calculaMax(qtdetotal);
    }

    public class OpcionaisAdapter extends RecyclerView.Adapter<AdicionaisViewHolder> {

        private final ArrayList<OpcionalLista> opcinallista;

        private OpcionaisAdapter(ArrayList opcinallista) {
            this.opcinallista = opcinallista;
        }

        @NonNull
        public AdicionaisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AdicionaisViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_adicionais, parent, false));
        }

        public void onBindViewHolder(@NonNull final AdicionaisViewHolder viewHolder, final int position) {
            viewHolder.setValorAdicional(Double.parseDouble(opcinallista.get(position).getValor()));
            viewHolder.setAdicional(opcinallista.get(position).getOpcional());

            if (opcionais != null) {
                if (opcionais[grupo][position][3] != null) {
                    viewHolder.txtQuantidade.setText(opcionais[grupo][position][3]);
                }else{
                    viewHolder.txtQuantidade.setText("0");
                }
            }

            viewHolder.imgMais.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quantidade = Integer.parseInt(viewHolder.txtQuantidade.getText().toString());
                    if (qtdetotal < qtdemax){
                        quantidade++;
                        qtdetotal++;
                        viewHolder.txtQuantidade.setText(String.valueOf(quantidade));
                    }
                    opcionais[grupo][position][3] = String.valueOf(quantidade);
                    ((ProdutoOpcionaisActivity) Objects.requireNonNull(getActivity())).calculaMax(qtdetotal);
                    ((ProdutoOpcionaisActivity)getActivity()).obrigatorio();
                    ((ProdutoOpcionaisActivity)getActivity()).calculaValores();
                }
            });

            viewHolder.imgMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quantidade = Integer.parseInt(viewHolder.txtQuantidade.getText().toString());
                    if (quantidade>0){
                        quantidade--;
                        qtdetotal--;
                        viewHolder.txtQuantidade.setText(String.valueOf(quantidade));
                    }
                    opcionais[grupo][position][3] = String.valueOf(quantidade);
                    ((ProdutoOpcionaisActivity) Objects.requireNonNull(getActivity())).calculaMax(qtdetotal);
                    ((ProdutoOpcionaisActivity)getActivity()).obrigatorio();
                    ((ProdutoOpcionaisActivity)getActivity()).calculaValores();
                }
            });
        }

        @Override
        public int getItemCount() {
            return opcinallista != null ? opcinallista.size() : 0;
        }
    }
}
