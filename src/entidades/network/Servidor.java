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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
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

    private static Thread threadWaitingRoom, threadStartGame, threadEndRound, threadStartValidation, threadShowScore;
    private static ArrayList<EchoThread> arrayAllThreads = new ArrayList<>();

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
                System.out.println("Não conseguiu finalizar thread.");
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
            Session.addLog("[OBJECT] recebido User " + data.nickname);
            networkClientsSockets.add(socket);
            Session.gRunTime.nicknamesNetwork.add(data.nickname);
        }

        public void endRoundType(EndRound obj) {
            //EndRound objRecebido = (EndRound) obj;
            /*if (Session.DEBUG) {
                System.out.println("------------------EndRound received--------------");
                objRecebido.printRespostasEAceitacao();
                System.out.println("--------------------------------------------------");
            }*/
            Session.addLog("[OBJECT] recebido EndRound");
            Session.addLog(obj.getRespostasEAceitacao());

            DataNetworkManager.respostasRecebidasDoRound.add(obj);
        }

        public void arrayListEndRound(List<EndRound> objRecebidoEndRound) {
            Session.addLog("[OBJECT] recebido ArrayList<EndRound>");
            try {

                if (Session.DEBUG) {
                    System.out.println("*****-------------EndRoundARRAY received---------*****");
                    for (EndRound teste1 : objRecebidoEndRound) {
                        System.out.println("------------------EndRound received--------------");
                        teste1.printRespostasEAceitacao();
                        System.out.println("--------------------------------------------------");
                    }
                    System.out.println("*****----------------------------------------*****");
                }
                //System.out.println("canOverrideMainArray = " + DataNetworkManager.canOverrideMainArray);
                DataNetworkManager.respostasRecebidasDoRound = objRecebidoEndRound;
            } catch (Exception e) {
            }
            Session.canValidateRespostas = true;
        }

        public void arrayListUser(List<User> objRecebidoUser) {
            Session.addLog("[OBJECT] recebido ArrayList<User>");
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
            System.out.println("Object received = " + obj.id);
            for (int i = 0; i < obj.respostasAceitacao.size(); i++) {
                System.out.println(obj.respostasAceitacao.get(i));
            }
        }

        public void gameRunType(GameRuntime obj) {
            Session.gRunTime = obj;
            Session.canStartGame = true;//Flag para thread q está ouvindo fazer action
        }

        @Override
        public void run() {
            if (socket == null || socket.isClosed()) {
                return;

            }
            String a = "Conexão recebida " + socket.getRemoteSocketAddress().toString();
            Session.addLog(a);

            BufferedReader in;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String msg = in.readLine();

                Session.addLog("#####RECEBENDO##### TEXTO: " + msg);

                try {
                    User user = User.convertFromString(Session.security.desbrincar(msg));
                    userType(user);
                    return;
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    EndRound endRound = EndRound.convertFromString(Session.security.desbrincar(msg));
                    endRoundType(endRound);
                    return;
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    RoundDataToValidate roundDataValidate = RoundDataToValidate.convertFromString(Session.security.desbrincar(msg));
                    roundDataToValidateType(roundDataValidate);
                    return;
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    GameRuntime gameRuntime = GameRuntime.convertFromString(Session.security.desbrincar(msg));
                    gameRunType(gameRuntime);
                    return;
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    EndRoundArray endRoundArray = EndRoundArray.convertFromStringArray(Session.security.desbrincar(msg));
                    arrayListEndRound(endRoundArray.array);
                    return;
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    UserArray userArray = UserArray.convertFromStringArray(Session.security.desbrincar(msg));
                    arrayListUser(userArray.array);
                    return;
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
