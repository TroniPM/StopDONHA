package ui;

import entidades.network.DataNetworkManager;
import entidades.network.sendible.User;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import javax.crypto.SecretKey;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import security.ChaveSessao;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class HighScore extends javax.swing.JPanel {

    private Timer waitingStartGame = new Timer();

    public void canStartGameThreadCheck() {
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                if (Session.canStartGame) {
                    Session.canStartGame = false;
                    Session.JFramePrincipal.changeScreen(new GameScreen());

                }
            }
        };
        waitingStartGame.scheduleAtFixedRate(tarefa, 0, 300);
    }

    public void fillTableServer() {
        Object columnNames[] = {"Posição", "Nome", "Pontuação"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        ArrayList<User> userAux = new ArrayList<>();
        for (int i = 0; i < Session.gRunTime.usuariosConectados.size(); i++) {
            User a = new User(Session.gRunTime.usuariosConectados.get(i).nickname, Session.gRunTime.usuariosConectados.get(i).pontuacao);
            userAux.add(a);
        }

        ordenarUsuariosPorPontuacaoDecrescente(userAux);
        for (int i = 0; i < userAux.size(); i++) {
            //System.out.println("------>" + userAux.get(i).nickname);
            Object rowData[] = {i + 1, userAux.get(i).nickname, userAux.get(i).pontuacao};
            model.addRow(rowData);
        }

        jTable1.setModel(model);
    }

    public void fillTableClient() {
        /*Como no cliente, ao receber o array, ele vai entrar na última posição do DataNetworkManager.respostasRecebidasValidated, 
         a pontuação verdadeira de cada USER vai estar na última posição. Isso vai ser corrigido no "new GameScreen()", q recebe
         um object GameRuntime, com a pontuação correta no objeto "Session.gRunTime.pontuacaoDoRound".
         */
        Object columnNames[] = {"Posição", "Nome", "Pontuação"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        ArrayList<User> userAux = (ArrayList<User>) DataNetworkManager.respostasRecebidasValidated.get(DataNetworkManager.respostasRecebidasValidated.size() - 1);

        ordenarUsuariosPorPontuacaoDecrescente(userAux);
        for (int i = 0; i < userAux.size(); i++) {
            //System.out.println("------>" + userAux.get(i).nickname);
            Object rowData[] = {i + 1, userAux.get(i).nickname, userAux.get(i).pontuacao};
            model.addRow(rowData);
        }

        jTable1.setModel(model);
    }

    public void ordenarUsuariosPorPontuacaoDecrescente(ArrayList<User> user) {
        Collections.sort(user, new ComparadorPontuacaoDecrescente());
    }

    private class ComparadorPontuacaoDecrescente implements Comparator<User> {

        @Override
        public int compare(User o1, User o2) {
            return (o1.pontuacao < o2.pontuacao) ? 1 : ((o1.pontuacao > o2.pontuacao) ? -1 : 0);
        }
    }

    public void recomecar() {
        if (Session.gRunTime.currentRound >= Session.gRunTime.qntdRounds) {
            Session.conexaoCliente.closeAndCleanAllData();
            Session.conexaoServidor.closeAndCleanAllData();
            Session.JFramePrincipal.changeScreen(new MainMenu());
        } else {
            Session.gRunTime.currentLetra = GameScreenConfigGame.selectLetraRandom();
            Session.gRunTime.currentRound += 1;

            startGame();
        }
    }

    private void startGame() {
        final String[] ips = Session.conexaoServidor.getIpsNetwork();
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

            //Jogo processamento pra uma thread auxiliar.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Session.conexaoCliente.sv_communicateStartRound(ip, Session.gRunTime,
                            autenticacao, encriptacao);
                }
            }).start();
        }
        Session.JFramePrincipal.changeScreen(new GameScreen());
    }

    public HighScore() {
        initComponents();
        Session.JFramePrincipal.changeTitleText("Pontuação");

        if (Session.isServidor) {

            if (Session.gRunTime.currentRound < Session.gRunTime.qntdRounds) {
                jButton1.setVisible(true);
            } else {
                jButton1.setVisible(false);
            }

            fillTableServer();

        } else {
            //IF se esse foi o último, não fazer nada. Deixar a tela morta.
            if (Session.gRunTime.currentRound < Session.gRunTime.qntdRounds) {
                canStartGameThreadCheck();
                Session.conexaoServidor.ListeningStartGame();
            }
            jButton1.setVisible(false);

            fillTableClient();
        }

        Session.addLog("HighScore() CLASS");
        for (int i = 0; i < Session.gRunTime.usuariosConectados.size(); i++) {
            Session.addLog("NICK: " + Session.gRunTime.usuariosConectados.get(i).nickname);
            Session.addLog("PONTUAÇÃO: " + Session.gRunTime.usuariosConectados.get(i).pontuacao);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(400, 300));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Bella Donna", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 204));
        jLabel1.setText("HighScores");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, -1, -1));

        jButton1.setText("Próximo ROUND");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 437, -1, 40));

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
        jScrollPane2.setViewportView(jTable1);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 130, 390, 250));

        jButton2.setText("Sair do JOGO");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 437, -1, 40));

        jButton3.setText("LOGS");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 450, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/tela.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -20, -1, 550));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        recomecar();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Session.clearAllData();

        Session.JFramePrincipal.changeScreen(new MainMenu());
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
