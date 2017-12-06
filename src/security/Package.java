/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.io.Serializable;

/**
 *
 * @author PMateus <paulomatew@gmailcom>
 */
public class Package implements Serializable {

    public static final long serialVersionUID = -9113128345134122381L;
    public byte[] auth;
    public byte[] data;
    public int number;
    public String tag;

    public Package(byte[] auth, byte[] data, int number) {
        this.auth = auth;
        this.data = data;
        //this.tag = tag;
        this.number = number;
    }

    @Override
    public String toString() {
        return "auth: " + auth + "\tdata: " + data;
    }
}
