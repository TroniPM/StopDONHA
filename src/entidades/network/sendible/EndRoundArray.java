package entidades.network.sendible;

import java.io.Serializable;
import java.util.ArrayList;

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
}
