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

    public String getRespostasEAceitacao() {
        String a = "";
        a = "NICKNAME: " + nickname + "\t" + "IP: " + ip + "\n\r";
        for (int i = 0; i < objResposta.respostas.size(); i++) {
            a += ("Resposta-> " + objResposta.respostas.get(i)
                    + " | Aceitação-> " + objResposta.respostasAceitacao.get(i));
        }

        return a;
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

    public static EndRound convertFromString(String str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(str));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (EndRound) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
