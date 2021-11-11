package br.com.limosapp.limos.firebase;

/**
 * Created by Brunno on 17/07/2018.
 */

public class OpcionaisFirebase {

    private Long id;
    private String nome;
    private Double valor;

    public OpcionaisFirebase() {
    }

    public OpcionaisFirebase(Long id, String nome, Double valor) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
