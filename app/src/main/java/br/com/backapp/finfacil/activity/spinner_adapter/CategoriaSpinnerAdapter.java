package br.com.backapp.finfacil.activity.spinner_adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.backapp.finfacil.model.Categoria;

/**
 * Created by raphael on 08/04/2015.
 */
public class CategoriaSpinnerAdapter extends ArrayAdapter<Categoria> {

    private Context context;
    private ArrayList<Categoria> categorias;

    public CategoriaSpinnerAdapter(Context context, int textViewResourceId, ArrayList<Categoria> categorias) {
        super(context, textViewResourceId, categorias);
        this.context = context;
        this.categorias = categorias;
    }

    public int getCount(){
        return categorias.size();
    }

    public Categoria getItem(int position){
        return categorias.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public int getPositioById(long id){
        int i;
        for (i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == id)
                return i;
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextSize(18);
        label.setPadding(10, 10, 10, 10);
        label.setText(categorias.get(position).getDescricao());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextSize(18);
        label.setPadding(10, 10, 10, 10);
        label.setText(categorias.get(position).getDescricao());
        return label;
    }
}
