package br.com.limosapp.limos.firebase;

public class ProdutosFirebase {

    private String produto, descricao, foto, tempo, apartir;
    private Long serve;
    private Double preco;

    public ProdutosFirebase() {

    }

    public ProdutosFirebase(String produto, String descricao, Long serve, String foto, String tempo, Double preco, String apartir) {
        this.produto = produto;
        this.descricao = descricao;
        this.serve = serve;
        this.foto = foto;
        this.tempo = tempo;
        this.preco = preco;
        this.apartir = apartir;
    }

    public String getProduto() {
        return produto;
    }
    public String getDescricao() {
        return descricao;
    }
    public Long getServe() {
        return serve;
    }
    public String getFoto() {
        return foto;
    }
    public String getTempo() {
        return tempo;
    }
    public Double getPreco() {
        return preco;
    }
    public String getApartir() {
        return apartir;
    }
}