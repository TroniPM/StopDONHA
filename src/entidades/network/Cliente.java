package entidades.network;

import entidades.GameRuntime;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
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
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.SerializationUtils;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import security.ChaveSessao;
import security.KeyAutenticacaoCliente;
import security.KeyAutenticacaoServidor;
import security.KeyEncriptacaoCliente;
import security.KeyEncriptacaoServidor;
import security.Security;
import util.Methods;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Cliente {

    private boolean isConnected = false;
    private Socket socket = null;
    //private ObjectOutputStream out = null;

    public Cliente() {
        try {
            //socket = new Socket(Session.masterIP, PORT_SERVER);
            //out = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeAndCleanAllData() {
        isConnected = false;
    }

    public static void main(String[] args) {
        Cliente c = new Cliente();

        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Session.security.KEY = new ChaveSessao(true);
        try {
            c.startScheme();
            c.communicateWaitingRoomEnter();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startScheme() throws IOException {
        String a = "startScheme: Vai começar scheme em " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        Socket socket = new Socket(Session.masterIP, PORT_SERVER);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        PublicKey pub = null;
        try {
            pub = Methods.readPublicKey("./public.der");
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Criando chaves
        //ChaveSessao cs = new ChaveSessao(true);
        //Session.security.KEY = cs;
        ChaveSessao cs = Session.security.KEY;
        //Encapsulando chaves para envio
        KeyAutenticacaoServidor kas = new KeyAutenticacaoServidor(cs.getAUTENTICACAO_SERVIDOR());
        KeyAutenticacaoCliente kac = new KeyAutenticacaoCliente(cs.getAUTENTICACAO_CLIENTE());
        KeyEncriptacaoServidor kes = new KeyEncriptacaoServidor(cs.getENCRIPTACAO_SERVIDOR());
        KeyEncriptacaoCliente kec = new KeyEncriptacaoCliente(cs.getENCRIPTACAO_CLIENTE());

        //Aviso ao servidor q vou iniciar
        out.writeObject("start".getBytes());
        out.flush();
        //System.out.println("Enviou Start");
        //Começo do envio de chaves
        byte[] b1 = SerializationUtils.serialize(kas);
        byte[] criptografa1 = security.Security.criptografa(b1, pub);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(criptografa1);
        out.flush();
        Session.addLog("Enviou chave1");
        byte[] b2 = SerializationUtils.serialize(kac);
        byte[] criptografa2 = security.Security.criptografa(b2, pub);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(criptografa2);
        out.flush();
        Session.addLog("Enviou chave2");
        byte[] b3 = SerializationUtils.serialize(kes);
        byte[] criptografa3 = security.Security.criptografa(b3, pub);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(criptografa3);
        out.flush();
        Session.addLog("Enviou chave3");
        byte[] b4 = SerializationUtils.serialize(kec);
        byte[] criptografa4 = security.Security.criptografa(b4, pub);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(criptografa4);
        out.flush();
        Session.addLog("Enviou chave4");
        Session.addLog("Enviado ChaveSessao para " + Session.masterIP + ":" + PORT_SERVER);

        Session.addLog("Aguardando confirmação de resposta");
        while (true) {
            try {
                byte[] msgBytes = (byte[]) new ObjectInputStream(socket.getInputStream()).readObject();
                if (new String(msgBytes).equals("start")) {
                    Session.addLog("Confirmação recebida. Prosseguir...");
                    break;
                }
            } catch (ClassNotFoundException ex) {
                //Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        out.close();
        socket.close();
    }

    public void communicateWaitingRoomEnter() throws IOException {
        String a = "communicateWaitingRoomEnter: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        Socket socket = new Socket(Session.masterIP, PORT_SERVER);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        User user = new User();
        user.nickname = Session.nickname;
        //user.ip = Session.security.passo2.IP;
        /*SEGURANÇA*/
        byte[] data = generatePackage(SerializationUtils.serialize(user),
                Session.security.KEY.AUTENTICACAO_CLIENTE,
                Session.security.KEY.ENCRIPTACAO_CLIENTE, "user");
        /*SEGURANÇA*/

 /*// Gera encriptação
        byte[] encryp = Session.security.brincar(SerializationUtils.serialize(user),
                Session.security.KEY.ENCRIPTACAO_CLIENTE);
        // Gera Autenticação
        byte[] auth = null;
        try {
            auth = Session.security.autenticacao(encryp, Session.security.KEY.AUTENTICACAO_CLIENTE);
        } catch (DataLengthException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidCipherTextException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Cria um Package para enviar
        security.Package p = new security.Package(auth, encryp, Session.security.TAG,
                Session.security.TAG_NUMBER++, "user");
        // Converte o Package em um array de bytes
        byte[] data = SerializationUtils.serialize(p);*/
        // Enviando dados
        out.writeObject(data);
        a = "Enviado User [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
    }

    /**
     * Só o clienteMAIN utiliza este método. Ao clicar em iniciar jogo, na sala
     * de espera.
     *
     * @param ip
     * @param obj
     * @param autenticacao
     * @param encriptar
     */
    public void sv_communicateStartGame(String ip, GameRuntime obj,
            SecretKey autenticacao, SecretKey encriptar) {
        try {
            String a = "sv_communicateStartRound: Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            /*SEGURANÇA*/
            byte[] data = generatePackage(SerializationUtils.serialize(obj),
                    Session.security.KEY.AUTENTICACAO_CLIENTE,
                    Session.security.KEY.ENCRIPTACAO_CLIENTE, "gameruntime");
            /*SEGURANÇA*/

            out.writeObject(data);

            a = "Enviado GameRuntime [OBJECT] para" + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            out.close();
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para criação de Package para envio pela rede.
     *
     * @param data dados que serão enviados. NÃO deve estar encriptado.
     * @param autenticacao chave para autenticação
     * @param encriptacao chave para encriptação
     * @param tag tag que será atribuído a classe Package para o casting
     * @return
     */
    public byte[] generatePackage(byte[] data, SecretKey autenticacao,
            SecretKey encriptacao, String tag) {
        byte[] encryp = Session.security.brincar(data, encriptacao);
        // Gera Autenticação
        byte[] auth = null;
        try {
            auth = Session.security.autenticacao(encryp, autenticacao);
        } catch (DataLengthException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidCipherTextException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Cria um Package para enviar
        security.Package p = new security.Package(auth, encryp, Session.security.TAG,
                Session.security.TAG_NUMBER++, tag);
        // Converte o Package em um array de bytes
        byte[] d = SerializationUtils.serialize(p);

        return d;
    }

    /**
     * Utilizado quando um cliente (EXCETO O ALOCADO DENTRO DO SERVIDOR) acaba
     * sua jogada e clica em finalizar. Essa classe contem suas respostas. Essa
     * classe será enviada ao servidor.
     *
     * @param obj
     */
    public void communicateAnswersFromClient(EndRound obj) {
        String a = "communicateAnswersFromClient: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
        try {
            Socket socket = new Socket(Session.masterIP, PORT_SERVER);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            /*SEGURANÇA*/
            byte[] data = generatePackage(SerializationUtils.serialize(obj),
                    Session.security.KEY.AUTENTICACAO_CLIENTE,
                    Session.security.KEY.ENCRIPTACAO_CLIENTE, "endround");
            /*SEGURANÇA*/

            out.writeObject(data);

            a = "Enviado EndRound [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Utilizado quando um cliente (EXCETO O ALOCADO DENTRO DO SERVIDOR) acaba
     * de validar todas as respostas e clica em finalizar. Essa classe contem a
     * validação do usuário para todas as respostas enviadas por todos os
     * clientes para o servidor.
     *
     * @param obj
     */
    public void communicateAswersValidatedFromClient(ArrayList<User> obj) {
        String a = "communicateAswersValidatedFromClient: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
        try {
            Socket socket = new Socket(Session.masterIP, PORT_SERVER);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            UserArray g = new UserArray(obj);

            /*SEGURANÇA*/
            byte[] data = generatePackage(SerializationUtils.serialize(g),
                    Session.security.KEY.AUTENTICACAO_CLIENTE,
                    Session.security.KEY.ENCRIPTACAO_CLIENTE, "userarray");
            /*SEGURANÇA*/

            out.writeObject(data);

            a = "Enviado ArrayList<User> [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            out.close();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    /**
     * Utilizado pelo clienteMAIN, após ver painel de highscores, ao clicar em
     * começar próximo round. Enviará GameRuntime, classe que contém todas as
     * pontuações, nicks e configurações da partida.
     *
     * @param ip
     * @param obj
     * @param autenticacao
     * @param encriptar
     */
    public void sv_communicateStartRound(String ip, GameRuntime obj,
            SecretKey autenticacao, SecretKey encriptar) {
        try {
            String a = "sv_communicateStartRound: Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            /*SEGURANÇA*/
            byte[] data = generatePackage(SerializationUtils.serialize(obj),
                    Session.security.KEY.AUTENTICACAO_CLIENTE,
                    Session.security.KEY.ENCRIPTACAO_CLIENTE, "gameruntime");
            /*SEGURANÇA*/

            out.writeObject(data);

            a = "Enviado GameRuntime [OBJECT] para" + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            out.close();
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utilizado pelo clienteMAIN, após ver painel de highscores, ao clicar em
     * começar próximo round. Enviará GameRuntime, classe que contém todas as
     * pontuações, nicks e configurações da partida.
     *
     * @param ip
     * @param obj
     * @param autenticacao
     * @param encriptar
     */
    public void sv_communicateStartValidation(String ip, ArrayList<EndRound> obj,
            SecretKey autenticacao, SecretKey encriptar) {
        try {
            String a = "sv_communicateStartValidation: Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            EndRoundArray g = new EndRoundArray(obj);

            /*SEGURANÇA*/
            byte[] data = generatePackage(SerializationUtils.serialize(g),
                    autenticacao, encriptar, "endroundarray");
            /*SEGURANÇA*/

            out.writeObject(data);

            a = "Enviado ArrayList<EndRound> [OBJECT] para " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            out.close();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    /**
     * Utilizado pelo clienteMAIN, após ver painel de highscores, ao clicar em
     * começar próximo round. Enviará GameRuntime, classe que contém todas as
     * pontuações, nicks e configurações da partida.
     *
     * @param ip
     * @param obj
     * @param autenticacao
     * @param encriptar
     */
    public void sv_communicateScoresToClient(String ip, ArrayList<User> obj,
            SecretKey autenticacao, SecretKey encriptar) {
        try {
            String a = "sv_communicateScoresToClient: Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            UserArray g = new UserArray(obj);
            /*SEGURANÇA*/
            byte[] data = generatePackage(SerializationUtils.serialize(g),
                    autenticacao, encriptar, "userarray");
            /*SEGURANÇA*/

            out.writeObject(data);

            a = "Enviado ArrayList<User> [OBJECT] para " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            out.close();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public void deletar_isso_daqui(String ip) {
        try {
            String a = "sv_communicateStepOne: Começando comunicação com " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);
            //Não deixo enviar para o mesmo ip da máquina servidor.
            if (ip.equals(Session.masterIP)) {
                return;
            }
            Socket socket = new Socket(ip, PORT_CLIENT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            String g = Session.security.passo1.convertToString();

            out.writeObject(g.getBytes());

            a = "Enviado StepOne [OBJECT] para" + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            out.close();
        } catch (SocketException se) {
            se.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
