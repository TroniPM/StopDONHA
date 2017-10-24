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
import entidades.network.sendible.RoundDataToValidate;
import entidades.network.sendible.User;
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
                Session.addLog("Ouvindo EndRound");
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

        public void userType(Object obj) {
            User data = (User) obj;
            Session.addLog("[OBJECT] recebido User " + data.nickname);
            networkClientsSockets.add(socket);
            Session.gRunTime.nicknamesNetwork.add(data.nickname);
        }

        public void endRoundType(Object obj) {
            EndRound objRecebido = (EndRound) obj;
            /*if (Session.DEBUG) {
                System.out.println("------------------EndRound received--------------");
                objRecebido.printRespostasEAceitacao();
                System.out.println("--------------------------------------------------");
            }*/
            Session.addLog("[OBJECT] recebido EndRound");
            Session.addLog(objRecebido.getRespostasEAceitacao());

            DataNetworkManager.respostasRecebidasDoRound.add(objRecebido);
        }

        public void arrayListType(Object obj) {
            try {
                
                ArrayList<EndRound> objRecebidoEndRound;
                objRecebidoEndRound = (ArrayList<EndRound>) obj;

                Session.addLog("[OBJECT] recebido ArrayList<EndRound>");
                
                if (Session.DEBUG) {
                    System.out.println("*****-------------EndRoundARRAY received---------*****");
                    for (EndRound teste1 : objRecebidoEndRound) {
                        System.out.println("------------------EndRound received--------------");
                        teste1.printRespostasEAceitacao();
                        System.out.println("--------------------------------------------------");
                    }
                    System.out.println("*****----------------------------------------*****");
                }
                System.out.println("canOverrideMainArray = " + DataNetworkManager.canOverrideMainArray);

                DataNetworkManager.respostasRecebidasDoRound = objRecebidoEndRound;
                Session.canValidateRespostas = true;

                return;

            } catch (Exception e) {
                Session.addLog("[OBJECT] recebido ArrayList<User>");
                try {
                    ArrayList<User> objRecebidoUser = (ArrayList<User>) obj;
                    DataNetworkManager.respostasRecebidasValidated.add(objRecebidoUser);
                } catch (Exception e1) {
                    throw new UnsupportedOperationException("Objeto é de tipo diferente."); //To change body of generated methods, choose Tools | Templates.
                }

                Session.canShowHighScores = true;
            }

        }

        public void roundDataToValidateType(Object obj) {
            RoundDataToValidate data = (RoundDataToValidate) obj;
            System.out.println("Object received = " + data.id);
            for (int i = 0; i < data.respostasAceitacao.size(); i++) {
                System.out.println(data.respostasAceitacao.get(i));
            }
        }

        public void gameRunType(Object obj) {
            Session.gRunTime = (GameRuntime) obj;
            Session.canStartGame = true;//Flag para thread q está ouvindo fazer action
        }

        @Override
        public void run() {
            if (socket == null || socket.isClosed()) {
                return;

            }
            String a = "Conexão recebida " + socket.getRemoteSocketAddress().toString();

            try {
                Session.addLog(a);
                //System.out.println("Connected - " + socket.getInetAddress().getHostAddress());
                inStream = new ObjectInputStream(socket.getInputStream());

                Object obj = inStream.readObject();
                if (obj instanceof User) {
                    userType(obj);
                } else if (obj instanceof EndRound) {
                    endRoundType(obj);
                } else if (obj instanceof ArrayList) {
                    arrayListType(obj);
                } else if (obj instanceof RoundDataToValidate) {
                    roundDataToValidateType(obj);
                } else if (obj instanceof GameRuntime) {
                    gameRunType(obj);
                }

            } catch (IOException | ClassNotFoundException ex) {
                //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                Session.canShowMainMenuByConnectionError = true;
            }
        }
    }
}
