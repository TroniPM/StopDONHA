package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Security {

    private static PrivateKey PRIVATE_KEY;
    private static PublicKey PUBLIC_KEY;
    private static final String TYPE = "RSA/ECB/PKCS1Padding";
    private static final int KEY_SIZE = 1024;//2048, 4096

    public Security() {
        try {
            generateKeys();
        } catch (Exception ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(KEY_SIZE);
        KeyPair rsaKeyPair = kpg.genKeyPair();

        PRIVATE_KEY = rsaKeyPair.getPrivate();
        PUBLIC_KEY = rsaKeyPair.getPublic();
    }

    public byte[] encrypt(String frase) {
        byte[] txt = frase.getBytes();
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, PUBLIC_KEY);
            txt = cipher.doFinal(txt);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
        return txt;
    }

    public byte[] decrypt(byte[] txt) {

        Cipher cipher;
        try {
            cipher = Cipher.getInstance(TYPE);
            cipher.init(Cipher.DECRYPT_MODE, PRIVATE_KEY);
            txt = cipher.doFinal(txt);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }

        return txt;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, UnsupportedEncodingException, IOException, FileNotFoundException, NoSuchProviderException, GeneralSecurityException {
        Security s = new Security();
        String frase = "O rato roeu a roupa do rei de roma, e a rainha resolveu costuráaa";
        System.out.println("Normal message    : " + frase);

        byte[] encrypt = s.encrypt(frase);
        System.out.println("Encrypted message : " + new String(encrypt));

        byte[] decrypt = s.decrypt(encrypt);
        System.out.println("Decrypted message : " + new String(decrypt));

        /*CERTIFICATE*/
        System.out.println("");
        System.out.println("###########PUBLIC###########");
        String p1 = new String(Base64.getEncoder().encodeToString(PUBLIC_KEY.getEncoded()));
        System.out.println(p1);
        System.out.println("###########PRIVATE###########");
        String p2 = new String(Base64.getEncoder().encodeToString(PRIVATE_KEY.getEncoded()));
        System.out.println(p2);
    }
}
