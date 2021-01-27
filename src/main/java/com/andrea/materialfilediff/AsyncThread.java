package com.andrea.materialfilediff;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import java.util.concurrent.*;

public class AsyncThread implements Runnable{
    private FileRepresentation fileRepresentation;
    private Uri uri;
    private Activity activity;
    CommunicationInterface com;


    public AsyncThread(Uri uri, Activity activity, CommunicationInterface com) {
        this.uri = uri;
        this.activity = activity;
        this.com = com;
    }

    @Override
    public void run() {

        FileRepresentation fileRepresentation = new FileRepresentation();

        FileUtils fu = new FileUtils();
        this.fileRepresentation = FileUtils.calcolaChecksumFromUri(uri, activity);
        com.updateProgress(10);

        Log.d("test-0X", fileRepresentation.nome);
        Log.d("test-0X", fileRepresentation.hash);

    }
}
