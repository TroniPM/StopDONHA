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
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import entidades.network.sendible.EndRound;
import entidades.network.sendible.StepOne;
import entidades.network.sendible.StepTwo;
import entidades.network.sendible.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import util.Methods;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Security {

    public static final String chave_this_public_path = "./key_this_pub.txt";
    public static final String chave_server_public_path = "./key_server_pub.txt";
    public static final String chave_this_private_path = "./key_this_pri.txt";

    private static final String param1 = "SHA1withRSA";
    private static final String param2 = "AES/CBC/PKCS7Padding";
    private static final String param3 = "BC";

    private PrivateKey chavePrivada = null;
    private PublicKey chavePublica = null;
    private SecretKey chaveSessao = null;

    public PublicKey chavePublicaSERVIDOR = null;

    public StepOne passo1 = null;
    public StepTwo passo2 = null;

    public String TAG = null;
    public int TAG_NUMBER = 1;

    public byte[] criptografaSimetrica(byte[] data) {
        byte[] encryptedIVAndText = null;
        try {
            //Cipher cipher = Cipher.getInstance(param2);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

            // Gera o vetor inicial		
            byte[] iv = new byte[cipher.getBlockSize()];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameter = new IvParameterSpec(iv);

            // Criptografa o texto puro usando a chave simétrica
            cipher.init(Cipher.ENCRYPT_MODE, Session.security.passo2.KEY_ENCRIPTACAO, ivParameter);
            byte[] cipherText = cipher.doFinal(data);

            // Combina o vetor inicial com o texto criptografado
            encryptedIVAndText = new byte[cipher.getBlockSize() + cipherText.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, cipher.getBlockSize());
            System.arraycopy(cipherText, 0, encryptedIVAndText, cipher.getBlockSize(), cipherText.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedIVAndText;
    }

    public byte[] decriptografaSimetrica(byte[] encryptedIvTextBytes, SecretKey chaveSimetrica) {
        byte[] decryptedText = null;
        try {
            //Cipher cipher = Cipher.getInstance(param2);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

            // Extrai o IV.
            byte[] iv = new byte[cipher.getBlockSize()];
            System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Extrai a mensagem criptografada
            int encryptedSize = encryptedIvTextBytes.length - cipher.getBlockSize();
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(encryptedIvTextBytes, cipher.getBlockSize(), encryptedBytes, 0, encryptedSize);

            // Decrypt	
            cipher.init(Cipher.DECRYPT_MODE, chaveSimetrica, ivParameterSpec);
            decryptedText = cipher.doFinal(encryptedBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return decryptedText;
    }

    public byte[] generateSignature(String mensagem, PrivateKey chave) {
        Signature sig = null;
        byte[] signature = null;
        try {
            //sig = Signature.getInstance(param1, param3);
            sig = Signature.getInstance("SHA1withRSA", "BC");

            // Inicializando Obj Signature com a Chave Privada
            sig.initSign(chave, new SecureRandom());

            // Gerar assinatura
            sig.update(mensagem.getBytes());

            signature = sig.sign();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return signature;
    }

    public boolean verifySignature(String msg, byte[] signature, PublicKey chave) {
        Signature clientSig;
        try {
            //clientSig = Signature.getInstance(param1, param3);
            clientSig = Signature.getInstance("SHA1withRSA", "BC");
            clientSig.initVerify(chave);
            clientSig.update(msg.getBytes());

            if (clientSig.verify(signature)) {
                return true; // Mensagem corretamente assinada
            } else {
                return false; // Mensagem não pode ser validada
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public boolean getPublicKeyServerFromFile() {
        if (Methods.fileExists(chave_server_public_path)) {
            Session.addLog("Recuperando Chave Pública do servidor...");
            try {
                chavePublicaSERVIDOR = (PublicKey) convertFromString(Methods.readFromFile(chave_server_public_path)[0]);
                Session.addLog("Chave Pública do servidor recuperada com sucesso.");
                return true;
            } catch (Exception ex) {
                Session.addLog("Erro ao recuperar Chave Pública do servidor.");
            }
        } else {
            Session.addLog("Chave Pública do servidor não existe...");
        }

        return false;
    }

    public void init() throws UnknownHostException {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Session.addLog("Criação de chaves...");
        if (Methods.fileExists(chave_this_public_path) && Methods.fileExists(chave_this_private_path)) {
            Session.addLog("Recuperando chaves...");
            try {
                chavePrivada = (PrivateKey) convertFromString(Methods.readFromFile(chave_this_private_path)[0]);
                chavePublica = (PublicKey) convertFromString(Methods.readFromFile(chave_this_public_path)[0]);
            } catch (Exception ex) {
                //Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
                Session.addLog("Erro ao recuperar chaves. Irá criar do zero...");
                createKeys();
            }
        } else {
            createKeys();
        }
        Session.addLog(chavePrivada.toString());
        Session.addLog(chavePublica.toString());

        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            chaveSessao = keygenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        passo1 = new StepOne();
        passo2 = new StepTwo();
        /**/
        passo1.KEY_PUBLICA = chavePublica;
        passo1.KEY_ENCRIPTACAO = chaveSessao;
        passo1.KEY_PRIVATE = chavePrivada;
        /**/
        passo2.KEY_PUBLICA = chavePublica;
        passo2.KEY_ENCRIPTACAO = chaveSessao;
        passo2.KEY_PRIVATE = chavePrivada;
        //Crio a chave de sessão.

        Session.addLog("Chaves criadas...");

        TAG = Inet4Address.getLocalHost().getHostAddress();
        passo2.IP = TAG;
        TAG_NUMBER = 0;
    }

    private void createKeys() {
        KeyPairGenerator assimetrica = null;
        try {
            assimetrica = KeyPairGenerator.getInstance("RSA", "BC");
            assimetrica.initialize(1024, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        KeyPair keyPair = assimetrica.generateKeyPair();
        chavePublica = keyPair.getPublic();
        chavePrivada = keyPair.getPrivate();

        String pub = convertToString(chavePublica);
        String pri = convertToString(chavePrivada);
        Methods.writeOnFile(chave_this_public_path, pub, false);
        Methods.writeOnFile(chave_this_private_path, pri, false);
    }

    public String convertToString(Object chave) {
        try {
            String str;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(chave);
            byte[] objeto = baos.toByteArray();
            str = Base64.encode(objeto);
            oos.close();
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object convertFromString(String str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(str));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*private CBCBlockCipher cbcBlockCipher = null;
    private SecureRandom random = null;
    private KeyParameter key = null;
    private BlockCipherPadding bcp = null;
    private static final String token = "AySgJhqmF6DH3OGEAmK7srx8DEyFijiqHDsEFkjbm4kL55OQ8pIoIprc4/8TO2xWX35iUWaNsR875cPmBkbO9I+/QcTu6cQu9kSB6z3ER18fQGAnFWH/n8PK914UzN8dtDxnMswfkCDMyNP7l+jUk6M7Px6/DV/LOK0BoNjxooQr01Ox5BNIfkx";

    private void setPadding(BlockCipherPadding bcp) {
        this.bcp = bcp;
    }

    private void setKey(byte[] key) {
        this.key = new KeyParameter(key);
    }

    private byte[] encrypt(byte[] input)
            throws DataLengthException, InvalidCipherTextException {
        return processing(input, true);
    }

    private byte[] decrypt(byte[] input)
            throws DataLengthException, InvalidCipherTextException {
        return processing(input, false);
    }

    private byte[] processing(byte[] input, boolean encrypt)
            throws DataLengthException, InvalidCipherTextException {

        PaddedBufferedBlockCipher pbbc
                = new PaddedBufferedBlockCipher(cbcBlockCipher, bcp);

        int blockSize = cbcBlockCipher.getBlockSize();
        int inputOffset = 0;
        int outputOffset = 0;
        int inputLength = input.length;

        byte[] iv = new byte[blockSize];
        if (encrypt) {
            random.nextBytes(iv);
            outputOffset += blockSize;
        } else {
            System.arraycopy(input, 0, iv, 0, blockSize);
            inputOffset += blockSize;
            inputLength -= blockSize;
        }

        pbbc.init(encrypt, new ParametersWithIV(key, iv));
        byte[] output = new byte[pbbc.getOutputSize(inputLength) + outputOffset];

        if (encrypt) {
            System.arraycopy(iv, 0, output, 0, blockSize);
        }

        int outputLength = outputOffset + pbbc.processBytes(
                input, inputOffset, inputLength, output, outputOffset);

        outputLength += pbbc.doFinal(output, outputLength);

        byte[] out = new byte[outputLength];
        System.arraycopy(output, 0, out, 0, outputLength);

        return out;

    }

    public Security() {
        byte[] key = null;
        try {
            key = token.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        this.cbcBlockCipher = new CBCBlockCipher(new AESEngine());
        this.random = new SecureRandom();
        this.bcp = new PKCS7Padding();

        this.setPadding(new PKCS7Padding());
        this.setKey(secretKeySpec.getEncoded());
    }*/

 /*public String brincar(String str) {
        byte[] msgEncriptada = null;
        try {
            byte[] bytes = str.getBytes("UTF-8");
            msgEncriptada = encrypt(bytes);
        } catch (DataLengthException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidCipherTextException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        String msgEncriptadaB64 = Base64.encode(msgEncriptada);

        return msgEncriptadaB64;
    }*/

 /*public String desbrincar(String str) {
        byte[] decode = Base64.decode(str);
        byte[] msgDecrypt = null;
        String decrypted = null;
        try {
            msgDecrypt = decrypt(decode);
            decrypted = new String(msgDecrypt, "UTF-8");
        } catch (DataLengthException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidCipherTextException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        return decrypted;
    }

    public byte[] criptografa(byte[] texto, PublicKey chave) {
        byte[] cipherText = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");

            // Criptografa o texto puro usando a chave Pública
            cipher.init(Cipher.ENCRYPT_MODE, chave);
            cipherText = cipher.doFinal(texto);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cipherText;
    }

    public byte[] decriptografia(byte[] texto, PublicKey chave) {
        byte[] dectyptedText = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");

            // Decriptografa o texto puro usando a chave Privada
            cipher.init(Cipher.DECRYPT_MODE, chave);
            dectyptedText = cipher.doFinal(texto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dectyptedText;
    }*/
}
