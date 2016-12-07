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

        socket = new Socket(Session.masterIP, PORT_SERVER);
        System.out.println("Conectado ao Server: " + Session.masterIP + ":" + PORT_SERVER);
        outputStream = new ObjectOutputStream(socket.getOutputStream());

        System.out.println("USER sent: " + Session.nickname);
        User user = new User();
        user.nickname = Session.nickname;

        outputStream.writeObject(user);

    }

    public void sv_communicateStartGame(String ip, GameRuntime obj) {
        try {
            System.out.println("communicateStartGame(): " + ip + ":" + PORT_CLIENT);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            System.out.println("Conectado ao Server (modo reverso)");
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            outputStream.writeObject(obj);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void communicateEndRoundToValidate(EndRound objToSend) {
        try {
            socket = new Socket(Session.masterIP, PORT_SERVER);
            System.out.println("communicateEndRound()");
            System.out.println("Conectado ao Server: " + Session.masterIP + ":" + PORT_SERVER);
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("EndRound sent from: " + objToSend.nickname);

            outputStream.writeObject(objToSend);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sv_communicateStartValidation(String ip, ArrayList<EndRound> obj) {
        try {
            System.out.println("communicateStartValidation(): " + ip + ":" + PORT_CLIENT);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            System.out.println("Conectado ao Server (modo reverso)");
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            outputStream.writeObject(obj);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void communicateDataValidated(ArrayList<User> objToSend) {
        try {
            socket = new Socket(Session.masterIP, PORT_SERVER);
            System.out.println("communicateDataValidated()");
            System.out.println("Conectado ao Server: " + Session.masterIP + ":" + PORT_SERVER);
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("ArrayList<EndRound> sent");

            outputStream.writeObject(objToSend);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sv_communicateScores(String ip, ArrayList<User> obj) {
        try {
            System.out.println("sv_communicateScores(): " + ip + ":" + PORT_CLIENT);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            System.out.println("Conectado ao Server (modo reverso)");
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            outputStream.writeObject(obj);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
