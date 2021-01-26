package com.andrea.materialfilediff;

import android.os.ParcelFileDescriptor;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils {

    public String calcolaChecksum(ParcelFileDescriptor pfd, CommunicationInterface com) {
        String md5=null;
        try(FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd)){
            md5 = new String(Hex.encodeHex(DigestUtils.md5(fis)));

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return  md5;

    }
}
