package com.andrea.materialfilediff;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {


    public static JSONObject createJSON (List<FileRepresentation> fileRepresentationList){
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();

        for(FileRepresentation fileRepresentation : fileRepresentationList){
            try {
                ja.put(new JSONObject().put(fileRepresentation.nome, fileRepresentation.hash));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            jo.put("filelist",ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jo;
    }


    public static String JSONObjectToString(JSONObject jo){
        String result = "";

        JSONObject tmpjsonob;

        try {
            result = jo.toString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }



    public static FileRepresentation calcolaChecksumFromUri(Uri uri, Activity activity ) {
        ParcelFileDescriptor pfd = null;
        pfd = getFileDescriptorFromUri(uri, activity);
        String filename =  UriToFileName(uri, activity);
        String hash=null;
        try(FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd)){
            hash = new String(Hex.encodeHex(DigestUtils.md5(fis)));
            //Log.d("test IO", "bbb!");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("test IO", "ccc!");
            //Log.d("test IO", "ECCEZIONE!");
        }
        return new FileRepresentation(filename, hash);
    }


    public static List<Uri> clipDataToUriList(ClipData clipData, Activity activity){
        List<Uri> uriList = new ArrayList<>();
        for(int i = 0; i < clipData.getItemCount(); i++) {
            Uri uri = clipData.getItemAt(i).getUri();
            uriList.add(uri);
        }
        return uriList;
    }



    public static String UriToFileName(Uri uri, Context context){
        return DocumentFile.fromSingleUri(context, uri).getName();
    }


    public static List<String> uriListToFileNameList(List<Uri> uriList, Context context){
        List<String> fileNameList = new ArrayList<>();
        for(Uri uri : uriList){
            fileNameList.add(UriToFileName(uri, context));
        }
        return fileNameList;
    }


    public static ParcelFileDescriptor getFileDescriptorFromUri(Uri uri, Activity activity){
        ParcelFileDescriptor pfd = null;
        try {
            pfd = activity.getApplicationContext().getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return pfd;
    }


    public  static boolean saveJSONExternalStorageFromUri(Uri uri, JSONObject jo,  Fragment fr){
        boolean success = false;
        String jsonParsed = JSONObjectToString(jo);
        try(OutputStream outputStream =  fr.getActivity().getContentResolver().openOutputStream(uri)){


            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(jsonParsed);
            bw.flush();
            bw.close();

            Log.d("test-save", "save: "+jsonParsed);
            success = true;

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }






        //
        return success;
    }




}
