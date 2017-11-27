package util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import entidades.network.Servidor;
import entidades.network.sendible.EndRound;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static void writeOnFile(String path, String content, boolean append) {
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
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
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

    public static String convertToString(Object obj) {
        try {
            String str;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] objeto = baos.toByteArray();
            str = Base64.encode(objeto);
            oos.close();
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fazer CASTING após uso.
     *
     * @param str
     * @return
     * @throws ClassNotFoundException
     */
    public static Object convertFromString(String str) throws ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(str));
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
        String ret = "";
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    //System.out.println(i.getHostAddress());
                    ret += "'" + i.getHostAddress() + "'";
                    if (ee.hasMoreElements()) {
                        ret += " ou ";
                    }
                }
                if (e.hasMoreElements()) {
                    ret += " ou ";
                }
            }
        } catch (Exception e) {
        }

        return ret;
    }
}
