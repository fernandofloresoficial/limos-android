package br.com.limosapp.limos.firebase;

public class ShoppingsFirebase {

    private String fantasia, fotocapa, fotoperfil;

    public ShoppingsFirebase() {
    }

    public ShoppingsFirebase(String fantasia, String fotocapa, String fotoperfil) {
        this.fantasia = fantasia;
        this.fotocapa = fotocapa;
        this.fotoperfil = fotoperfil;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getFotocapa() {
        return fotocapa;
    }

    public void setFotocapa(String fotocapa) {
        this.fotocapa = fotocapa;
    }

    public String getFotoperfil() {
        return fotoperfil;
    }

    public void setFotoperfil(String fotoperfil) {
        this.fotoperfil = fotoperfil;
    }
}
