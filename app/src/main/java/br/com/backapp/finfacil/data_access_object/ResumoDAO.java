package br.com.backapp.finfacil.data_access_object;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.backapp.finfacil.model.Resumo;
import br.com.backapp.finfacil.resources.Recursos;

/**
 * Created by raphael on 20/02/2015.
 */
public class ResumoDAO {
    public static String NOME_DA_TABELA = "resumo";
    private int COLUNA_ID = 0;
    private int COLUNA_DESCRICAO = 1;
    private int COLUNA_VALOR = 2;
    private int COLUNA_DATA = 3;
    private int COLUNA_CATEGORIA = 4;
    private int COLUNA_PREVISAO = 5;
    private SQLiteDatabase database;

    public ResumoDAO(SQLiteDatabase database) {
        this.database = database;
    }

    public void inserir(Resumo resumo){
        ContentValues valores = new ContentValues();
        valores.put("descricao", resumo.getDescricao());
        valores.put("valor", resumo.getValor());
        valores.put("data", resumo.getData());
        valores.put("categoria_id", resumo.getCategoria_id());
        valores.put("previsao", resumo.isPrevisao());

        this.database.insert(NOME_DA_TABELA, null, valores);
    }

    public void atualizar(Resumo resumo){
        ContentValues valores = new ContentValues();
        valores.put("descricao", resumo.getDescricao());
        valores.put("valor", resumo.getValor());
        valores.put("data", resumo.getData());
        valores.put("categoria_id", resumo.getCategoria_id());
        valores.put("previsao", resumo.isPrevisao());

        this.database.update(NOME_DA_TABELA, valores, "_id = ?", new String[]{"" + resumo.getId()});
    }

    public void deletar(Resumo resumo){
        this.database.delete(NOME_DA_TABELA, "_id = ?", new String[]{"" + resumo.getId()});
    }

    public ArrayList<Resumo> obterTodosNaDataAtual(boolean ordenarPorDataDesc) {
        ArrayList<Resumo> lancamentos = new ArrayList<Resumo>();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, "data BETWEEN ? and ?", Recursos.whereBetweenMesAtual(), null, null, ordenarPorDataDesc ? "data desc" : "data");
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Resumo resumo = new Resumo();
            resumo.setId(cursor.getLong(COLUNA_ID));
            resumo.setDescricao(cursor.getString(COLUNA_DESCRICAO));
            resumo.setValor(cursor.getDouble(COLUNA_VALOR));
            resumo.setData(cursor.getString(COLUNA_DATA));
            resumo.setCategoria_id(cursor.getLong(COLUNA_CATEGORIA));
            resumo.setPrevisao(cursor.getInt(COLUNA_PREVISAO) == 1);
            lancamentos.add(resumo);
            cursor.moveToNext();
        }
        cursor.close();

        return lancamentos;
    }

    public Resumo obterResumoOndeIdIgual(long id){
        Resumo resumo = new Resumo();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, "_id=?", new String[]{Long.toString(id)}, null, null, null);
        cursor.moveToFirst();

        if ( cursor.getCount() > 0){
            resumo.setId(cursor.getLong(COLUNA_ID));
            resumo.setDescricao(cursor.getString(COLUNA_DESCRICAO));
            resumo.setValor(cursor.getDouble(COLUNA_VALOR));
            resumo.setData(cursor.getString(COLUNA_DATA));
            resumo.setCategoria_id(cursor.getLong(COLUNA_CATEGORIA));
            resumo.setPrevisao(cursor.getInt(COLUNA_PREVISAO) == 1);
        }
        cursor.close();

        return resumo;
    }

    public double obterTotalResumo(){
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

    public double obterTotal(){
        double valor = 0;
        Cursor cursor = this.database.rawQuery("SELECT SUM(valor) FROM " + NOME_DA_TABELA + " WHERE (previsao = 0)", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(0);
            }
            cursor.close();
        }

        return valor;
    }

    public double obterTotalResumoPrevisto(){
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

    public double obterTotalContaCorrenteAnterior(){
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
}
