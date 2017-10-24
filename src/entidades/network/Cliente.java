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
import entidades.network.sendible.User;
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
        String a = "Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
        
        socket = new Socket(Session.masterIP, PORT_SERVER);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
        
        User user = new User();
        user.nickname = Session.nickname;
        outputStream.writeObject(user);
        a = "Enviado User [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
    }

    public void sv_communicateStartGame(String ip, GameRuntime obj) {
        try {
            String a = "Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            outputStream.writeObject(obj);
            a = "Enviado GameRuntime [OBJECT] para" + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void communicateEndRoundToValidate(EndRound objToSend) {
        String a = "Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
        try {
            socket = new Socket(Session.masterIP, PORT_SERVER);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            outputStream.writeObject(objToSend);
            a = "Enviado EndRound [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sv_communicateStartValidation(String ip, ArrayList<EndRound> obj) {
        try {
            String a = "Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            outputStream.writeObject(obj);
            a = "Enviado ArrayList<EndRound> [OBJECT] para " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void communicateDataValidated(ArrayList<User> objToSend) {
        String a = "Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
        try {
            socket = new Socket(Session.masterIP, PORT_SERVER);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            outputStream.writeObject(objToSend);
            a = "Enviado ArrayList<User> [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sv_communicateScores(String ip, ArrayList<User> obj) {
        try {
            String a = "Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            outputStream.writeObject(obj);
            a = "Enviado ArrayList<User> [OBJECT] para " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
