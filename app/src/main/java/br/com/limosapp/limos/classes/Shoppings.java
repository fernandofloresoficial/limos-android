package br.com.limosapp.limos.classes;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.limosapp.limos.EnderecoTrocarActivity;
import br.com.limosapp.limos.firebase.ShoppingsFirebase;
import br.com.limosapp.limos.IndicarRestauranteActivity;
import br.com.limosapp.limos.R;

public class Shoppings {
    private Activity activity;
    private RecyclerView rvShoppings;

    private DatabaseReference dbshopping = FirebaseDatabase.getInstance().getReference().child("shoppings");

    private ImageView imgfechar;
    private Button btnindicarrestaurante;
    private TextView txtmudarendereco;    
    
    private static AlertDialog dialog;

    public Shoppings(Activity activity, RecyclerView rvShoppings) {
        this.activity = activity;
        this.rvShoppings = rvShoppings;
    }

    public void carregarShoppings(){
        dbshopping.keepSynced(true);

        FirebaseRecyclerAdapter<ShoppingsFirebase, ShoppingsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ShoppingsFirebase, ShoppingsViewHolder>
                (ShoppingsFirebase.class,R.layout.recyclerview_shoppings, ShoppingsViewHolder.class, dbshopping) {
            @Override
            protected void populateViewHolder(ShoppingsViewHolder viewHolder, ShoppingsFirebase model, int position) {
                //final String idshopping = getRef(position).getKey();

                viewHolder.setFantasia(model.getFantasia());
                viewHolder.setFotoCapa(model.getFotocapa());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder mbuilder = new AlertDialog.Builder(activity);
                        View mview = activity.getLayoutInflater().inflate(R.layout.dialog_sem_restaurantes, (ViewGroup) v.findViewById(R.id.dialog_sem_restaurantes_root));
                        mbuilder.setView(mview);
                        dialog = mbuilder.create();
                        dialog.show();

                        btnindicarrestaurante = mview.findViewById(R.id.btnIndicarRestaurante);
                        txtmudarendereco = mview.findViewById(R.id.txtMudarEndereco);
                        imgfechar = mview.findViewById(R.id.imgFechar);

                        btnindicarrestaurante.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                activity.startActivity(new Intent(activity, IndicarRestauranteActivity.class));
                                dialog.dismiss();
                                activity.finish();
                            }
                        });

                        txtmudarendereco.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                activity.startActivity(new Intent(activity, EnderecoTrocarActivity.class));
                                dialog.dismiss();
                            }
                        });

                        imgfechar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                });

            }
        };
        rvShoppings.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ShoppingsViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView txtnome;
        SimpleDraweeView imgfotocapa;

        public ShoppingsViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            txtnome = view.findViewById(R.id.txtEndereco);
            imgfotocapa = view.findViewById(R.id.imgCapa);
        }

        private void setFantasia(String fantasia){
            txtnome.setText(fantasia);
        }

        private void setFotoCapa(String fotocapa){
            imgfotocapa.setImageURI(fotocapa);
        }

    }
}
