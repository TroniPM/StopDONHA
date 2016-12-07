package ui;

import entidades.Cronometro;
import java.awt.event.KeyEvent;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import entidades.network.DataNetworkManager;
import entidades.network.sendible.EndRound;
import entidades.network.sendible.RoundDataToValidate;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class GameScreen extends javax.swing.JPanel {

    public static Cronometro cronometro;
    public static Timer handler;
    private int currentIndexViewing = 0;
    private ArrayList<String> respostas;

    public static ArrayList<Boolean> waitingToFinishRound = new ArrayList<>();

    public void initCronometro() {
        handler = new Timer();
        cronometro.cronometro();

        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                jLabelCronometro.setText(cronometro.currentTempo);
                if (cronometro.currentTempo.equals("fim")) {
                    finishRound();

                    handler.cancel();
                }

                if (!Session.isServidor) {
                    if (Session.canValidateRespostas) {
                        Session.canValidateRespostas = false;
                        Session.JFramePrincipal.changeScreen(new GameScreenValidation());
                        handler.cancel();
                    } else if (Session.canShowHighScores) {
                        Session.canShowHighScores = false;
                        Session.JFramePrincipal.changeScreen(new HighScore());
                        handler.cancel();
                    } else if (Session.canStartGame) {
                        Session.canStartGame = false;
                        Session.JFramePrincipal.changeScreen(new GameScreen());

                    }
                }
            }
        };
        handler.scheduleAtFixedRate(tarefa, 0, 100);
    }

    public void setViewingData(int toWhere) {
        respostas.set(currentIndexViewing, jTextArea1.getText());

        currentIndexViewing += toWhere;
        if (currentIndexViewing < 0) {
            currentIndexViewing = 0;
        } else if (currentIndexViewing > Session.gRunTime.itens.size() - 1) {
            currentIndexViewing = Session.gRunTime.itens.size() - 1;
        }

        jLabelObjeto.setText(Session.gRunTime.itens.get(currentIndexViewing));
        jTextArea1.setText(respostas.get(currentIndexViewing));

        jButtonAnt.setEnabled(true);
        jButtonProx.setEnabled(true);
        if (currentIndexViewing == 0) {
            jButtonAnt.setEnabled(false);
        } else if (currentIndexViewing >= Session.gRunTime.itens.size() - 1) {
            jButtonProx.setEnabled(false);
        }
    }

    public void initArrays() {
        respostas = new ArrayList<>();
        for (int i = 0; i < Session.gRunTime.itens.size(); i++) {
            respostas.add("");
        }
        /*for (int i = 0; i < Session.gRunTime.nicknamesNetwork.size(); i++) {
         User user = new User();
         user.nickname = Session.gRunTime.nicknamesNetwork.get(i);
         try {
         user.ip = Session.conexaoServidor.networkClientsSockets.get(i).getInetAddress().getHostAddress();
         } catch (Exception e) {
         user.ip = "";
         }
         //Session.gRunTime.pontuacaoDoRound.add(new User());
         }*/

    }

    public int findPontuationByNickname() {
        System.out.println("findPontuationByNickname()");
        int i;
        for (i = 0; i < Session.gRunTime.pontuacaoDoRound.size(); i++) {
            if (Session.nickname.equals(Session.gRunTime.pontuacaoDoRound.get(i).nickname)) {
                System.out.println("nickname: " + Session.nickname + " | pontuação: " + Session.gRunTime.pontuacaoDoRound.get(i).pontuacao);
                return Session.gRunTime.pontuacaoDoRound.get(i).pontuacao;
            }
        }
        return 0;
    }

    public GameScreen() {
        DataNetworkManager.cleanAllData();

        cronometro = new Cronometro(Session.gRunTime.tempoPorRound / 60, Cronometro.REGRESSIVA);
        initComponents();

        Session.JFramePrincipal.changeTitleText("Game");
        initCronometro();
        initArrays();

        setViewingData(0);

        jLabelPontuacao.setText(String.valueOf(findPontuationByNickname()));
        jLabelRodada.setText(String.valueOf(Session.gRunTime.currentRound));
        jLabelLetra.setText(Session.gRunTime.currentLetra.toUpperCase());

        //Listener para saber se todos jogadores acabaram
        if (Session.isServidor) {
            String[] ips = Session.conexaoServidor.getIpsNetwork();
            for (String ip : ips) {
                waitingToFinishRound.add(Boolean.FALSE);
            }

            //Litener para receber respostas de jogadores
            Session.conexaoServidor.ListeningEndRoundToValidate();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelPontuacao = new javax.swing.JLabel();
        jLabelCronometro = new javax.swing.JLabel();
        jLabelRodada = new javax.swing.JLabel();
        jLabelObjeto = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButtonProx = new javax.swing.JButton();
        jButtonAnt = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabelLetra = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Relógio:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 20, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Pontuação:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Rodada:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        jLabelPontuacao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelPontuacao.setForeground(new java.awt.Color(255, 0, 0));
        jLabelPontuacao.setText("99999");
        add(jLabelPontuacao, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 50, -1, -1));

        jLabelCronometro.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelCronometro.setForeground(new java.awt.Color(0, 0, 204));
        jLabelCronometro.setText("99:99");
        add(jLabelCronometro, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 50, -1, -1));

        jLabelRodada.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelRodada.setForeground(new java.awt.Color(51, 102, 0));
        jLabelRodada.setText("10");
        add(jLabelRodada, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 50, -1, -1));

        jLabelObjeto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelObjeto.setText("ITEM");
        add(jLabelObjeto, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 220, -1, -1));

        jButton1.setText("Sair do JOGO");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 470, -1, -1));

        jButtonProx.setText("Próximo");
        jButtonProx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProxActionPerformed(evt);
            }
        });
        add(jButtonProx, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 356, -1, 50));

        jButtonAnt.setText("Anterior");
        jButtonAnt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAntActionPerformed(evt);
            }
        });
        add(jButtonAnt, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 356, -1, 50));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextArea1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 244, 280, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Letra:");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 130, -1, -1));

        jLabelLetra.setFont(new java.awt.Font("Tahoma", 0, 125)); // NOI18N
        jLabelLetra.setForeground(new java.awt.Color(102, 102, 102));
        jLabelLetra.setText("Z");
        jLabelLetra.setToolTipText(Session.gRunTime.currentLetra.toUpperCase());
        add(jLabelLetra, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 70, -1, -1));

        jButton2.setText("Finalizar ROUND");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 470, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/tela.png"))); // NOI18N
        jLabel5.setText("jLabel5");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -20, 620, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Session.clearAllData();

        cronometro = null;
        handler.cancel();
        handler = null;

        Session.JFramePrincipal.changeScreen(new MainMenu());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButtonAntActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAntActionPerformed
        setViewingData(-1);
    }//GEN-LAST:event_jButtonAntActionPerformed

    private void jButtonProxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProxActionPerformed
        setViewingData(+1);
    }//GEN-LAST:event_jButtonProxActionPerformed

    private RoundDataToValidate arrayObjectToRoundInoValidate(ArrayList<String> array) {
        RoundDataToValidate round = new RoundDataToValidate();
        round.respostas = array;
        for (int i = 0; i < array.size(); i++) {
            String aux = array.get(i).toLowerCase();
            if (array.get(i).equals("")) {
                round.respostasAceitacao.add(Boolean.FALSE);
                continue;
            } else if (!aux.startsWith(Session.gRunTime.currentLetra)) {
                round.respostasAceitacao.add(Boolean.FALSE);
                continue;
            }
            round.respostasAceitacao.add(Boolean.TRUE);
        }

        return round;
    }

    private void finishRound() {
        setViewingData(0);

        EndRound fimDoRound = new EndRound();
        fimDoRound.nickname = Session.nickname;
        try {
            fimDoRound.ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(GameScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        fimDoRound.objResposta = arrayObjectToRoundInoValidate(respostas);

        if (Session.isServidor) {
            DataNetworkManager.respostasRecebidasDoRound.add(fimDoRound);
        } else {
            Session.conexaoCliente.communicateEndRoundToValidate(fimDoRound);
        }
        Session.JFramePrincipal.changeScreen(new GameScreenWaitingRoomPreValidation());
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        finishRound();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextArea1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            finishRound();
        } else if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            setViewingData(+1);
            //Isso faz com que ao dar tab, não dê espaço.
            evt.consume();
        } else if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
            setViewingData(-1);
        }

    }//GEN-LAST:event_jTextArea1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonAnt;
    private javax.swing.JButton jButtonProx;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelCronometro;
    private javax.swing.JLabel jLabelLetra;
    private javax.swing.JLabel jLabelObjeto;
    private javax.swing.JLabel jLabelPontuacao;
    private javax.swing.JLabel jLabelRodada;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}