package br.com.backapp.finfacil.data_access_object;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

import br.com.backapp.finfacil.model.Categoria;

/**
 * Created by raphael on 06/04/2015.
 */
public class CategoriaDAO {
    public static String NOME_DA_TABELA = "categoria";
    private int COLUNA_ID = 0;
    private int COLUNA_DESCRICAO = 1;
    private SQLiteDatabase database;

    public CategoriaDAO(SQLiteDatabase database) {
        this.database = database;
    }

    public void inserir(Categoria categoria){
        ContentValues valores = new ContentValues();
        valores.put("descricao", categoria.getDescricao());

        this.database.insert(NOME_DA_TABELA, null, valores);
    }

    public void atualizar(Categoria categoria){
        ContentValues valores = new ContentValues();
        valores.put("descricao", categoria.getDescricao());

        this.database.update(NOME_DA_TABELA, valores, "_id = ?", new String[]{""+categoria.getId()});
    }

    public void deletar(Categoria categoria){
        this.database.delete(NOME_DA_TABELA, "_id = ?", new String[]{""+categoria.getId()});
    }

    public ArrayList<Categoria> obterTodos() {
        ArrayList<Categoria> categorias = new ArrayList<Categoria>();
        Cursor cursor = this.database.query(NOME_DA_TABELA, null, null, null, null, null, "descricao");
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            Categoria categoria = new Categoria();
            categoria.setId(cursor.getLong(COLUNA_ID));
            categoria.setDescricao(cursor.getString(COLUNA_DESCRICAO));

            categorias.add(categoria);
            cursor.moveToNext();
        }
        cursor.close();

        return categorias;
    }
}
