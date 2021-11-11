package br.com.limosapp.limos.sqlite;

import java.io.Serializable;

public class ProdutosCarrinho implements Serializable {

    private int id;
    private String hashproduto, produto, obs;
    private Integer quantidade;
    private Double valorunitario;


    public ProdutosCarrinho(int id, String hashproduto, String produto, Integer quantidade, Double valorunitario, String obs) {
        this.id = id;
        this.hashproduto = hashproduto;
        this.produto = produto;
        this.quantidade = quantidade;
        this.valorunitario = valorunitario;
        this.obs = obs;
    }


    public long getId() {
        return this.id;
    }
    public String getHashproduto() {
        return this.hashproduto;
    }
    public String getObs() {
        return this.obs;
    }
    public String getProduto() {
        return this.produto;
    }
    public Integer getQuantidade() {
        return this.quantidade;
    }
    public Double getValorunitario() {
        return this.valorunitario;
    }


    @Override
    public boolean equals(Object o){
        return this.id == ((ProdutosCarrinho)o).id;
    }

    @Override
    public int hashCode(){
        return this.id;
    }
}
