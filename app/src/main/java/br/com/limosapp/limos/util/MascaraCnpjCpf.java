package br.com.limosapp.limos.util;

/**
 * Created by Fabio on 11/11/2017.
 */

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MascaraCnpjCpf {
    public TextWatcher insert(final EditText ediTxt) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";
            String mask;
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                String str = new Mascara().unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                if (ediTxt.length()>14){
                    mask = "##.###.###/####-##";
                }else{
                    mask="###.###.###-##";
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        };
    }
}
