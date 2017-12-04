package util;

import entidades.GameRuntime;
import entidades.network.Cliente;
import entidades.network.DataNetworkManager;
import entidades.network.Servidor;
import java.io.File;
import security.Security;
import ui.GameScreenConfigGame;
import ui.JFramePrincipal;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Session {

    public static boolean DEBUG = true;

    public static Cliente conexaoCliente = new Cliente();
    public static Servidor conexaoServidor = new Servidor();
    public static GameRuntime gRunTime = new GameRuntime();

    public static String nickname = "Server";
    //public static String masterIP = "127.0.0.1";
    public static String masterIP = "localhost";
    public static boolean isServidor = true;

    public static boolean canStartGame = false;
    public static boolean canValidateRespostas = false;
    public static boolean canShowHighScores = false;
    public static boolean canShowMainMenuByConnectionError = false;
    public final static boolean canPrint = true;

    public static String log = "";
    public static Security security = new Security();

    public static void addLog(String s) {
        String a = ((isServidor == true) ? "SERVER >> " : "CLIENT >> ") + s;
        if (canPrint) {
            System.out.println(a);
        }
        log += a + "\n\r";
    }

    public static String getLog() {
        return log;
    }

    public static void clearAllData() {
        masterIP = "127.0.0.1";
        nickname = "Server";
        isServidor = true;

        conexaoCliente.closeAndCleanAllData();
        conexaoServidor.closeAndCleanAllData();

        Session.gRunTime.cleanAllData();
        DataNetworkManager.cleanAllData();

        conexaoCliente = new Cliente();
        conexaoServidor = new Servidor();

        GameScreenConfigGame.certificadoSelecionado = false;
        Security.certificado = null;
        Security.certificadoSenha = null;
    }

    public static JFramePrincipal JFramePrincipal;
}
