package ui;

import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class MainMenu extends javax.swing.JPanel {

    private Timer threadListeningConnectionKill = new Timer();

    /*Thread que vai rodar o programa inteiro. Se houver mudança dessa variável, 
     vai entrar no if, limpar todos os dados e voltar a tela inicial.*/
    public void canStartGameThreadCheck() {
        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                if (Session.canShowMainMenuByConnectionError) {
                    Session.canShowMainMenuByConnectionError = false;

                    Session.clearAllData();

                    Session.JFramePrincipal.changeScreen(new MainMenu());
                }
            }
        };
        threadListeningConnectionKill.scheduleAtFixedRate(tarefa, 0, 500);
    }

    public MainMenu() {
        Session.clearAllData();

        initComponents();

        Session.JFramePrincipal.changeTitleText("Inicio");

        canStartGameThreadCheck();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setToolTipText("");
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/entrar.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 260, 50, -1));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/como.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 390, 50, -1));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/sobre.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 390, 50, -1));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/sair1.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(547, 460, 50, -1));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/jogar.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 260, 50, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/Fundo.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        jLabel2.setFocusable(false);
        jLabel2.setInheritsPopupMenu(false);
        jLabel2.setRequestFocusEnabled(false);
        jLabel2.setVerifyInputWhenFocusTarget(false);
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 620, 520));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Session.JFramePrincipal.changeScreen(new Sobre());
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Session.JFramePrincipal.changeScreen(new ComoJogar());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        Session.JFramePrincipal.changeScreen(new GameScreenConfigGame());
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        Session.gRunTime.currentNickname = JOptionPane.showInputDialog(this, "Digite seu nickname:", Session.nickname);
        if (Session.gRunTime.currentNickname != null) {
            Session.nickname = Session.gRunTime.currentNickname;
            String ip = "";
            ip = JOptionPane.showInputDialog(this, "Digite o ip da Sala:", "192.168.0.104");
            if (ip == null) {
                JOptionPane.showMessageDialog(this, "IP inválido.");
            } else {
                Session.masterIP = ip;
                Session.JFramePrincipal.changeScreen(new SalaDeEspera(false));
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}