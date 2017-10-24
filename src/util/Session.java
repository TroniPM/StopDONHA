package util;

import entidades.GameRuntime;
import entidades.network.Cliente;
import entidades.network.DataNetworkManager;
import entidades.network.Servidor;
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

    public static String masterIP = "127.0.0.1";
    public static boolean isServidor = true;
    public static String nickname = "OwnerServer";

    public static boolean canStartGame = false;
    public static boolean canValidateRespostas = false;
    public static boolean canShowHighScores = false;
    public static boolean canShowMainMenuByConnectionError = false;
    
    public String log = "";

    public static void clearAllData() {
        masterIP = "127.0.0.1";
        isServidor = true;

        conexaoCliente.closeAndCleanAllData();
        conexaoServidor.closeAndCleanAllData();

        Session.gRunTime.cleanAllData();
        DataNetworkManager.cleanAllData();

        conexaoCliente = new Cliente();
        conexaoServidor = new Servidor();
    }

    public static JFramePrincipal JFramePrincipal;
}
