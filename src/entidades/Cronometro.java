package entidades;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Cronometro {

    private Timer cronometro;
    private DateFormat formato = new SimpleDateFormat("mm:ss");//HH:mm:ss
    private Calendar calendario = Calendar.getInstance();
    private final byte contagem;
    public static final byte PROGRESSIVA = 1;
    public static final byte REGRESSIVA = -1;

    public String currentTempo = "";

    public Cronometro(int minutos, byte tipoContagem) {
        int ano = 0, mes = 0, dia = 0, horas = 0, segundos = 0;
        this.cronometro = new Timer();
        calendario.set(ano, mes, dia, horas, minutos, segundos);
        this.contagem = tipoContagem;
    }

    public void cronometro() {
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                currentTempo = getTime();
            }
        };
        cronometro.scheduleAtFixedRate(tarefa, 0, 1000);
    }

    public void stopCronometro() {
        try {
            cronometro.cancel();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public String getTime() {
        calendario.add(Calendar.SECOND, contagem);
        String vr = formato.format(calendario.getTime());
        if (vr.equals("00:00")) {
            cronometro.cancel();
            return "fim";
        }
        return vr;
    }
}
