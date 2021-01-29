package com.andrea.materialfilediff;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
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


    /** Questo metodo genera un JSONObject contenente un JSON Array  a partire da una lista di oggetti FileRepresentation*/
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


    /** Questo metodo ritorna la rappresentazione in stringa (formato JSON) di un JSONObject */
    public static String JSONObjectToString(JSONObject jo){
        String result = "";
        try {
            result = jo.toString(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    /** Questo metodo calcolaa l'MD5 di un file, a partire da un Uri
     *
     * @param uri Uri del file
     * @param context  Android activity context, necessario per l'accesso al file ottenuto mediante android file storage framework
     * @return FileRepresentation - Rappresentazione delle proprietà principali di un file
     */

    public static FileRepresentation calcolaChecksumFromUri(Uri uri, Context context ) {
        ParcelFileDescriptor pfd = null;
        pfd = getFileDescriptorFromUri(uri, context);
        String filename =  UriToFileName(uri, context);
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


    /**Restituisce una lista di Uri a partire da un oggetto Clipdata
     *
     * @param clipData
     * @return List<Uri>
     */
    public static List<Uri> clipDataToUriList(ClipData clipData){
        List<Uri> uriList = new ArrayList<>();
        if (clipData != null){
            for(int i = 0; i < clipData.getItemCount(); i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                uriList.add(uri);
            }
        }
        return uriList;
    }


    /**Restituisce una lista di Uri a partire da un Intent
     *
     * @param  data Intent
     * @return List<Uri>
     */
    public static List<Uri> clipDataToUriList(Intent data){
        List<Uri> uriList = new ArrayList<>();
        if( data != null){
            ClipData clipData = data.getClipData();
            if (clipData != null){
                for(int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    uriList.add(uri);
                }
            } else{ //è stato selezionato un solo file
                uriList.add(data.getData());
            }

        }
        return uriList;
    }


    /**
     * Metodo per ottenere il nome di un file a partire da un Uri
     * @param uri Uri del file
     * @param context Android Context, necessario per ottenere l'accesso al file selezionato mediante Android Storage Framework
     * @return
     */
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


    public static ParcelFileDescriptor getFileDescriptorFromUri(Uri uri, Context context){
        ParcelFileDescriptor pfd = null;
        try {
            pfd = context.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return pfd;
    }


    /**
     *
     * @param uri Uri del file
     * @param jo JsonObject che si desidera salvare
     * @param context Activity Context, necessario per accedere al file ottenuto mediante Android storage framework
     * @return
     */

    public  static boolean saveJSONExternalStorageFromUri(Uri uri, JSONObject jo, Context context){
        boolean success = false;
        String jsonParsed = JSONObjectToString(jo);
        try(OutputStream outputStream =  context.getContentResolver().openOutputStream(uri)){


            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            bw.write(jsonParsed);
            bw.flush();
            bw.close();

            Log.d("test-save", "save: "+jsonParsed);
            success = true;

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return success;
    }




}
