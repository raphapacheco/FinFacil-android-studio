package br.com.backapp.finfacil.data_access_object;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.backapp.finfacil.model.Conta;
import br.com.backapp.finfacil.resources.Recursos;

/**
 * Created by raphael on 20/02/2015.
 */
public class ContaDAO {
    public static String NOME_DA_TABELA = "resumo";
    private int COLUNA_ID = 0;
    private int COLUNA_DESCRICAO = 1;
    private int COLUNA_VALOR = 2;
    private int COLUNA_DATA = 3;
    private int COLUNA_CATEGORIA = 4;
    private int COLUNA_PREVISAO = 5;
    private SQLiteDatabase database;

    public ContaDAO(SQLiteDatabase database) {
        this.database = database;
    }

    public void inserir(Conta conta){
        ContentValues valores = new ContentValues();
        valores.put("descricao", conta.getDescricao());
        valores.put("valor", conta.getValor());
        valores.put("data", conta.getData());
        valores.put("categoria_id", conta.getCategoria_id());
        valores.put("previsao", conta.isPrevisao());

        this.database.insert(NOME_DA_TABELA, null, valores);
    }

    public void atualizar(Conta conta){
        ContentValues valores = new ContentValues();
        valores.put("descricao", conta.getDescricao());
        valores.put("valor", conta.getValor());
        valores.put("data", conta.getData());
        valores.put("categoria_id", conta.getCategoria_id());
        valores.put("previsao", conta.isPrevisao());

        this.database.update(NOME_DA_TABELA, valores, "_id = ?", new String[]{"" + conta.getId()});
    }

    public void deletar(Conta conta){
        this.database.delete(NOME_DA_TABELA, "_id = ?", new String[]{"" + conta.getId()});
    }

    public ArrayList<Conta> obterTodosNaDataAtual(boolean ordenarPorDataDesc) {
        ArrayList<Conta> lancamentos = new ArrayList<Conta>();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, "data BETWEEN ? and ?", Recursos.whereBetweenMesAtual(), null, null, ordenarPorDataDesc ? "data desc" : "data");
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Conta conta = new Conta();
            conta.setId(cursor.getLong(COLUNA_ID));
            conta.setDescricao(cursor.getString(COLUNA_DESCRICAO));
            conta.setValor(cursor.getDouble(COLUNA_VALOR));
            conta.setData(cursor.getString(COLUNA_DATA));
            conta.setCategoria_id(cursor.getLong(COLUNA_CATEGORIA));
            conta.setPrevisao(cursor.getInt(COLUNA_PREVISAO) == 1);
            lancamentos.add(conta);
            cursor.moveToNext();
        }
        cursor.close();

        return lancamentos;
    }

    public Conta obterContaOndeIdIgual(long id){
        Conta conta = new Conta();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, "_id=?", new String[]{Long.toString(id)}, null, null, null);
        cursor.moveToFirst();

        if ( cursor.getCount() > 0){
            conta.setId(cursor.getLong(COLUNA_ID));
            conta.setDescricao(cursor.getString(COLUNA_DESCRICAO));
            conta.setValor(cursor.getDouble(COLUNA_VALOR));
            conta.setData(cursor.getString(COLUNA_DATA));
            conta.setCategoria_id(cursor.getLong(COLUNA_CATEGORIA));
            conta.setPrevisao(cursor.getInt(COLUNA_PREVISAO) == 1);
        }
        cursor.close();

        return conta;
    }

    public double obterTotalConta(){
        double valor = 0;
        Cursor cursor = this.database.rawQuery("SELECT SUM(valor) FROM " + NOME_DA_TABELA + " WHERE (data between ? and ?) and (previsao = 0)", Recursos.whereBetweenMesAtual());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(0);
            }
            cursor.close();
        }

        return valor;
    }

    public double obterTotalContaPrevisto(){
        double valor = 0;
        Cursor cursor = this.database.rawQuery("SELECT SUM(valor) FROM " + NOME_DA_TABELA + " WHERE (data between ? and ?)", Recursos.whereBetweenMesAtual());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(0);
            }
            cursor.close();
        }

        return valor;
    }

    public double obterTotalContaAnterior(){
        double valor = 0;
        Cursor cursor = this.database.rawQuery("SELECT SUM(valor) FROM " + NOME_DA_TABELA + " WHERE (data < ?) and (previsao = 0) ", Recursos.whereMesAtual());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(0);
            }
            cursor.close();
        }

        return valor;
    }

    public double obterTotalContaPrevistoAnterior(){
        double valor = 0;
        Cursor cursor = this.database.rawQuery("SELECT SUM(valor) FROM " + NOME_DA_TABELA + " WHERE (data < ?)", Recursos.whereMesAtual());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(0);
            }
            cursor.close();
        }

        return valor;
    }
}
