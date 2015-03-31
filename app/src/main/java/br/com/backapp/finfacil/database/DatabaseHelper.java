package br.com.backapp.finfacil.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.data_access_object.CartaoDAO;
import br.com.backapp.finfacil.data_access_object.CarteiraDAO;
import br.com.backapp.finfacil.data_access_object.ResumoDAO;

/**
 * Created by raphael on 20/02/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String NOME_DO_BANCO_DE_DADOS = "fin_facil.db";
    private static int VERSAO_DO_BANCO_DE_DADOS = 2;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, NOME_DO_BANCO_DE_DADOS, null, VERSAO_DO_BANCO_DE_DADOS);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ResumoDAO.CREATE_SCRIPT);
        db.execSQL(CarteiraDAO.CREATE_SCRIPT);
        db.execSQL(CartaoDAO.CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(lerArquivoSql(R.raw.ajuste_data_resumo));
            db.execSQL(lerArquivoSql(R.raw.ajuste_data_carteira));
            db.execSQL(lerArquivoSql(R.raw.ajuste_data_cartao));
        }
    }

    public String lerArquivoSql(int rawId) {
        InputStream inputStream = context.getResources().openRawResource(rawId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }
}
