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
import java.util.List;

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
    public ArrayList<User> pontuacaoDoRound = new ArrayList<>();
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
        pontuacaoDoRound.clear();
        nicknamesNetwork.clear();
    }

    public void addScore(ArrayList<User> newRound) {
        if (pontuacaoDoRound.size() == 0) {
            pontuacaoDoRound = newRound;
        } else {

            for (int i = 0; i < pontuacaoDoRound.size(); i++) {
                for (int j = 0; j < newRound.size(); j++) {
                    System.out.println("addScore() \nnome: " + pontuacaoDoRound.get(i).nickname + "==" + newRound.get(j).nickname
                            + "\nip: " + pontuacaoDoRound.get(i).ip + "==" + newRound.get(j).ip);
                    if (pontuacaoDoRound.get(i).nickname.equals(newRound.get(j).nickname)
                            && pontuacaoDoRound.get(i).ip.equals(newRound.get(j).ip)) {
                        User a = new User();
                        a = pontuacaoDoRound.get(i);
                        a.pontuacao += newRound.get(j).pontuacao;
                        pontuacaoDoRound.set(i, a);
                    }
                }
            }
        }
    }

    public void ordenarUsuariosPorPontuacaoCrescente() {
        Collections.sort(pontuacaoDoRound, new ComparadorPontuacaoCrescente());
    }

    public void ordenarUsuariosPorPontuacaoDecrescente() {
        Collections.sort(pontuacaoDoRound, new ComparadorPontuacaoDecrescente());
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

    public String convertToString() {
        try {
            String str;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            byte[] objeto = baos.toByteArray();
            str = Base64.encode(objeto);
            oos.close();
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GameRuntime convertFromString(String str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(str));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (GameRuntime) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
