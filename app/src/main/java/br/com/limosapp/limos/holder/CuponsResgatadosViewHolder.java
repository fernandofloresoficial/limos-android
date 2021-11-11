package br.com.limosapp.limos.holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

import br.com.limosapp.limos.R;

public class CuponsResgatadosViewHolder extends ChildViewHolder {
    public View view;
    private TextView txtdesconto, txtvalidade;
    private CheckBox chkseg, chkter, chkqua, chkqui, chksex, chksab, chkdom;

    public CuponsResgatadosViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        txtdesconto = view.findViewById(R.id.txtCupomDesconto);
        txtvalidade = view.findViewById(R.id.txtCupomValidade);
        chkseg = view.findViewById(R.id.chkSeg);
        chkter = view.findViewById(R.id.chkTer);
        chkqua = view.findViewById(R.id.chkQua);
        chkqui = view.findViewById(R.id.chkQui);
        chksex = view.findViewById(R.id.chkSex);
        chksab = view.findViewById(R.id.chkSab);
        chkdom = view.findViewById(R.id.chkDom);
    }

     public void setDesconto(Integer desconto) {
        txtdesconto.setText(view.getContext().getString(R.string.percentual, desconto+"%"));
    }

    public void setValidade(String validade) {
        txtvalidade.setText(view.getContext().getString(R.string.validoate, validade));
    }

    public void setSeg(Integer flegado) {
        if(flegado.equals(0)) {
            chkseg.setChecked(false);
        }else {
            chkseg.setChecked(true);
        }
    }

    public void setTer(Integer flegado) {
        if(flegado.equals(0)) {
            chkter.setChecked(false);
        }else {
            chkter.setChecked(true);
        }
    }

    public void setQua(Integer flegado) {
        if(flegado.equals(0)) {
            chkqua.setChecked(false);
        }else {
            chkqua.setChecked(true);
        }
    }

    public void setQui(Integer flegado) {
        if(flegado.equals(0)) {
            chkqui.setChecked(false);
        }else {
            chkqui.setChecked(true);
        }
    }

    public void setSex(Integer flegado) {
        if(flegado.equals(0)) {
            chksex.setChecked(false);
        }else {
            chksex.setChecked(true);
        }
    }

    public void setSab(Integer flegado) {
        if(flegado.equals(0)) {
            chksab.setChecked(false);
        }else {
            chksab.setChecked(true);
        }
    }

    public void setDom(Integer flegado) {
        if(flegado.equals(0)) {
            chkdom.setChecked(false);
        }else {
            chkdom.setChecked(true);
        }
    }
}
