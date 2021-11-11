package br.com.limosapp.limos.firebase;

public class RestaurantesAvaliacoesFirebase {

    private String comentario, datapedido, nomeusuario, resposta, usuario;
    private Float nota;
    private Integer numeropedido;

    public RestaurantesAvaliacoesFirebase() {
    }

    public RestaurantesAvaliacoesFirebase(String comentario, String datapedido, String nomeusuario, String resposta, String usuario, Float nota, Integer numeropedido) {
        this.comentario = comentario;
        this.datapedido = datapedido;
        this.nomeusuario = nomeusuario;
        this.resposta = resposta;
        this.usuario = usuario;
        this.nota = nota;
        this.numeropedido = numeropedido;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getDatapedido() {
        return datapedido;
    }

    public void setDatapedido(String datapedido) {
        this.datapedido = datapedido;
    }

    public String getNomeusuario() {
        return nomeusuario;
    }

    public void setNomeusuario(String nomeusuario) {
        this.nomeusuario = nomeusuario;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Float getNota() {
        return nota;
    }

    public void setNota(Float nota) {
        this.nota = nota;
    }

    public Integer getNumeropedido() {
        return numeropedido;
    }

    public void setNumeropedido(Integer numeropedido) {
        this.numeropedido = numeropedido;
    }
}

