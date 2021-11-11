package br.com.limosapp.limos.firebase;

public class CuponsResgatadosFirebase {

    private Integer desconto, seg, ter, qua, qui, sex, sab, dom;
    private String validade;
    private Long validademseg;

    public CuponsResgatadosFirebase(Integer desconto, Integer seg, Integer ter, Integer qua, Integer qui, Integer sex, Integer sab, Integer dom, String validade, Long validademseg) {
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