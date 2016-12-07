package entidades.network.sendible;

import java.io.Serializable;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class EndRound implements Serializable {

    private static final long serialVersionUID = 1L;

    public String nickname = "", ip = "";
    public int pontuacao;
    public RoundDataToValidate objResposta = new RoundDataToValidate();

    public void printRespostasEAceitacao() {
        System.out.println("NICKNAME: " + nickname);
        System.out.println("IP: " + ip);
        for (int i = 0; i < objResposta.respostas.size(); i++) {
            System.out.println("Resposta-> " + objResposta.respostas.get(i)
                    + " | Aceitação-> " + objResposta.respostasAceitacao.get(i));
        }
    }
}
