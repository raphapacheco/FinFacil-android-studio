package br.com.backapp.finfacil.data_access_object;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import br.com.backapp.finfacil.model.Cartao;
import br.com.backapp.finfacil.resources.Recursos;

/**
 * Created by raphael on 20/02/2015.
 */
public class CartaoDAO {
    public static String NOME_DA_TABELA = "cartao";
    public static String CREATE_SCRIPT = " CREATE TABLE " + NOME_DA_TABELA
            + " ("
            + "    _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "   descricao TEXT,"
            + "   valor REAL,"
            + "   parcela TEXT,"
            + "   data TEXT"
            + " )";
    public static String DROP_SCRIPT = "DROP TABLE IF EXISTS " + NOME_DA_TABELA;
    private int COLUNA_ID = 0;
    private int COLUNA_DESCRICAO = 1;
    private int COLUNA_VALOR = 2;
    private int COLUNA_PARCELA = 3;
    private int COLUNA_DATA = 4;
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

        this.database.insert(NOME_DA_TABELA, null, valores);
    }

    public void atualizar(Cartao cartao){
        ContentValues valores = new ContentValues();
        valores.put("descricao", cartao.getDescricao());
        valores.put("valor", cartao.getValor());
        valores.put("data", cartao.getData());
        valores.put("parcela", cartao.getParcela());

        this.database.update(NOME_DA_TABELA, valores, "_id = ?", new String[]{""+cartao.getId()});
    }

    public void deletar(Cartao cartao){
        this.database.delete(NOME_DA_TABELA, "_id = ?", new String[]{""+cartao.getId()});
    }

    public ArrayList<Cartao> obterTodosNaDataAtual() {
        ArrayList<Cartao> lancamentos = new ArrayList<Cartao>();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, "data BETWEEN ? and ?", Recursos.whereBetweenDataAtual(), null, null, "data");
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Cartao cartao = new Cartao();
            cartao.setId(cursor.getLong(COLUNA_ID));
            cartao.setDescricao(cursor.getString(COLUNA_DESCRICAO));
            cartao.setValor(cursor.getDouble(COLUNA_VALOR));
            cartao.setParcela(cursor.getString(COLUNA_PARCELA));
            cartao.setData(cursor.getString(COLUNA_DATA));
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
        }
        cursor.close();

        return cartao;
    }

    public double obterTotalCartao(){
        double valor = 0;
        Cursor cursor = this.database.rawQuery("SELECT SUM(valor) FROM " + NOME_DA_TABELA + " WHERE data between ? and ?", Recursos.whereBetweenDataAtual());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(0);
            }
            cursor.close();
        }

        return valor;
    }
}
