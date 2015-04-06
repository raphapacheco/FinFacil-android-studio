package br.com.backapp.finfacil.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.data_access_object.ResumoDAO;
import br.com.backapp.finfacil.database.DatabaseHelper;
import br.com.backapp.finfacil.model.Resumo;
import br.com.backapp.finfacil.resources.Recursos;


/**
 * Created by raphael on 22/02/2015.
 */
public class ResumoActivity extends ActionBarActivity {
    public static final String PARAMETRO_RESUMO_ID = "_id";
    private Resumo resumo;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase base;
    private long resumoIdSelecionado;
    private EditText editDescricao;
    private EditText editValor;
    private RadioButton radioButtonDebito;
    private Spinner spinnerRepetir;
    private TextView textData;
    private Date dataLancamento;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo);
        this.databaseHelper = new DatabaseHelper(this);
        base = databaseHelper.getWritableDatabase();
        this.context = this;
        dataLancamento = Recursos.getDataAtual();
        preencherVariaveisEdit();
        obterParametros();
        carregarResumo();
        configurarActionBar();
        configurarCampoValor();
    }

    private void preencherVariaveisEdit() {
        this.editDescricao = (EditText) findViewById(R.id.edit_descricao_resumo);
        this.editValor = (EditText) findViewById(R.id.edit_valor_resumo);
        this.editDescricao.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        this.radioButtonDebito = (RadioButton) findViewById(R.id.radioButton_debito_resumo);
        this.spinnerRepetir = (Spinner) findViewById(R.id.spinner_repetir_resumo);
        this.spinnerRepetir.setAdapter(Recursos.adapterTextoRepetirLancamento(this));
        this.textData = (TextView) findViewById(R.id.text_data_resumo);
        textData.setText(Recursos.converterDataParaStringFormatoCurto(dataLancamento));

        textData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar dataAtual = Calendar.getInstance();
                dataAtual.setTime(dataLancamento);
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar novaData = Calendar.getInstance();
                        novaData.set(year, monthOfYear, dayOfMonth);
                        dataLancamento = novaData.getTime();
                        textData.setText(Recursos.converterDataParaStringFormatoCurto(dataLancamento));
                    }
                },dataAtual.get(Calendar.YEAR), dataAtual.get(Calendar.MONTH), dataAtual.get(Calendar.DAY_OF_MONTH));

                datePicker.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.padrao, menu);

        if (resumoIdSelecionado == 0) {
            MenuItem item = menu.findItem(R.id.action_deletar);
            item.setVisible(false);
        }

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

            case R.id.action_deletar:
                deletar();
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

    private void obterParametros() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
          this.resumoIdSelecionado = bundle.getLong(PARAMETRO_RESUMO_ID);
    }

    private void carregarResumo() {
        if (this.resumoIdSelecionado == 0)
            resumo = new Resumo();
        else{
            ResumoDAO resumoDAO = new ResumoDAO(base);
            resumo = resumoDAO.obterResumoOndeIdIgual(this.resumoIdSelecionado);

            if (resumo != null){
                editDescricao.setText(resumo.getDescricao());
                editValor.setText(Recursos.converterDoubleParaString(Math.abs(resumo.getValor())));
                radioButtonDebito.setChecked(resumo.getValor() < 0);
                dataLancamento = Recursos.converterStringParaData(resumo.getData());
                textData.setText(Recursos.converterDataParaStringFormatoCurto(dataLancamento));
            }
        }
    }

    private void configurarCampoValor() {
        Recursos.configurarMascaraCasasDecimais(editValor);
    }

    private void salvar(){
        if (validarCampos()) {
            int numeroParcelas = 1;
            numeroParcelas += (int) spinnerRepetir.getSelectedItemId();

            Double valor = Double.valueOf(editValor.getText().toString());

            if (radioButtonDebito.isChecked())
                valor = valor * -1;

            resumo.setDescricao(editDescricao.getText().toString() + (numeroParcelas == 1 ? "": " (1/" + String.valueOf(numeroParcelas) + ")"));
            resumo.setValor(valor);
            resumo.setData(Recursos.converterDataParaStringBD(dataLancamento));

            ResumoDAO resumoDAO = new ResumoDAO(base);

            if (resumoIdSelecionado > 0)
                resumoDAO.atualizar(resumo);
            else
                resumoDAO.inserir(resumo);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataLancamento);

            for (int i = 2; i <= numeroParcelas; i++) {
                calendar.set(calendar.MONTH, calendar.get(calendar.MONTH)+ 1);
                resumo = new Resumo();
                resumo.setDescricao(editDescricao.getText().toString() + " (" + String.valueOf(i) + "/" + String.valueOf(numeroParcelas) + ")");
                resumo.setValor(valor);
                resumo.setData(Recursos.converterDataParaStringBD(calendar.getTime()));

                resumoDAO.inserir(resumo);
            }

            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();

            Toast.makeText(getApplicationContext(), R.string.msg_lancamento_efetuado, Toast.LENGTH_SHORT).show();
        }
    }

    private void deletar(){
        Recursos.confirmar(this, getResources().getString(R.string.msg_confirmar_exclusao), new Runnable() {
            @Override
            public void run() {
                ResumoDAO resumoDAO = new ResumoDAO(base);
                resumoDAO.deletar(resumo);

                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);

                finish();

                Toast.makeText(getApplicationContext(), R.string.msg_lancamento_excluido, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validarCampos(){
        boolean validou;
        validou = Recursos.obrigatorio(this, editDescricao);
        validou = validou && Recursos.obrigatorio(this, editValor);
        return validou;
    }
}
