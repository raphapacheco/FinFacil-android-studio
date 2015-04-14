package br.com.backapp.finfacil.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.activity.list_view_adapter.CartaoListViewAdapter;
import br.com.backapp.finfacil.activity.list_view_adapter.CarteiraListViewAdapter;
import br.com.backapp.finfacil.activity.list_view_adapter.ResumoListViewAdapter;
import br.com.backapp.finfacil.data_access_object.CartaoDAO;
import br.com.backapp.finfacil.data_access_object.CarteiraDAO;
import br.com.backapp.finfacil.data_access_object.ResumoDAO;
import br.com.backapp.finfacil.database.DatabaseHelper;
import br.com.backapp.finfacil.model.Cartao;
import br.com.backapp.finfacil.model.Carteira;
import br.com.backapp.finfacil.model.Resumo;
import br.com.backapp.finfacil.resources.Configuracoes;
import br.com.backapp.finfacil.resources.Recursos;

public class ContasActivity extends ActionBarActivity {
    public static final String ABA_RESUMO_NOME = "Resumo";
    public static final String ABA_CARTEIRA_NOME = "Carteira";
    public static final String ABA_CARTAO_NOME = "Cartao";
    public static final String CONFIG_ABA_SELECIONADA = "CONFIG_ABA_SELECIONADA";
    private Context context;
    private ListView listViewResumo;
    private ListView listViewCarteira;
    private ListView listViewCartao;
    private ImageButton buttonTotalizadores;
    private TextView textTotal;
    private TextView textTotalPrevisto;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase base;
    private ResumoDAO resumoDAO;
    private CarteiraDAO carteiraDAO;
    private CartaoDAO cartaoDAO;
    private ArrayList<Resumo> resumos;
    private ArrayList<Carteira> carteiras;
    private ArrayList<Cartao> cartaos;
    private String abaSelecionada;
    private double totalResumo = 0;
    private double totalResumoPrevisto = 0;
    private double totalCarteira = 0;
    private double totalCarteiraPrevisto = 0;
    private double totalCartao = 0;
    private double totalCarteiraAnterior = 0;
    private double totalCarteiraPrevistoAnterior = 0;
    private Integer posicaoItemSelecionado = -1;
    private MenuItem menuData;
    private Resumo resumoSelecionado;
    private Carteira carteiraSelecionado;
    private Cartao cartaoSelecionado;
    private Configuracoes configuracoes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas);
        this.context = getApplicationContext();
        this.databaseHelper = new DatabaseHelper(this);
        this.base = databaseHelper.getWritableDatabase();
        this.resumoDAO = new ResumoDAO(base);
        this.carteiraDAO = new CarteiraDAO(base);
        this.cartaoDAO = new CartaoDAO(base);
        this.configuracoes = new Configuracoes(context);

        preencherVariaveisCampos();
        configurarActionBar();
        carregarContasSelecionadas();
        configurarListView();
        configurarTabs();
        preencherTotal();
        configurarBotoes();

        if (savedInstanceState != null) {
            this.abaSelecionada = savedInstanceState.getString(CONFIG_ABA_SELECIONADA);
            TabHost abas = (TabHost) findViewById(android.R.id.tabhost);

            if (abaSelecionada.equals(ABA_RESUMO_NOME))
                abas.setCurrentTab(0);

            if (abaSelecionada.equals(ABA_CARTEIRA_NOME))
                abas.setCurrentTab(1);

            if (abaSelecionada.equals(ABA_CARTAO_NOME))
                abas.setCurrentTab(2);
        }
    }

    private void preencherVariaveisCampos() {
        this.textTotal = (TextView) findViewById(R.id.activity_contas_total);
        this.textTotalPrevisto = (TextView) findViewById(R.id.activity_contas_total_previsto);
        this.buttonTotalizadores = (ImageButton) findViewById(R.id.activity_contas_botao_total);

        this.buttonTotalizadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configuracoes.setModoVisualizacao(configuracoes.getModoVisualizacao() + 1);
                configuracoes.salvar();
                atualizarContas();
            }
        });
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CONFIG_ABA_SELECIONADA, abaSelecionada);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.contas, menu);
        this.menuData = menu.findItem(R.id.action_data);
        menuData.setTitle(Recursos.dataAtualFormatoMesAno());

        //TODO: Remover após criar as configurações
        MenuItem conf = menu.findItem(R.id.action_settings);
        conf.setVisible(false);

        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(ContasActivity.this, ConfiguracaoActivity.class);
            this.startActivity(intent);

            return true;
        }

        if (id == R.id.action_data){
            Calendar dataAtual = Calendar.getInstance();
            dataAtual.setTime(Recursos.getDataAtual());
            DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar novaData = Calendar.getInstance();
                    novaData.set(year, monthOfYear, dayOfMonth);
                    Recursos.setDataAtual(novaData.getTime());
                    menuData.setTitle(Recursos.dataAtualFormatoMesAno());
                    atualizarContas();
                }

            },dataAtual.get(Calendar.YEAR), dataAtual.get(Calendar.MONTH), dataAtual.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK)
                atualizarContas();
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //Se for negativo quer dizer que é um totalizador, logo, não mostra o menu
        if (posicaoItemSelecionado >= 0) {
            menu.add(0, v.getId(), 0, R.string.text_deletar);

            if ((abaSelecionada.equals(ABA_RESUMO_NOME) && resumoSelecionado.isPrevisao()) ||
                (abaSelecionada.equals(ABA_CARTEIRA_NOME) && carteiraSelecionado.isPrevisao()))
                menu.add(0, v.getId(), 0, R.string.text_efetivar);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()==getResources().getString(R.string.text_deletar)){
            if (abaSelecionada.equals(ABA_RESUMO_NOME)){
                Recursos.confirmar(this, getResources().getString(R.string.msg_confirmar_exclusao), new Runnable() {
                    @Override
                    public void run() {
                        resumoDAO.deletar(resumoSelecionado);
                        atualizarContas();
                        Toast.makeText(getApplicationContext(), R.string.msg_lancamento_excluido, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (abaSelecionada.equals(ABA_CARTEIRA_NOME)) {
                Recursos.confirmar(this, getResources().getString(R.string.msg_confirmar_exclusao), new Runnable() {
                    @Override
                    public void run() {
                        carteiraDAO.deletar(carteiraSelecionado);
                        atualizarContas();
                        Toast.makeText(getApplicationContext(), R.string.msg_lancamento_excluido, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (abaSelecionada.equals(ABA_CARTAO_NOME)){
                Recursos.confirmar(this, getResources().getString(R.string.msg_confirmar_exclusao), new Runnable() {
                    @Override
                    public void run() {
                        cartaoDAO.deletar(cartaoSelecionado);
                        atualizarContas();
                        Toast.makeText(getApplicationContext(), R.string.msg_lancamento_excluido, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        if(item.getTitle()==getResources().getString(R.string.text_efetivar)){
            if (abaSelecionada.equals(ABA_RESUMO_NOME)){
                Recursos.confirmar(this, getResources().getString(R.string.msg_confirmar_efetivar), new Runnable() {
                    @Override
                    public void run() {
                        resumoSelecionado.setPrevisao(false);
                        resumoDAO.atualizar(resumoSelecionado);
                        atualizarContas();
                        Toast.makeText(getApplicationContext(), R.string.msg_lancamento_efetivado, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (abaSelecionada.equals(ABA_CARTEIRA_NOME)) {
                Recursos.confirmar(this, getResources().getString(R.string.msg_confirmar_efetivar), new Runnable() {
                    @Override
                    public void run() {
                        carteiraSelecionado.setPrevisao(false);
                        carteiraDAO.atualizar(carteiraSelecionado);
                        atualizarContas();
                        Toast.makeText(getApplicationContext(), R.string.msg_lancamento_efetivado, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        return true;
    }

    private void atualizarContas() {
        carregarContasSelecionadas();
        configurarListView();
        preencherTotal();
    }

    private void carregarContasSelecionadas() {
        resumos = resumoDAO.obterTodosNaDataAtual();
        totalResumo = resumoDAO.obterTotalResumo();
        totalResumoPrevisto = resumoDAO.obterTotalResumoPrevisto();
        carteiras = carteiraDAO.obterTodosNaDataAtual();
        totalCarteira = carteiraDAO.obterTotalCarteira();
        totalCarteiraPrevisto = carteiraDAO.obterTotalCarteiraPrevisto();
        totalCarteiraAnterior = carteiraDAO.obterTotalCarteiraAnterior();
        totalCarteiraPrevistoAnterior = carteiraDAO.obterTotalCarteiraPrevistoAnterior();
        cartaos = cartaoDAO.obterTodosNaDataAtual();
        totalCartao = cartaoDAO.obterTotalCartao();

        Resumo resumoCarteira = new Resumo();
        resumoCarteira.setId(-1);
        resumoCarteira.setDescricao(this.getResources().getString(R.string.text_carteira));
        resumoCarteira.setValor(totalCarteira);
        resumoCarteira.setData(Recursos.dataAtualString());
        resumos.add(0, resumoCarteira);

        Resumo resumoCartao = new Resumo();
        resumoCartao.setId(-2);
        resumoCartao.setDescricao(this.getResources().getString(R.string.text_cartao));
        resumoCartao.setValor(totalCartao * -1);
        resumoCartao.setData(Recursos.dataAtualString());
        resumos.add(1, resumoCartao);

        Carteira saldoCarteiraAnterior = new Carteira();
        saldoCarteiraAnterior.setId(-1);
        saldoCarteiraAnterior.setDescricao(this.getResources().getString(R.string.text_saldo_anterior));
        saldoCarteiraAnterior.setValor(totalCarteiraAnterior);
        saldoCarteiraAnterior.setData(Recursos.dataAtualString());
        carteiras.add(0, saldoCarteiraAnterior);
    }

    private void configurarActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_title);
        actionBar.setIcon(R.mipmap.ic_launcher);
    }

    private void configurarTabs() {
        abaSelecionada = ABA_RESUMO_NOME;
        LayoutInflater inflater = this.getLayoutInflater();
        TabHost abas = (TabHost) findViewById(android.R.id.tabhost);
        abas.setup();
        TabHost.TabSpec aba;

        // Aba Resumo
        View abaResumo = inflater.inflate(R.layout.view_aba, null);
        TextView tituloResumno = (TextView)abaResumo.findViewById(R.id.view_aba_titulo);
        tituloResumno.setText(getString(R.string.list_view_resumo));

        aba = abas.newTabSpec(ABA_RESUMO_NOME);
        aba.setContent(R.id.list_view_resumo);
        aba.setIndicator(abaResumo);
        abas.addTab(aba);

        // Aba Carteira
        View abaCarteira = inflater.inflate(R.layout.view_aba, null);
        TextView tituloCarteira = (TextView)abaCarteira.findViewById(R.id.view_aba_titulo);
        tituloCarteira.setText(getString(R.string.list_view_carteira));

        aba = abas.newTabSpec(ABA_CARTEIRA_NOME);
        aba.setContent(R.id.list_view_carteira);
        aba.setIndicator(abaCarteira);
        abas.addTab(aba);

        // Aba Cartao
        View abaCartao = inflater.inflate(R.layout.view_aba, null);
        TextView tituloCartao = (TextView)abaCartao.findViewById(R.id.view_aba_titulo);
        tituloCartao.setText(getString(R.string.list_view_cartao));

        aba = abas.newTabSpec(ABA_CARTAO_NOME);
        aba.setContent(R.id.list_view_cartao);
        aba.setIndicator(abaCartao);
        abas.addTab(aba);

        abas.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                abaSelecionada = tabId;
                preencherTotal();
            }
        });
    }

    private void configurarListView() {
        this.listViewResumo = (ListView) findViewById(R.id.list_view_resumo);
        this.listViewResumo.setAdapter(
            new ResumoListViewAdapter(this.context, this.resumos));

        registerForContextMenu(this.listViewResumo);
        this.listViewCarteira = (ListView) findViewById(R.id.list_view_carteira);
        this.listViewCarteira.setAdapter(
            new CarteiraListViewAdapter(this.context, this.carteiras));

        this.listViewCartao = (ListView) findViewById(R.id.list_view_cartao);
        this.listViewCartao.setAdapter(
            new CartaoListViewAdapter(this.context, this.cartaos));

        this.listViewResumo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter adapter = ContasActivity.this.listViewResumo.getAdapter();
                Resumo resumo = (Resumo) adapter.getItem(position);

                if (resumo.getId() > 0) {
                    Intent intent = new Intent(ContasActivity.this, ResumoActivity.class);
                    intent.putExtra(ResumoActivity.PARAMETRO_RESUMO_ID, resumo.getId());
                    startActivityForResult(intent, 1);
                }
                else{
                    TabHost abas = (TabHost) findViewById(android.R.id.tabhost);
                    abas.setCurrentTab(Integer.parseInt(String.valueOf(resumo.getId()*-1)));
                }
            }
        });

        this.listViewCarteira.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter adapter = ContasActivity.this.listViewCarteira.getAdapter();
                Carteira carteira = (Carteira) adapter.getItem(position);

                if (carteira.getId() > 0) {
                    Intent intent = new Intent(ContasActivity.this, CarteiraActivity.class);
                    intent.putExtra(CarteiraActivity.PARAMETRO_CARTEIRA_ID, carteira.getId());
                    startActivityForResult(intent, 1);
                }
            }
        });

        this.listViewCartao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter adapter = ContasActivity.this.listViewCartao.getAdapter();
                Cartao cartao = (Cartao) adapter.getItem(position);

                Intent intent = new Intent(ContasActivity.this, CartaoActivity.class);
                intent.putExtra(CartaoActivity.PARAMETRO_CARTAO_ID, cartao.getId());
                startActivityForResult(intent, 1);
            }
        });

        this.listViewResumo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
               //negativo não mostra o context menu
               if (position < 2) {
                   resumoSelecionado = null;
                   posicaoItemSelecionado = -1;
               }
               else{
                   resumoSelecionado = (Resumo) ContasActivity.this.listViewResumo.getAdapter().getItem(position);
                   posicaoItemSelecionado = position;
               }
               return false;
           }
        });

        this.listViewCarteira.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                //negativo não mostra o context menu
                if (position < 1) {
                    carteiraSelecionado = null;
                    posicaoItemSelecionado = -1;
                }
                else {
                    carteiraSelecionado = (Carteira) ContasActivity.this.listViewCarteira.getAdapter().getItem(position);
                    posicaoItemSelecionado = position;
                }
                return false;
            }
        });

        this.listViewCartao.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                cartaoSelecionado = (Cartao) ContasActivity.this.listViewCartao.getAdapter().getItem(position);
                posicaoItemSelecionado = position;
                return false;
            }
        });

        registerForContextMenu(this.listViewResumo);
        registerForContextMenu(this.listViewCarteira);
        registerForContextMenu(this.listViewCartao);
    }

    public void configurarBotoes(){
        FloatingActionButton botaoAdicionar = (FloatingActionButton) findViewById(R.id.botao_adicionar);
        botaoAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = null;

            if (abaSelecionada.equals(ABA_RESUMO_NOME))
                intent = new Intent(v.getContext(), ResumoActivity.class);

            if (abaSelecionada.equals(ABA_CARTEIRA_NOME))
                intent = new Intent(v.getContext(), CarteiraActivity.class);

            if (abaSelecionada.equals(ABA_CARTAO_NOME))
                intent = new Intent(v.getContext(), CartaoActivity.class);

            if (intent != null)
              startActivityForResult(intent, 1);
            }
        });
    }

    public void preencherTotal() {
        String textMoeda = getResources().getString(R.string.text_moeda_para_formatacao);
        String textoTotal = getResources().getString(R.string.text_total);
        String textoTotalPrevisto = getResources().getString(R.string.text_total_previsao);

        double total = 0;
        double previsto = 0;

        if (configuracoes.getModoVisualizacao() <=3) {
            totalCarteira += totalCarteiraAnterior;
            totalCarteiraPrevisto += totalCarteiraPrevistoAnterior;
        }

        totalResumo += totalCarteira - totalCartao;
        totalResumoPrevisto += totalCarteiraPrevisto - totalCartao;

        if (abaSelecionada.equals(ABA_RESUMO_NOME)) {
            total = totalResumo;
            previsto = totalResumoPrevisto;
        }

        if (abaSelecionada.equals(ABA_CARTEIRA_NOME)) {
            total = totalCarteira;
            previsto = totalCarteiraPrevisto;
        }

        if (abaSelecionada.equals(ABA_CARTAO_NOME)){
            total = totalCartao;
            previsto = totalCartao;
        }

        textTotal.setText(textoTotal + " " + String.format(textMoeda, total));
        textTotal.setTypeface(null, Typeface.BOLD);

        textTotalPrevisto.setText(textoTotalPrevisto + " " + String.format(textMoeda, previsto));
        textTotalPrevisto.setTypeface(null, Typeface.BOLD);

        textTotal.setVisibility(View.VISIBLE);
        textTotalPrevisto.setVisibility(View.VISIBLE);

        //if (total == previsto)
        //    textTotalPrevisto.setVisibility(View.GONE);

        textTotal.setTextColor(total < 0 ? getResources().getColor(R.color.theme_red_primary) : getResources().getColor(R.color.white));
        textTotalPrevisto.setTextColor(previsto < 0 ? getResources().getColor(R.color.theme_red_primary) : getResources().getColor(R.color.white));

        switch (configuracoes.getModoVisualizacao()){
            case 2:
                buttonTotalizadores.setImageResource(R.drawable.ic_total_2);
                textTotalPrevisto.setVisibility(View.GONE);
                break;
            case 3:
                buttonTotalizadores.setImageResource(R.drawable.ic_total_3);
                textTotal.setVisibility(View.GONE);
                break;
            case 4:
                buttonTotalizadores.setImageResource(R.drawable.ic_total_4);
                break;
            case 5:
                buttonTotalizadores.setImageResource(R.drawable.ic_total_5);
                textTotalPrevisto.setVisibility(View.GONE);
                break;
            case 6:
                buttonTotalizadores.setImageResource(R.drawable.ic_total_6);
                textTotal.setVisibility(View.GONE);
                break;
            default:buttonTotalizadores.setImageResource(R.drawable.ic_total_1);
        }
    }
}
