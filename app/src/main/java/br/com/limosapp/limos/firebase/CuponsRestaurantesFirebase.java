package br.com.limosapp.limos.firebase;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

public class CuponsRestaurantesFirebase implements ParentObject {

    private List<Object> CuponsList;
    private String idrestaurante, restaurante, fotoperfil;

    public CuponsRestaurantesFirebase(){

    }

    public CuponsRestaurantesFirebase(String idrestaurante,String restaurante, String fotoperfil) {
        this.idrestaurante = idrestaurante;
        this.restaurante = restaurante;
        this.fotoperfil = fotoperfil;
    }

    public String getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(String idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public String getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }

    public String getFotoperfil() {
        return fotoperfil;
    }

    public void setFotoperfil(String fotoperfil) {
        this.fotoperfil = fotoperfil;
    }

    @Override
    public List<Object> getChildObjectList() {
        return CuponsList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        CuponsList = list;
    }
}