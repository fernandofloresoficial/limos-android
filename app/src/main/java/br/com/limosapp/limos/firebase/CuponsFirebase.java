package br.com.limosapp.limos.firebase;

public class CuponsFirebase {

    private Integer ativo, status, desconto, seg, ter, qua, qui, sex, sab, dom;
    private String restaurante, validade;
    private Long validademseg;

    public CuponsFirebase() {
    }

    public CuponsFirebase(String restaurante, Integer ativo, Integer status, Integer desconto, Integer seg, Integer ter, Integer qua, Integer qui, Integer sex, Integer sab, Integer dom, String validade, Long validademseg) {
        this.restaurante = restaurante;
        this.ativo = ativo;
        this.status = status;
        this.desconto = desconto;
        this.seg = seg;
        this.ter = ter;
        this.qua = qua;
        this.qui = qui;
        this.sex = sex;
        this.sab = sab;
        this.dom = dom;
        this.validade = validade;
        this.validademseg = validademseg;
    }
    public String getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }

    public Integer getAtivo() {
        return ativo;
    }

    public void setAtivo(Integer ativo) {
        this.ativo = ativo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDesconto() {
        return desconto;
    }

    public void setDesconto(Integer desconto) {
        this.desconto = desconto;
    }

    public Integer getSeg() {
        return seg;
    }

    public void setSeg(Integer seg) {
        this.seg = seg;
    }

    public Integer getTer() {
        return ter;
    }

    public void setTer(Integer ter) {
        this.ter = ter;
    }

    public Integer getQua() {
        return qua;
    }

    public void setQua(Integer qua) {
        this.qua = qua;
    }

    public Integer getQui() {
        return qui;
    }

    public void setQui(Integer qui) {
        this.qui = qui;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getSab() {
        return sab;
    }

    public void setSab(Integer sab) {
        this.sab = sab;
    }

    public Integer getDom() {
        return dom;
    }

    public void setDom(Integer dom) {
        this.dom = dom;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public Long getValidademseg() {
        return validademseg;
    }

    public void setValidademseg(Long validademseg) {
        this.validademseg = validademseg;
    }
}