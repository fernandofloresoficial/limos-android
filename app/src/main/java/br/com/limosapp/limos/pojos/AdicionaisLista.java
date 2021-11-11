package br.com.limosapp.limos.pojos;

public class AdicionaisLista {

    private String nome, vlr, qtde, hash;

    public AdicionaisLista(String nome, String vlr, String qtde, String hash) {
        this.nome = nome;
        this.vlr = vlr;
        this.qtde = qtde;
        this.hash = hash;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getVlr() {
        return vlr;
    }

    public void setVlr(String vlr) {
        this.vlr = vlr;
    }

    public String getQtde() {
        return qtde;
    }

    public void setQtde(String qtde) {
        this.qtde = qtde;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
