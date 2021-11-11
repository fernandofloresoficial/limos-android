package br.com.limosapp.limos.firebase;

public class IndicarRestauranteFirebase {

    private String data, fantasia, nomeusuario, telefone, usuario;

    public IndicarRestauranteFirebase() {
    }

    public IndicarRestauranteFirebase(String data, String fantasia, String nomeusuario, String telefone, String usuario) {
        this.data = data;
        this.fantasia = fantasia;
        this.nomeusuario = nomeusuario;
        this.telefone = telefone;
        this.usuario = usuario;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getNomeusuario() {
        return nomeusuario;
    }

    public void setNomeusuario(String nomeusuario) {
        this.nomeusuario = nomeusuario;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
