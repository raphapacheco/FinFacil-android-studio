package br.com.backapp.finfacil.activity;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.data_access_object.CartaoDAO;
import br.com.backapp.finfacil.data_access_object.CarteiraDAO;
import br.com.backapp.finfacil.data_access_object.ResumoDAO;
import br.com.backapp.finfacil.database.DatabaseHelper;

public class GraficoActivity extends ActionBarActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase base;
    private ResumoDAO resumoDAO;
    private CarteiraDAO carteiraDAO;
    private CartaoDAO cartaoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

        configurarActionBar();
        carregaDAO();
        preencheValores();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.contas, menu);
        menu.clear();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurarActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_title);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void carregaDAO() {
        this.databaseHelper = new DatabaseHelper(this);
        this.base = databaseHelper.getWritableDatabase();
        this.resumoDAO = new ResumoDAO(base);
        this.carteiraDAO = new CarteiraDAO(base);
        this.cartaoDAO = new CartaoDAO(base);
    }

    private void preencheValores() {
        String textMoeda = getResources().getString(R.string.text_moeda_para_formatacao);
        Double valorTotal = 0.0;
        TextView total = (TextView) findViewById(R.id.total);
        TextView contaCorrente = (TextView) findViewById(R.id.contaCorrente);
        TextView carteira = (TextView) findViewById(R.id.carteira);
        TextView cartao = (TextView) findViewById(R.id.cartao);

        valorTotal = resumoDAO.obterTotal() + carteiraDAO.obterTotal() + cartaoDAO.obterTotal();
        total.setText(String.format(textMoeda, valorTotal));
        contaCorrente.setText(String.format(textMoeda, resumoDAO.obterTotal()));
        carteira.setText(String.format(textMoeda, carteiraDAO.obterTotal()));
        cartao.setText(String.format(textMoeda, cartaoDAO.obterTotal()));
    }
}
