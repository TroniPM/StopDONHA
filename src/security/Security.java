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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import util.Methods;
import util.Session;

public class Security {

    private final String privatePath = "./chavePrivada.txt";
    private final String publicPath = "./chavePublica.txt";

    private CBCBlockCipher cbcBlockCipher;
    private SecureRandom random;
    private KeyParameter key;
    private BlockCipherPadding bcp;
    private static final String token = "AySgJhqmF6DH3OGEAmK7srx8DEyFijiqHDsEFkjbm4kL55OQ8pIoIprc4/8TO2xWX35iUWaNsR875cPmBkbO9I+/QcTu6cQu9kSB6z3ER18fQGAnFWH/n8PK914UzN8dtDxnMswfkCDMyNP7l+jUk6M7Px6/DV/LOK0BoNjxooQr01Ox5BNIfkx";

    /**/
    private KeyPairGenerator assimetrica;
    private PrivateKey chavePrivada;
    private PublicKey chavePublica;

    public StepOne passo1;
    public StepTwo passo2;

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
    }

    public String brincar(String str) {
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
    }

    public String desbrincar(String str) {
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

    /**
     * Criptografa o texto puro usando a chave pública.
     */
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

    /**
     * Decriptografa o texto puro usando a chave privada.
     */
    public byte[] decriptografa(byte[] texto, PublicKey chave) {
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
    }

    /**
     * Criptografa o texto puro usando a chave simétrica.
     */
    public byte[] criptografaSimetrica(byte[] data, SecretKey chaveSimetrica) {
        byte[] encryptedIVAndText = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

            // Gera o vetor inicial		
            byte[] iv = new byte[cipher.getBlockSize()];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameter = new IvParameterSpec(iv);

            // Criptografa o texto puro usando a chave simétrica
            cipher.init(Cipher.ENCRYPT_MODE, chaveSimetrica, ivParameter);
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

    /**
     * Decriptografa o texto puro usando a chave simétrica.
     */
    public byte[] decriptografaSimetrica(byte[] encryptedIvTextBytes, SecretKey chaveSimetrica) {
        byte[] decryptedText = null;
        try {
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

    public void init() {
        Session.addLog("Criação de chaves...");
        if (Methods.fileExists(privatePath) && Methods.fileExists(publicPath)) {
            try {
                chavePublica = (PublicKey) convertFromString(Methods.readFromFile(publicPath)[0]);
                chavePrivada = (PrivateKey) convertFromString(Methods.readFromFile(privatePath)[0]);
            } catch (Exception ex) {
                //Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
                Session.addLog("Erro ao recuperar chaves. Irá criar do zero...");
                createKeys();
            }
        } else {
            createKeys();
        }
        passo1 = new StepOne();
        passo1.chavePublicaSERVIDOR = chavePublica;
        passo2 = new StepTwo();
        passo2.chavePublicaCLIENTE = chavePublica;
        //Crio a chave de sessão.
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            passo2.chaveSessaoCLIENTE = keygenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Session.addLog(chavePrivada.toString());
        Session.addLog(chavePublica.toString());

        Session.addLog("Chaves criadas...");
    }

    private void createKeys() {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

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
        chavePrivada = keyPair.getPrivate();
        chavePublica = keyPair.getPublic();

        String pub = convertToString(chavePublica);
        String pri = convertToString(chavePrivada);
        Methods.writeOnFile(privatePath, pri, false);
        Methods.writeOnFile(publicPath, pub, false);
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

    public static void main(String[] args) {
        try {
            Security security = new Security();

            /*security.init();
            System.out.println(security.chavePrivada);
            System.out.println(security.chavePublica);
            System.out.println("##########################################################");
            security.init();
            System.out.println(security.chavePrivada);
            System.out.println(security.chavePublica);*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
