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
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class SalaDeEspera extends javax.swing.JPanel {

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
                /*System.out.println(
                 "Player qntd: " + playersWaiting.size()
                 + " || networkClientsSockets player: " + Session.conexaoServidor.networkClientsSockets.size());*/
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
                        System.out.println("Esperando inserção no array...");
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

    public SalaDeEspera(boolean isMaster) {
        initComponents();
        Session.JFramePrincipal.changeTitleText("Sala de Espera");

        jButton3.setVisible(false);
        jButton3.setEnabled(false);

        System.out.println(isMaster);
        if (!isMaster) {
            Session.isServidor = false;
            this.jLabel1.setText("Sala de Espera em " + Session.masterIP + ". Aguardando o início do jogo...");
            jScrollPane1.setEnabled(false);
            jScrollPane1.setVisible(false);
            this.remove(jScrollPane1);

            jButton1.setVisible(false);
            jButton1.setEnabled(false);

            try {
                Session.conexaoCliente.communicateWaitingRoom();
                Session.conexaoServidor.ListeningStartGame();
                canStartGameThreadCheck();
            } catch (IOException ex) {
                jLabel1.setText("Erro: " + ex.getLocalizedMessage());
                Session.JFramePrincipal.changeScreen(new MainMenu());

            }

        } else {
            Session.isServidor = true;
            try {
                Session.masterIP = Inet4Address.getLocalHost().getHostAddress();

                Session.conexaoServidor.ListeningWaitingRoom();
                try {
                    Session.conexaoCliente.communicateWaitingRoom();
                } catch (IOException ex) {
                    Session.clearAllData();
                    cancelAllThreads();

                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
                    Session.JFramePrincipal.changeScreen(new MainMenu());

                }
                this.jLabel1.setText("Sala de Espera criada. Seu IP local é: " + Session.masterIP);

            } catch (UnknownHostException ex) {
                Logger.getLogger(SalaDeEspera.class
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(400, 300));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Sala de Espera");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, -1, -1));

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

        jButton3.setText("PING");
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

    private void startGame() {
        String[] ips = Session.conexaoServidor.getIpsNetwork();
        for (int i = 0; i < ips.length; i++) {
            User user = new User();
            user.nickname = Session.gRunTime.nicknamesNetwork.get(i);
            user.ip = ips[i];

            //Inicio o array com todos os nicks e pontuações
            Session.gRunTime.pontuacaoDoRound.add(user);
        }

        for (int i = 0; i < ips.length; i++) {
            if (ips[i].equals(Session.masterIP)) {
                continue;
            }
            //Envio pra cada cliente o OBJECT
            Session.conexaoCliente.sv_communicateStartGame(ips[i], Session.gRunTime);
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
        pingar();
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
