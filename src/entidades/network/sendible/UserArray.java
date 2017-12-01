package entidades.network.sendible;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class UserArray implements Serializable {

    public String FLAG1 = null;
    public ArrayList<User> array = null;

    public UserArray(ArrayList<User> array) {
        this.array = array;
    }

}
