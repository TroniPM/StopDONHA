package entidades.network;

import static entidades.GameRuntime.PONTO_BONIFICACAO_POR_VELOCIDADE;
import static entidades.GameRuntime.PONTO_POR_ACERTO;
import java.util.ArrayList;
import entidades.network.sendible.EndRound;
import entidades.network.sendible.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
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

            System.out.println("\n\n####################");
            System.out.println("CALCULANDO PONTUAÇÃO");
            System.out.println("Usuário: " + auxUser.nickname);
            System.out.println("Ip: " + auxUser.ip);
            System.out.println("Pontuação: " + pontos);
            System.out.println("####################");

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

        System.out.println("cl_calculateScore() calculou notas de " + user.size() + " jogadores.");

        return user;
    }

    public static ArrayList<User> sv_sumScore() {
        ArrayList<List> arrayAux = DataNetworkManager.respostasRecebidasValidated;
        ArrayList<User> arrayToSendBack = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        //AVALIADOR
        for (int m = 0; m < arrayAux.size(); m++) {
            User userToUse = new User();
            //PESSOA AVALIADA
            for (int i = 0; i < arrayAux.size(); i++) {

                try {
                    mapper.writeValue(new File("./json.txt"), (List<User>) arrayAux.get(i));
                    String jsonInString = mapper.writeValueAsString((List<User>) arrayAux.get(i));
                    escreverEmArquivo("./json2.txt", jsonInString, true);
                } catch (IOException ex) {
                    Logger.getLogger(DataNetworkManager.class.getName()).log(Level.SEVERE, null, ex);
                }

                List<User> helper = ((List<User>) arrayAux.get(i));
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

    public static void escreverEmArquivo(String caminho, String content, boolean isAppend) {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(caminho);
            fop = new FileOutputStream(file, isAppend);
            //Se arquivo não existe, é criado
            if (!file.exists()) {
                file.createNewFile();
            }
            //pega o content em bytes
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
            //flush serve para garantir o envio do último lote de bytes
            fop.flush();
            fop.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
