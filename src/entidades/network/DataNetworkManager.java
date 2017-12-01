package entidades.network;

import static entidades.GameRuntime.PONTO_POR_ACERTO;
import java.util.ArrayList;
import entidades.network.sendible.EndRound;
import entidades.network.sendible.User;
import java.util.List;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class DataNetworkManager {

    public static ArrayList<EndRound> respostasRecebidasDoRound = new ArrayList<>();
    public static ArrayList<List> respostasRecebidasValidated = new ArrayList<>();

    public static boolean canOverrideMainArray = true;

    public static ArrayList<User> cl_calculateScore() {
        Session.addLog("cl_calculateScore() (chamado dentro de GameScreenValidation)");
        ArrayList<User> user = new ArrayList<>();
        ArrayList<EndRound> arrayAux = DataNetworkManager.respostasRecebidasDoRound;

        for (int i = 0; i < arrayAux.size(); i++) {
            User auxUser = new User();

            auxUser.nickname = arrayAux.get(i).nickname;
            auxUser.ip = arrayAux.get(i).ip;
            int pontos = 0;
            for (int j = 0; j < arrayAux.get(i).objResposta.respostasAceitacao.size(); j++) {
                if (arrayAux.get(i).objResposta.respostasAceitacao.get(j)) {
                    pontos += PONTO_POR_ACERTO;
                }
            }

            auxUser.pontuacao = pontos;

            user.add(auxUser);
        }

        //System.out.println("cl_calculateScore() calculou notas de " + user.size() + " jogadores.");
        return user;
    }

    public static ArrayList<User> sv_sumScore() {
        ArrayList<List> arrayAux = DataNetworkManager.respostasRecebidasValidated;
        ArrayList<User> arrayToSendBack = new ArrayList<>();

        //AVALIADOR
        for (int m = 0; m < arrayAux.size(); m++) {
            ArrayList<User> arrayUser = (ArrayList<User>) arrayAux.get(m);
            for (int j = 0; j < arrayUser.size(); j++) {
                User user = arrayUser.get(j);
                int index = check(arrayToSendBack, user);
                //Usuário ainda não foi adicionado
                if (index == -1) {
                    arrayToSendBack.add(user);
                } else {//usuário já foi adicionado. Então só atualizo pontuação
                    arrayToSendBack.get(index).pontuacao += user.pontuacao;
                }
            }
        }

        return arrayToSendBack;
    }

    /**
     * Checar se usuário já está no array.
     *
     * @param array
     * @param checkFor
     * @return
     */
    private static int check(ArrayList<User> array, User checkFor) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).ip.equals(checkFor.ip)
                    && array.get(i).nickname.equals(checkFor.nickname)) {
                return i;
            }
        }
        return -1;
    }

    public static void cleanAllData() {
        respostasRecebidasDoRound.clear();
        respostasRecebidasValidated.clear();
        canOverrideMainArray = true;
    }
}
