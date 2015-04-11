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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.activity.spinner_adapter.CategoriaSpinnerAdapter;
import br.com.backapp.finfacil.data_access_object.CartaoDAO;
import br.com.backapp.finfacil.database.DatabaseHelper;
import br.com.backapp.finfacil.model.Cartao;
import br.com.backapp.finfacil.model.Categoria;
import br.com.backapp.finfacil.resources.Recursos;

/**
 * Created by raphael on 23/02/2015.
 */
public class CartaoActivity extends ActionBarActivity {
    public static final String PARAMETRO_CARTAO_ID = "_id";
    private Cartao cartao;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase base;
    private long cartaoIdSelecionado;
    private EditText editDescricao;
    private EditText editValor;
    private Spinner spinnerRepetir;
    private Spinner spinnerCategoria;
    private TextView textData;
    private Date dataLancamento;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartao);
        this.databaseHelper = new DatabaseHelper(this);
        base = databaseHelper.getWritableDatabase();
        this.context = this;
        dataLancamento = Recursos.getDataAtual();
        preencherVariaveisEdit();
        obterParametros();
        carregarCartao();
        configurarActionBar();
    }

    private void preencherVariaveisEdit() {
        this.editDescricao = (EditText) findViewById(R.id.edit_descricao_cartao);
        this.editValor = (EditText) findViewById(R.id.edit_valor_cartao);
        this.editDescricao.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        Recursos.configurarMascaraCasasDecimais(editValor);
        this.spinnerRepetir = (Spinner) findViewById(R.id.spinner_repetir_cartao);
        this.spinnerRepetir.setAdapter(Recursos.adapterTextoRepetirLancamento(this));
        this.textData = (TextView) findViewById(R.id.text_data_cartao);
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

        this.spinnerCategoria = (Spinner) findViewById(R.id.spinner_categoria_cartao);
        spinnerCategoria.setAdapter(Recursos.adapterCategoria(context, base));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.padrao, menu);

        if (cartaoIdSelecionado == 0) {
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
            this.cartaoIdSelecionado = bundle.getLong(PARAMETRO_CARTAO_ID);
    }

    private void carregarCartao() {
        if (this.cartaoIdSelecionado == 0)
            cartao = new Cartao();
        else{
            CartaoDAO cartaoDAO = new CartaoDAO(base);
            cartao = cartaoDAO.obterCartaoOndeIdIgual(cartaoIdSelecionado);

            if (cartao != null){
                editDescricao.setText(cartao.getDescricao());
                editValor.setText(Recursos.converterDoubleParaString(cartao.getValor()));
                dataLancamento = Recursos.converterStringParaData(cartao.getData());
                textData.setText(Recursos.converterDataParaStringFormatoCurto(dataLancamento));
                spinnerCategoria.setSelection((((CategoriaSpinnerAdapter) spinnerCategoria.getAdapter()).getPositioById(cartao.getCategoria_id())));
            }
        }
    }

    private void salvar(){
        if (validarCampos()) {
            int numeroParcelas = 1;
            numeroParcelas += (int) spinnerRepetir.getSelectedItemId();

            cartao.setDescricao(editDescricao.getText().toString() + (numeroParcelas == 1 ? "": " (1/" + String.valueOf(numeroParcelas) + ")"));
            cartao.setValor(Double.valueOf(editValor.getText().toString()));
            cartao.setParcela("1");
            cartao.setData(Recursos.converterDataParaStringBD(dataLancamento));
            cartao.setCategoria_id(((Categoria) spinnerCategoria.getSelectedItem()).getId());

            CartaoDAO cartaoDAO = new CartaoDAO(base);

            if (cartaoIdSelecionado > 0)
                cartaoDAO.atualizar(cartao);
            else
                cartaoDAO.inserir(cartao);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataLancamento);

            for (int i = 2; i <= numeroParcelas; i++) {
                calendar.set(calendar.MONTH, calendar.get(calendar.MONTH)+ 1);
                cartao = new Cartao();
                cartao.setDescricao(editDescricao.getText().toString() + " (" + String.valueOf(i) + "/" + String.valueOf(numeroParcelas) + ")");
                cartao.setValor(Double.valueOf(editValor.getText().toString()));
                cartao.setData(Recursos.converterDataParaStringBD(calendar.getTime()));
                cartao.setCategoria_id(((Categoria) spinnerCategoria.getSelectedItem()).getId());

                cartaoDAO.inserir(cartao);
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
                CartaoDAO cartaoDAO = new CartaoDAO(base);
                cartaoDAO.deletar(cartao);

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
