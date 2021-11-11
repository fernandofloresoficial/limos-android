package br.com.limosapp.limos.firebase;

public class AdicionaisFirebase {

    private Long id;
    private String adicional;
    private Double valoradicional;

    public AdicionaisFirebase() {
    }

    public AdicionaisFirebase(Long id, String adicional, Double valoradicional) {
        this.id = id;
        this.adicional = adicional;
        this.valoradicional = valoradicional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdicional() {
        return adicional;
    }

    public void setAdicional(String adicional) {
        this.adicional = adicional;
    }

    public Double getValoradicional() {
        return valoradicional;
    }

    public void setValoradicional(Double valoradicional) {
        this.valoradicional = valoradicional;
    }
}
