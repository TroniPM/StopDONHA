package entidades.network.sendible;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class RoundDataToValidate implements Serializable {

    public String id = "";

    private static final long serialVersionUID = 1L;

    public ArrayList<String> respostas = new ArrayList<>();
    public ArrayList<Boolean> respostasAceitacao = new ArrayList<>();

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

    public static RoundDataToValidate convertFromString(String str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(str));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (RoundDataToValidate) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
