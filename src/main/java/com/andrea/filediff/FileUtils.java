package com.andrea.filediff;
import android.util.Log;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public static String calculateMD5(String filename){
        String md5 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try (InputStream is = Files.newInputStream(Paths.get(filename))) {
                md5 = DigestUtils.md5Hex(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d("Errore","versione Android non supportata! ");
        }

        return md5;

    }
}
