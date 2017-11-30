/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.io.Serializable;
import javax.crypto.SecretKey;

public class KeyAutenticacaoServidor implements Serializable {

    private static final long serialVersionUID = -116243875381L;
    public SecretKey chave = null;

    public KeyAutenticacaoServidor(SecretKey chave) {
        this.chave = chave;
    }
}
