package ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class GameScreenConfigGame extends javax.swing.JPanel {

    public List<JRadioButton> buttons = new ArrayList<>();

    /**
     * Creates new form GameScreenConfigGame
     */
    public GameScreenConfigGame() {
        initComponents();
        Session.JFramePrincipal.changeTitleText("Configurando Partida");
        buttons.add(jRadioButton1);
        buttons.add(jRadioButton2);
        buttons.add(jRadioButton3);
        buttons.add(jRadioButton4);
        buttons.add(jRadioButton5);
        buttons.add(jRadioButton6);
        buttons.add(jRadioButton7);
        buttons.add(jRadioButton8);
        buttons.add(jRadioButton9);
        buttons.add(jRadioButton10);
        buttons.add(jRadioButton11);
        buttons.add(jRadioButton12);
        buttons.add(jRadioButton13);
        buttons.add(jRadioButton14);
        buttons.add(jRadioButton15);
        buttons.add(jRadioButton16);
        buttons.add(jRadioButton17);
        buttons.add(jRadioButton18);
        buttons.add(jRadioButton19);
        buttons.add(jRadioButton20);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton21 = new javax.swing.JRadioButton();
        jRadioButton22 = new javax.swing.JRadioButton();
        jRadioButton23 = new javax.swing.JRadioButton();
        jRadioButton24 = new javax.swing.JRadioButton();
        jRadioButton26 = new javax.swing.JRadioButton();
        jRadioButton27 = new javax.swing.JRadioButton();
        jRadioButton28 = new javax.swing.JRadioButton();
        jRadioButton29 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jRadioButton13 = new javax.swing.JRadioButton();
        jRadioButton14 = new javax.swing.JRadioButton();
        jRadioButton15 = new javax.swing.JRadioButton();
        jRadioButton16 = new javax.swing.JRadioButton();
        jRadioButton17 = new javax.swing.JRadioButton();
        jRadioButton18 = new javax.swing.JRadioButton();
        jRadioButton19 = new javax.swing.JRadioButton();
        jRadioButton20 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(620, 520));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Seu nickname:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Quantidade de Rounds:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Tempo por Round:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Opções:");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 190, -1, -1));

        jRadioButton1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("País");
        add(jRadioButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 215, -1, -1));

        jRadioButton2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("Cidade");
        add(jRadioButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 245, -1, -1));

        jRadioButton3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton3.setSelected(true);
        jRadioButton3.setText("Estado");
        add(jRadioButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 275, -1, -1));

        jRadioButton4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton4.setSelected(true);
        jRadioButton4.setText("Comida");
        add(jRadioButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 305, -1, -1));

        jRadioButton5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton5.setSelected(true);
        jRadioButton5.setText("Objeto");
        add(jRadioButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 335, -1, -1));

        buttonGroup2.add(jRadioButton21);
        jRadioButton21.setText("300s");
        add(jRadioButton21, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 135, -1, -1));

        buttonGroup2.add(jRadioButton22);
        jRadioButton22.setText("240s");
        add(jRadioButton22, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 135, -1, -1));

        buttonGroup2.add(jRadioButton23);
        jRadioButton23.setText("180s");
        add(jRadioButton23, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 135, -1, -1));

        buttonGroup2.add(jRadioButton24);
        jRadioButton24.setSelected(true);
        jRadioButton24.setText("120s");
        add(jRadioButton24, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 135, -1, -1));

        buttonGroup2.add(jRadioButton26);
        jRadioButton26.setText("60s");
        add(jRadioButton26, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 135, -1, -1));

        buttonGroup1.add(jRadioButton27);
        jRadioButton27.setSelected(true);
        jRadioButton27.setText("5");
        add(jRadioButton27, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 80, -1, -1));

        buttonGroup1.add(jRadioButton28);
        jRadioButton28.setText("10");
        add(jRadioButton28, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, -1, -1));

        buttonGroup1.add(jRadioButton29);
        jRadioButton29.setText("15");
        add(jRadioButton29, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 80, -1, -1));

        jRadioButton6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton6.setText("Automóvel");
        add(jRadioButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 215, -1, -1));

        jRadioButton7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton7.setText("Ator/Atriz");
        add(jRadioButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 245, -1, -1));

        jRadioButton8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton8.setText("Filme");
        add(jRadioButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 275, -1, -1));

        jRadioButton9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton9.setText("Cor");
        add(jRadioButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 305, -1, -1));

        jRadioButton10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton10.setText("Jogo");
        add(jRadioButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 335, -1, -1));

        jRadioButton11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton11.setText("Profissão");
        add(jRadioButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 215, -1, -1));

        jRadioButton12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton12.setText("Fruta");
        add(jRadioButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 245, -1, -1));

        jRadioButton13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton13.setText("Música");
        add(jRadioButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 275, -1, -1));

        jRadioButton14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton14.setText("Esporte");
        add(jRadioButton14, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 305, -1, -1));

        jRadioButton15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton15.setText("Marca");
        add(jRadioButton15, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 335, -1, -1));

        jRadioButton16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton16.setText("Animal");
        add(jRadioButton16, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 215, -1, -1));

        jRadioButton17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton17.setText("Cantor(a)");
        add(jRadioButton17, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 245, -1, -1));

        jRadioButton18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton18.setText("Novela");
        add(jRadioButton18, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 275, -1, -1));

        jRadioButton19.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton19.setText("Time");
        add(jRadioButton19, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 305, -1, -1));

        jRadioButton20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRadioButton20.setText("Medicamento");
        add(jRadioButton20, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 335, -1, -1));

        jButton1.setText("Voltar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 410, 80, 40));

        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextField1.setText(Session.nickname);
        add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, 150, -1));

        jButton2.setText("Criar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 410, 80, 40));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/tela.png"))); // NOI18N
        jLabel5.setText("jLabel5");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -20, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Session.JFramePrincipal.changeScreen(new MainMenu());

    }//GEN-LAST:event_jButton1ActionPerformed

    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    public static String selectLetraRandom() {
        //instância um objeto da classe Random especificando a semente 
        Random gerador = new Random();
        //imprime sequência de 10 números inteiros aleatórios entre 0 e 25
        int index = 0;
        index = gerador.nextInt(26);

        String[] alfabeto = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "x", "w", "y", "z"};

        return alfabeto[index];
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Session.nickname = jTextField1.getText();
        Session.gRunTime.currentNickname = Session.nickname;
        Session.gRunTime.qntdRounds = Integer.parseInt(getSelectedButtonText(buttonGroup1));
        Session.gRunTime.tempoPorRound = Integer.parseInt(getSelectedButtonText(buttonGroup2).replace("s", ""));
        Session.gRunTime.currentLetra = selectLetraRandom();

        boolean ctrl = false;
        for (JRadioButton checkbox : buttons) {
            if (checkbox.isSelected()) {
                ctrl = true;
                Session.gRunTime.itens.add(checkbox.getText());
            }
        }
        if (ctrl) {
            Session.JFramePrincipal.changeScreen(new SalaDeEspera(true));
        } else{
            JOptionPane.showMessageDialog(this, "Selecione pelo menos UM tema.");
        }

    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton13;
    private javax.swing.JRadioButton jRadioButton14;
    private javax.swing.JRadioButton jRadioButton15;
    private javax.swing.JRadioButton jRadioButton16;
    private javax.swing.JRadioButton jRadioButton17;
    private javax.swing.JRadioButton jRadioButton18;
    private javax.swing.JRadioButton jRadioButton19;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton20;
    private javax.swing.JRadioButton jRadioButton21;
    private javax.swing.JRadioButton jRadioButton22;
    private javax.swing.JRadioButton jRadioButton23;
    private javax.swing.JRadioButton jRadioButton24;
    private javax.swing.JRadioButton jRadioButton26;
    private javax.swing.JRadioButton jRadioButton27;
    private javax.swing.JRadioButton jRadioButton28;
    private javax.swing.JRadioButton jRadioButton29;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}