package entidades.network.sendible;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        System.out.println("USER---------------------------INI");
        System.out.println("NICKNAME: " + nickname);
        System.out.println("IP: " + ip);
        System.out.println("PONTUAÇÃO: " + pontuacao);
        System.out.println("USER---------------------------FIM");
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

    public static User convertFromString(String str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(str));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (User) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
