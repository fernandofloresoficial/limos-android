package br.com.limosapp.limos.firebase;

public class PedidosDetalhesFirebase {

    private int quantidade;
    private String produto, obs, complemento;
    private double valortotal;

    public PedidosDetalhesFirebase() {
    }

    public PedidosDetalhesFirebase(int quantidade, String produto, String obs, String complemento, double valortotal) {
        this.quantidade = quantidade;
        this.produto = produto;
        this.obs = obs;
        this.complemento = complemento;
        this.valortotal = valortotal;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public double getValortotal() {
        return valortotal;
    }

    public void setValortotal(double valortotal) {
        this.valortotal = valortotal;
    }
}
