package br.com.limosapp.limos.firebase;

public class UsuariosPedidosFirebase {

    private String datapedido, restaurante, comentario, nomeusuario, resposta;
    private Double nota;
    private Long numeropedido;
    private Integer statuspedido, avaliado;

    public UsuariosPedidosFirebase() {
    }

    public UsuariosPedidosFirebase(String comentario, String nomeusuario, String resposta, Long numeropedido, String datapedido, String restaurante, Double nota, Integer statuspedido, Integer avaliado) {
        this.datapedido = datapedido;
        this.nota = nota;
        this.statuspedido = statuspedido;
        this.restaurante = restaurante;
        this.avaliado = avaliado;
        this.comentario = comentario;
        this.nomeusuario = nomeusuario;
        this.resposta = resposta;
        this.numeropedido = numeropedido;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
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

    public Long getNumeropedido() {
        return numeropedido;
    }

    public void setNumeropedido(Long numeropedido) {
        this.numeropedido = numeropedido;
    }

    public String getDatapedido() {
        return datapedido;
    }

    public void setDatapedido(String datapedido) {
        this.datapedido = datapedido;
    }

    public String getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public int getStatuspedido() {
        return statuspedido;
    }

    public void setStatuspedido(Integer statuspedido) {
        this.statuspedido = statuspedido;
    }

    public int getAvaliado() {
        return avaliado;
    }

    public void setAvaliado(Integer avaliado) {
        this.avaliado = avaliado;
    }

}
