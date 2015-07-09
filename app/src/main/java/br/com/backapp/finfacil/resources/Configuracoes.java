package br.com.backapp.finfacil.resources;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by raphael on 13/04/2015.
 */
public class Configuracoes {

    private SharedPreferences config;
    private SharedPreferences.Editor editor;
    private String DIA_FECHAMENTO_CONFIG = "DIA_FECHAMENTO";
    private String MODO_VISUALIZACAO_TOTALIZADOR_CONFIG = "DIA_FECHAMENTO";
    private String ORDENACAO_LANCAMENTOS = "ORDENACAO_LANCAMENTOS";

    private Integer diaFechamento = 0;
    private Integer modoVisualizacao = 0;
    private Integer ordenacaoLancamentos = 0;

    public Configuracoes(Context context){
        this.config = context.getSharedPreferences("FinFacilConfig", Context.MODE_PRIVATE);
        this.editor = config.edit();

        if (config.contains(DIA_FECHAMENTO_CONFIG))
            this.setDiaFechamento(config.getInt(DIA_FECHAMENTO_CONFIG, 1));

        if (config.contains(MODO_VISUALIZACAO_TOTALIZADOR_CONFIG))
            this.setModoVisualizacao(config.getInt(MODO_VISUALIZACAO_TOTALIZADOR_CONFIG, 1));

        if (config.contains(ORDENACAO_LANCAMENTOS))
            this.setOrdenacaoLancamentos(config.getInt(ORDENACAO_LANCAMENTOS, 1));
    }

    public void salvar(){
        editor.putInt(DIA_FECHAMENTO_CONFIG, diaFechamento);
        editor.putInt(MODO_VISUALIZACAO_TOTALIZADOR_CONFIG, modoVisualizacao);
        editor.putInt(ORDENACAO_LANCAMENTOS, ordenacaoLancamentos);

        editor.commit(); // commit changes
    }

    public Integer getDiaFechamento() {
        return diaFechamento;
    }

    public void setDiaFechamento(Integer diaFechamento) {
        this.diaFechamento = diaFechamento;
    }

    public Integer getModoVisualizacao() {
        return modoVisualizacao;
    }

    public void setModoVisualizacao(Integer modoVisualizacao) {
        this.modoVisualizacao = modoVisualizacao;
    }

    public Integer getOrdenacaoLancamentos() {
        return ordenacaoLancamentos;
    }

    public void setOrdenacaoLancamentos(Integer ordenacaoLancamentos) {
        this.ordenacaoLancamentos = ordenacaoLancamentos;
    }
}
