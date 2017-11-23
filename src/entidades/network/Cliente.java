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
import entidades.network.sendible.StepOne;
import entidades.network.sendible.StepTwo;
import entidades.network.sendible.User;
import entidades.network.sendible.UserArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import javax.crypto.SecretKey;
import util.Methods;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Cliente {

    //public Socket socket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    private boolean isConnected = false;

    public Cliente() {
    }

    public void closeAndCleanAllData() {
        /*try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        socket = null;*/
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

    public void communicateDummy() throws IOException {
        String a = "communicateDummy: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        Socket socket = new Socket(Session.masterIP, PORT_SERVER);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        out.println("getkey");

        a = "Enviado DummyConnection para " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
    }

    public void communicateWaitingRoom() throws IOException {
        String a = "communicateWaitingRoom: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        Socket socket = new Socket(Session.masterIP, PORT_SERVER);
        //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        User user = new User();
        user.nickname = Session.nickname;
        user.KEY_ASSINATURA = Session.security.passo2.KEY_PRIVATE;
        user.KEY_ENCRIPTACAO = Session.security.passo2.KEY_ENCRIPTACAO;
        user.KEY_PUBLICA = Session.security.passo2.KEY_PUBLICA;
        user.ip = Session.security.passo2.IP;

        //Encriptar com chave de sessão e enviar
        String g = user.convertToString();

        /*SEGURANÇA*/
        // Gera Assinatura
        byte[] signature = Session.security.generateSignature(g, Session.security.passo2.KEY_PRIVATE);
        // Cria um Package para enviar
        security.Package p = new security.Package(signature, g.getBytes(), Session.security.TAG,
                Session.security.TAG_NUMBER++, "user");
        // Converte o Package em um array de bytes
        byte[] data = p.convertToByteArray();
        // Encriptar
        byte[] msgCriptografada = Session.security.criptografaSimetrica(data);
        /*SEGURANÇA*/

        //out.println(g);
        out.writeObject(msgCriptografada);

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

            //out.println(Session.security.brincar(obj.convertToString()));
            //Encriptar com chave de sessão e enviar
            out.println((obj.convertToString()));

            a = "Enviado GameRuntime [OBJECT] para" + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sv_communicateStepOne(String ip, StepOne obj) {
        try {
            String a = "sv_communicateStepOne: Começando comunicação com " + ip + ":" + PORT_CLIENT;
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

            //Não é necessário encriptar chave pública do servidor
            out.println(obj.convertToString());

            a = "Enviado StepOne [OBJECT] para" + ip + ":" + PORT_CLIENT;
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

            //out.println(Session.security.brincar(obj.convertToString()));
            //Encriptar com chave de sessão e enviar
            out.println((obj.convertToString()));

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

            String objMain = new EndRoundArray(obj).convertToString();
            //out.println(Session.security.brincar(objMain));
            //Encriptar com chave de sessão e enviar
            out.println((objMain));
            a = "Enviado ArrayList<EndRound> [OBJECT] para " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void communicateDataValidated(ArrayList<User> obj) {
        String a = "communicateDataValidated: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
        try {
            Socket socket = new Socket(Session.masterIP, PORT_SERVER);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            String objMain = new UserArray(obj).convertToString();
            //out.println(Session.security.brincar(objMain));
            //Encriptar com chave de sessão e enviar
            out.println((objMain));
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

            String objMain = new UserArray(obj).convertToString();
            //out.println(Session.security.brincar(objMain));
            //Encriptar com chave de sessão e enviar
            out.println((objMain));
            a = "Enviado ArrayList<User> [OBJECT] para " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void communicateStepTwo(StepTwo obj) {
        try {
            String a = "communicateStepTwo: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            Socket socket = new Socket(Session.masterIP, PORT_SERVER);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            out.println(obj.convertToString());

            a = "Enviado StepTwo [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
