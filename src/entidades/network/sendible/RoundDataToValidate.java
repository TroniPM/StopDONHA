package entidades.network.sendible;

import java.io.Serializable;
import java.util.ArrayList;

public class RoundDataToValidate implements Serializable {

    public String id = "";

    private static final long serialVersionUID = 1L;

    public ArrayList<String> respostas = new ArrayList<>();
    public ArrayList<Boolean> respostasAceitacao = new ArrayList<>();
}
