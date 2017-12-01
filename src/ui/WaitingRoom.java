package ui;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import entidades.network.Servidor;
import entidades.network.sendible.User;
import java.awt.Dimension;
import javax.crypto.SecretKey;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import security.ChaveSessao;
import util.Methods;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class WaitingRoom extends javax.swing.JPanel {

    private Object columnNames[] = {"IP", "Nickname"};
    public DefaultTableModel modelJtable = new DefaultTableModel(columnNames, 0);

    private Timer waitingPlayers = new Timer();
    private Timer waitingStartGame = new Timer();
    private ArrayList<String> playersWaiting = new ArrayList<>();
    private int lastSize = 0;

    public void fillTable(Object[] rowData) {
        //Object rowData[] = {arrayPontuacao[i].posicao, arrayPontuacao[i].nome, arrayPontuacao[i].pontuacao};
        modelJtable.addRow(rowData);
        jTable1.setModel(modelJtable);
    }

    public void waitingPlayersThreadCheck() {
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                if (playersWaiting.size() > 0 && playersWaiting.size() < Session.conexaoServidor.networkClientsSockets.size()
                        && Session.conexaoServidor.networkClientsSockets.size() == Session.gRunTime.nicknamesNetwork.size()) {
                    for (int i = playersWaiting.size(); i < Session.conexaoServidor.networkClientsSockets.size(); i++) {
                        Object rowData[] = {
                            Session.conexaoServidor.networkClientsSockets.get(i).getInetAddress().getHostAddress(),
                            Session.gRunTime.nicknamesNetwork.get(i)};

                        playersWaiting.add(String.valueOf(rowData[0]));

                        fillTable(rowData);
                    }
                } else if (playersWaiting.size() == 0) {
                    String a1, a2;
                    Object rowData[] = null;
                    try {
                        a1 = Session.conexaoServidor.networkClientsSockets.get(0).getInetAddress().getHostAddress();
                        a2 = Session.gRunTime.nicknamesNetwork.get(0);
                        rowData = new Object[]{a1, a2};
                    } catch (Exception e) {
                        return;
                    }

                    playersWaiting.add(String.valueOf(rowData[0]));
                    fillTable(rowData);
                }
            }
        };
        waitingPlayers.scheduleAtFixedRate(tarefa,
                0, 300);
    }

    public void canStartGameThreadCheck() {
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                if (Session.canStartGame) {
                    Session.canStartGame = false;
                    startGame();

                }
            }
        };
        waitingStartGame.scheduleAtFixedRate(tarefa, 0, 500);
    }

    public void cancelThreadWaitingPlayers() {
        if (waitingPlayers != null) {
            waitingPlayers.cancel();
        }
    }

    public void cancelThreadStartGame() {
        if (waitingStartGame != null) {
            waitingStartGame.cancel();
        }
    }

    public WaitingRoom(boolean isMaster) {
        initComponents();
        Session.JFramePrincipal.changeTitleText("Sala de Espera");

        jButton3.setVisible(true);
        jButton3.setEnabled(true);

        if (!isMaster) {
            Session.isServidor = false;
            this.jLabel3.setText(Session.masterIP);
            //this.jLabel1.setText("Sala de Espera em " + Session.masterIP + ". Aguardando o início do jogo...");
            jScrollPane1.setEnabled(false);
            jScrollPane1.setVisible(false);
            this.remove(jScrollPane1);

            jButton1.setVisible(false);
            jButton1.setEnabled(false);

            try {
                /**
                 * Processo de troca de chaves: 1*- Inicialmente o Cliente cria
                 * 4 chaves, uma de autenticação e uma encriptação para o
                 * cliente (ele memso), e para o servidor, uma de autenticação e
                 * uma de encriptação. Avisa ao servidor que vai enviar as
                 * chaves e envia as quatro, uma de cada vez (porque o tamanho
                 * das chaves AES são grandes, e a criptografia assimétrica não
                 * consegue encriptar tudo junto. Logo, encripto/envio uma de
                 * cada vez. Após o servidor receber as quatro chaves, ele avisa
                 * ao cliente que recebeu, e então o cliente já pode enviar os
                 * seus dados para entrar na sala (utilizando suas chaves
                 * simétricas). A partir dai, TODA a comunicação é encriptada
                 * com chave simétrica, do servidor para o cliente (com as
                 * chaves criadas pelo cliente) e do cliente para o servidor
                 * (com as chaves criadas pelo cliente). Vale salientar que CADA
                 * cliente cria 4 chaves. Ou seja, existem 3 clientes A, B e C,
                 * cada um cria 4 chaves (totalizando 12), quando o servidor vai
                 * se comunicar com A, ele usa as chaves criadas por A, quando A
                 * vai se comunicar com o servidor, ele usa as chaves criadas
                 * por A (ele mesmo). Desta forma, mesmo que o servidor envie
                 * vários dados iguais aos clientes, eles só poderão ser
                 * decriptados pelo cliente específico dono daquela chave.
                 *
                 */

                //Criando chaves
                Session.security.KEY = new ChaveSessao(true);
                //Enviando para o servidor as 4 chaves e esperando resposta de recebimento
                Session.conexaoCliente.startScheme();
                //Resposta de envio recebida e enviando dados para entrar na sala.
                Session.conexaoCliente.communicateWaitingRoomEnter();
                //Socket esperando gatilho ser disparado no clienteMAIN
                Session.conexaoServidor.ListeningStartGame();
                //thread que verifica se gatilho foi disparado
                canStartGameThreadCheck();
            } catch (Exception ex) {
                jLabel1.setText("Erro: " + ex.getLocalizedMessage());
                Session.JFramePrincipal.changeScreen(new MainMenu());

            }

        } else {
            Session.isServidor = true;
            try {
                Session.masterIP = Inet4Address.getLocalHost().getHostAddress();

                //Socket aberto esperando as conexões dos clientes
                Session.conexaoServidor.ListeningWaitingRoom();
                try {
                    //Crio a chave de sessão pra ele mesmo
                    Session.security.KEY = new ChaveSessao(true);
                    //Envio para o servidor (ele mesmo) suas chaves e aguardo resposta
                    Session.conexaoCliente.startScheme();
                    //Resposta recebida, envio os dados para entrada na sala (encriptados)
                    Session.conexaoCliente.communicateWaitingRoomEnter();

                } catch (IOException ex) {
                    Session.clearAllData();
                    cancelAllThreads();

                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
                    Session.JFramePrincipal.changeScreen(new MainMenu());

                }
                this.jLabel3.setText("<html>" + Methods.getAvaliableIps() + "</html>");
                //this.jLabel1.setText("Sala de Espera criada. Seu IP local é: " + Session.masterIP);

            } catch (UnknownHostException ex) {
                Logger.getLogger(WaitingRoom.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

            waitingPlayersThreadCheck();
            jButton1.setVisible(true);
            jButton1.setEnabled(true);
        }
        jTable1.setModel(modelJtable);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(400, 300));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Aguardando o início do jogo... Sala de Espera em");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, -1, -1));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("aaaaaaaaaaaa");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 420, 50));

        jTable1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 140, 420, 250));

        jButton1.setText("Jogar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 440, 80, 40));

        jButton2.setText("Sair da SALA");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 440, 120, 40));

        jButton3.setText("LOGS");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 440, -1, 40));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/telaespera.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -20, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Session.clearAllData();
        cancelAllThreads();
        Session.JFramePrincipal.changeScreen(new MainMenu());
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * Mesclar esta esses objetos usuários com os objetos usuário da classe
     * Server.arrayUsuariosComChaves;
     */
    private void startGame() {
        String[] ips = Session.conexaoServidor.getIpsNetwork();
        for (int i = 0; i < ips.length; i++) {
            User user = new User();
            user.nickname = Session.gRunTime.nicknamesNetwork.get(i);
            user.ip = ips[i];

            //Não vou adicionar isso pq não quero que todos os clientes possuam esta informação
            /*for (int j = 0; j < Session.conexaoServidor.arrayUsuariosComChaves.size(); j++) {
                User aux = Session.conexaoServidor.arrayUsuariosComChaves.get(j);
                if (aux.nickname.equals(user.nickname)
                        && aux.ip.equals(user.ip)) {
                    user.chaveSessao = aux.chaveSessao;
                    user.chavePublica = aux.chavePublica;
                }
            }*/
            //Inicio o array com todos os nicks e pontuações
            Session.gRunTime.usuariosConectados.add(user);
        }

        for (int i = 0; i < ips.length; i++) {
            if (ips[i].equals(Session.masterIP)) {
                continue;
            }
            ChaveSessao cs = null;
            for (ChaveSessao in : Session.conexaoServidor.arrayChaveSessao) {
                //System.out.println(in.ip + " <><><> " + ips[i]);
                if (in.ip.equals(ips[i])) {
                    cs = in;
                }
            }
            //System.out.println("ENVIANDO GAMERUNTIME");
            //System.out.println("#########################" + "\n" + cs);

            final String ip = ips[i];
            final SecretKey autenticacao = cs.AUTENTICACAO_SERVIDOR;
            final SecretKey encriptacao = cs.ENCRIPTACAO_SERVIDOR;

            //Envio pra cada cliente o OBJECT de configuração de partida.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Session.conexaoCliente.sv_communicateStartRound(ip, Session.gRunTime,
                            autenticacao, encriptacao);
                }
            }).start();
        }
        cancelAllThreads();
        Session.JFramePrincipal.changeScreen(new GameScreen());
    }

    private void cancelAllThreads() {
        cancelThreadWaitingPlayers();
        cancelThreadStartGame();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        startGame();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        JTextArea textArea = new JTextArea(Session.getLog());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(null, scrollPane, "Logs",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void pingar() {
        //System.out.println("PING()");
        if (Session.conexaoServidor.networkClientsSockets.size() == Session.gRunTime.nicknamesNetwork.size()) {
            for (int i = 0; i < Session.conexaoServidor.networkClientsSockets.size(); i++) {
                Socket aux = Session.conexaoServidor.networkClientsSockets.get(i);
                String ip = aux.getInetAddress().getHostAddress();
                if (ip.equals(Session.masterIP)) {
                    continue;
                }

                try {
                    Socket newSocket = new Socket(ip, Servidor.PORT_CLIENT);
                    System.out.println("socket posição " + i + " isConnected = " + newSocket.isConnected() + " | ip do socket: " + newSocket.getInetAddress().getHostAddress());
                } catch (IOException ex) {
                    System.out.println("Socket -> " + aux.getInetAddress().getHostAddress() + " está FECHADO.");
                    modelJtable.removeRow(i);

                    Session.conexaoServidor.networkClientsSockets.remove(i);
                    Session.gRunTime.nicknamesNetwork.remove(i);

                    jTable1.setModel(modelJtable);
                }
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
