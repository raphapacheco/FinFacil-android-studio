package br.com.backapp.finfacil.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.resources.MascaraCasasDecimaisInputFilter;
import br.com.backapp.finfacil.resources.Recursos;


/**
 * Created by raphael on 01/03/2015.
 */
public class ConfiguracaoActivity extends ActionBarActivity {
    private String SALARIO_CONFIG = "salario";
    private String DIA_FECHAMENTO_CONFIG = "dia_fechamento_cartao";
    private TextView textDiaFechamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
        configurarActionBar();
        configurarCampos();
        carregarConfiguracoes();
    }

    private void configurarCampos() {
        this.textDiaFechamento = (TextView) findViewById(R.id.text_dia_fechamento_cartao_configuracao);

        textDiaFechamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.padrao, menu);
        MenuItem item = menu.findItem(R.id.action_deletar);
        item.setVisible(false);

        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.action_salvar:
                salvar();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void configurarActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void carregarConfiguracoes(){
        SharedPreferences config = getApplicationContext().getSharedPreferences("FinFacilConfig", MODE_PRIVATE);
        textDiaFechamento.setText(config.getString(DIA_FECHAMENTO_CONFIG, null));
    }

    private void salvar(){
        SharedPreferences config = getApplicationContext().getSharedPreferences("FinFacilConfig", MODE_PRIVATE);
        SharedPreferences.Editor editor = config.edit();

        TextView textDiaFechamento = (TextView) findViewById(R.id.text_dia_fechamento_cartao_configuracao);
        editor.putString(DIA_FECHAMENTO_CONFIG, textDiaFechamento.getText().toString());

        editor.commit(); // commit changes
        finish();
    }
}
