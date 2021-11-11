package br.com.limosapp.limos.classes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import br.com.limosapp.limos.EnderecoTrocarActivity;
import br.com.limosapp.limos.util.Toast_layout;

public class BuscarEnderecoLocalizacao {
    private final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private Location location = null;
    private AlertDialog alert;


    public Location buscaCoordenadas(final Activity activity) {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }

        }else {
            boolean GPS_Habilitado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!GPS_Habilitado) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder
                        .setMessage("O serviço de localização está desabilitado. Deseja mudar a configuração?")
                        .setCancelable(false)
                        .setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                (activity).startActivity(callGPSSettingIntent);
                            }
                        });

                alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        new Toast_layout(activity).mensagem("Ops! parece que não estamos conseguindo te localizar. Favor informar o endereço");
                        Intent intent = new Intent(activity, EnderecoTrocarActivity.class);
                        activity.startActivity(intent);
                    }
                });

                alert = alertDialogBuilder.create();

                // Mostra a caixa de diálogo.
                if (!alert.isShowing()) {
                    alert.show();
                }
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        return location;
    }
}
