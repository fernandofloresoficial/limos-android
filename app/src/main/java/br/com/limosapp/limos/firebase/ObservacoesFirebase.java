package br.com.limosapp.limos.firebase;

public class ObservacoesFirebase {

    private String Observacoes;

    public ObservacoesFirebase() {
    }

    public ObservacoesFirebase(String observacoes) {
        Observacoes = observacoes;
    }

    public String getObservacoes() {
        return Observacoes;
    }

    public void setObservacoes(String observacoes) {
        Observacoes = observacoes;
    }
}
