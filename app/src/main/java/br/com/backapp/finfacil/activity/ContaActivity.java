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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.activity.spinner_adapter.CategoriaSpinnerAdapter;
import br.com.backapp.finfacil.data_access_object.ContaDAO;
import br.com.backapp.finfacil.database.DatabaseHelper;
import br.com.backapp.finfacil.model.Categoria;
import br.com.backapp.finfacil.model.Conta;
import br.com.backapp.finfacil.resources.Recursos;


/**
 * Created by raphael on 22/02/2015.
 */
public class ContaActivity extends ActionBarActivity {
    public static final String PARAMETRO_CONTA_ID = "_id";
    private Conta conta;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase base;
    private long contaIdSelecionado;
    private EditText editDescricao;
    private EditText editValor;
    private RadioButton radioButtonCredito;
    private Spinner spinnerRepetir;
    private Spinner spinnerCategoria;
    private TextView textData;
    private CheckBox checkPrevisao;
    private Date dataLancamento;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);
        this.databaseHelper = new DatabaseHelper(this);
        base = databaseHelper.getWritableDatabase();
        this.context = this;
        dataLancamento = Recursos.getDataAtual();
        preencherVariaveisEdit();
        obterParametros();
        carregarConta();
        configurarActionBar();
        configurarCampoValor();
    }

    private void preencherVariaveisEdit() {
        this.editDescricao = (EditText) findViewById(R.id.edit_descricao_conta);
        this.editValor = (EditText) findViewById(R.id.edit_valor_conta);
        this.editDescricao.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        this.radioButtonCredito = (RadioButton) findViewById(R.id.radioButton_credito_conta);
        this.spinnerRepetir = (Spinner) findViewById(R.id.spinner_repetir_conta);
        this.spinnerRepetir.setAdapter(Recursos.adapterTextoRepetirLancamento(this));
        this.textData = (TextView) findViewById(R.id.text_data_conta);
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

        this.spinnerCategoria = (Spinner) findViewById(R.id.spinner_categoria_conta);
        spinnerCategoria.setAdapter(Recursos.adapterCategoria(context, base));
        this.checkPrevisao = (CheckBox) findViewById(R.id.check_previsao_conta);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.padrao, menu);

        if (contaIdSelecionado == 0) {
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
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void obterParametros() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
          this.contaIdSelecionado = bundle.getLong(PARAMETRO_CONTA_ID);
    }

    private void carregarConta() {
        if (this.contaIdSelecionado == 0)
            conta = new Conta();
        else{
            ContaDAO contaDAO = new ContaDAO(base);
            conta = contaDAO.obterContaOndeIdIgual(this.contaIdSelecionado);

            if (conta != null){
                editDescricao.setText(conta.getDescricao());
                editValor.setText(Recursos.converterDoubleParaString(Math.abs(conta.getValor())));
                radioButtonCredito.setChecked(conta.getValor() >= 0);
                dataLancamento = Recursos.converterStringParaData(conta.getData());
                textData.setText(Recursos.converterDataParaStringFormatoCurto(dataLancamento));
                spinnerCategoria.setSelection(((CategoriaSpinnerAdapter) spinnerCategoria.getAdapter()).getPositioById(conta.getCategoria_id()));
                checkPrevisao.setChecked(conta.isPrevisao());
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

            if (!radioButtonCredito.isChecked())
                valor = valor * -1;

            conta.setDescricao(editDescricao.getText().toString() + (numeroParcelas == 1 ? "": " (1/" + String.valueOf(numeroParcelas) + ")"));
            conta.setValor(valor);
            conta.setData(Recursos.converterDataParaStringBD(dataLancamento));
            conta.setCategoria_id(((Categoria) spinnerCategoria.getSelectedItem()).getId());
            conta.setPrevisao(checkPrevisao.isChecked());

            ContaDAO contaDAO = new ContaDAO(base);

            if (contaIdSelecionado > 0)
                contaDAO.atualizar(conta);
            else
                contaDAO.inserir(conta);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataLancamento);

            for (int i = 2; i <= numeroParcelas; i++) {
                calendar.set(calendar.MONTH, calendar.get(calendar.MONTH)+ 1);
                conta = new Conta();
                conta.setDescricao(editDescricao.getText().toString() + " (" + String.valueOf(i) + "/" + String.valueOf(numeroParcelas) + ")");
                conta.setValor(valor);
                conta.setData(Recursos.converterDataParaStringBD(calendar.getTime()));
                conta.setCategoria_id(((Categoria) spinnerCategoria.getSelectedItem()).getId());
                conta.setPrevisao(checkPrevisao.isChecked());

                contaDAO.inserir(conta);
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
                ContaDAO contaDAO = new ContaDAO(base);
                contaDAO.deletar(conta);

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
