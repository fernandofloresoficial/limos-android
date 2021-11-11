package br.com.limosapp.limos.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

import br.com.limosapp.limos.R;
import br.com.limosapp.limos.firebase.CuponsResgatadosFirebase;
import br.com.limosapp.limos.firebase.CuponsRestaurantesFirebase;
import br.com.limosapp.limos.holder.CuponsResgatadosRestaurantesViewHolder;
import br.com.limosapp.limos.holder.CuponsResgatadosViewHolder;

public class CuponsResgatadosAdapter extends ExpandableRecyclerAdapter<CuponsResgatadosRestaurantesViewHolder, CuponsResgatadosViewHolder> {
    private LayoutInflater inflater;

    public CuponsResgatadosAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CuponsResgatadosRestaurantesViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.recyclerview_cupons_resgatados_restaurantes,viewGroup,false);
        return new CuponsResgatadosRestaurantesViewHolder(view);
    }

    @Override
    public CuponsResgatadosViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.recyclerview_cupons_resgatados,viewGroup,false);
        return new CuponsResgatadosViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(final CuponsResgatadosRestaurantesViewHolder restaurantesCuponsResgatadosViewHolder, int i, Object o) {
        final CuponsRestaurantesFirebase cuponsRestaurantesFirebase = (CuponsRestaurantesFirebase)o;
        restaurantesCuponsResgatadosViewHolder.setFotoPerfil(cuponsRestaurantesFirebase.getFotoperfil());
        restaurantesCuponsResgatadosViewHolder.setFantasia(cuponsRestaurantesFirebase.getRestaurante());
    }

    @Override
    public void onBindChildViewHolder(CuponsResgatadosViewHolder cuponsResgatadosViewHolder, int i, Object o) {
        CuponsResgatadosFirebase cuponsResgatadosFirebase = (CuponsResgatadosFirebase)o;
        cuponsResgatadosViewHolder.setDesconto(cuponsResgatadosFirebase.getDesconto());
        cuponsResgatadosViewHolder.setValidade(cuponsResgatadosFirebase.getValidade());
        cuponsResgatadosViewHolder.setSeg(cuponsResgatadosFirebase.getSeg());
        cuponsResgatadosViewHolder.setTer(cuponsResgatadosFirebase.getTer());
        cuponsResgatadosViewHolder.setQua(cuponsResgatadosFirebase.getQua());
        cuponsResgatadosViewHolder.setQui(cuponsResgatadosFirebase.getQui());
        cuponsResgatadosViewHolder.setSex(cuponsResgatadosFirebase.getSex());
        cuponsResgatadosViewHolder.setSab(cuponsResgatadosFirebase.getSab());
        cuponsResgatadosViewHolder.setDom(cuponsResgatadosFirebase.getDom());
    }
}