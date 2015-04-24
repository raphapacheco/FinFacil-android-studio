package br.com.backapp.finfacil.resources;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputFilter;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.widget.SpinnerAdapter;

import br.com.backapp.finfacil.R;
import br.com.backapp.finfacil.activity.spinner_adapter.CategoriaSpinnerAdapter;
import br.com.backapp.finfacil.data_access_object.CategoriaDAO;
import br.com.backapp.finfacil.model.Categoria;

/**
 * Created by raphael on 20/02/2015.
 */
public class Recursos {
    private static Date dataAtual;
    private static ArrayList<String> listaCategorias;
    private static ArrayList<Categoria> categorias;

    public static Date converterStringParaData(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static String converterDataParaStringFormatoCurto(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String convertedDate = dateFormat.format(date);
        return convertedDate;
    }

    public static String converterDataParaStringDiaMes(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        String convertedDate = dateFormat.format(date);
        return convertedDate;
    }

    public static String dataAtualFormatoMesAno(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/yyyy");
        String convertedDate = dateFormat.format(getDataAtual());
        return convertedDate;
    }

    public static String dataAtualString(){
        return converterDataParaStringBD(getDataAtual());
    }

    public static String converterDataParaStringFormatoCurtoBD(Date date){
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String convertedDate = dateFormat.format(date);
        return convertedDate;
    }

    public static String converterDataParaStringBD(Date date){
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String convertedDate = dateFormat.format(date);
        return convertedDate;
    }

    public static void configurarMascaraCasasDecimais(EditText editText){
        editText.setFilters(new InputFilter[] {new MascaraCasasDecimaisInputFilter(10,2)});
    }

    public static String converterDoubleParaString(Double valor){
        return String.format(Locale.ENGLISH, "%.2f", valor);
    }

    public static boolean obrigatorio(Context context, EditText editText){
        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.requestFocus();
            editText.setError(context.getResources().getString(R.string.text_obrigatorio));
            return false;
        }

        return true;
    }

    public static boolean confirmar(Context context, final String mensagem, final Runnable metodo) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        //dialog.setIcon(android.R.drawable.ic_dialog_alert);
        //dialog.setTitle(R.string.text_confirmacao);
        dialog.setMessage(mensagem);
        dialog.setCancelable(false);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.text_sim), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                metodo.run();
                dialog.dismiss();
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.text_nao), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                dialog.dismiss();
            }
        });
        dialog.show();

        return true;
    }

    public static SpinnerAdapter adapterTextoRepetirLancamento(Context context){
        String[] textoRepetir = {"1 vez", "2 vezes", "3 vezes", "4 vezes", "5 vezes", "6 vezes", "7 vezes",
                                 "8 vezes", "9 vezes", "10 vezes", "11 vezes", "12 vezes"};

        SpinnerAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, textoRepetir);

        return  adapter;
    }

    public static SpinnerAdapter adapterTextoCategoria(Context context, SQLiteDatabase database){
        if (listaCategorias == null) {
            listaCategorias = new ArrayList<String>();
            CategoriaDAO categoriaDAO = new CategoriaDAO(database);
            ArrayList<Categoria> categorias = categoriaDAO.obterTodos();

            listaCategorias.add(context.getResources().getString(R.string.text_sem_categoria));
            for (int i = 0; i < categorias.size(); i++) {
                listaCategorias.add(categorias.get(i).getDescricao());
            }
        }
        SpinnerAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listaCategorias);

        return  adapter;
    }

    public static Date getDataAtual() {
        if (dataAtual == null)
            dataAtual = new Date();

        return dataAtual;
    }

    public static void setDataAtual(Date data) {
        Recursos.dataAtual = data;
    }

    public static String primeiraDataMesAtual(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDataAtual());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(calendar.DATE));
        return Recursos.converterDataParaStringFormatoCurtoBD(calendar.getTime());
    }

    public static String primeiraDataAnterior(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDataAtual());
        calendar.set(calendar.MONTH, calendar.get(calendar.MONTH)- 1);
        calendar.set(Calendar.DAY_OF_MONTH,  calendar.getActualMinimum(calendar.DATE));
        return Recursos.converterDataParaStringFormatoCurtoBD(calendar.getTime());
    }

    public static String primeiraDataMesSeguinte(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDataAtual());
        calendar.set(calendar.MONTH, calendar.get(calendar.MONTH)+ 1);
        calendar.set(Calendar.DAY_OF_MONTH,  calendar.getActualMinimum(calendar.DATE));
        return Recursos.converterDataParaStringFormatoCurtoBD(calendar.getTime());
    }

    public static String[] whereBetweenMesAtual(){
        return new String[] {primeiraDataMesAtual(), primeiraDataMesSeguinte()};
    }

    public static String[] whereMesAtual(){
        return new String[] {primeiraDataMesAtual()};
    }

    public static CategoriaSpinnerAdapter adapterCategoria(Context context, SQLiteDatabase database){
        if (categorias == null) {
            CategoriaDAO categoriaDAO = new CategoriaDAO(database);
            categorias = categoriaDAO.obterTodos();

            Categoria categoria = new Categoria();
            categoria.setId(-1);
            categoria.setDescricao(context.getResources().getString(R.string.text_sem_categoria));
            categorias.add(0, categoria);
        }

        CategoriaSpinnerAdapter categoriaSpinnerAdapter = new CategoriaSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, categorias);

        return  categoriaSpinnerAdapter;
    }
}
