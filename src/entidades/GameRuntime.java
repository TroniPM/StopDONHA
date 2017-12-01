package entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import entidades.network.sendible.User;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class GameRuntime implements Serializable {

    public static final int PONTO_POR_ACERTO = 5;
    public static final int PONTO_BONIFICACAO_POR_VELOCIDADE = 6;//multiplicado pela quantidade de pessoas

    private static final long serialVersionUID = 1L;
    /**
     * itens que serão utilizados no jogo (marca, ator, comida, etc).
     */
    public ArrayList<String> itens = new ArrayList<>();
    /**
     * Pontuação dos jogadores.
     */
    public ArrayList<User> usuariosConectados = new ArrayList<>();
    /**
     * Nomes dos jogadores.
     */
    public ArrayList<String> nicknamesNetwork = new ArrayList<>();

    public int currentRound = 1;
    public String currentLetra = "";
    public int tempoPorRound = 0;
    public int qntdRounds = 0;

    public String currentNickname = "";

    public void cleanAllData() {
        currentRound = 1;
        currentLetra = "";
        tempoPorRound = 0;
        qntdRounds = 0;
        currentNickname = "";

        itens.clear();
        usuariosConectados.clear();
        nicknamesNetwork.clear();
    }

    public void addScore(ArrayList<User> newRound) {
        if (usuariosConectados.size() == 0) {
            usuariosConectados = newRound;
        } else {

            for (int i = 0; i < usuariosConectados.size(); i++) {
                for (int j = 0; j < newRound.size(); j++) {
                    Session.addLog("addScore() \nnome: " + usuariosConectados.get(i).nickname + "==" + newRound.get(j).nickname
                            + "\nip: " + usuariosConectados.get(i).ip + "==" + newRound.get(j).ip);
                    if (usuariosConectados.get(i).nickname.equals(newRound.get(j).nickname)
                            && usuariosConectados.get(i).ip.equals(newRound.get(j).ip)) {
                        User a = usuariosConectados.get(i);
                        a.pontuacao += newRound.get(j).pontuacao;
                        usuariosConectados.set(i, a);
                    }
                }
            }
        }
    }

    public void ordenarUsuariosPorPontuacaoCrescente() {
        Collections.sort(usuariosConectados, new ComparadorPontuacaoCrescente());
    }

    public void ordenarUsuariosPorPontuacaoDecrescente() {
        Collections.sort(usuariosConectados, new ComparadorPontuacaoDecrescente());
    }

    private class ComparadorPontuacaoCrescente implements Comparator<User> {

        @Override
        public int compare(User o1, User o2) {

            return (o1.pontuacao < o2.pontuacao) ? -1 : ((o1.pontuacao > o2.pontuacao) ? 1 : 0);
        }
    }

    private class ComparadorPontuacaoDecrescente implements Comparator<User> {

        @Override
        public int compare(User o1, User o2) {

            return (o1.pontuacao < o2.pontuacao) ? 1 : ((o1.pontuacao > o2.pontuacao) ? -1 : 0);
        }
    }
}
