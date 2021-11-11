package br.com.limosapp.limos.firebase;

public class CategoriasFirebase {

    private String categoria;

    public CategoriasFirebase() {
    }

    public CategoriasFirebase(String categoria) {
        this.categoria = categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
