package br.com.limosapp.limos.firebase;

public class UsuariosFirebase {
    private String nome, email, telefone, cpf, urlfoto;
    private double cashbacksaldo;

    public UsuariosFirebase() {
    }

    public String getNome() { return nome; }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getUrlfoto() { return urlfoto; }

    public void setUrlfoto(String urlfoto) { this.urlfoto = urlfoto; }

    public double getCashbacksaldo() { return cashbacksaldo; }

    public void setCashbacksaldo(double cashbacksaldo) { this.cashbacksaldo = cashbacksaldo; }
}
