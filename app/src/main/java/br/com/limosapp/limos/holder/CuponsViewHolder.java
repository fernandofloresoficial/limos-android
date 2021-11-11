package br.com.limosapp.limos.holder;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import br.com.limosapp.limos.R;

public class CuponsViewHolder extends RecyclerView.ViewHolder {
    public View view;
    private SimpleDraweeView imgperfil;
    private TextView txtfantasia, txtdesconto, txtvalidade;
    public Button btnresgatarcupom;
    private CheckBox chkseg, chkter, chkqua, chkqui, chksex, chksab, chkdom;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public CuponsViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        imgperfil = view.findViewById(R.id.imgPerfil);
        txtfantasia = view.findViewById(R.id.txtFantasia);
        txtdesconto = view.findViewById(R.id.txtCupomDesconto);
        txtvalidade = view.findViewById(R.id.txtCupomValidade);
        btnresgatarcupom = view.findViewById(R.id.btnResgatarCupom);
        chkseg = view.findViewById(R.id.chkSeg);
        chkter = view.findViewById(R.id.chkTer);
        chkqua = view.findViewById(R.id.chkQua);
        chkqui = view.findViewById(R.id.chkQui);
        chksex = view.findViewById(R.id.chkSex);
        chksab = view.findViewById(R.id.chkSab);
        chkdom = view.findViewById(R.id.chkDom);
    }

    public void setRestaurante(String idrestaurante) {
        db.child("restaurantes").child(idrestaurante).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("fotoperfil").exists()) imgperfil.setImageURI(Objects.requireNonNull(dataSnapshot.child("fotoperfil").getValue()).toString());
                if (dataSnapshot.child("fantasia").exists()) txtfantasia.setText(Objects.requireNonNull(dataSnapshot.child("fantasia").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setDesconto(Integer desconto) {
        txtdesconto.setText(view.getContext().getString(R.string.percentual,desconto+"%"));
    }

    public void setValidade(String validade) {
        txtvalidade.setText(view.getContext().getString(R.string.validoate, validade));
    }

    public void verifValidade(String strvalidade) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        String dataformat = sdf1.format(new Date()); //formatando apenas para pegar a data e não a hora
        try {
            Date validade = sdf1.parse(strvalidade), data = sdf1.parse(dataformat);
            if (validade.before(data)){
                btnresgatarcupom.setEnabled(false);
                btnresgatarcupom.setText(R.string.vencido);
                btnresgatarcupom.setBackgroundResource(R.drawable.botalogincinza);
            }else{
                btnresgatarcupom.setEnabled(true);
                btnresgatarcupom.setText(R.string.resgatar);
                btnresgatarcupom.setBackgroundResource(R.drawable.botaologin);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void verifAtivo(Integer ativo) {
        if (ativo.equals(0)){
            btnresgatarcupom.setEnabled(false);
            btnresgatarcupom.setText(R.string.desativado);
            btnresgatarcupom.setBackgroundResource(R.drawable.botalogincinza);
        }
    }

    public boolean verifDia(Integer dom, Integer seg, Integer ter, Integer qua, Integer qui, Integer sex, Integer sab) {
        Locale locale = new Locale("pt","BR");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", locale);
        String dataFormatada = dateFormat.format(new Date());
        Date date;
        try {
            date = dateFormat.parse(dataFormatada);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            int diasemana = cal.get(Calendar.DAY_OF_WEEK);

            if (diasemana == 1 && dom == 1){
                return true;
            }else if (diasemana == 2 && seg == 1){
                return true;
            }else if(diasemana == 3 && ter == 1){
                return true;
            }else if (diasemana == 4 && qua == 1){
                return true;
            }else if (diasemana == 5 && qui == 1){
                return true;
            }else if (diasemana == 6 && sex == 1){
                return true;
            }else return diasemana == 7 && sab == 1;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
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

    public void setResgatado(final String idusuario, final String idrestaurante, final String idcupom){
        db.child("usuariocuponsresgatados").child(idusuario).child(idrestaurante).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(idcupom)) {
                    btnresgatarcupom.setEnabled(false);
                    btnresgatarcupom.setText(R.string.resgatado);
                    btnresgatarcupom.setBackgroundResource(R.drawable.botalogincinza);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setTipoBotao(){
        btnresgatarcupom.setBackgroundResource(R.drawable.botaologinvermelho);
        btnresgatarcupom.setText(R.string.aplicarcupom);
    }
}