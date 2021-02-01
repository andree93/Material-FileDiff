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

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FileUtils {

    public static HashMap<String,FileRepresentation> jsonObjectParser(Uri uri, Context context){
        HashMap<String, FileRepresentation> fileRepresentationHashMap = new HashMap<>();
        String filecontent="";

        ParcelFileDescriptor pfd = null;
        String filename =  UriToFileName(uri, context);
        Log.d("test2", "nomefile: "+ filename);

        pfd = getFileDescriptorFromUri(uri, context);
        try(FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd)){

            filecontent= IOUtils.toString(fis, "UTF-8");
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        JSONObject obj= null;
        try {
             obj = new JSONObject(filecontent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonarr = null;

        try {
            jsonarr = obj.getJSONArray("filelist");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i<jsonarr.length(); i++){
            JSONObject tmpjsobj=null;
            try {
                tmpjsobj = jsonarr.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String tmpNomeFile = "";
            String tmpHashFile = "";

            try {
                tmpNomeFile = tmpjsobj.getString("nome");
                tmpHashFile = tmpjsobj.getString("hash");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            FileRepresentation fileRepresentation = new FileRepresentation(tmpNomeFile,tmpHashFile);
            fileRepresentationHashMap.put(tmpNomeFile, fileRepresentation);
        }
        return fileRepresentationHashMap;
    }





    /** Questo metodo genera un JSONObject contenente un JSON Array  a partire da una lista di oggetti FileRepresentation*/
    public static JSONObject createJSON (List<FileRepresentation> fileRepresentationList){
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();

        for(FileRepresentation fileRepresentation : fileRepresentationList){
            try {
                ja.put(new JSONObject().put("nome", fileRepresentation.nome).put("hash", fileRepresentation.hash));
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

    /** IMPORTANTE: Modificare il metodo in modo da gestire un'eventuale eccezione del metodo  getFileDescriptorFromUri
     * e restituire un FileRepresentation con valori sentinella (o nulli) anzichè propagare l'eccezione
     * al metodo chiamante, che causerebbe l'invocazione del metodo onError in RXJava2.
     * Sarà possibile ottenere l'elenco dei file mancanti confrontando la lista di Uri (o nomi dei file)
     * con quella ottenuta dopo il calcolo dell'hash dei file.
     * Nel metodo onNext, si potrebbe controllare se le proprietà dell'oggetto FileRepresentation contengono valori nulli
     * e nel caso, non aggiungerlo alla lista**/
    public static FileRepresentation calcolaChecksumFromUri(Uri uri, Context context ) {
        ParcelFileDescriptor pfd = null;
        String filename =  UriToFileName(uri, context);
        Log.d("test2", "nomefile: "+ filename);

        pfd = getFileDescriptorFromUri(uri, context);
        String hash=null;
        try(FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd)){
            hash = new String(Hex.encodeHex(DigestUtils.md5(fis)));
            //Log.d("test IO", "bbb!");

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            Log.d("test IO", "ccc!");
            //Log.d("test IO", "ECCEZIONE!");
        }
        Log.d("test", "calcolaChecksumFromUri return");
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
