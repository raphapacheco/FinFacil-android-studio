package br.com.backapp.finfacil.activity.list_view_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.model.Cartao;
import br.com.backapp.finfacil.resources.Recursos;

/**
 * Created by raphael on 21/02/2015.
 */
public class CartaoListViewAdapter extends BaseAdapter{
    private ArrayList<Cartao> cartaos;
    private LayoutInflater inflater;
    private Context context;

    private class ViewHolder {
        TextView textViewLineDescricao;
        TextView textViewLineValor;
        TextView textViewLineData;

        public ViewHolder(View view){
            this.textViewLineDescricao = (TextView) view.findViewById(R.id.list_view_cartao_descricao);
            this.textViewLineValor = (TextView) view.findViewById(R.id.list_view_cartao_valor);
            this.textViewLineData = (TextView) view.findViewById(R.id.list_view_cartao_data);
        }
    }

    public CartaoListViewAdapter(Context context, ArrayList<Cartao> cartaos) {
        this.cartaos = cartaos;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return cartaos.size();
    }

    @Override
    public Cartao getItem(int position) {
        return cartaos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_view_cartao, null);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.textViewLineDescricao.setText(cartaos.get(position).getDescricao());

        String textMoeda = context.getResources().getString(R.string.text_moeda_para_formatacao);
        mViewHolder.textViewLineValor.setText(String.format(textMoeda, cartaos.get(position).getValor()));

        //Positivo verde
        if (cartaos.get(position).getValor() >= 0)
            mViewHolder.textViewLineValor.setTextColor(context.getResources().getColor(R.color.text_green));
        else //Negativo vermelho
            mViewHolder.textViewLineValor.setTextColor(context.getResources().getColor(R.color.text_red));

        String dia = Recursos.converterDataParaStringDiaMes(Recursos.converterStringParaData(cartaos.get(position).getData()));
        mViewHolder.textViewLineData.setText(dia);

        return convertView;
    }
}
