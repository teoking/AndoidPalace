package com.teok.android.xposed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
* Created by teo on 10/9/14.
*/
public class InnoLog {
    static File logFile = new File("/data/data/com.teok.android/app_files", "android_palace_log.txt");

    static void append(String logLine) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(logFile, "rw");
            byte[] bytes = (logLine + "\r\n").getBytes();
            raf.setLength(raf.length() + (long) bytes.length + 1);
            raf.seek(raf.length() - 1);
            raf.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
//        Log.d("xposed", "add log :: " + logLine + "  pid=" + android.os.Process.myPid());
    }

    static void clear() {
        try {
            RandomAccessFile raf = new RandomAccessFile(logFile, "rw");
            raf.setLength(0);
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static List<String> getCachedLogs() {
        List<String> log = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(logFile));
            String line;
            log = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
                log.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (log != null) {
                log.add(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.add(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        return log;
    }

}
