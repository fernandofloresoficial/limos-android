package br.com.limosapp.limos.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Fabio on 28/07/2018.
 */

public class VerificaFechado {

    private SimpleDateFormat formatador = new SimpleDateFormat("HH:mm", Locale.US);

    public boolean verificaEstaFechado(String horaAbre , String horaFecha) {
        String horaAtual = formatador.format(new Date().getTime());// Pega hora atual do Sistema

        Integer horarioAbre[]  = getHorAndMinute(horaAbre);
        Integer horarioFecha[] = getHorAndMinute(horaFecha);

        if( horarioFecha[0] < horarioAbre[0] ) {
            Integer horarioAtual[] = getHorAndMinute(horaAtual);

            if(horarioAtual[0] <= horarioFecha[0]) {
                return horarioAtual[1] >= horarioFecha[1] && horarioAtual[0] >= horarioFecha[0];
            }  else {
                horaFecha = "23:59";
            }
        }

        try {
            Boolean abrecom = ComparaHoraAbre(formatador.parse(horaAtual).getTime(), formatador.parse(horaAbre).getTime());
            Boolean fechacom = ComparaHorafecha(formatador.parse(horaAtual).getTime(), formatador.parse(horaFecha).getTime());

            return abrecom || fechacom;
        } catch (ParseException ex) {
            //System.out.println("Horário Inválido...");
            return false;
        }

    }

    private Integer[] getHorAndMinute(String hora) {
        int horario, minutos;
        horario = Integer.parseInt(hora.substring(0, 2));
        minutos = Integer.parseInt(hora.substring(3, 5));
        return new Integer[]{horario, minutos};
    }

    private boolean ComparaHoraAbre(Long horaAtual, Long horaAbre) {
        return horaAtual < horaAbre;
    }

    private  boolean ComparaHorafecha(Long horaAtual, Long horaFecha) {
        return horaAtual > horaFecha;
    }
}