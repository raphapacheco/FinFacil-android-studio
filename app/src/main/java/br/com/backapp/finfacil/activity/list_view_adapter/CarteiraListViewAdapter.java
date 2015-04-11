package br.com.backapp.finfacil.activity.list_view_adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.model.Carteira;
import br.com.backapp.finfacil.resources.Recursos;

/**
 * Created by raphael on 21/02/2015.
 */
public class CarteiraListViewAdapter extends BaseAdapter {
    private ArrayList<Carteira> carteiras;
    private LayoutInflater inflater;
    private Context context;

    private class ViewHolder {
        TextView textViewLineDescricao;
        TextView textViewLineValor;
        TextView textViewLineData;

        public ViewHolder(View view){
            this.textViewLineDescricao = (TextView) view.findViewById(R.id.list_view_carteira_descricao);
            this.textViewLineValor = (TextView) view.findViewById(R.id.list_view_carteira_valor);
            this.textViewLineData = (TextView) view.findViewById(R.id.list_view_carteira_data);
        }
    }

    public CarteiraListViewAdapter(Context context, ArrayList<Carteira> carteiras) {
        this.carteiras = carteiras;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return carteiras.size();
    }

    @Override
    public Carteira getItem(int position) {
        return carteiras.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_view_carteira, null);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.textViewLineDescricao.setText((carteiras.get(position).isPrevisao() ? "(P)  " : "") + carteiras.get(position).getDescricao());

        String textMoeda = context.getResources().getString(R.string.text_moeda_para_formatacao);
        mViewHolder.textViewLineValor.setText(String.format(textMoeda, carteiras.get(position).getValor()));

        mViewHolder.textViewLineDescricao.setTypeface(null, Typeface.NORMAL);
        mViewHolder.textViewLineValor.setTypeface(null, Typeface.NORMAL);
        mViewHolder.textViewLineData.setVisibility(View.VISIBLE);

        //Id < 0 quer dizer que é um totalizador
        if (carteiras.get(position).getId() < 0) {
            mViewHolder.textViewLineDescricao.setTypeface(null, Typeface.BOLD);
            mViewHolder.textViewLineValor.setTypeface(null, Typeface.BOLD);
            mViewHolder.textViewLineData.setVisibility(View.INVISIBLE);
        }

        if (carteiras.get(position).isPrevisao())
            mViewHolder.textViewLineDescricao.setTextColor(context.getResources().getColor(R.color.text_previsao));

        //Positivo verde
        if (carteiras.get(position).getValor() >= 0)
            mViewHolder.textViewLineValor.setTextColor(context.getResources().getColor(R.color.text_green));
        else //Negativo vermelho
            mViewHolder.textViewLineValor.setTextColor(context.getResources().getColor(R.color.text_red));

        String dia = Recursos.converterDataParaStringDiaMes(Recursos.converterStringParaData(carteiras.get(position).getData()));
        mViewHolder.textViewLineData.setText(dia);

        return convertView;
    }
}
