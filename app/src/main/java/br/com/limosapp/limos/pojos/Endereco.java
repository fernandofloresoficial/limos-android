package br.com.limosapp.limos.pojos;

public class Endereco {
    String endereco, numero, complemento, bairro, cidade, estado, cep, enderecomostrar;
    double latitudeatual, longitudeatual;

    public Endereco(){

    }

    public Endereco(String endereco, String numero, String complemento, String bairro, String cidade, String estado, String cep, String enderecomostrar, double latitudeatual, double longitudeatual) {
        this.endereco = endereco;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.enderecomostrar = enderecomostrar;
        this.latitudeatual = latitudeatual;
        this.longitudeatual = longitudeatual;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEnderecomostrar() {
        return enderecomostrar;
    }

    public void setEnderecomostrar(String enderecomostrar) {
        this.enderecomostrar = enderecomostrar;
    }

    public double getLatitudeatual() {
        return latitudeatual;
    }

    public void setLatitudeatual(double latitudeatual) {
        this.latitudeatual = latitudeatual;
    }

    public double getLongitudeatual() {
        return longitudeatual;
    }

    public void setLongitudeatual(double longitudeatual) {
        this.longitudeatual = longitudeatual;
    }
}
