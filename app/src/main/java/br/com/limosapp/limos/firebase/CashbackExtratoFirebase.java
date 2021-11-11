package br.com.limosapp.limos.firebase;

public class CashbackExtratoFirebase {

    private String datahora, restaurante;
    private Integer pedido;
    private Double valor, saldo;

    public CashbackExtratoFirebase() {
    }

    public CashbackExtratoFirebase(String datahora, String restaurante, Integer pedido, Double valor, Double saldo) {
        this.datahora = datahora;
        this.restaurante = restaurante;
        this.pedido = pedido;
        this.valor = valor;
        this.saldo = saldo;
    }

    public String getDatahora() {
        return datahora;
    }

    public void setDatahora(String datahora) {
        this.datahora = datahora;
    }

    public String getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }
}
