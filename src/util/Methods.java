package util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import entidades.network.Servidor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Methods {

    /**
     *
     * @param path
     * @param content
     * @param append
     */
    public static void writeOnFile(String path, byte[] content, boolean append) {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(path);
            fop = new FileOutputStream(file, append);
            //Se arquivo não existe, é criado
            if (!file.exists()) {
                file.createNewFile();
            }
            //pega o content em bytes
            //byte[] contentInBytes = content.getBytes();
            fop.write(content);
            //flush serve para garantir o envio do último lote de bytes
            fop.flush();
            fop.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param path
     * @return
     */
    public static String[] readFromFile(String path) {
        String[] linhas = null;
        try {
            FileInputStream fin = new FileInputStream(path);
            byte[] a = new byte[fin.available()];
            fin.read(a);
            fin.close();
            linhas = new String(a).split("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linhas;
    }

    /**
     *
     * @param path
     * @return
     */
    public static boolean fileExists(String path) {
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {
            return true;
        }

        return false;
    }

    public static byte[] convertToByteArray(Object obj) {
        try {
            //String str;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] objeto = baos.toByteArray();
            //str = Base64.encode(objeto);
            oos.close();
            return objeto;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encode(byte[] b) {
        return Base64.encode(b);
    }

    private static byte[] decode(String b) {
        return Base64.decode(b);
    }

    public static String convertToString(Object obj) {
        try {
            String str;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] objeto = baos.toByteArray();
            str = encode(objeto);
            oos.close();
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object convertFromByteArray(byte[] str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(str);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object convertFromString(String str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(decode(str));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isIpFromServidor(String ip) {
        try {
            if (ip.equals(Inet4Address.getLocalHost().getHostAddress())) {
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    //System.out.println(i.getHostAddress());
                    if (i.getHostAddress().equals(ip)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    public static String getAvaliableIps() {
        String retorno = "";
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    //System.out.println(i.getHostAddress());
                    retorno += "'" + i.getHostAddress() + "'";
                    if (ee.hasMoreElements()) {
                        retorno += ", ";
                    }
                }

                String substring = retorno.substring(retorno.length() - 1);
                if (substring.equals("'")) {
                    retorno += ", ";
                }
            }
        } catch (Exception e) {
        }
        if (retorno.length() > 0) {
            retorno = retorno.substring(0, retorno.length() - 1);
        }

        return retorno;
    }

    public static byte[] readFileBytes(String filename) throws IOException {
        Path path = Paths.get(filename);
        return Files.readAllBytes(path);
    }

    public static void print(Object p) {
        if (p instanceof PrivateKey) {
            StringWriter sw = new StringWriter();
            JcaPEMWriter writer = new JcaPEMWriter(sw);
            try {
                writer.writeObject(p);
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Methods.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(sw.getBuffer().toString());
        } else {
            System.out.println(p);
        }

    }
}
