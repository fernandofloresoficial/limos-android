package br.com.limosapp.limos.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Fabio on 28/07/2018.
 */

public class VerificaDiaSemana {

    public String verifDiaSemana(String data){
        String diadasemana = "";
        String[] sourceSplit= data.split("/");
        int a = Integer.parseInt(sourceSplit[2]);
        int m = Integer.parseInt(sourceSplit[1]);
        int d = Integer.parseInt(sourceSplit[0]);

        GregorianCalendar c = new GregorianCalendar();
        c.set(a,m-1,d);
        int dia = c.get(c.DAY_OF_WEEK);
        switch(dia){
            case Calendar.SUNDAY: diadasemana = "Domingo";break;
            case Calendar.MONDAY: diadasemana = "Segunda";break;
            case Calendar.TUESDAY: diadasemana = "Terça";break;
            case Calendar.WEDNESDAY: diadasemana = "Quarta";break;
            case Calendar.THURSDAY: diadasemana = "Quinta";break;
            case Calendar.FRIDAY: diadasemana = "Sexta";break;
            case Calendar.SATURDAY: diadasemana = "Sábado";break;
        }
        return diadasemana;
    }
}
