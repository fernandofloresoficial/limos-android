package br.com.limosapp.limos.pojos;

public class OpcionalLista {

    private String opcional, valor;

    public OpcionalLista(String opcional, String valor) {
        this.opcional = opcional;
        this.valor = valor;
    }

    public String getOpcional() {
        return opcional;
    }

    public void setOpcional(String opcional) {
        this.opcional = opcional;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
