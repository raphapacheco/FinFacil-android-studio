package br.com.backapp.finfacil.data_access_object;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.backapp.finfacil.model.Cartao;
import br.com.backapp.finfacil.resources.Recursos;

/**
 * Created by raphael on 20/02/2015.
 */
public class CartaoDAO {
    public static String NOME_DA_TABELA = "cartao";
    private int COLUNA_ID = 0;
    private int COLUNA_DESCRICAO = 1;
    private int COLUNA_VALOR = 2;
    private int COLUNA_PARCELA = 3;
    private int COLUNA_DATA = 4;
    private int COLUNA_CATEGORIA = 5;
    private SQLiteDatabase database;

    public CartaoDAO(SQLiteDatabase database) {
        this.database = database;
    }

    public void inserir(Cartao cartao){
        ContentValues valores = new ContentValues();
        valores.put("descricao", cartao.getDescricao());
        valores.put("valor", cartao.getValor());
        valores.put("data", cartao.getData());
        valores.put("parcela", cartao.getParcela());
        valores.put("categoria_id", cartao.getCategoria_id());

        this.database.insert(NOME_DA_TABELA, null, valores);
    }

    public void atualizar(Cartao cartao){
        ContentValues valores = new ContentValues();
        valores.put("descricao", cartao.getDescricao());
        valores.put("valor", cartao.getValor());
        valores.put("data", cartao.getData());
        valores.put("parcela", cartao.getParcela());
        valores.put("categoria_id", cartao.getCategoria_id());

        this.database.update(NOME_DA_TABELA, valores, "_id = ?", new String[]{""+cartao.getId()});
    }

    public void deletar(Cartao cartao){
        this.database.delete(NOME_DA_TABELA, "_id = ?", new String[]{""+cartao.getId()});
    }

    public ArrayList<Cartao> obterTodosNaDataAtual() {
        ArrayList<Cartao> lancamentos = new ArrayList<Cartao>();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, "data BETWEEN ? and ?", Recursos.whereBetweenMesAtual(), null, null, "data");
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Cartao cartao = new Cartao();
            cartao.setId(cursor.getLong(COLUNA_ID));
            cartao.setDescricao(cursor.getString(COLUNA_DESCRICAO));
            cartao.setValor(cursor.getDouble(COLUNA_VALOR));
            cartao.setParcela(cursor.getString(COLUNA_PARCELA));
            cartao.setData(cursor.getString(COLUNA_DATA));
            cartao.setCategoria_id(cursor.getLong(COLUNA_CATEGORIA));
            lancamentos.add(cartao);
            cursor.moveToNext();
        }
        cursor.close();

        return lancamentos;
    }

    public Cartao obterCartaoOndeIdIgual(long id){
        Cartao cartao = new Cartao();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, "_id=?", new String[]{Long.toString(id)}, null, null, null);
        cursor.moveToFirst();

        if ( cursor.getCount() > 0){
            cartao.setId(cursor.getLong(COLUNA_ID));
            cartao.setDescricao(cursor.getString(COLUNA_DESCRICAO));
            cartao.setValor(cursor.getDouble(COLUNA_VALOR));
            cartao.setParcela(cursor.getString(COLUNA_PARCELA));
            cartao.setData(cursor.getString(COLUNA_DATA));
            cartao.setCategoria_id(cursor.getLong(COLUNA_CATEGORIA));
        }
        cursor.close();

        return cartao;
    }

    public double obterTotalCartao(){
        double valor = 0;
        Cursor cursor = this.database.rawQuery("SELECT SUM(valor) FROM " + NOME_DA_TABELA + " WHERE data between ? and ?", Recursos.whereBetweenMesAtual());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(0);
            }
            cursor.close();
        }

        return valor;
    }
}
