package entidades.network.sendible;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PublicKey;
import javax.crypto.SecretKey;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class StepTwo implements Serializable {

    public PublicKey chavePublicaCLIENTE;
    public SecretKey chaveSessaoCLIENTE;

    /**
     *
     * @return
     */
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

    /**
     * Fazer CASTING após uso.
     *
     * @param str
     * @return
     * @throws ClassNotFoundException
     */
    public static StepTwo convertFromString(String str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(str));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (StepTwo) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        String a = "Chave Pública CLIENTE:\n"
                + chavePublicaCLIENTE.toString() + "\n"
                + "Chave de Sessão CLIENTE: " + chaveSessaoCLIENTE.toString();
        return a;
    }
}
