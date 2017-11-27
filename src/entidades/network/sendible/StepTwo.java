package entidades.network.sendible;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;

/**
 * Utilizado pelo CLIENTE.
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class StepTwo implements Serializable {

    private String TAG = "Enviado do Cliente para o SERVIDOR";
    public PublicKey KEY_PUBLICA = null;
    public PrivateKey KEY_PRIVATE = null;
    public SecretKey KEY_ENCRIPTACAO = null;
    public String IP = null;

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
        String a = "########## KEY_PUBLICA: " + KEY_PUBLICA + "\n";
        a += "########## KEY_PRIVATE: " + KEY_PRIVATE + "\n";
        a += "########## KEY_ENCRIPTACAO: " + KEY_ENCRIPTACAO + "\n";

        return a;
    }
}
