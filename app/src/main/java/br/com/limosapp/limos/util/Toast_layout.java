package br.com.limosapp.limos.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.com.limosapp.limos.R;

public class Toast_layout {

    public Activity activity;

    public Toast_layout( Activity activity){
        this.activity = activity;
    }

    public void mensagem(String menssagem){
        View layout = activity.getLayoutInflater().inflate(R.layout.toast, (ViewGroup) this.activity.findViewById(R.id.toast_layout_root));

        TextView text = layout.findViewById(R.id.txtMensagem);
        text.setText(menssagem);

        Toast toast = new Toast(activity.getApplicationContext());
        //toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
