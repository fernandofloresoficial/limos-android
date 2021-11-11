package br.com.limosapp.limos.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class VerificaCoordenadas {
    private Geocoder geocoder;
    private Address address = null;
    private List<Address> addresses;

    public Address verificaCoordenadasPorEnd(Context context, String endereco) {
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocationName(endereco, 1);
            while (addresses.size() == 0) {
                addresses = geocoder.getFromLocationName(endereco, 1);
            }
            address = addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public Address verificaCoordenadasPorLatLon(Context context, double latitude, double longitude) {
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
            if (addresses.size()>0){
                address= addresses.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
