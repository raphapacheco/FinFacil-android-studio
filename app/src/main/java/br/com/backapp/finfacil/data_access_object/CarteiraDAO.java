package br.com.backapp.finfacil.data_access_object;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.backapp.finfacil.model.Carteira;
import br.com.backapp.finfacil.resources.Recursos;

/**
 * Created by raphael on 20/02/2015.
 */
public class CarteiraDAO {
    public static String NOME_DA_TABELA = "carteira";
    public static String CREATE_SCRIPT = " CREATE TABLE " + NOME_DA_TABELA
            + " ("
            + "    _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "   descricao TEXT,"
            + "   valor REAL,"
            + "   data TEXT"
            + " )";
    public static String DROP_SCRIPT = "DROP TABLE IF EXISTS " + NOME_DA_TABELA;
    private int COLUNA_ID = 0;
    private int COLUNA_DESCRICAO = 1;
    private int COLUNA_VALOR = 2;
    private int COLUNA_DATA = 3;
    private SQLiteDatabase database;

    public CarteiraDAO(SQLiteDatabase database) {
        this.database = database;
    }

    public void inserir(Carteira carteira){
        ContentValues valores = new ContentValues();
        valores.put("descricao", carteira.getDescricao());
        valores.put("valor", carteira.getValor());
        valores.put("data", carteira.getData());

        this.database.insert(NOME_DA_TABELA, null, valores);
    }

    public void atualizar(Carteira carteira){
        ContentValues valores = new ContentValues();
        valores.put("descricao", carteira.getDescricao());
        valores.put("valor", carteira.getValor());
        valores.put("data", carteira.getData());

        this.database.update(NOME_DA_TABELA, valores, "_id = ?", new String[]{""+carteira.getId()});
    }

    public void deletar(Carteira carteira){
        this.database.delete(NOME_DA_TABELA, "_id = ?", new String[]{""+carteira.getId()});
    }

    public ArrayList<Carteira> obterTodosNaDataAtual() {
        ArrayList<Carteira> lancamentos = new ArrayList<Carteira>();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, "data BETWEEN ? and ?", Recursos.whereBetweenMesAtual(), null, null, "data");
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Carteira carteira = new Carteira();
            carteira.setId(cursor.getLong(COLUNA_ID));
            carteira.setDescricao(cursor.getString(COLUNA_DESCRICAO));
            carteira.setValor(cursor.getDouble(COLUNA_VALOR));
            carteira.setData(cursor.getString(COLUNA_DATA));
            lancamentos.add(carteira);
            cursor.moveToNext();
        }
        cursor.close();

        return lancamentos;
    }

    public Carteira obterCarteiraOndeIdIgual(long id){
        Carteira carteira = new Carteira();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, "_id=?", new String[]{Long.toString(id)}, null, null, null);
        cursor.moveToFirst();

        if ( cursor.getCount() > 0){
            carteira.setId(cursor.getLong(COLUNA_ID));
            carteira.setDescricao(cursor.getString(COLUNA_DESCRICAO));
            carteira.setValor(cursor.getDouble(COLUNA_VALOR));
            carteira.setData(cursor.getString(COLUNA_DATA));
        }
        cursor.close();

        return carteira;
    }

    public double obterTotalCarteira(){
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

    public double obterTotalCarteiraAnterior(){
        double valor = 0;
        Cursor cursor = this.database.rawQuery("SELECT SUM(valor) FROM " + NOME_DA_TABELA + " WHERE data < ?", Recursos.whereMesAtual());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(0);
            }
            cursor.close();
        }

        return valor;
    }
}

