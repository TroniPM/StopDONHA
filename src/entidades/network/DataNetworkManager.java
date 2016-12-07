package entidades.network;

import static entidades.GameRuntime.PONTO_BONIFICACAO_POR_VELOCIDADE;
import static entidades.GameRuntime.PONTO_POR_ACERTO;
import java.util.ArrayList;
import entidades.network.sendible.EndRound;
import entidades.network.sendible.User;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class DataNetworkManager {

    public static ArrayList<EndRound> respostasRecebidasDoRound = new ArrayList<>();
    public static ArrayList<ArrayList> respostasRecebidasValidated = new ArrayList<>();

    public static boolean canOverrideMainArray = true;

    public static ArrayList<User> cl_calculateScore() {
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
            /*Faço esse if para o caso de usuário não inserir nada. Ele ganharia pontos
             só pelo fato de ter enviado primeiro. Então se ele não pontuou nad ano round, 
             não ganha a pontuação extra.*/
            //TODO
            /*if (pontos != 0) {
             pontos += PONTO_BONIFICACAO_POR_VELOCIDADE * (arrayAux.size() - i);
             }*/
            auxUser.pontuacao = pontos;

            user.add(auxUser);
        }

        System.out.println("cl_calculateScore() calculou notas de --> " + user.size());

        return user;
    }

    public static ArrayList<User> sv_sumScore() {
        ArrayList<ArrayList> arrayAux = DataNetworkManager.respostasRecebidasValidated;
        ArrayList<User> arrayToSendBack = new ArrayList<>();

        //AVALIADOR
        for (int m = 0; m < arrayAux.size(); m++) {
            User userToUse = new User();
            //PESSOA AVALIADA
            for (int i = 0; i < arrayAux.size(); i++) {
                ArrayList<User> helper = ((ArrayList<User>) arrayAux.get(i));
                //Para o caro so SERVIDOR forçar finalização das avaliações
                if (helper.size() - 1 <= m) {
                    User helperInner = helper.get(m);
                    userToUse.nickname = helperInner.nickname;
                    userToUse.ip = helperInner.ip;
                    userToUse.pontuacao += helperInner.pontuacao;
                }
            }
            System.out.println("sv_sumScore() NICKNAME: " + userToUse.nickname + " | PONTUAÇÃO: " + userToUse.pontuacao);
            arrayToSendBack.add(userToUse);
        }
        return arrayToSendBack;
    }

    public static void cleanAllData() {
        respostasRecebidasDoRound.clear();
        respostasRecebidasValidated.clear();
        canOverrideMainArray = true;
    }

}
