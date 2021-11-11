package br.com.limosapp.limos.firebase;

public class PedidoProdutosFirebase {

    private String complemento, obs, produto;
    private Double valor, valortotal;
    private Integer quantidade;

    public PedidoProdutosFirebase() {
    }

    public PedidoProdutosFirebase(String complemento, String obs, String produto, Double valor, Double valortotal, Integer quantidade) {
        this.complemento = complemento;
        this.obs = obs;
        this.produto = produto;
        this.valor = valor;
        this.valortotal = valortotal;
        this.quantidade = quantidade;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getValortotal() {
        return valortotal;
    }

    public void setValortotal(Double valortotal) {
        this.valortotal = valortotal;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
