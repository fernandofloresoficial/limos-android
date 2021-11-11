package br.com.limosapp.limos.firebase;

public class PedidosHorarioFirebase {

    private String aprovado, cancelado, concluido, enviado, realizado, recusado;
    private Integer retirabalcao;

    public PedidosHorarioFirebase() {

    }

    public PedidosHorarioFirebase(String aprovado, String cancelado, String concluido, String enviado, String realizado, String recusado, Integer retirabalcao) {
        this.aprovado = aprovado;
        this.cancelado = cancelado;
        this.concluido = concluido;
        this.enviado = enviado;
        this.realizado = realizado;
        this.recusado = recusado;
        this.retirabalcao = retirabalcao;
    }

    public String getAprovado() {
        return aprovado;
    }

    public void setAprovado(String aprovado) {
        this.aprovado = aprovado;
    }

    public String getCancelado() {
        return cancelado;
    }

    public void setCancelado(String cancelado) {
        this.cancelado = cancelado;
    }

    public String getConcluido() {
        return concluido;
    }

    public void setConcluido(String concluido) {
        this.concluido = concluido;
    }

    public String getEnviado() {
        return enviado;
    }

    public void setEnviado(String enviado) {
        this.enviado = enviado;
    }

    public String getRealizado() {
        return realizado;
    }

    public void setRealizado(String realizado) {
        this.realizado = realizado;
    }

    public String getRecusado() {
        return recusado;
    }

    public void setRecusado(String recusado) {
        this.recusado = recusado;
    }

    public Integer getRetirabalcao() {
        return retirabalcao;
    }

    public void setRetirabalcao(Integer retirabalcao) {
        this.retirabalcao = retirabalcao;
    }
}
