package br.com.backapp.finfacil.resources;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by raphael on 13/04/2015.
 */
public class Configuracoes {


    public enum ModoVisualizacaoTotalizador { mvtTotal };

    private SharedPreferences config;
    private SharedPreferences.Editor editor;
    private String DIA_FECHAMENTO_CONFIG = "DIA_FECHAMENTO";
    private String MODO_VISUALIZACAO_TOTALIZADOR_CONFIG = "DIA_FECHAMENTO";

    private Integer diaFechamento = 1;
    private Integer modoVisualizacao = 1;

    public Configuracoes(Context context){
        this.config = context.getSharedPreferences("FinFacilConfig", Context.MODE_PRIVATE);
        this.editor = config.edit();

        if (config.contains(DIA_FECHAMENTO_CONFIG))
            this.setDiaFechamento(config.getInt(DIA_FECHAMENTO_CONFIG, 1));

        if (config.contains(MODO_VISUALIZACAO_TOTALIZADOR_CONFIG))
            this.setModoVisualizacao(config.getInt(MODO_VISUALIZACAO_TOTALIZADOR_CONFIG, 1));
    }

    public void salvar(){
        editor.putInt(DIA_FECHAMENTO_CONFIG, diaFechamento);
        editor.putInt(MODO_VISUALIZACAO_TOTALIZADOR_CONFIG, modoVisualizacao);

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

        if (modoVisualizacao > 6)
            this.modoVisualizacao = 1;
    }

}
