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
import br.com.backapp.finfacil.data_access_object.CarteiraDAO;
import br.com.backapp.finfacil.database.DatabaseHelper;
import br.com.backapp.finfacil.model.Carteira;
import br.com.backapp.finfacil.resources.Recursos;

/**
 * Created by raphael on 23/02/2015.
 */
public class CarteiraActivity extends ActionBarActivity {
    public static final String PARAMETRO_CARTEIRA_ID = "_id";
    private Carteira carteira;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase base;
    private long carteiraIdSelecionado;
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
        setContentView(R.layout.activity_carteira);
        this.databaseHelper = new DatabaseHelper(this);
        base = databaseHelper.getWritableDatabase();
        this.context = this;
        dataLancamento = Recursos.getDataAtual();
        preencherVariaveisEdit();
        obterParametros();
        carregarCarteira();
        configurarActionBar();
        configurarCampoValor();
    }

    private void preencherVariaveisEdit() {
        this.editDescricao = (EditText) findViewById(R.id.edit_descricao_carteira);
        this.editValor = (EditText) findViewById(R.id.edit_valor_carteira);
        this.editDescricao.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        this.radioButtonDebito = (RadioButton) findViewById(R.id.radioButton_debito_carteira);
        this.spinnerRepetir = (Spinner) findViewById(R.id.spinner_repetir_carteira);
        this.spinnerRepetir.setAdapter(Recursos.adapterTextoRepetirLancamento(this));
        this.textData = (TextView) findViewById(R.id.text_data_carteira);
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

        if (carteiraIdSelecionado == 0) {
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
            this.carteiraIdSelecionado = bundle.getLong(PARAMETRO_CARTEIRA_ID);
    }

    private void carregarCarteira() {
        if (this.carteiraIdSelecionado == 0)
            carteira = new Carteira();
        else{
            CarteiraDAO carteiraDAO = new CarteiraDAO(base);
            carteira = carteiraDAO.obterCarteiraOndeIdIgual(this.carteiraIdSelecionado);

            if (carteira != null){
                editDescricao.setText(carteira.getDescricao());
                editValor.setText(Recursos.converterDoubleParaString(Math.abs(carteira.getValor())));
                radioButtonDebito.setChecked(carteira.getValor() < 0);
                dataLancamento = Recursos.converterStringParaData(carteira.getData());
                textData.setText(Recursos.converterDataParaStringFormatoCurto(dataLancamento));
            }
        }
    }

    private void configurarCampoValor() {
        Recursos.configurarMascaraCasasDecimais(editValor);
    }

    private void salvar(){
        if (validarCampos()) {
            Double valor = Double.valueOf(editValor.getText().toString());

            if (radioButtonDebito.isChecked())
                valor = valor * -1;

            carteira.setDescricao(editDescricao.getText().toString());
            carteira.setValor(valor);
            carteira.setData(Recursos.converterDataParaStringBD(dataLancamento));

            CarteiraDAO carteiraDAO = new CarteiraDAO(base);

            if (carteiraIdSelecionado > 0)
                carteiraDAO.atualizar(carteira);
            else
                carteiraDAO.inserir(carteira);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataLancamento);

            for (int i = 0; i < spinnerRepetir.getSelectedItemId(); i++) {
                calendar.set(calendar.MONTH, calendar.get(calendar.MONTH)+ 1);
                carteira = new Carteira();
                carteira.setDescricao(editDescricao.getText().toString());
                carteira.setValor(valor);
                carteira.setData(Recursos.converterDataParaStringBD(calendar.getTime()));

                carteiraDAO.inserir(carteira);
            }

            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();

            Toast.makeText(getApplicationContext(), R.string.msg_lancamento_efetuado, Toast.LENGTH_SHORT).show();
        }
    }

    public void deletar(){
        Recursos.confirmar(this, getResources().getString(R.string.msg_confirmar_exclusao), new Runnable() {
            @Override
            public void run() {
                CarteiraDAO carteiraDAO = new CarteiraDAO(base);
                carteiraDAO.deletar(carteira);

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
