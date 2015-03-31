package br.com.backapp.finfacil.resources;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.widget.SpinnerAdapter;

import br.com.backapp.finfacil.R;

/**
 * Created by raphael on 20/02/2015.
 */
public class Recursos {
    private static Date dataAtual;

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
        dialog.setTitle(R.string.text_confirmacao);
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
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();

        return true;
    }

    public static SpinnerAdapter adapterTextoRepetirLancamento(Context context){
        String[] textoRepetir = {"Nunca", /*"Sempre",*/ "1 vez", "2 vezes", "3 vezes", "4 vezes", "5 vezes", "6 vezes", "7 vezes",
                                 "8 vezes", "9 vezes", "10 vezes", "11 vezes", "12 vezes"};

        SpinnerAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, textoRepetir);

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

    public static String primeiraDataMesSeguinte(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDataAtual());
        calendar.set(calendar.MONTH, calendar.get(calendar.MONTH)+ 1);
        calendar.set(Calendar.DAY_OF_MONTH,  calendar.getActualMinimum(calendar.DATE));
        return Recursos.converterDataParaStringFormatoCurtoBD(calendar.getTime());
    }

    public static String[] whereBetweenDataAtual(){
        return new String[] {primeiraDataMesAtual(), primeiraDataMesSeguinte()};
    }
}
