package br.com.limosapp.limos.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import br.com.limosapp.limos.SemInternetActivity;

/**
 * Created by Fabio on 13/01/2018.
 */

public class VerificaInternet {

    public boolean verificaConexao(Context context) {

        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         assert conectivtyManager != null;
         if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            Intent intent = new Intent(context, SemInternetActivity.class);
            context.startActivity(intent);
            return false;
        }
    }
}


