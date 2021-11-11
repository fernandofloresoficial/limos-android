package br.com.limosapp.limos.classes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import br.com.limosapp.limos.CuponsActivity;
import br.com.limosapp.limos.R;
import br.com.limosapp.limos.holder.CuponsRestaurantesViewHolder;

import static br.com.limosapp.limos.MainActivity.enderecomain;

class Cupons {
    private Activity activity;
    private TextView txtCupons;
    private ImageView imgSemCupons;
    private RecyclerView rvCupons;

    private ArrayList<String> restauranteArray = new ArrayList<>();

    private boolean temcupom;

    private DatabaseReference dbcupons = FirebaseDatabase.getInstance().getReference().child("restaurantecupons");

    Cupons(Activity activity, TextView txtCupons, ImageView imgSemCupons, RecyclerView rvCupons) {
        this.activity = activity;
        this.txtCupons = txtCupons;
        this.imgSemCupons = imgSemCupons;
        this.rvCupons = rvCupons;
    }

    public class CuponsAdapter extends RecyclerView.Adapter<CuponsRestaurantesViewHolder> {

        private final ArrayList hashrestaurante;

        private CuponsAdapter(ArrayList hashrestaurante) {
            this.hashrestaurante = hashrestaurante;
        }

        @NonNull
        public CuponsRestaurantesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CuponsRestaurantesViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_cupons_restaurante, parent, false));
        }

        public void onBindViewHolder(@NonNull final CuponsRestaurantesViewHolder holder, final int position) {
            final String restaurante = hashrestaurante.get(position).toString() ;
            holder.setDesconto(restaurante);
            holder.setRestaurante(restaurante);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CuponsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("idrestaurante", restaurante);
                    bundle.putString("nomerestaurante", holder.txtFantasia.getText().toString());
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return hashrestaurante != null ? hashrestaurante.size() : 0;
        }
    }

    void verificaTemCupons(){
        Query querycupons;
        //querycupons = dbcupons.child("cupons").orderByChild("validademseg").startAt(System.currentTimeMillis());
        querycupons = dbcupons.orderByChild("ativo_cidade").equalTo("1_"+enderecomain.getCidade());
        querycupons.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                temcupom = false;
                restauranteArray.clear();
                if (dataSnapshot.hasChild("")) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot postSnapshot2: postSnapshot.getChildren()) {
                            if (Objects.requireNonNull(postSnapshot2.getKey()).equals("cupons")){
                                for (DataSnapshot postSnapshot3: postSnapshot2.getChildren()) {
                                    long bancomili = Long.parseLong(Objects.requireNonNull(postSnapshot3.child("validademseg").getValue()).toString());
                                    int bancoativo = Integer.parseInt(Objects.requireNonNull(postSnapshot3.child("ativo").getValue()).toString());

                                    if (System.currentTimeMillis() <= bancomili && bancoativo == 1) {
                                        restauranteArray.add(postSnapshot.getKey());
                                        temcupom = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (temcupom) {
                    txtCupons.setText(activity.getString(R.string.cupomdesconto));
                    imgSemCupons.setVisibility(View.INVISIBLE);
                    rvCupons.setVisibility(View.VISIBLE);
                    CuponsAdapter adapter = new CuponsAdapter(restauranteArray);
                    rvCupons.setAdapter(adapter);
                } else {
                    txtCupons.setText(activity.getString(R.string.nenhumcupomdesconto));
                    imgSemCupons.setVisibility(View.VISIBLE);
                    rvCupons.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
