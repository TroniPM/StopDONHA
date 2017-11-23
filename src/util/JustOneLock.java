/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class JustOneLock {

    FileLock lock;
    FileChannel channel;

    public boolean isAppActive() throws Exception {
        File file = new File(System.getProperty("user.home"),
                "FireZeMissiles1111" + ".tmp");
        channel = new RandomAccessFile(file, "rw").getChannel();

        lock = channel.tryLock();
        if (lock == null) {
            return true;
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    lock.release();
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return false;
    }
}
