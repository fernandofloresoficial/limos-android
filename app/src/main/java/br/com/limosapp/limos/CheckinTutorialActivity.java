package br.com.limosapp.limos;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.limosapp.limos.util.VerificaInternet;

public class CheckinTutorialActivity extends AppCompatActivity {

    Button btnproximo, btnindicarrestaurante;
    ImageView imgfechar;
    TextView txtmudarendereco;
    public static AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_tutorial);

        btnproximo = findViewById(R.id.btnProximo);

        btnproximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mbuilder = new AlertDialog.Builder(CheckinTutorialActivity.this);
                View mview = getLayoutInflater().inflate(R.layout.dialog_sem_restaurantes, (ViewGroup) view.findViewById(R.id.dialog_sem_restaurantes_root));
                mbuilder.setView(mview);
                dialog = mbuilder.create();
                dialog.show();

                btnindicarrestaurante = mview.findViewById(R.id.btnIndicarRestaurante);
                txtmudarendereco = mview.findViewById(R.id.txtMudarEndereco);
                imgfechar = mview.findViewById(R.id.imgFechar);

                btnindicarrestaurante.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CheckinTutorialActivity.this, IndicarRestauranteActivity.class));
                        dialog.dismiss();
                        finish();
                    }
                });

                txtmudarendereco.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CheckinTutorialActivity.this, EnderecoTrocarActivity.class));
                        dialog.dismiss();
                        finish();
                    }
                });

                imgfechar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new VerificaInternet().verificaConexao(this);
    }
}
