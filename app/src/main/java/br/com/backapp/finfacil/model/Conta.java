package br.com.backapp.finfacil.model;

/**
 * Created by raphael on 20/02/2015.
 */
public class Conta {
    private long id;
    private String descricao;
    private double valor;
    private String data;
    private long categoria_id;
    private boolean previsao;

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

    public long getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(long categoria_id) {
        this.categoria_id = categoria_id;
    }

    public boolean isPrevisao() {
        return previsao;
    }

    public void setPrevisao(boolean previsao) {
        this.previsao = previsao;
    }
}
