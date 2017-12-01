package ui;

import java.util.Timer;
import java.util.TimerTask;
import entidades.network.DataNetworkManager;
import java.awt.Dimension;
import javax.crypto.SecretKey;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import security.ChaveSessao;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class GameScreenWaitingRoomPreValidation extends javax.swing.JPanel {

    public String txt = "Aguardando os outros jogadores...";
    private Timer handler = new Timer(), handlerChecker = new Timer();

    public void threadChecker() {
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                jLabelTempo.setText(GameScreen.cronometro.currentTempo);

                if (Session.isServidor) {
                    jLabelJogadoresRestantes.setText(
                            String.valueOf(
                                    Session.conexaoServidor.networkClientsSockets.size() - DataNetworkManager.respostasRecebidasDoRound.size())
                            + " de " + Session.conexaoServidor.networkClientsSockets.size());
                    if (GameScreen.cronometro.currentTempo.equals("fim")) {
                        startValidationScheme();
                        cancelAllThreads();

                    } else if (DataNetworkManager.respostasRecebidasDoRound.size()
                            == Session.conexaoServidor.networkClientsSockets.size()) {
                        startValidationScheme();
                        cancelAllThreads();
                    }
                } else if (Session.canValidateRespostas) {
                    Session.canValidateRespostas = false;
                    Session.JFramePrincipal.changeScreen(new GameScreenValidation());

                    cancelAllThreads();
                } else if (Session.canShowHighScores) {
                    Session.canShowHighScores = false;
                    Session.JFramePrincipal.changeScreen(new HighScore());
                    cancelAllThreads();
                } else if (Session.canStartGame) {
                    Session.canStartGame = false;
                    Session.JFramePrincipal.changeScreen(new GameScreen());
                    cancelAllThreads();
                }
            }
        };
        handlerChecker.scheduleAtFixedRate(tarefa, 0, 100);
    }

    private void cancelAllThreads() {
        GameScreen.cronometro.stopCronometro();

        try {
            GameScreen.handler.cancel();
        } catch (Exception e) {
            e.getStackTrace();
        }
        try {
            if (handler != null) {
                handler.cancel();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        try {
            if (handlerChecker != null) {
                handlerChecker.cancel();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void startValidationScheme() {
        if (Session.isServidor) {
            DataNetworkManager.canOverrideMainArray = false;

            String[] ips = Session.conexaoServidor.getIpsNetwork();
            for (int i = 0; i < ips.length; i++) {
                //Não deixo enviar para o mesmo ip da máquina servidor.
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
                        Session.conexaoCliente.sv_communicateStartValidation(ip,
                                DataNetworkManager.respostasRecebidasDoRound,
                                autenticacao, encriptacao);
                    }
                }).start();
            }
            Session.JFramePrincipal.changeScreen(new GameScreenValidation());
        }
    }

    public GameScreenWaitingRoomPreValidation() {
        initComponents();
        Session.JFramePrincipal.changeTitleText("Aguardando Jogadores");
        threadChecker();

        if (Session.isServidor) {
            jButton2.setVisible(true);
            jButton2.setEnabled(true);
            jLabel2.setVisible(true);
            jLabel4.setVisible(true);
            jLabelJogadoresRestantes.setVisible(true);
        } else {
            jButton2.setVisible(false);
            jButton2.setEnabled(false);
            jLabel2.setVisible(false);
            jLabel4.setVisible(false);
            jLabelJogadoresRestantes.setVisible(false);
        }
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelTempo = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabelJogadoresRestantes = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText(txt);
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 519, -1));

        jButton1.setText("Sair do JOGO");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 440, -1, -1));

        jButton2.setText("Forçar fim do ROUND");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 273, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("(apenas para o Criador da sala)");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 275, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Tempo restante:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, -1, -1));

        jLabelTempo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelTempo.setForeground(new java.awt.Color(0, 0, 204));
        jLabelTempo.setText("jLabel4");
        add(jLabelTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 105, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Jogadores restantes:");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 80, -1, -1));

        jLabelJogadoresRestantes.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelJogadoresRestantes.setForeground(new java.awt.Color(255, 0, 0));
        jLabelJogadoresRestantes.setText("jLabel5");
        add(jLabelJogadoresRestantes, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 105, -1, -1));

        jButton3.setText("LOGS");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 440, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/tela.png"))); // NOI18N
        jLabel5.setText("jLabel5");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -20, 620, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Session.clearAllData();
        Session.JFramePrincipal.changeScreen(new MainMenu());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        startValidationScheme();
    }//GEN-LAST:event_jButton2ActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelJogadoresRestantes;
    private javax.swing.JLabel jLabelTempo;
    // End of variables declaration//GEN-END:variables
}
