package security;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class ChaveSessao implements Serializable {

    private static final long serialVersionUID = -116243875381L;
    public SecretKey ENCRIPTACAO_CLIENTE;
    public SecretKey ENCRIPTACAO_SERVIDOR;
    public SecretKey AUTENTICACAO_SERVIDOR;
    public SecretKey AUTENTICACAO_CLIENTE;

    public String ip = null;//Só utilizado pelo servidor.
    public String nickname = null;//Só utilizado pelo servidor.

    public ChaveSessao(SecretKey ENCRIPTACAO_CLIENTE, SecretKey ENCRIPTACAO_SERVIDOR, SecretKey AUTENTICACAO_SERVIDOR, SecretKey AUTENTICACAO_CLIENTE) {
        this.ENCRIPTACAO_CLIENTE = ENCRIPTACAO_CLIENTE;
        this.ENCRIPTACAO_SERVIDOR = ENCRIPTACAO_SERVIDOR;
        this.AUTENTICACAO_SERVIDOR = AUTENTICACAO_SERVIDOR;
        this.AUTENTICACAO_CLIENTE = AUTENTICACAO_CLIENTE;
    }

    @Override
    public String toString() {
        return "ENCRIPTACAO_CLIENTE: " + ENCRIPTACAO_CLIENTE
                + "\nENCRIPTACAO_SERVIDOR: " + ENCRIPTACAO_SERVIDOR
                + "\nAUTENTICACAO_SERVIDOR: " + AUTENTICACAO_SERVIDOR
                + "\nAUTENTICACAO_CLIENTE: " + AUTENTICACAO_CLIENTE;
    }

    public ChaveSessao(boolean t) {
        if (t) {
            try {
                KeyGenerator keygenerator1 = KeyGenerator.getInstance("AES");
                keygenerator1.init(256, new SecureRandom());
                this.ENCRIPTACAO_CLIENTE = keygenerator1.generateKey();

                KeyGenerator keygenerator2 = KeyGenerator.getInstance("AES");
                keygenerator2.init(256, new SecureRandom());
                this.ENCRIPTACAO_SERVIDOR = keygenerator2.generateKey();

                KeyGenerator keygenerator3 = KeyGenerator.getInstance("AES");
                keygenerator3.init(256, new SecureRandom());
                this.AUTENTICACAO_CLIENTE = keygenerator3.generateKey();

                KeyGenerator keygenerator4 = KeyGenerator.getInstance("AES");
                keygenerator4.init(256, new SecureRandom());
                this.AUTENTICACAO_SERVIDOR = keygenerator4.generateKey();
            } catch (Exception ex) {
                Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isValid() {
        return AUTENTICACAO_SERVIDOR != null && AUTENTICACAO_CLIENTE != null
                && ENCRIPTACAO_SERVIDOR != null && ENCRIPTACAO_CLIENTE != null;
    }
}
