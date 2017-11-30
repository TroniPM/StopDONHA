/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.io.Serializable;
import javax.crypto.SecretKey;

public class KeyAutenticacaoCliente implements Serializable {

    private static final long serialVersionUID = -226243875381L;
    public SecretKey chave = null;

    public KeyAutenticacaoCliente(SecretKey chave) {
        this.chave = chave;
    }
}
