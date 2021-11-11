package br.com.limosapp.limos.firebase;

public class RestaurantesFirebase {
    private String fantasia, cozinha, espera, fotocapa, fotoperfil, cidade;
    private Double lt, lg, mediaavaliacao;
    private Long qtavaliacao;

    public RestaurantesFirebase() {
    }

    public RestaurantesFirebase(String fantasia, String cozinha, String espera, String fotocapa, String fotoperfil, String cidade, Double lt, Double lg, Double mediaavaliacao, Long qtavaliacao) {
        this.fantasia = fantasia;
        this.cozinha = cozinha;
        this.espera = espera;
        this.fotocapa = fotocapa;
        this.fotoperfil = fotoperfil;
        this.cidade = cidade;
        this.lt = lt;
        this.lg = lg;
        this.mediaavaliacao = mediaavaliacao;
        this.qtavaliacao = qtavaliacao;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getCozinha() {
        return cozinha;
    }

    public void setCozinha(String cozinha) {
        this.cozinha = cozinha;
    }

    public String getEspera() {
        return espera;
    }

    public void setEspera(String espera) {
        this.espera = espera;
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

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public double getLt() {
        return lt;
    }

    public void setLt(Double lt) {
        this.lt = lt;
    }

    public double getLg() {
        return lg;
    }

    public void setLg(Double lg) {
        this.lg = lg;
    }

    public double getMediaavaliacao() {
        return mediaavaliacao;
    }

    public void setMediaavaliacao(Double mediaavaliacao) {
        this.mediaavaliacao = mediaavaliacao;
    }

    public long getQtavaliacao() {
        return qtavaliacao;
    }

    public void setQtavaliacao(Long qtavaliacao) {
        this.qtavaliacao = qtavaliacao;
    }
}