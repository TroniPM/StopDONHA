package security;

import java.io.File;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import util.Session;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Security {

    //public static final String chave_this_public_path = "./public_this.stopdonha";
    //public static final String chave_this_private_path = "./private_this.stopdonha";
    //public static final String chave_server_public_path = "./certificate/self_public.cer";
    private static final String param1 = "RSA";

    public PublicKey chavePublicaSERVIDOR = null;
    //public PublicKey chavePublicaTHIS = null;
    public PrivateKey chavePrivadaTHIS = null;

    public String TAG = null;
    public int TAG_NUMBER = 1;

    public ChaveSessao KEY;
    public static File certificado = null;
    public static String certificadoSenha = null;

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

    public void init() throws UnknownHostException {
        Session.addLog("Adicionando provider ao java.security.Security...");
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        TAG = Inet4Address.getLocalHost().getHostAddress();
        TAG_NUMBER = 0;
    }

    public PrivateKey getPrivateKeyFromCert(String path, String senha) {
        PrivateKey pKey = null;
        try {
            KeyStore p12 = KeyStore.getInstance("pkcs12");
            p12.load(new FileInputStream(path), senha.toCharArray());
            Enumeration e = p12.aliases();
            String alias = (String) e.nextElement();
            pKey = (PrivateKey) p12.getKey(alias, senha.toCharArray());

        } catch (KeyStoreException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pKey;
    }

    public PublicKey getPublicKeyFromCert(String path) {
        PublicKey pk = null;
        try {
            FileInputStream fin = new FileInputStream(path);
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);
            pk = certificate.getPublicKey();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pk;
    }

    public PublicKey getPublicKeyFromCert(File path) {
        PublicKey pk = null;
        try {
            FileInputStream fin = new FileInputStream(path);
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) f.generateCertificate(fin);
            pk = certificate.getPublicKey();
        } catch (Exception ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pk;
    }

    public boolean checkPasswordFromCert(String path, String senha) {
        PrivateKey pKey = null;
        try {
            KeyStore p12 = KeyStore.getInstance("pkcs12");
            p12.load(new FileInputStream(path), senha.toCharArray());
            Enumeration e = p12.aliases();
            String alias = (String) e.nextElement();
            pKey = (PrivateKey) p12.getKey(alias, senha.toCharArray());

            if (pKey != null) {
                return true;
            }

        } catch (KeyStoreException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public boolean checkValidity(String path, String senha) {
        try {
            KeyStore p12 = KeyStore.getInstance("pkcs12");
            p12.load(new FileInputStream(path), senha.toCharArray());
            Enumeration e = p12.aliases();
            String alias = (String) e.nextElement();
            /*if (p12.getCertificate(alias).getType().equals("X.509")) {
                System.out.println(alias + " expires "
                        + ((X509Certificate) p12.getCertificate(alias)).getNotAfter());
            }*/

            //Date d = new Date();
            //d.setYear(2030);
            ((X509Certificate) p12.getCertificate(alias)).checkValidity();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        Security s = new Security();
        PrivateKey privateKeyFromCert = s.getPrivateKeyFromCert("./certificate/self_pkcs12.p12", "password");
        PublicKey publicKeyFromCert = s.getPublicKeyFromCert("./certificate/self_public.cer");

        String a = "texto1 normal a ser encriptadoáéíóLoÇô";
        System.out.println("NORMAL: " + a);

        byte[] criptografaAssimetrica = Security.criptografaAssimetrica(a.getBytes(), publicKeyFromCert);
        System.out.println("ENCRIP: " + criptografaAssimetrica);

        byte[] decriptografiaAssimetrica = Security.decriptografiaAssimetrica(criptografaAssimetrica, privateKeyFromCert);
        System.out.println("DECRIP: " + new String(decriptografiaAssimetrica));
    }
}
