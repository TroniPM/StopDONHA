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
import java.net.Inet4Address;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import javax.crypto.SecretKey;
import org.bouncycastle.crypto.DataLengthException;
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
    public ArrayList<StepTwo> arrayStepTwo = new ArrayList<>();
    private ArrayList<EchoThread> arrayAllThreads = new ArrayList<>();
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

            /* Adiciono a um array para poder mesclar esse objeto junto com os
            objetos do GameRuntime */
            data.ip = socket.getInetAddress().getHostAddress();
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

        public void stepOneType(StepOne obj) {
            Session.addLog("RECEIVED: stepOneType(): [OBJECT] recebido StepOne: " + obj);
            Session.security.passo1 = obj;
        }

        public void stepTwoType(StepTwo obj) {
            Session.addLog("RECEIVED: stepTwoType(): [OBJECT] recebido StepTwo: " + obj);
            obj.IP = socket.getInetAddress().getHostAddress();
            arrayStepTwo.add(obj);
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
            try {
                msgBytes = (byte[]) new ObjectInputStream(socket.getInputStream()).readObject();
                System.out.println("<<<<<<<<<<<<<<<<<" + msgBytes);
                msg = new String(msgBytes);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            Session.addLog("#####RECEBEU##### TEXTO: " + msg);

            try {
                if (msg.equals("getkey")) {
                    Session.addLog("VAI ENVIAR CHAVE PUBLICA. Chegou ao fim. É DUMMY connection");
                    Session.conexaoCliente.sv_communicateStepOne(socket.getInetAddress().getHostAddress(), Session.security.passo1);
                    return;
                }

                //#2 - ENCONTRAR CHAVE DE SESSÃO DO CLIENTE ATUAL.
                SecretKey keySessao = null;
                PublicKey keyPublic = null;
                PrivateKey keyPrivate = null;
                //Caso seja uma conexão cliente normal
                for (User inUser : arrayUsuariosComChaves) {
                    if (inUser.ip.equals(socket.getInetAddress().getHostAddress())) {
                        keySessao = inUser.KEY_ENCRIPTACAO;
                        keyPublic = inUser.KEY_PUBLICA;
                        keyPrivate = inUser.KEY_ASSINATURA;
                    }
                }
                //Caso seja conexão do cliente DENTRO do servidor

                if (socket.getInetAddress().getHostAddress().equals(Inet4Address.getLocalHost().getHostAddress())) {
                    keySessao = Session.security.passo2.KEY_ENCRIPTACAO;
                    keyPublic = Session.security.passo2.KEY_PUBLICA;
                    keyPrivate = Session.security.passo2.KEY_PRIVATE;
                } else if (socket.getInetAddress().getHostAddress().equals(Session.masterIP)) {
                    //para pegar a chave específica do servidor caso conexão venha do servidor
                    Session.addLog("PACKAGE recebido foi do servidor...");
                    keySessao = Session.security.passo1.KEY_ENCRIPTACAO;
                    //keyPublic = Session.security.passo1.KEY_PUBLICA;
                    keyPrivate = Session.security.passo1.KEY_PRIVATE;

                    keyPublic = Session.security.chavePublicaSERVIDOR;
                }

                //#3 - PRINCIPAL
                try {
                    byte[] data = Session.security.decriptografaSimetrica(msgBytes, keySessao);

                    security.Package p = security.Package.convertFromByteArray(data);

                    if (Session.security.verifySignature(new String(p.data), p.signature, keyPublic)) {
                        Session.addLog("PACKAGE foi assinado corretamente.");
                    } else {
                        Session.addLog("PACKAGE não foi assinado corretamente. Descartando...");
                        return;
                    }

                    if (p.objeto.equals("user")) {
                        try {
                            userType(User.convertFromString(new String(p.data)));
                            return;
                        } catch (Exception ex) {
                        }
                    } else if (p.objeto.equals("endround")) {
                        try {
                            endRoundType(EndRound.convertFromString(new String(p.data)));
                            return;
                        } catch (Exception ex) {
                        }
                    } else if (p.objeto.equals("userarray")) {
                        try {
                            arrayListUser(UserArray.convertFromStringArray(new String(p.data)).array);
                            return;
                        } catch (Exception ex) {
                        }
                    } else if (p.objeto.equals("steptwo")) {
                        try {
                            stepTwoType(StepTwo.convertFromString(new String(p.data)));
                            return;
                        } catch (Exception ex) {
                        }
                    } else if (p.objeto.equals("gameruntime")) {
                        try {
                            gameRunType(GameRuntime.convertFromString(new String(p.data)));
                            return;
                        } catch (Exception ex) {
                        }
                    } else if (p.objeto.equals("stepone")) {
                        try {
                            stepOneType(StepOne.convertFromString(new String(p.data)));
                            Session.conexaoCliente.communicateStepTwo(Session.security.passo2);
                            return;
                        } catch (Exception ex) {
                        }
                    } else if (p.objeto.equals("endroundarray")) {
                        try {
                            arrayListEndRound(EndRoundArray.convertFromStringArray(new String(p.data)).array);
                            return;
                        } catch (Exception ex) {
                        }
                    }
                    //VERIFICAR SE MENSAGEM JÁ FOI ENVIADA
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Se já for o servidor, desconsidero isto.
                /*try {
                    StepOne one = StepOne.convertFromString(msg);
                    stepOneType(one);
                    //Envio pro servidor chave de sessão e chave publica do cliente
                    //ENCRIPTADO com chave pública do servidor a partir de DENTRO da
                    //thread do ListeningStepOne(), para que ele só envie StepTwo 
                    //encriptado após receber a chave publica do servidor.
                    Session.conexaoCliente.communicateStepTwo(Session.security.passo2);
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                //#1 - DESCRIPTOGRAFAR COM CHAVE PUBLICA SERVIDOR
                //TODO
                /*try {
                    //StepTwo two = StepTwo.convertFromString(new String(Session.security.decriptografa(msg.getBytes("UTF-8"), Session.security.passo1.chavePublicaSERVIDOR)));
                    StepTwo two = StepTwo.convertFromString(msg);
                    //SALVAR EM UM ARRAY.
                    stepTwoType(two);
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }*/

                //msg = new String(Session.security.decriptografaSimetrica(msg.getBytes(), keySessao));
                //TODO

                /*try {
                    //EndRound endRound = EndRound.convertFromString(Session.security.desbrincar(msg));
                    EndRound endRound = EndRound.convertFromString((msg));
                    endRoundType(endRound);
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }*/
 /*try {
                    //RoundDataToValidate roundDataValidate = RoundDataToValidate.convertFromString(Session.security.desbrincar(msg));
                    RoundDataToValidate roundDataValidate = RoundDataToValidate.convertFromString((msg));
                    roundDataToValidateType(roundDataValidate);
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }*/
 /*try {
                    //GameRuntime gameRuntime = GameRuntime.convertFromString(Session.security.desbrincar(msg));
                    GameRuntime gameRuntime = GameRuntime.convertFromString((msg));
                    gameRunType(gameRuntime);
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }*/
 /*try {
                    //EndRoundArray endRoundArray = EndRoundArray.convertFromStringArray(Session.security.desbrincar(msg));
                    EndRoundArray endRoundArray = EndRoundArray.convertFromStringArray((msg));
                    arrayListEndRound(endRoundArray.array);
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }*/
 /*try {
                    //UserArray userArray = UserArray.convertFromStringArray(Session.security.desbrincar(msg));
                    UserArray userArray = UserArray.convertFromStringArray((msg));
                    arrayListUser(userArray.array);
                    return;
                } catch (DataLengthException ex) {
                } catch (Exception ex) {
                    //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            } catch (IOException ex) {
                //Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                Session.canShowMainMenuByConnectionError = true;
            }
        }
    }
}
