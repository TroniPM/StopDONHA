/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certificado;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

/**
 *
 * @author Mateus
 */
public class Rodar {

    private static void printCertificateData(String path, String senha) {
        KeyStore p12;
        try {
            p12 = KeyStore.getInstance("pkcs12");
            p12.load(new FileInputStream(path), senha.toCharArray());
            Enumeration e = p12.aliases();
            while (e.hasMoreElements()) {
                String alias = (String) e.nextElement();
                X509Certificate c = (X509Certificate) p12.getCertificate(alias);
                Principal subject = c.getSubjectDN();
                String subjectArray[] = subject.toString().split(",");
                System.out.println("ALIAS - " + alias);
                for (String s : subjectArray) {
                    String[] str = s.trim().split("=");
                    String key = str[0];
                    String value = str[1];
                    System.out.println(key + " - " + value);
                }
            }
        } catch (KeyStoreException ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void print(Object p) {
        if (p instanceof PrivateKey) {
            StringWriter sw = new StringWriter();
            JcaPEMWriter writer = new JcaPEMWriter(sw);
            try {
                writer.writeObject(p);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(sw.getBuffer().toString());
        } else {
            System.out.println(p);
        }

    }

    public static PrivateKey getPrivateKeyFromCert(String path, String senha) {
        PrivateKey pKey = null;
        try {
            KeyStore p12 = KeyStore.getInstance("pkcs12");
            p12.load(new FileInputStream(path), senha.toCharArray());
            Enumeration e = p12.aliases();
            String alias = (String) e.nextElement();
            X509Certificate c = (X509Certificate) p12.getCertificate(alias);
            pKey = (PrivateKey) p12.getKey(alias, senha.toCharArray());

            print(pKey);
        } catch (KeyStoreException ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        }

        return pKey;
    }

    public static PublicKey getPublicKeyFromCert(String path, String senha) {
        PublicKey pKey = null;
        try {
            //https://coderanch.com/t/134042/Reading-file-keypair
            KeyStore ks = KeyStore.getInstance("PKCS12");
            /*FileInputStream fis = new FileInputStream(path);
            //ks.load(fis, senha.toCharArray());
            Enumeration e = ks.aliases();
            String alias = (String) e.nextElement();
            Key key = null;
            Certificate cert = null;
            key = ks.getKey(alias, senha.toCharArray());
            cert = ks.getCertificate(alias);

            System.out.println(cert.getPublicKey());*/

            ks.load(new FileInputStream(new File(path)), null);
            Enumeration e = ks.aliases();
            String alias = (String) e.nextElement();
            System.out.println("alias: " + alias);
            Certificate cert = null;
            cert = ks.getCertificate(alias);
            //System.out.println("certificate: " + cert);

            System.out.println(cert.getPublicKey());
        } catch (Exception ex) {
            Logger.getLogger(Rodar.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pKey;
    }

    public static void main(String[] args) {

        //printCertificateData("./certificado.p12", "password");
        //System.out.println("Lendo certificado");
        //System.out.println("Lendo chave privada");
        //getPrivateKeyFromCert("./certificado.p12", "password");
        //System.out.println("##########################################");
        //System.out.println("Lendo chave pública");
        getPublicKeyFromCert("./certificado.p12", "password");
    }
}
