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
import entidades.network.sendible.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;

public class Security {

    private CBCBlockCipher cbcBlockCipher;
    private SecureRandom random;
    private KeyParameter key;
    private BlockCipherPadding bcp;
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
        }
        return decrypted;
    }

    public static void main(String[] args) {
        try {
            Security security = new Security();
            /*//Servidor
            User u1 = new User("pmateus", 5800, "192.168.0.104");
            String objetoEmStr = u1.convertToString();
            byte[] bytes = objetoEmStr.getBytes("UTF-8");
            byte[] msgEncriptada = security.encrypt(bytes);
            String msgEncriptadaB64 = Base64.encode(msgEncriptada);
            System.out.println(msgEncriptadaB64);
            //ENVIAR STR

            //CLIENTE
            //RECEBE STR
            byte[] decode = Base64.decode(msgEncriptadaB64);
            byte[] msgDecrypt = security.decrypt(decode);
            String decrypted = new String(msgDecrypt, "UTF-8");
            User u2 = User.convertFromString(decrypted);
            u2.printData();*/

            String brincar1 = security.brincar("mateu");
            String brincar2 = security.brincar("mateus");
            String brincar3 = security.brincar("mateuss");

            System.out.println("ENC >> " + brincar1);
            System.out.println("ENC >> " + brincar2);
            System.out.println("ENC >> " + brincar3);

            String desbrincar1 = security.desbrincar(brincar1);
            String desbrincar2 = security.desbrincar(brincar2);
            String desbrincar3 = security.desbrincar(brincar3);

            System.out.println("DEC >> " + desbrincar1);
            System.out.println("DEC >> " + desbrincar2);
            System.out.println("DEC >> " + desbrincar3);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
