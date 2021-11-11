package br.com.limosapp.limos.firebase;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PedidoFirebase {
    private String numero, endereco, bairro, cidade, complemento, uf, cep, data, email, fantasia, formapagamento, hora, nomeusuario, restaurante, telefone, usuario, cpfcnpj, hashcupom;
    private Integer status, retirabalcao;
    private Long datamilisegundos, pedido;
    private Double valorfrete, valorlimos, valorprodutos,  valorcash, valordesconto, valortotal, valorcashganho;

    public PedidoFirebase() {
    }

    public PedidoFirebase(String numero,String hashcupom,String endereco,String  bairro,String  cep , String  cidade, String  complemento, String  uf, String  data, Long datamilisegundos, String  email, String  fantasia, String  formapagamento, String  hora, String  nomeusuario, Long  pedido, String  restaurante, String  telefone, String  usuario, String  cpfcnpj, Integer status, Double valorfrete, Double  valorlimos, Double  valorprodutos, Double  valorcash, Double  valordesconto, Double valortotal,Double valorcashganho , Integer retirabalcao) {
        this.restaurante = restaurante;
        this.hashcupom = hashcupom;
        this.endereco = endereco;
        this.status = status;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.cep = cep;
        this.complemento = complemento;
        this.uf = uf;
        this.data = data;
        this.datamilisegundos = datamilisegundos;
        this.email = email;
        this.fantasia = fantasia;
        this.formapagamento = formapagamento;
        this.hora = hora;
        this.nomeusuario = nomeusuario;
        this.pedido = pedido;
        this.telefone = telefone;
        this.usuario = usuario;
        this.cpfcnpj = cpfcnpj;
        this.valorfrete = valorfrete;
        this.valorprodutos = valorprodutos;
        this.valorlimos = valorlimos;
        this.valorcash = valorcash;
        this.valordesconto = valordesconto;
        this.valortotal = valortotal;
        this.retirabalcao = retirabalcao;
        this.valorcashganho = valorcashganho;
    }

    public Double getValorcashganho() {
        return valorcashganho;
    }

    public void setValorcashganho(Double valorcashganho) {
        this.valorcashganho = valorcashganho;
    }

    public Integer getRetirabalcao() {
        return retirabalcao;
    }

    public void setRetirabalcao(Integer retirabalcao) {
        this.retirabalcao = retirabalcao;
    }

    public Long getDatamilisegundos() {
        return datamilisegundos;
    }

    public void setDatamilisegundos(Long datamilisegundos) {
        this.datamilisegundos = datamilisegundos;
    }

    public String getHashcupom() {
        return hashcupom;
    }

    public void setHashcupom(String hashcupom) {
        this.hashcupom = hashcupom;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFantasia() { return fantasia; }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getFormapagamento() {
        return formapagamento;
    }

    public void setFormapagamento(String formapagamento) {
        this.formapagamento = formapagamento;
    }

    public String getHora() {
        return hora;
    }

    public void setHora() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
        this.hora = horaFormat.format(new Date());
    }

    public String getNomeusuario() {
        return nomeusuario;
    }

    public void setNomeusuario(String nomeusuario) {
        this.nomeusuario = nomeusuario;
    }

    public Long getPedido() {
        return pedido;
    }

    public void setPedido(Long pedido) {
        this.pedido = pedido;
    }

    public String getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCpfcnpj() {
        return cpfcnpj;
    }

    public void setCpfcnpj(String cpfcnpj) {
        this.cpfcnpj = cpfcnpj;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getValorfrete() {
        return valorfrete;
    }

    public void setValorfrete(Double valorfrete) {
        this.valorfrete = valorfrete;
    }

    public Double getValorprodutos() {
        return valorprodutos;
    }

    public void setValorprodutos(Double valorprodutos) {
        this.valorprodutos = valorprodutos;
    }

    public Double getValorlimos() {
        return valorlimos;
    }

    public void setValorlimos(Double valorlimos) {
        this.valorlimos = valorlimos;
    }

    public Double getValorcash() {
        return valorcash;
    }

    public void setValorcash(Double valorcash) {
        this.valorcash = valorcash;
    }

    public Double getValordesconto() {
        return valordesconto;
    }

    public void setValordesconto(Double valordesconto) {
        this.valordesconto = valordesconto;
    }

    public Double getValortotal() {
        return valortotal;
    }

    public void setValortotal(Double valortotal) {
        this.valortotal = valortotal;
    }
}
