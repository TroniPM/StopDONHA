package entidades.network;

import entidades.GameRuntime;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import entidades.network.sendible.EndRound;
import entidades.network.sendible.EndRoundArray;
import entidades.network.sendible.RoundDataToValidate;
import entidades.network.sendible.User;
import entidades.network.sendible.UserArray;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.security.PrivateKey;
import java.util.List;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.SerializationUtils;
import org.bouncycastle.util.Arrays;
import security.ChaveSessao;
import security.KeyAutenticacaoCliente;
import security.KeyAutenticacaoServidor;
import security.KeyEncriptacaoCliente;
import security.KeyEncriptacaoServidor;
import util.Methods;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Servidor {

    private ServerSocket serverSocket = null;
    private ServerSocket serverClientSocket = null;
    public ObjectInputStream inStream = null;

    public ArrayList<Socket> networkClientsSockets = new ArrayList<>();
    public ArrayList<User> arrayUsuariosComChaves = new ArrayList<>();
    private ArrayList<EchoThread> arrayAllThreads = new ArrayList<>();
    public ArrayList<ChaveSessao> arrayChaveSessao = new ArrayList<>();

    private String[] ipsNetwork = null;
    public static final int PORT_SERVER = 12345;
    public static final int PORT_CLIENT = 12346;

    private static Thread threadWaitingKey, threadWaitingRoom, threadStartGame, threadEndRound, threadStartValidation, threadShowScore;

    public Servidor() {

    }

    public String[] getIpsNetwork() {
        updateIpsNetwork();
        return ipsNetwork;
    }

    private void updateIpsNetwork() {
        ipsNetwork = new String[networkClientsSockets.size()];
        for (int i = 0; i < networkClientsSockets.size(); i++) {
            ipsNetwork[i] = networkClientsSockets.get(i).getInetAddress().getHostAddress();
        }
    }

    public ServerSocket makeConnectionServer() {

        if (serverSocket != null && !serverSocket.isClosed()) {
            return serverSocket;
        }
        ServerSocket aux = null;
        try {
            aux = new ServerSocket(PORT_SERVER);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return aux;
    }

    public ServerSocket makeConnectionClientServer() {

        if (serverClientSocket != null && !serverClientSocket.isClosed()) {
            return serverClientSocket;
        }
        ServerSocket aux = null;
        try {
            aux = new ServerSocket(PORT_CLIENT);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return aux;
    }

    public void closeAndCleanAllData() {
        try {
            threadWaitingRoom.interrupt();
        } catch (Exception e) {
            e.getStackTrace();
        }
        try {
            threadStartGame.interrupt();
        } catch (Exception e) {
            e.getStackTrace();
        }
        try {
            threadEndRound.interrupt();
        } catch (Exception e) {
            e.getStackTrace();
        }
        try {
            threadStartValidation.interrupt();
        } catch (Exception e) {
            e.getStackTrace();
        }
        try {
            threadShowScore.interrupt();
        } catch (Exception e) {
            e.getStackTrace();
        }

        networkClientsSockets.clear();
        ipsNetwork = null;

        for (int i = 0; i < arrayAllThreads.size(); i++) {
            if (arrayAllThreads.get(i) == null) {
                continue;
            }
            arrayAllThreads.get(i).closeSocket();
            try {
                arrayAllThreads.get(i).interrupt();
                arrayAllThreads.set(i, null);
            } catch (Exception a) {
                //System.out.println("Não conseguiu finalizar thread.");
            }
        }
        closeConnectionServer();
        closeConnectionClientServer();
    }

    public void closeConnectionServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();

            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeConnectionClientServer() {
        try {
            if (serverClientSocket != null && !serverClientSocket.isClosed()) {
                serverClientSocket.close();

            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ListeningStepOne() {
        //Não está dentro de um WHILE. Só recebe UMA conexão.
        threadWaitingKey = new Thread(new Runnable() {
            @Override
            public void run() {
                serverSocket = makeConnectionServer();
                Session.addLog("Ouvindo ListeningStepOne");

                try {
                    Socket socket = serverSocket.accept();
                    EchoThread a = new EchoThread(socket);
                    a.start();
                } catch (IOException e) {
                    Session.addLog("ListeningStepOne() I/O error: " + e.getLocalizedMessage() + ". Fechando conexão...");
                }
            }
        });
        threadWaitingKey.start();

    }

    public void ListeningWaitingRoom() {
        threadWaitingRoom = new Thread(new Runnable() {
            @Override
            public void run() {
                serverSocket = makeConnectionServer();
                Session.addLog("Ouvindo WaitingRoom");
                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept();
                    } catch (IOException e) {
                        Session.addLog("ListeningWaitingRoom() I/O error: " + e.getLocalizedMessage() + ". Fechando conexão...");
                        break;
                    }
                    // new threa for a client
                    EchoThread a = new EchoThread(socket);
                    arrayAllThreads.add(a);
                    a.start();
                }
            }
        });
        threadWaitingRoom.start();

    }

    public void ListeningStartGame() {
        threadStartGame = new Thread(new Runnable() {
            @Override
            public void run() {
                serverClientSocket = makeConnectionClientServer();
                Session.addLog("Ouvindo StartGame");
                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverClientSocket.accept();
                    } catch (IOException e) {
                        Session.addLog("ListeningWaitingRoom() I/O error: " + e.getLocalizedMessage() + ". Fechando conexão...");
                        break;
                    }
                    // new threa for a client
                    EchoThread a = new EchoThread(socket);
                    arrayAllThreads.add(a);
                    a.start();
                }
            }
        });
        threadStartGame.start();
    }

    public void ListeningEndRoundToValidate() {
        threadEndRound = new Thread(new Runnable() {
            @Override
            public void run() {
                serverSocket = makeConnectionServer();
                Session.addLog("Ouvindo ListeningEndRoundToValidate");
                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept();
                    } catch (IOException e) {
                        Session.addLog("ListeningWaitingRoom() I/O error: " + e.getLocalizedMessage() + ". Fechando conexão...");
                        break;
                    }
                    // new threa for a client
                    EchoThread a = new EchoThread(socket);
                    arrayAllThreads.add(a);
                    a.start();
                }
            }
        });
        threadEndRound.start();
    }

    public void ListeningStartValidation() {
        threadStartValidation = new Thread(new Runnable() {
            @Override
            public void run() {
                serverClientSocket = makeConnectionClientServer();
                Session.addLog("Ouvindo StartValidation");
                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverClientSocket.accept();
                    } catch (IOException e) {
                        Session.addLog("ListeningWaitingRoom() I/O error: " + e.getLocalizedMessage() + ". Fechando conexão...");
                        break;
                    }
                    // new threa for a client
                    EchoThread a = new EchoThread(socket);
                    arrayAllThreads.add(a);
                    a.start();
                }
            }
        });
        threadStartValidation.start();
    }

    public void ListeningShowScore() {
        threadShowScore = new Thread(new Runnable() {
            @Override
            public void run() {
                serverClientSocket = makeConnectionClientServer();
                Session.addLog("Ouvindo ListeningShowScore");
                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverClientSocket.accept();
                    } catch (IOException e) {
                        Session.addLog("ListeningWaitingRoom() I/O error: " + e.getLocalizedMessage() + ". Fechando conexão...");
                        break;
                    }
                    // new threa for a client
                    EchoThread a = new EchoThread(socket);
                    arrayAllThreads.add(a);
                    a.start();
                }
            }
        });
        threadShowScore.start();

    }

    public class EchoThread extends Thread {

        private Socket socket;

        public EchoThread(Socket clientSocket) {
            this.socket = clientSocket;
        }

        public void closeSocket() {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void userType(User data) {
            //User data = (User) obj;
            data.ip = socket.getInetAddress().getHostAddress();
            Session.addLog("RECEIVED: userType(): [OBJECT] recebido User: " + data.nickname);
            networkClientsSockets.add(socket);
            Session.gRunTime.nicknamesNetwork.add(data.nickname);

            /* Adiciono a um array para poder mesclar esse objeto junto com os
            objetos do GameRuntime */
            arrayUsuariosComChaves.add(data);
        }

        public void endRoundType(EndRound obj) {
            Session.addLog("RECEIVED: endRoundType(): [OBJECT] recebido EndRound");
            Session.addLog(obj.getRespostasEAceitacao());

            DataNetworkManager.respostasRecebidasDoRound.add(obj);
        }

        public void arrayListEndRound(ArrayList<EndRound> objRecebidoEndRound) {
            Session.addLog("RECEIVED: arrayListEndRound(): [OBJECT] recebido ArrayList<EndRound>");
            try {
                DataNetworkManager.respostasRecebidasDoRound = objRecebidoEndRound;
            } catch (Exception e) {
            }
            Session.canValidateRespostas = true;
        }

        public void arrayListUser(List<User> objRecebidoUser) {
            Session.addLog("RECEIVED: arrayListUser(): [OBJECT] recebido ArrayList<User>");
            try {
                //ArrayList<User> objRecebidoUser = (ArrayList<User>) obj;
                DataNetworkManager.respostasRecebidasValidated.add(objRecebidoUser);
            } catch (Exception e1) {
                throw new UnsupportedOperationException("Objeto é de tipo diferente."); //To change body of generated methods, choose Tools | Templates.
            }
            Session.canShowHighScores = true;
        }

        public void roundDataToValidateType(RoundDataToValidate obj) {
            //RoundDataToValidate data = (RoundDataToValidate) obj;
            Session.addLog("RECEIVED: roundDataToValidateType(): [OBJECT] recebido RoundDataToValidate: " + obj.id);
            for (int i = 0; i < obj.respostasAceitacao.size(); i++) {
                Session.addLog(obj.respostasAceitacao.get(i).toString());
            }
        }

        public void gameRunType(GameRuntime obj) {
            Session.addLog("RECEIVED: gameRunType(): [OBJECT] recebido gameRunType: " + obj.currentNickname);
            Session.gRunTime = obj;
            Session.canStartGame = true;//Flag para thread q está ouvindo fazer action
        }

        @Override
        public void run() {
            if (socket == null || socket.isClosed()) {
                return;
            }
            Session.addLog("Conexão recebida " + socket.getRemoteSocketAddress().toString());
            Session.addLog("Conexão recebida " + socket.getInetAddress().getHostAddress());

            byte[] msgBytes = null;
            String msg = null;

            while (true) {
                //Recebendo dados
                try {
                    msgBytes = (byte[]) new ObjectInputStream(socket.getInputStream()).readObject();
                    msg = new String(msgBytes);
                } catch (Exception ex) {
                    msgBytes = null;
                    System.out.println("erro");
                }
                //do stuff
                if (msgBytes == null) {
                    //break;
                } else {
                    Session.addLog("#####RECEBEU##### TEXTO: " + msgBytes);

                    if (msg.equals("start")) {
                        ChaveSessao cs = new ChaveSessao(false);
                        PrivateKey pri = null;
                        try {
                            //pri = Methods.readPrivateKey("./private.der");
                            pri = Session.security.chavePrivadaTHIS;
                        } catch (Exception ex) {
                            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        int qtd = 0;
                        //Espero receber todas as chaves.
                        inner:
                        while (true) {
                            if (socket == null || socket.isClosed()) {
                                return;
                            }
                            //Recebendo dados
                            try {
                                msgBytes = (byte[]) new ObjectInputStream(socket.getInputStream()).readObject();
                            } catch (Exception ex) {
                                msgBytes = null;
                            }

                            if (msgBytes != null) {
                                //Decriptando conteúdo com a chave privada do servidor
                                try {
                                    msgBytes = security.Security.decriptografiaAssimetrica(msgBytes, pri);
                                } catch (Exception e) {
                                    msgBytes = null;
                                }

                                //Caso consiga desencriptar dados com chave privada, tento o casting
                                if (msgBytes != null) {
                                    Object deserialize = (Object) SerializationUtils.deserialize(msgBytes);
                                    if (deserialize.getClass().getName().equals("security.KeyAutenticacaoServidor")) {
                                        Session.addLog("Recebeu KeyAutenticacaoServidor");
                                        cs.AUTENTICACAO_SERVIDOR = ((KeyAutenticacaoServidor) deserialize).chave;
                                    } else if (deserialize.getClass().getName().equals("security.KeyAutenticacaoCliente")) {
                                        Session.addLog("Recebeu KeyAutenticacaoCliente");
                                        cs.AUTENTICACAO_CLIENTE = ((KeyAutenticacaoCliente) deserialize).chave;
                                    } else if (deserialize.getClass().getName().equals("security.KeyEncriptacaoServidor")) {
                                        Session.addLog("Recebeu KeyEncriptacaoServidor");
                                        cs.ENCRIPTACAO_SERVIDOR = ((KeyEncriptacaoServidor) deserialize).chave;
                                    } else if (deserialize.getClass().getName().equals("security.KeyEncriptacaoCliente")) {
                                        Session.addLog("Recebeu KeyEncriptacaoCliente");
                                        cs.ENCRIPTACAO_CLIENTE = ((KeyEncriptacaoCliente) deserialize).chave;
                                    }
                                }
                            }
                            //Quando receber as 4 chaves, paro de receber dados
                            qtd++;
                            if (qtd == 4) {
                                break inner;
                            }
                        }//while

                        //Mapeio o IP com a ChaveSessao
                        cs.ip = socket.getInetAddress().getHostAddress();
                        Session.addLog("\n" + cs.toString());

                        String response = null;
                        if (!cs.isValid()) {
                            Session.addLog("Chave de sessão recebida pelo cliente é inválida. "
                                    + "Não vai salvar e avisará ao cliente que ele não pode participar.");
                            response = "invalid";
                        } else {
                            Session.addLog("Chave de sessão recebida pelo cliente é válida. "
                                    + "Notificando cliente para ele poder entrar na sala.");
                            response = "valid";
                            //Para manter referência de chaveSessao ao longo do programa
                            arrayChaveSessao.add(cs);
                        }

                        //Avisar cliente q recebeu as 4 chaves
                        try {
                            Session.addLog("Avisando ao cliente que recebeu as 4 chaves.");
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                            out.writeObject(response.getBytes());
                            out.flush();
                        } catch (IOException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        /**
                         * ACABANDO MUDANÇA
                         */
                    } else {
                        ChaveSessao cs = null;//new ChaveSessao(false);
                        //Procurado ChaveSessao previamente guardada
                        inner:
                        for (ChaveSessao inChaveSessao : arrayChaveSessao) {
                            if (inChaveSessao.ip.equals(socket.getInetAddress().getHostAddress())) {
                                cs = inChaveSessao;
                                break inner;
                            }
                        }
                        SecretKey autenticacao = null;
                        SecretKey encriptacao = null;

                        /**
                         * FAZER COM QUE QUANDO FOR OBJETO GAMERUNTIME PEGUE A
                         * CHAVE DO SERVIDOR, E NÃO DO CLIENTE PARA DECRIPTAR
                         */
                        //Caso seja conexão do cliente DENTRO do servidor
                        if (Methods.isIpFromServidor(socket.getInetAddress().getHostAddress())) {
                            Session.addLog("PACKAGE recebido foi do Cliente DENTRO servidor...");
                            cs = Session.security.KEY;
                            autenticacao = cs.AUTENTICACAO_CLIENTE;
                            encriptacao = cs.ENCRIPTACAO_CLIENTE;
                        } else if (socket.getInetAddress().getHostAddress().equals(Session.masterIP)) {
                            //Caso conexão venha do servidor
                            Session.addLog("PACKAGE recebido foi do servidor...");
                            cs = Session.security.KEY;
                            autenticacao = cs.AUTENTICACAO_SERVIDOR;
                            encriptacao = cs.ENCRIPTACAO_SERVIDOR;
                        } else if (cs == null) {
                            Session.addLog("ChaveSessao não encontrada (cliente desconhecido). Dropando recepção dos dados");
                            return;
                        } else {
                            Session.addLog("PACKAGE recebido foi de um cliente normal...");
                            autenticacao = cs.AUTENTICACAO_CLIENTE;
                            encriptacao = cs.ENCRIPTACAO_CLIENTE;
                        }

                        int obj = getObject(msgBytes, autenticacao, encriptacao);

                    }
                }
            }
        }

        private int getObject(byte[] msgBytes, SecretKey autenticacao, SecretKey encriptacao) {
            Object deserialize = SerializationUtils.deserialize(msgBytes);
            if (deserialize.getClass().getName().equals("security.Package")) {
                security.Package p = (security.Package) deserialize;

                //Verificar autenticação
                byte[] auth = null;
                try {
                    auth = Session.security.autenticacao(p.data, autenticacao);
                } catch (Exception ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }

                //dropo recepção de dados por autenticação não estar correta
                if (!Arrays.areEqual(p.auth, auth)) {
                    Session.addLog("Assinatura diferente. Ignorando dados.");
                    return 2;
                } else {
                    Session.addLog("Assinatura COMBINA. Receptando dados.");
                }
                //Desencriptar
                byte[] dec;
                try {
                    Session.addLog("Decriptando dados recebidos com ChaveSessao.");
                    dec = Session.security.decriptografiaSimetrica(p.data, encriptacao);
                    deserialize = SerializationUtils.deserialize(dec);

                    //System.out.println(deserialize.getClass().getName());
                    Session.addLog("Fazendo casting de dado recebido.");
                    //do stuff here
                    if (deserialize.getClass().getName().equals("entidades.network.sendible.User")) {
                        userType((User) deserialize);
                    } else if (deserialize.getClass().getName().equals("entidades.GameRuntime")) {
                        gameRunType((GameRuntime) deserialize);
                    } else if (deserialize.getClass().getName().equals("entidades.network.sendible.EndRound")) {
                        endRoundType((EndRound) deserialize);
                    } else if (deserialize.getClass().getName().equals("entidades.network.sendible.EndRoundArray")) {
                        arrayListEndRound(((EndRoundArray) deserialize).array);
                    } else if (deserialize.getClass().getName().equals("entidades.network.sendible.UserArray")) {
                        arrayListUser(((UserArray) deserialize).array);
                    }

                    return 1;
                } catch (Exception ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                    return 2;
                }
            } else {
                Session.addLog("Dado recebido desconhecido.");
            }
            return 0;
        }
    }
}
