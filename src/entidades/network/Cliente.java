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
import entidades.network.sendible.User;
import entidades.network.sendible.UserArray;
import java.security.PublicKey;
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
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Cliente {

    //private boolean isConnected = false;
    private Socket socket = null;

    public Cliente() {

    }

    public Socket getSocket() {
        if (socket == null || socket.isClosed()) {
            System.out.println("VAI CRIAR SOCKET");
            try {
                socket = new Socket(Session.masterIP, PORT_SERVER);
            } catch (Exception ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            return socket;
        } else {
            return socket;
        }
    }

    private boolean writeOnOutputStream(Socket socket, byte[] obj) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(obj);
            out.flush();

            return true;
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void closeAndCleanAllData() {
        //isConnected = false;
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
            SecretKey encriptacao, String ipFromDestinatiario) {
        byte[] encryp = Session.security.criptografaSimetrica(data, encriptacao);
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
        security.Package p = null;
        if (ipFromDestinatiario == null) {
            p = new security.Package(auth, encryp, ++Session.security.TAG_NUMBER);
        } else if (Session.security.mensagensEnviadasAClientes.containsKey(ipFromDestinatiario)) {
            //já enviou dados pra esse ip. Então pego o último número.
            int get = Session.security.mensagensEnviadasAClientes.get(ipFromDestinatiario);
            /**
             * TODO. colocar 0 ao invés de ++get para gerar erro de pacote
             * anterior ao já enviado.
             */
            //p = new security.Package(auth, encryp, 0);
            p = new security.Package(auth, encryp, ++get);
            //Atualizo númeração do ip específico.
            Session.security.mensagensEnviadasAClientes.
                    put(ipFromDestinatiario, get);
        } else {
            //Primeira mensagem a enviar para esse ip
            p = new security.Package(auth, encryp, 1);
            Session.security.mensagensEnviadasAClientes.
                    put(ipFromDestinatiario, 1);
        }

        // Converte o Package em um array de bytes
        byte[] d = SerializationUtils.serialize(p);

        return d;
    }

    public boolean startScheme() throws IOException {
        String a = "startScheme: Vai começar scheme em " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        //socket = new Socket(Session.masterIP, PORT_SERVER);
        socket = getSocket();

        a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        PublicKey pub = null;
        try {
            //pub = Methods.readPublicKey("./public.der");
            pub = Session.security.chavePublicaSERVIDOR;
            System.out.println(pub);
        } catch (Exception ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Criando chaves
        //ChaveSessao cs = new ChaveSessao(true);
        //Session.security.KEY = cs;
        ChaveSessao cs = Session.security.KEY;
        //Encapsulando chaves para envio
        KeyAutenticacaoServidor kas = new KeyAutenticacaoServidor(cs.AUTENTICACAO_SERVIDOR);
        KeyAutenticacaoCliente kac = new KeyAutenticacaoCliente(cs.AUTENTICACAO_CLIENTE);
        KeyEncriptacaoServidor kes = new KeyEncriptacaoServidor(cs.ENCRIPTACAO_SERVIDOR);
        KeyEncriptacaoCliente kec = new KeyEncriptacaoCliente(cs.ENCRIPTACAO_CLIENTE);

        //Aviso ao servidor q vou iniciar
        writeOnOutputStream(socket, "start".getBytes());
        //System.out.println("Enviou Start");
        //Começo do envio de chaves
        byte[] b1 = SerializationUtils.serialize(kas);
        byte[] criptografa1 = security.Security.criptografaAssimetrica(b1, pub);
        writeOnOutputStream(socket, criptografa1);
        Session.addLog("Enviou chave1");
        byte[] b2 = SerializationUtils.serialize(kac);
        byte[] criptografa2 = security.Security.criptografaAssimetrica(b2, pub);
        writeOnOutputStream(socket, criptografa2);
        Session.addLog("Enviou chave2");
        byte[] b3 = SerializationUtils.serialize(kes);
        byte[] criptografa3 = security.Security.criptografaAssimetrica(b3, pub);
        writeOnOutputStream(socket, criptografa3);
        Session.addLog("Enviou chave3");
        byte[] b4 = SerializationUtils.serialize(kec);
        byte[] criptografa4 = security.Security.criptografaAssimetrica(b4, pub);
        writeOnOutputStream(socket, criptografa4);
        Session.addLog("Enviou chave4");
        Session.addLog("Enviado ChaveSessao para " + Session.masterIP + ":" + PORT_SERVER);

        Session.addLog("Aguardando confirmação de resposta");
        while (true) {
            try {
                byte[] msgBytes = (byte[]) new ObjectInputStream(socket.getInputStream()).readObject();
                if (new String(msgBytes).equals("valid")) {
                    Session.addLog("Confirmação recebida. Prosseguir...");
                    //out.close();
                    //socket.close();

                    return true;
                } else if (new String(msgBytes).equals("invalid")) {
                    Session.addLog("Servidor respondeu: chave de sessão enviada "
                            + "é inválida. Provavelmente a chave pública do "
                            + "servidor dentro do cliente não dá match com a "
                            + "chave privada dentro do servidor. NÃO vai prosseguir...");
                    socket.close();

                    return false;
                }
            } catch (ClassNotFoundException ex) {
                //Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void communicateWaitingRoomEnter() throws IOException {
        String a = "communicateWaitingRoomEnter: Começando comunicação com " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        //socket = new Socket(Session.masterIP, PORT_SERVER);
        socket = getSocket();
        //ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);

        User user = new User();
        user.nickname = Session.nickname;
        //user.ip = Session.security.passo2.IP;
        /*SEGURANÇA*/
        byte[] data = generatePackage(SerializationUtils.serialize(user),
                Session.security.KEY.AUTENTICACAO_CLIENTE,
                Session.security.KEY.ENCRIPTACAO_CLIENTE, null);
        /*SEGURANÇA*/
        writeOnOutputStream(socket, data);
        a = "Enviado User [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
        Session.addLog(a);
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
            //socket = new Socket(Session.masterIP, PORT_SERVER);
            socket = getSocket();
            //ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            /*SEGURANÇA*/
            byte[] data = generatePackage(SerializationUtils.serialize(obj),
                    Session.security.KEY.AUTENTICACAO_CLIENTE,
                    Session.security.KEY.ENCRIPTACAO_CLIENTE, null);
            /*SEGURANÇA*/

            writeOnOutputStream(socket, data);

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
            //socket = new Socket(Session.masterIP, PORT_SERVER);
            socket = getSocket();
            //ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            a = "Conectado a " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);

            UserArray g = new UserArray(obj);

            /*SEGURANÇA*/
            byte[] data = generatePackage(SerializationUtils.serialize(g),
                    Session.security.KEY.AUTENTICACAO_CLIENTE,
                    Session.security.KEY.ENCRIPTACAO_CLIENTE, null);
            /*SEGURANÇA*/
            writeOnOutputStream(socket, data);

            a = "Enviado ArrayList<User> [OBJECT] para " + Session.masterIP + ":" + PORT_SERVER;
            Session.addLog(a);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    //////////////////////Conexões utilizadas pelo servidor
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
                    autenticacao, encriptar, ip);
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
                    autenticacao, encriptar, ip);
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
                    autenticacao, encriptar, ip);
            /*SEGURANÇA*/

            out.writeObject(data);

            a = "Enviado ArrayList<User> [OBJECT] para " + ip + ":" + PORT_CLIENT;
            Session.addLog(a);

            out.close();
        } catch (Exception se) {
            se.printStackTrace();
        }
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
                    Session.security.KEY.ENCRIPTACAO_CLIENTE, ip);
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
}
