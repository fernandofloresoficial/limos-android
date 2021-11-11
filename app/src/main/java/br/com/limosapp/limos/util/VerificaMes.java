package br.com.limosapp.limos.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Fabio on 28/07/2018.
 */

public class VerificaMes {

    public String verifMes(String data){
        String mesdata = "";
        String[] sourceSplit= data.split("/");
        int a = Integer.parseInt(sourceSplit[2]);
        int m = Integer.parseInt(sourceSplit[1]);
        int d = Integer.parseInt(sourceSplit[0]);

        GregorianCalendar c = new GregorianCalendar();
        c.set(a,m-1,d);
        int mes = c.get(c.MONTH);
        switch(mes){
            case Calendar.JANUARY: mesdata = "JAN";break;
            case Calendar.FEBRUARY: mesdata = "FEV";break;
            case Calendar.MARCH: mesdata = "MAR";break;
            case Calendar.APRIL: mesdata = "ABR";break;
            case Calendar.MAY: mesdata = "MAI";break;
            case Calendar.JUNE: mesdata = "JUN";break;
            case Calendar.JULY: mesdata = "JUL";break;
            case Calendar.AUGUST: mesdata = "AGO";break;
            case Calendar.SEPTEMBER: mesdata = "SET";break;
            case Calendar.OCTOBER: mesdata = "OUT";break;
            case Calendar.NOVEMBER: mesdata = "NOV";break;
            case Calendar.DECEMBER: mesdata = "DEZ";break;
        }
        return mesdata;
    }
}
