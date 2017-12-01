package security;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import entidades.network.sendible.User;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import org.apache.commons.lang3.SerializationUtils;
import util.Methods;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Security {

    public static final String chave_this_public_path = "./key_this_pub.txt";
    public static final String chave_this_private_path = "./key_this_pri.txt";
    public static final String chave_server_public_path = "./public.der";
    public static final String chave_server_private_path = "./private.der";

    private static final String param1 = "RSA";

    public PublicKey chavePublicaSERVIDOR = null;
    public PrivateKey chavePrivadaSERVIDOR = null;

    public String TAG = null;
    public int TAG_NUMBER = 1;

    public ChaveSessao KEY;

    public byte[] criptografaSimetrica(byte[] data, SecretKey secret) {
        try {
            return process(data, true, secret);
        } catch (DataLengthException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidCipherTextException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] decriptografiaSimetrica(byte[] data, SecretKey secret) {
        try {
            return process(data, false, secret);
        } catch (DataLengthException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidCipherTextException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private byte[] process(byte[] input, boolean encrypt, SecretKey secret) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        CBCBlockCipher cbcBlockCipher = new CBCBlockCipher(new AESEngine());
        BlockCipherPadding bcp = new PKCS7Padding();
        PaddedBufferedBlockCipher pbbc = new PaddedBufferedBlockCipher(cbcBlockCipher, bcp);

        int blockSize = cbcBlockCipher.getBlockSize();
        int inputOffset = 0;
        int inputLength = input.length;
        int outputOffset = 0;

        byte[] iv = new byte[blockSize];
        if (encrypt) {
            //random.nextBytes(iv);
            outputOffset += blockSize;
        } else {
            System.arraycopy(input, 0, iv, 0, blockSize);
            inputOffset += blockSize;
            inputLength -= blockSize;
        }

        pbbc.init(encrypt, new ParametersWithIV(new KeyParameter(secret.getEncoded()), iv));
        byte[] output = new byte[pbbc.getOutputSize(inputLength) + outputOffset];

        if (encrypt) {
            System.arraycopy(iv, 0, output, 0, blockSize);
        }

        int outputLength = outputOffset + pbbc.processBytes(input, inputOffset, inputLength, output, outputOffset);

        outputLength += pbbc.doFinal(output, outputLength);

        byte[] out = new byte[outputLength];
        System.arraycopy(output, 0, out, 0, outputLength);

        return out;
    }

    public byte[] autenticacao(byte[] input, SecretKey secret) throws DataLengthException, InvalidCipherTextException {
        CBCBlockCipher cbcBlockCipher = new CBCBlockCipher(new AESEngine());
        //SecureRandom random = new SecureRandom();
        BlockCipherPadding bcp = new PKCS7Padding();
        PaddedBufferedBlockCipher pbbc = new PaddedBufferedBlockCipher(cbcBlockCipher, bcp);

        int blockSize = cbcBlockCipher.getBlockSize();
        int inputOffset = 0;
        int inputLength = input.length;
        int outputOffset = 0;

        byte[] iv = new byte[blockSize];
        outputOffset += blockSize;

        pbbc.init(true, new ParametersWithIV(new KeyParameter(secret.getEncoded()), iv));
        byte[] output = new byte[pbbc.getOutputSize(inputLength) + outputOffset];

        System.arraycopy(iv, 0, output, 0, blockSize);

        int outputLength = outputOffset + pbbc.processBytes(input, inputOffset, inputLength, output, outputOffset);

        outputLength += pbbc.doFinal(output, outputLength);

        //Pego apenas o último bloco
        byte[] out = new byte[blockSize];
        System.arraycopy(output, outputLength - blockSize, out, 0, blockSize);

        return out;
    }

    public boolean getPublicKeyServerFromFile() {
        if (Methods.fileExists(chave_server_public_path)) {
            Session.addLog("Recuperando Chave Pública do servidor...");
            try {
                chavePublicaSERVIDOR = Methods.readPublicKey(chave_server_public_path);
                Session.addLog("Chave Pública do servidor recuperada com sucesso.");
                return true;
            } catch (Exception ex) {
                Session.addLog("Erro ao recuperar Chave Pública do servidor.");
            }
        } else {
            Session.addLog("Chave Pública do servidor não existe...");
        }

        if (Methods.fileExists(chave_server_private_path)) {
            Session.addLog("Recuperando Chave Privada do servidor...");
            try {
                chavePrivadaSERVIDOR = Methods.readPrivateKey(chave_server_private_path);
                Session.addLog("Chave Privada do servidor recuperada com sucesso.");
            } catch (Exception ex) {
                Session.addLog("Erro ao recuperar Chave Privada do servidor.");
            }
        } else {
            Session.addLog("Chave Privada do servidor não existe...");
        }

        return false;
    }

    public void init() throws UnknownHostException {
        Session.addLog("Adicionando provider ao java.security.Security...");
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        TAG = Inet4Address.getLocalHost().getHostAddress();
        TAG_NUMBER = 0;
    }

    public static byte[] criptografaAssimetrica(byte[] texto, PublicKey chave) {
        byte[] cipherText = null;

        try {
            Cipher cipher = Cipher.getInstance(param1);

            // Criptografa o texto puro usando a chave Pública
            cipher.init(Cipher.ENCRYPT_MODE, chave);
            cipherText = cipher.doFinal(texto);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cipherText;
    }

    public static byte[] decriptografiaAssimetrica(byte[] texto, PrivateKey chave) {
        byte[] dectyptedText = null;
        try {
            Cipher cipher = Cipher.getInstance(param1);

            // Decriptografa o texto puro usando a chave Privada
            cipher.init(Cipher.DECRYPT_MODE, chave);
            dectyptedText = cipher.doFinal(texto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dectyptedText;
    }

    public static void main(String[] args) {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        try {
            publicKey = Methods.readPublicKey("public.der");
            privateKey = Methods.readPrivateKey("private.der");
        } catch (IOException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }

        //ChaveSessao cs = new ChaveSessao();
        try {

            User u = new User("nome_de_usuario", 100, "192.168.0.5");
            byte[] convertToByteArray = SerializationUtils.serialize(u);

            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(256, new SecureRandom());
            SecretKey generateKey = keygenerator.generateKey();

            KeyEncriptacaoCliente kec = new KeyEncriptacaoCliente(generateKey);
            KeyEncriptacaoServidor kes = new KeyEncriptacaoServidor(generateKey);

            byte[] p1 = SerializationUtils.serialize(kec);
            System.out.println(p1.length);
            //byte[] p1 = generateKey.getEncoded();
            //byte[] p1 = u.convertToByteArray();
            byte[] e1 = criptografaAssimetrica(p1, publicKey);
            //byte[] e2 = _encrypt(CHAVE, chavePublica.getEncoded());
            byte[] d1 = decriptografiaAssimetrica(e1, privateKey);
            for (int i = 0; i < p1.length; i++) {
                System.out.println(p1[i] + ":" + d1[i]);
            }

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
