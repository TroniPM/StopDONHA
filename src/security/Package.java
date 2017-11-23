/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.crypto.SecretKey;
import util.Session;

/**
 *
 * @author Matt
 */
public class Package implements Serializable {

    public static final long serialVersionUID = -916243193401875381L;
    public byte[] signature;
    public byte[] data;
    public String objeto;//qual o tipo do objeto para fazer o casting
    public int number;
    public String tag;

    public Package(byte[] signature, byte[] data, String tag, int number, String objeto) {
        this.signature = signature;
        this.tag = tag;
        this.number = number;
        this.data = data;
        this.objeto = objeto;
    }

    public void desbrincar() {

    }

    public byte[] convertToByteArray() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            byte[] objeto = baos.toByteArray();
            oos.close();

            return objeto;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Package convertFromByteArray(byte[] str) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(str);
            ObjectInputStream ois = new ObjectInputStream(bais);

            return (Package) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }
}
