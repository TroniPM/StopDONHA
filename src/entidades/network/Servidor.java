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
import entidades.network.sendible.StepOne;
import entidades.network.sendible.StepTwo;
import entidades.network.sendible.User;
import entidades.network.sendible.UserArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.security.PublicKey;
import java.util.List;
import org.bouncycastle.crypto.DataLengthException;
import util.Methods;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Servidor {

    private static ServerSocket serverSocket = null;
    private static ServerSocket serverClientSocket = null;
    public static ObjectInputStream inStream = null;

    public static ArrayList<Socket> networkClientsSockets = new ArrayList<>();
    private static String[] ipsNetwork = null;
    public static final int PORT_SERVER = 12345;
    public static final int PORT_CLIENT = 12346;

    private static Thread threadWaitingKey, threadWaitingRoom, threadStartGame, threadEndRound, threadStartValidation, threadShowScore;
    private static ArrayList<EchoThread> arrayAllThreads = new ArrayList<>();
    private boolean first = false;

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

        protected Socket socket;

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
            Session.addLog("RECEIVED: userType(): [OBJECT] recebido User: " + data.nickname);
            networkClientsSockets.add(socket);
            Session.gRunTime.nicknamesNetwork.add(data.nickname);
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

        public void stepOneType(StepOne obj) {
            Session.addLog("RECEIVED: stepOneType(): [OBJECT] recebido StepOne: " + obj);
            Session.security.passo1 = obj;
        }

        public void stepTwoType(StepTwo obj) {
            Session.addLog("RECEIVED: stepTwoType(): [OBJECT] recebido StepTwo: " + obj);
            Session.security.passo2 = obj;
        }

        @Override
        public void run() {
            if (socket == null || socket.isClosed()) {
                return;

            }
            Session.addLog("Conexão recebida " + socket.getRemoteSocketAddress().toString());
            Session.addLog("Conexão recebida " + socket.getInetAddress().getHostAddress());

            try {
                BufferedReader in;
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String msg = in.readLine();

                Session.addLog("#####RECEBENDO##### TEXTO: " + msg);

                //Vai enviar chave publica
                if (msg.equals("getkey")) {
                    Session.addLog("VAI ENVIAR CHAVE PUBLICA. Chegou ao fim. É DUMMY connection");
                    Session.conexaoCliente.sv_communicateStepOne(socket.getInetAddress().getHostAddress(), Session.security.passo1);
                    return;
                }
                /**
                 * OBRIGATORIAMENTE PRECISA VIR ANTES.
                 */
                //Se já for o servidor, desconsidero isto.
                //if (first) {
                try {
                    StepOne one = StepOne.convertFromString(msg);
                    stepOneType(one);
                    /* Envio pro servidor chave de sessão e chave publica do cliente
                    ENCRIPTADO com chave pública do servidor a partir de DENTRO da
                    thread do ListeningStepOne(), para que ele só envie StepTwo 
                    encriptado após receber a chave publica do servidor. */
                    Session.conexaoCliente.communicateStepTwo(Session.security.passo2);
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    //StepTwo two = StepTwo.convertFromString(new String(Session.security.decriptografa(msg.getBytes("UTF-8"), Session.security.passo1.chavePublicaSERVIDOR)));
                    StepTwo two = StepTwo.convertFromString(msg);
                    System.out.println(two.toString());
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                //}
                /**
                 * OBRIGATORIAMENTE PRECISA VIR ANTES.
                 */
                try {
                    //User user = User.convertFromString(Session.security.desbrincar(msg));
                    User user = User.convertFromString((msg));
                    userType(user);
                    first = true;
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    //EndRound endRound = EndRound.convertFromString(Session.security.desbrincar(msg));
                    EndRound endRound = EndRound.convertFromString((msg));
                    endRoundType(endRound);
                    first = true;
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    //RoundDataToValidate roundDataValidate = RoundDataToValidate.convertFromString(Session.security.desbrincar(msg));
                    RoundDataToValidate roundDataValidate = RoundDataToValidate.convertFromString((msg));
                    roundDataToValidateType(roundDataValidate);
                    first = true;
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    //GameRuntime gameRuntime = GameRuntime.convertFromString(Session.security.desbrincar(msg));
                    GameRuntime gameRuntime = GameRuntime.convertFromString((msg));
                    gameRunType(gameRuntime);
                    first = true;
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    //EndRoundArray endRoundArray = EndRoundArray.convertFromStringArray(Session.security.desbrincar(msg));
                    EndRoundArray endRoundArray = EndRoundArray.convertFromStringArray((msg));
                    arrayListEndRound(endRoundArray.array);
                    first = true;
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    //UserArray userArray = UserArray.convertFromStringArray(Session.security.desbrincar(msg));
                    UserArray userArray = UserArray.convertFromStringArray((msg));
                    arrayListUser(userArray.array);
                    first = true;
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                Session.canShowMainMenuByConnectionError = true;
            }
        }
    }
}
