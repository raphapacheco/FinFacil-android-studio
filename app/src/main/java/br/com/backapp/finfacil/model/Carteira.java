package br.com.backapp.finfacil.model;

import java.util.Date;

/**
 * Created by raphael on 20/02/2015.
 */
public class Carteira {
    private long id;
    private String descricao;
    private double valor;
    private String data;


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
}
