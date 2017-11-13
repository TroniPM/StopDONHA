package entidades.network.sendible;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class EndRoundArray implements Serializable {

    public int FLAG = 0;
    public ArrayList<EndRound> array = null;

    public EndRoundArray(ArrayList<EndRound> array) {
        this.array = array;
    }

    public static EndRoundArray convertFromStringArray(String str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(str));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (EndRoundArray) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
}
