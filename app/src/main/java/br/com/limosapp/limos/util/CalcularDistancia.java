package br.com.limosapp.limos.util;

import android.annotation.SuppressLint;

import com.google.maps.android.SphericalUtil;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Fabio on 07/02/2018.
 * Alterado por Brunno 13/02/2019.
 */

public class CalcularDistancia {

    private double distance;

    public CalcularDistancia(double lat1, double lng1, double lat2, double lng2) {
        LatLng posicaoinicial = new LatLng(lat1, lng1);
        LatLng posicaofinal = new LatLng(lat2, lng2);

        distance = SphericalUtil.computeDistanceBetween(posicaoinicial, posicaofinal);
        distance /= 1000;
    }

    @SuppressLint("DefaultLocale")
    public String carregarDistanciaString() {
        return String.format("%4.1f km", distance);
    }

    public Double carregarDistanciaDouble() {
        return distance;
    }
}

