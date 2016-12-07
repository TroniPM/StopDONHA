package entidades.network.sendible;

import java.io.Serializable;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    public String nickname = "", ip = "";
    public int pontuacao = 0;

    public User() {
    }

    public User(String nickname, int pontuacao, String ip) {
        this.nickname = nickname;
        this.pontuacao = pontuacao;
        this.ip = ip;
    }

    public User(String nickname, int pontuacao) {
        this.nickname = nickname;
        this.pontuacao = pontuacao;
    }

    public User somarPontuacao(User A, User B) {
        User C = new User();
        C.pontuacao = A.pontuacao + B.pontuacao;
        C.nickname = A.nickname;
        C.ip = A.ip;

        return C;
    }

    public void printData() {
        System.out.println("USER---------------------------");
        System.out.println("NICKNAME: " + nickname);
        System.out.println("IP: " + ip);
        System.out.println("PONTUAÇÃO: " + pontuacao);
        System.out.println("USER---------------------------FIM");
    }
}
