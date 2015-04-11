package br.com.backapp.finfacil.model;

/**
 * Created by raphael on 20/02/2015.
 */
public class Cartao {
    private long id;
    private String descricao;
    private double valor;
    private String data;
    private String parcela;
    private long categoria_id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getParcela() {
        return parcela;
    }

    public void setParcela(String parcela) {
        this.parcela = parcela;
    }

    public long getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(long categoria_id) {
        this.categoria_id = categoria_id;
    }

}
