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
            this.jLabel3.setText(Methods.getAvaliableIps());
            //this.jLabel1.setText("Sala de Espera em " + Session.masterIP + ". Aguardando o início do jogo...");
            jScrollPane1.setEnabled(false);
            jScrollPane1.setVisible(false);
            this.remove(jScrollPane1);

            jButton1.setVisible(false);
            jButton1.setEnabled(false);

            try {
                /**
                 * Processo de troca de chaves: 1*- Inicialmente o Cliente envia
                 * sua chave de sessão/publica para o servidor. 2*- Quando o
                 * servidor recebe essas chaves, envia as suas chave de
                 * sessão/pública para o determinado ip. 3*- O cliente irá
                 * receber essas chaves. 4*- Quando o cliente receber essas
                 * chaves, irá entrar na sala, enviando os dados necessário.
                 * APÓS O 3*, TODOS OS DADOS ENVIADOS SÃO CRITOGRAFADOS. Ao
                 * Cliente enviar para o servidor alguma informação, é
                 * criptografado com sua própria chave de sessão (o servidor ao
                 * receber esta informação, usará a chave de sessão (1*) para
                 * desencriptar os dados e a chave pública para verificar a
                 * assinatura e só então utilizará os dados). De igual forma, ao
                 * receber dados do servidor, o cliente desencriptará os dados
                 * (2*) com a chave de sessão do servidor e verificar a
                 * assinatura com a chave pública do servidor, para então, se
                 * tudo ok, utilizar os dados.
                 *
                 */

                //Inicialmente envio para o servidor a chave de sessão e pública.
                //Session.conexaoCliente.communicateStepTwo();
                //Recebo chave publica e de sessão do servidor  dentro faço o envio da classe user
                //Session.conexaoServidor.ListeningStepOne();
                //Espero o servidor iniciar o jogo.
                Session.security.KEY = new ChaveSessao(true);
                Session.conexaoCliente.startScheme();
                Session.conexaoCliente.communicateWaitingRoomEnter();
                Session.conexaoServidor.ListeningStartGame();
                canStartGameThreadCheck();
            } catch (Exception ex) {
                jLabel1.setText("Erro: " + ex.getLocalizedMessage());
                Session.JFramePrincipal.changeScreen(new MainMenu());

            }

        } else {
            Session.isServidor = true;
            try {
                Session.masterIP = Inet4Address.getLocalHost().getHostAddress();

                Session.conexaoServidor.ListeningWaitingRoom();
                try {
                    //Crio a chave de sessão
                    Session.security.KEY = new ChaveSessao(true);
                    Session.conexaoCliente.startScheme();
                    /**
                     *
                     */
                    //Session.conexaoCliente.communicateWaitingRoom();
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
                System.out.println(in.ip + " <><><> " + ips[i]);
                if (in.ip.equals(ips[i])) {
                    cs = in;
                }
            }
            System.out.println("ENVIANDO GAMERUNTIME");
            System.out.println("#########################" + "\n" + cs);
            //Envio pra cada cliente o OBJECT
            //Session.conexaoCliente.sv_communicateStartGame(ips[i], Session.gRunTime);
            Session.conexaoCliente.communicateStartRound(ips[i], Session.gRunTime,
                    cs.AUTENTICACAO_SERVIDOR, cs.ENCRIPTACAO_SERVIDOR);
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
        System.out.println("PING()");
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
