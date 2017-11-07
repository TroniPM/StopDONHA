package entidades.network;

import entidades.GameRuntime;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static entidades.network.Servidor.PORT_CLIENT;
import static entidades.network.Servidor.PORT_SERVER;
import entidades.network.sendible.EndRound;
import entidades.network.sendible.EndRoundArray;
import entidades.network.sendible.User;
import entidades.network.sendible.UserArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Cliente {

    public Socket socket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    private boolean isConnected = false;

    public Cliente() {
    }

    public void closeAndCleanAllData() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        socket = null;
        isConnected = false;
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void communicateWaitingRoom() throws IOException {
        String a = "communicateWaitingRoom: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        Socket socket = new Socket(Session.masterIP, PORT_SERVER);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        User user = new User();
        user.nickname = Session.nickname;

        out.println(Session.security.brincar(user.convertToString()));

        a = "Enviado User [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
    }

    public void sv_communicateStartGame(String ip, GameRuntime obj) {
        try {
            String a = "sv_communicateStartGame: Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            out.println(Session.security.brincar(obj.convertToString()));

            a = "Enviado GameRuntime [OBJECT] para" + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void communicateEndRoundToValidate(EndRound obj) {
        String a = "communicateEndRoundToValidate: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
        try {
            Socket socket = new Socket(Session.masterIP, PORT_SERVER);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            out.println(Session.security.brincar(obj.convertToString()));

            a = "Enviado EndRound [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sv_communicateStartValidation(String ip, List<EndRound> obj) {
        try {
            String a = "sv_communicateStartValidation: Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            String objMain = new EndRoundArray(obj).convertToStringArray();
            out.println(Session.security.brincar(objMain));
            a = "Enviado ArrayList<EndRound> [OBJECT] para " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            //Session.addLog(objMain);
            System.out.println("<><><><><><><><><><><><><><><EndRound1> " + objMain);
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void communicateDataValidated(List<User> obj) {
        String a = "communicateDataValidated: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
        try {
            Socket socket = new Socket(Session.masterIP, PORT_SERVER);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            String objMain = new UserArray(obj).convertToStringArray();
            out.println(Session.security.brincar(objMain));
            a = "Enviado ArrayList<User> [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            System.out.println("<><><><><><><><><><><><><><><User2> " + objMain);
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sv_communicateScores(String ip, List<User> obj) {
        try {
            String a = "sv_communicateScores: Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            String objMain = new UserArray(obj).convertToStringArray();
            out.println(Session.security.brincar(objMain));
            a = "Enviado ArrayList<User> [OBJECT] para " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            System.out.println("<><><><><><><><><><><><><><><User3> " + objMain);
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
