package br.com.backapp.finfacil.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.data_access_object.CartaoDAO;
import br.com.backapp.finfacil.data_access_object.CarteiraDAO;
import br.com.backapp.finfacil.data_access_object.ResumoDAO;

/**
 * Created by raphael on 20/02/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String NOME_DO_BANCO_DE_DADOS = "fin_facil.db";
    private static int VERSAO_DO_BANCO_DE_DADOS = 3;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, NOME_DO_BANCO_DE_DADOS, null, VERSAO_DO_BANCO_DE_DADOS);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(lerArquivoSql(R.raw.criacao_tabela_categoria));
        db.execSQL(lerArquivoSql(R.raw.criacao_tabela_resumo));
        db.execSQL(lerArquivoSql(R.raw.criacao_tabela_carteira));
        db.execSQL(lerArquivoSql(R.raw.criacao_tabela_cartao));
        lerArquivoSQLExecutarLinhaALinha(db, R.raw.valores_padroes_categoria);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(lerArquivoSql(R.raw.ajuste_data_resumo));
            db.execSQL(lerArquivoSql(R.raw.ajuste_data_carteira));
            db.execSQL(lerArquivoSql(R.raw.ajuste_data_cartao));
        }

        if (oldVersion < 3){
            db.execSQL(lerArquivoSql(R.raw.criacao_tabela_categoria));
            lerArquivoSQLExecutarLinhaALinha(db, R.raw.valores_padroes_categoria);
            db.execSQL(lerArquivoSql(R.raw.criacao_campo_categoria_resumo));
            db.execSQL(lerArquivoSql(R.raw.criacao_campo_categoria_carteira));
            db.execSQL(lerArquivoSql(R.raw.criacao_campo_categoria_cartao));
            db.execSQL(lerArquivoSql(R.raw.criacao_campo_previsao_resumo));
            db.execSQL(lerArquivoSql(R.raw.criacao_campo_previsao_carteira));
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

    public void lerArquivoSQLExecutarLinhaALinha(SQLiteDatabase db, int rawId){
        try {
            InputStream is = this.context.getResources().openRawResource(rawId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.i("SQL Script", line);
                if (!line.isEmpty() && !line.trim().startsWith("--"))
                    db.execSQL(line);
            }
        } catch (IOException e) {
            Log.e("SQL Script", e.getMessage());
        }
        Log.i("SQL Script", "script executed");    }


}
