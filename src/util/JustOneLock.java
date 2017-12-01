/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JustOneLock {

    private FileLock lock;
    private FileChannel channel;
    private String pathname = "./";
    private String filename = "simplelock.tmp";
    private File file = null;

    public boolean isAppActive() throws Exception {/*System.getProperty("user.home")*/
        file = new File(pathname, filename);
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

    public void removeFile() {
        try {
            lock.release();
            channel.close();

            Files.deleteIfExists(file.toPath());
        } catch (IOException ex) {
            Logger.getLogger(JustOneLock.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JustOneLock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
